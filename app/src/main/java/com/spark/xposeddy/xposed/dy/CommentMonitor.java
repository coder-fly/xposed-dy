package com.spark.xposeddy.xposed.dy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.spark.xposeddy.xposed.receiver.XpReceiver;
import com.spark.xposeddy.floatwindow.FloatWindowMgr;
import com.spark.xposeddy.net.impl.ApiUrl;
import com.spark.xposeddy.persist.PersistKey;
import com.spark.xposeddy.repository.bean.CommentBean;
import com.spark.xposeddy.util.JSONObjectPack;
import com.spark.xposeddy.util.TraceUtil;
import com.spark.xposeddy.xposed.receiver.XpBroadcast;
import com.spark.xposeddy.xposed.provider.DbProvider;
import com.spark.xposeddy.xposed.HttpHelper;
import com.spark.xposeddy.xposed.provider.PropertyProvider;
import com.spark.xposeddy.xposed.dy.api.DyApi;
import com.spark.xposeddy.xposed.dy.api.DyApiRes;
import com.spark.xposeddy.xposed.dy.api.Result;
import com.spark.xposeddy.xposed.dy.bean.Aweme;
import com.spark.xposeddy.xposed.dy.bean.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 视频评论监听器
 */
public class CommentMonitor extends Thread {
    private String domain;
    private String deviceId;
    private String typeId;
    private int videoCount;
    private int commentPage;
    private int sampleDiff;
    private int intervalTime;
    private String sampleAccounts; // 采集达人列表
    private String sampleVideos; // 采集视频列表

    private Context mContext;
    private DyApi mDyApi;
    private DyApiRes mDyApiRes;
    private ExecutorService executor;
    private volatile boolean isRunning;
    private AwemeMonitor awemeMonitor; // aweme更新任务
    private List<Aweme> mAwemeList = new ArrayList<>(); // 采集的视频aweme列表
    private AtomicInteger commentSampleCnt; // 评论采集次数
    private AtomicInteger awemeNum; // 获取视频评论序号
    private int uploadCnt; // 评论上传数量
    private HandlerThread mHandlerThread;
    private Handler mHandler;

    public CommentMonitor(Context context, DyApi dyApi, DyApiRes dyApiRes) {
        this.domain = PropertyProvider.readData(context, PersistKey.DOMAIN);
        this.deviceId = PropertyProvider.readData(context, PersistKey.DEVICE_ID);
        this.typeId = PropertyProvider.readData(context, PersistKey.TYPE_ID);
        this.commentPage = Integer.parseInt(PropertyProvider.readData(context, PersistKey.COMMENT_PAGE));
        this.videoCount = Integer.parseInt(PropertyProvider.readData(context, PersistKey.VIDEO_COUNT));
        this.sampleDiff = Integer.parseInt(PropertyProvider.readData(context, PersistKey.SAMPLE_DIFF));
        this.intervalTime = Integer.parseInt(PropertyProvider.readData(context, PersistKey.INTERVAL_TIME));
        this.sampleAccounts = PropertyProvider.readData(context, PersistKey.SAMPLE_ACCOUNTS);
        this.sampleVideos = PropertyProvider.readData(context, PersistKey.SAMPLE_VIDEOS);
        TraceUtil.e("CommentMonitor param: commentPage = " + commentPage + ", videoCount = " + videoCount + ", sampleDiff = " + sampleDiff + ", intervalTime = " + intervalTime);
        TraceUtil.e("CommentMonitor param: sampleAccounts = " + sampleAccounts + ", sampleVideos = " + sampleVideos);

        this.mContext = context;
        this.mDyApi = dyApi;
        this.mDyApiRes = dyApiRes;
        this.isRunning = false;
        this.awemeMonitor = null;
        this.commentSampleCnt = new AtomicInteger();
        this.commentSampleCnt.set(0);
        this.awemeNum = new AtomicInteger();
        this.awemeNum.set(0);
        this.uploadCnt = 0;
        this.mHandlerThread = new HandlerThread("uploadComment");
        this.mHandlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper()) {
            private HashMap<String, List<CommentBean>> commentMap = new HashMap<>();

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) { // 1 发送中，0 发送结束
                    if (this.commentMap.size() > 0) {
                        uploadComment(this.commentMap);
                        this.commentMap.clear();
                    }
                } else {
                    HashMap<String, List<CommentBean>> map = (HashMap<String, List<CommentBean>>) msg.obj;
                    TraceUtil.e("handleMessage aweme size = " + map.size());
                    this.commentMap.putAll(map);
                    if (this.commentMap.size() >= 5) {
                        uploadComment(this.commentMap);
                        this.commentMap.clear();
                    }
                }
            }
        };
        this.executor = new ThreadPoolExecutor(5, 5, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public void startMonitor() {
        this.isRunning = true;
        this.start();
    }

    public void stopMonitor() {
        this.isRunning = false;
        this.mHandlerThread.quit();
        this.executor.shutdown();
    }

    @Override
    public void run() {
        super.run();

        long runCnt = 0; // 运行次数
        Set<String> videoSet = new LinkedHashSet<>(); // 需要采集的视频
        Set<String> uidSet = new LinkedHashSet<>(); // 需要采集的达人
        List<Profile> profileList = new ArrayList<>(); // 需要采集达人的信息缓存

        String profileStr = PropertyProvider.readData(mContext, PersistKey.CACHE_PROFILE);
        if (!TextUtils.isEmpty(profileStr)) {
            profileList = JSON.parseArray(profileStr, Profile.class);
            TraceUtil.e("profileList size = " + profileList.size() + ", data = " + profileStr);
        }

        String awemeStr = PropertyProvider.readData(mContext, PersistKey.CACHE_AWEME);
        if (!TextUtils.isEmpty(awemeStr)) {
            this.mAwemeList = JSON.parseArray(awemeStr, Aweme.class);
            TraceUtil.e("mAwemeList size = " + mAwemeList.size() + ", data = " + awemeStr);

            if (this.awemeMonitor == null && profileList.size() > 0) {
                this.awemeMonitor = new AwemeMonitor(this.mContext, this.mDyApiRes, this.videoCount, profileList, mAwemeList);
                this.awemeMonitor.startMonitor();
            }
        }

        while (isRunning) {
            try {
                TraceUtil.e("----------- " + "task " + ++runCnt + " -----------");
                XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_COUNTS, String.valueOf(runCnt)));

                // 获取设备的采集任务，并更新profile缓存
                Map<String, String> map = getDeviceTask(this.domain, this.deviceId);
                if (!map.isEmpty()) {
                    String uids = map.get("uids");
                    String videos = map.get("videos");
                    this.sampleAccounts = uids;
                    this.sampleVideos = videos;
                    XpBroadcast.sendDeviceTask(mContext, this.sampleAccounts, this.sampleVideos);

                    // 更新profile缓存
                    if (!TextUtils.isEmpty(sampleAccounts)) {
                        uidSet.clear();
                        uidSet.addAll(Arrays.asList(sampleAccounts.split(";")));

                        profileList = getProfileByUser(uidSet);
                        if (isRunning && profileList.size() > 0) {
                            String profileListJson = JSON.toJSONString(profileList);
                            PropertyProvider.writeData(mContext, PersistKey.CACHE_PROFILE, profileListJson);
                            PropertyProvider.writeData(mContext, PersistKey.CACHE_AWEME, "");
                        }
                    }
                }

                // 当没有profile缓存时，把达人列表转换profile列表，并更新profile缓存
                if (TextUtils.isEmpty(PropertyProvider.readData(mContext, PersistKey.CACHE_PROFILE))) {
                    if (!TextUtils.isEmpty(sampleAccounts)) {
                        uidSet.clear();
                        uidSet.addAll(Arrays.asList(sampleAccounts.split(";")));

                        // 更新profileList
                        profileList = getProfileByUser(uidSet);
                        if (isRunning && profileList.size() > 0) {
                            String profileListJson = JSON.toJSONString(profileList);
                            PropertyProvider.writeData(mContext, PersistKey.CACHE_PROFILE, profileListJson);
                            PropertyProvider.writeData(mContext, PersistKey.CACHE_AWEME, "");
                        }
                    }
                }

                // 当没有aweme缓存时，通过profile获取aweme列表，并更新aweme缓存
                if (TextUtils.isEmpty(PropertyProvider.readData(mContext, PersistKey.CACHE_AWEME))) {
                    if (this.awemeMonitor != null) {
                        this.awemeMonitor.stopMonitor();
                        this.awemeMonitor = null;
                    }

                    // 更新videoSet集合
                    if (!TextUtils.isEmpty(this.sampleVideos)) {
                        videoSet.clear();
                        videoSet.addAll(Arrays.asList(sampleVideos.split(";")));
                    }

                    // profile列表为空，直接添加videoSet集合
                    if (profileList.size() == 0) {
                        // 清空AwemeList
                        mAwemeList.clear();

                        // 添加采样视频id
                        for (String aweme : videoSet) {
                            mAwemeList.add(new Aweme(aweme, null));
                        }
                    } else {
                        List<Aweme> list = getAweme(profileList);
                        if (list == null) {
                            riskHandle(videoSet.size(), mAwemeList);
                            break;
                        } else if (list.size() > 0) {
                            // 清空AwemeList
                            mAwemeList.clear();

                            // 添加采样视频id
                            for (String aweme : videoSet) {
                                mAwemeList.add(new Aweme(aweme, null));
                            }

                            mAwemeList.addAll(list);
                        }
                    }

                    // 更新aweme缓存
                    if (isRunning && mAwemeList.size() > 0) {
                        String awemeListJson = JSON.toJSONString(mAwemeList);
                        PropertyProvider.writeData(mContext, PersistKey.CACHE_AWEME, awemeListJson);
                        TraceUtil.e("mAwemeList size = " + mAwemeList.size() + ", data = " + awemeListJson);
                        if (this.awemeMonitor == null && profileList.size() > 0) {
                            this.awemeMonitor = new AwemeMonitor(this.mContext, this.mDyApiRes, this.videoCount, profileList, mAwemeList);
                            this.awemeMonitor.startMonitor();
                        }
                    }
                }
                // 获取视频的评论
                int size = 0;
                synchronized (mAwemeList) {
                    awemeNum.set(0);
                    size = mAwemeList.size();
                    for (Aweme aweme : mAwemeList) {
                        // shutdown后，在执行execute会提示: java.util.concurrent.RejectedExecutionException
                        if (!isRunning) break;
                        int finalSize = size;
                        executor.execute(new Runnable() {
                            @SuppressLint("DefaultLocale")
                            @Override
                            public void run() {
                                if (!isRunning) return;
                                TraceUtil.e("----------- " + "aweme = " + aweme + " -----------");
                                List<CommentBean> list = getAwemeComment(aweme);
                                XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_TARGET, String.format("%d / %d", awemeNum.incrementAndGet(), finalSize)));
                                if (list.size() > 0) {
                                    int tempCommentCnt = commentSampleCnt.addAndGet(list.size());
                                    XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_SAMPLE, String.valueOf(tempCommentCnt)));

                                    // 上传有效的评论
                                    List<CommentBean> noUploadComment = getValidSortComments(aweme.getAwemeId(), list);
                                    if (noUploadComment.isEmpty()) {
                                        TraceUtil.e("no valid comments");
                                    } else {
                                        TraceUtil.e("valid comments: " + JSON.toJSONString(noUploadComment));
                                        HashMap<String, List<CommentBean>> map = new HashMap<>();
                                        map.put(aweme.getAwemeId(), noUploadComment);
                                        Message msg = mHandler.obtainMessage();
                                        msg.what = 1; // 1 发送中，0 发送结束
                                        msg.obj = map;
                                        mHandler.sendMessage(msg);
                                    }
                                }
                            }
                        });
                    }
                }

                // 等待execute都执行完
                do {
                    Thread.sleep(1000);
                    if (!isRunning) {
                        break;
                    }
                } while (awemeNum.get() < size);
                XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_SAMPLE, String.valueOf(commentSampleCnt.get())));
                Message msg = mHandler.obtainMessage();
                msg.what = 0; // 1 发送中，0 发送结束
                mHandler.sendMessageDelayed(msg, 1000);

                // 风控处理
                if (isRunning) {
                    riskHandle(commentSampleCnt.get());
                }

                // 新任务间隔时间
                Thread.sleep(this.intervalTime * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (this.awemeMonitor != null) {
            this.awemeMonitor.stopMonitor();
            this.awemeMonitor = null;
        }
    }

    /**
     * 获取设备的采集任务，更新设备任务状态
     *
     * @return map
     * --empty
     * --uids, videos
     */
    private Map<String, String> getDeviceTask(String domain, String deviceId) {
        Map<String, String> reqMap = new HashMap(), resMap = new HashMap();
        reqMap.put("deviceNum", deviceId);
        // 获取设备的采集任务
        JSONObject res = HttpHelper.get(ApiUrl.getDeviceTask(domain), reqMap);
        TraceUtil.e("getDeviceTask res = " + res.toString());
        if (res.optBoolean("success")) {
            JSONObject data = res.optJSONObject("data");
            if (data.has("info")) {
                JSONObject info = data.optJSONObject("info");
                String uids = info.optString("dy_ids");
                String videos = info.optString("video_urls");
                resMap.put("uids", uids);
                resMap.put("videos", videos);

                // 更新设备任务状态
                res = HttpHelper.get(ApiUrl.updateDeviceTaskState(domain), reqMap);
                TraceUtil.e("updateDeviceTaskState res = " + res.toString());
                return resMap;
            }
        }

        return resMap;
    }

    /**
     * 过滤掉超时、已经上传的评论，并排序
     *
     * @param sourceList 某个视频的评论列表
     * @return
     */
    private List<CommentBean> getValidSortComments(@NonNull String awemeId, @NonNull List<CommentBean> sourceList) {
        // 过滤掉超时的评论
        long now = System.currentTimeMillis() / 1000;
        long diff = 0;
        List<CommentBean> noOvertimeComment = new ArrayList<>();
        for (CommentBean bean : sourceList) {
            diff = now - bean.getCreate_time();
            if (diff > 0 && diff < sampleDiff) {
                noOvertimeComment.add(bean);
            }
        }
        if (noOvertimeComment.isEmpty()) {
            return noOvertimeComment;
        }

        // 对评论列表做排序
        Collections.sort(noOvertimeComment);
        // 过滤掉已经上传的评论
        List<CommentBean> noUploadComment = new ArrayList<>();
        CommentBean dbLatest = DbProvider.readComment(mContext, awemeId);
        if (dbLatest == null) {
            noUploadComment.addAll(noOvertimeComment);
        } else {
            long dbLatestTick = dbLatest.getCreate_time();
            for (CommentBean bean : noOvertimeComment) {
                if (bean.getCreate_time() > dbLatestTick) {
                    noUploadComment.add(bean);
                }
            }
        }

        return noUploadComment;
    }

    /**
     * 上传评论列表
     *
     * @param mapComment
     */
    private void uploadComment(@NonNull HashMap<String, List<CommentBean>> mapComment) {
        TraceUtil.e("uploadComment aweme size = " + mapComment.size() + ", content = " + JSON.toJSONString(mapComment));
        List<CommentBean> noUploadComment = new ArrayList<>();
        for (String awemeId : mapComment.keySet()) {
            noUploadComment.addAll(mapComment.get(awemeId));
        }

        uploadCnt += noUploadComment.size();
        String str = JSON.toJSONString(noUploadComment);
        JSONArray list = new JSONArray();
        try {
            list = new JSONArray(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject obj = new JSONObjectPack()
                .putValue("jsonArr", list)
                .getJSONObject();

        JSONObject res = HttpHelper.post(ApiUrl.uploadComment(domain), obj);
        if (res.optBoolean("success")) {
            for (String awemeId : mapComment.keySet()) {
                saveComment(awemeId, mapComment.get(awemeId));
            }
            XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_COMMENT_UPLOAD, String.valueOf(uploadCnt)));
        }
    }

    /**
     * 存储最新的评论
     *
     * @param awemeId
     * @param noUploadComment
     */
    private void saveComment(@NonNull String awemeId, @NonNull List<CommentBean> noUploadComment) {
        if (noUploadComment != null && noUploadComment.size() > 0) {
            CommentBean dbLatest = DbProvider.readComment(mContext, awemeId);
            CommentBean mapLatest = noUploadComment.get(0);

            if (dbLatest == null || (dbLatest.getCreate_time() < mapLatest.getCreate_time())) {
                DbProvider.insertComment(mContext, mapLatest);
            }
        }
    }

    /**
     * 把uid列表转换为Profile列表
     *
     * @param uidSet uid列表
     * @return Profile列表
     */
    private List<Profile> getProfileByUser(Set<String> uidSet) {
        List<Profile> profileList = new ArrayList<>();

        int cnt = 0;
        XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_TARGET, String.format("%d / %d", cnt, uidSet.size())));
        for (String uid : uidSet) {
            // 提前退出
            if (!isRunning) return profileList;
            if (TextUtils.isEmpty(uid)) continue;

            Result res = mDyApi.getProfileByUserIdSync(uid);
            XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_TARGET, String.format("%d / %d", ++cnt, uidSet.size())));
            int code = res.getCode();
            if (code != Result.ResultCode.OK) {
                continue;
            }
            String sec_uid = res.getData();
            if (TextUtils.isEmpty(sec_uid)) continue;
            profileList.add(new Profile(uid, sec_uid));
        }

        return profileList;
    }

    /**
     * 通过user获取aweme列表
     *
     * @param userList user列表
     * @return aweme列表
     * null，awemeList 风控
     * 空列表，提前退出
     * 非空列表，正常
     */
    private List<Aweme> getAweme(List<Profile> userList) {
        List<Aweme> awemeList = new ArrayList<>();
        int parseErrCnt = 0; // 解析错误次数

        int cnt = 0;
        XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_TARGET, String.format("%d / %d", cnt, userList.size())));
        for (Profile user : userList) {
            // 提前退出
            if (!isRunning) return awemeList;

            Result res = mDyApiRes.getAwemeListSync(user.getSecUid(), 0, this.videoCount);
            XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_TARGET, String.format("%d / %d", ++cnt, userList.size())));
            int code = res.getCode();

            // 如果连续3次解析错误，接口风控
            if (code == Result.ResultCode.PARSE_ERR) {
                if (++parseErrCnt > 2) {
                    awemeList.clear();
                    return null;
                }
            } else {
                parseErrCnt = 0;
            }

            if (code != Result.ResultCode.OK) {
                continue;
            }

            String videoStr = res.getData();
            if (TextUtils.isEmpty(videoStr)) continue;
            String[] videos = videoStr.split(";");
            TraceUtil.e("getAwemeList setCount = " + this.videoCount + ", getCount = " + videos.length);
            if (videos.length > this.videoCount) {
                videos = Arrays.copyOfRange(videos, 0, this.videoCount);
            }

            for (String aweme : videos) {
                if (!TextUtils.isEmpty(aweme)) {
                    awemeList.add(new Aweme(aweme, user.getUid()));
                }
            }
        }

        XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_STATUS, "拉取列表正常"));
        return awemeList;
    }

    /**
     * 获取视频评论列表
     *
     * @param aweme 视频id
     * @return 评论列表
     */
    private List<CommentBean> getAwemeComment(Aweme aweme) {
        List<CommentBean> commentList = new ArrayList<>();

        for (int i = 0; i < commentPage; i++) {
            Result res = mDyApiRes.getCommentListSync(aweme.getAwemeId(), i * 20, 20);
            int code = res.getCode();
            if (code != Result.ResultCode.OK) {
                return commentList;
            }
            String data = res.getData();
            if (TextUtils.isEmpty(data)) break;

            List<CommentBean> list = JSON.parseArray(data, CommentBean.class);
            if (list != null && list.size() > 0) {
                String awemeId = list.get(0).getAwemeId();
                if (aweme.getAwemeId().equals(awemeId)) {
                    commentList.addAll(list);
                } else {
                    TraceUtil.e("getCommentList error, aweme != awemeId");
                }
            }

            // 评论不足20条，没有新评论，提前退出
            if (list.size() < 20) {
                break;
            }
        }

        // 给评论添加typeId，删除视频发布者自己的评论
        Iterator<CommentBean> iterator = commentList.iterator();
        while (iterator.hasNext()) {
            CommentBean bean = iterator.next();
            String uid = aweme.getUid();
            // 采集视频列表uid为null
            if (!TextUtils.isEmpty(uid) && uid.equals(bean.getUid())) {
                iterator.remove();
                TraceUtil.d("不能自己评论自己，contentID = " + bean.getContentID());
            } else {
                bean.setTypeCode(this.typeId);
            }
        }

        return commentList;
    }

    /**
     * 达人视频风控处理
     *
     * @param sampleVideosLen
     * @param awemeList
     */
    private void riskHandle(int sampleVideosLen, List<Aweme> awemeList) {
        if (awemeList.size() <= sampleVideosLen) {
            stopMonitor();
            XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_STATUS, "拉取列表已风控，请切换设备后重试"));
            XpBroadcast.sendRisk(mContext, XpReceiver.RISK_AWEME_LIST_SEVERE);
        } else {
            XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_STATUS, "拉取列表已风控"));
            XpBroadcast.sendRisk(mContext, XpReceiver.RISK_AWEME_LIST_MILD);
        }
        TraceUtil.e("riskHandle awemeList: sampleVideosLen = " + sampleVideosLen + ", awemeSet = " + awemeList.size());
    }

    private long lastCommentCnt = 0;

    /**
     * 视频评论风控处理
     *
     * @param commentCnt
     */
    private void riskHandle(long commentCnt) {
        if (lastCommentCnt == commentCnt) {
            stopMonitor();
            XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_STATUS, "评论列表已风控"));
            XpBroadcast.sendRisk(mContext, XpReceiver.RISK_COMMENT_LIST_MILD);
        } else {

        }
        TraceUtil.e("riskHandle commentList: lastCommentCnt = " + lastCommentCnt + ", commentCnt = " + commentCnt);
        lastCommentCnt = commentCnt;
    }

}
