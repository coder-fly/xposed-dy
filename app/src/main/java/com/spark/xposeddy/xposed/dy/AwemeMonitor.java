package com.spark.xposeddy.xposed.dy;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.spark.xposeddy.xposed.receiver.XpReceiver;
import com.spark.xposeddy.floatwindow.FloatWindowMgr;
import com.spark.xposeddy.persist.PersistKey;
import com.spark.xposeddy.util.JSONObjectPack;
import com.spark.xposeddy.util.TraceUtil;
import com.spark.xposeddy.xposed.receiver.XpBroadcast;
import com.spark.xposeddy.xposed.provider.PropertyProvider;
import com.spark.xposeddy.xposed.dy.api.DyApiRes;
import com.spark.xposeddy.xposed.dy.api.Result;
import com.spark.xposeddy.xposed.dy.bean.Aweme;
import com.spark.xposeddy.xposed.dy.bean.Profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 达人视频监听器
 */
public class AwemeMonitor extends Thread {

    private Context mContext;
    private DyApiRes mDyApiRes;
    private int videoCount;
    private List<Profile> profileList;
    private List<Aweme> mAwemeList;

    private long awemeSmallInterval;
    private long awemeLargeInterval;
    private volatile boolean isRunning;

    public AwemeMonitor(Context context, DyApiRes dyApiRes, int videoCount, List<Profile> profileList, List<Aweme> awemeList) {
        this.mContext = context;
        this.mDyApiRes = dyApiRes;
        this.videoCount = videoCount;
        this.profileList = profileList;
        this.mAwemeList = awemeList;

        this.awemeSmallInterval = Integer.parseInt(PropertyProvider.readData(context, PersistKey.AWEME_SMALL_INTERVAL_CNT));
        this.awemeLargeInterval = Integer.parseInt(PropertyProvider.readData(context, PersistKey.AWEME_LARGE_INTERVAL_CNT));
        TraceUtil.e("AwemeMonitor param: awemeSmallInterval = " + awemeSmallInterval + ", awemeLargeInterval = " + awemeLargeInterval);
    }

    public void startMonitor() {
        this.isRunning = true;
        this.start();
    }

    public void stopMonitor() {
        this.isRunning = false;
    }

    /**
     * 以小间隔-awemeSmallInterval更新达人视频列表
     * 更新一圈后休息大间隔-awemeLargeInterval再继续轮询
     */
    @Override
    public void run() {
        super.run();

        long runCnt = 0; // 运行次数
        int parseErrCnt = 0; // 解析错误次数
        int i = 0, profileCnt = profileList.size();
        XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_AWEME_COUNTS, String.valueOf(++runCnt)));

        while (isRunning) {
            Profile profile = profileList.get(i);
            Result res = mDyApiRes.getAwemeListSync(profile.getSecUid(), 0, this.videoCount);
            int code = res.getCode();
            // 如果连续3次解析错误，接口风控
            if (code == Result.ResultCode.PARSE_ERR) {
                if (++parseErrCnt > 2) {
//                        if (runCnt == 3) {
//                            stopMonitor();
//                            sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_STATUS, "拉取列表已风控，请切换设备后重试"));
//                            sendRisk(mContext, ReceiverMain.RISK_AWEME_LIST_SEVERE);
//                        } else {
                    XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_STATUS, "拉取列表已风控"));
                    XpBroadcast.sendRisk(mContext, XpReceiver.RISK_AWEME_LIST_MILD);
//                        }
                }
            } else {
                parseErrCnt = 0;
            }

            if (code == Result.ResultCode.OK) {
                String videoStr = res.getData();
                if (TextUtils.isEmpty(videoStr)) continue;
                String[] videos = videoStr.split(";");
                TraceUtil.e("getAwemeList setCount = " + this.videoCount + ", getCount = " + videos.length);
                if (videos.length > this.videoCount) {
                    videos = Arrays.copyOfRange(videos, 0, this.videoCount);
                }

                List<Aweme> awemeList = new ArrayList<>();
                for (String aweme : videos) {
                    if (!TextUtils.isEmpty(aweme)) {
                        awemeList.add(new Aweme(aweme, profile.getUid()));
                    }
                }
                if (awemeList.size() > 0) {
                    updateAwemeList(profile.getUid(), awemeList);
                }
            }

            try {
                if (++i >= profileCnt) {
                    XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_AWEME_TARGET, String.format("%d / %d", i, profileCnt)));
                    i = 0;
                    Thread.sleep(awemeLargeInterval);
                    XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_AWEME_COUNTS, String.valueOf(++runCnt)));
                } else {
                    XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_AWEME_TARGET, String.format("%d / %d", i, profileCnt)));
                    Thread.sleep(awemeSmallInterval);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新达人的视频列表
     *
     * @param uid
     * @param awemeList
     */
    private void updateAwemeList(String uid, List<Aweme> awemeList) {
        synchronized (mAwemeList) {
            // 删除uid之前的视频
            Iterator<Aweme> iterator = mAwemeList.iterator();
            while (iterator.hasNext()) {
                Aweme aweme = iterator.next();
                if (uid.equals(aweme.getUid())) {
                    iterator.remove();
                }
            }

            // 增加uid新的视频
            mAwemeList.addAll(awemeList);
            if (mAwemeList.size() > 0) {
                String awemeListJson = JSON.toJSONString(mAwemeList);
                PropertyProvider.writeData(mContext, PersistKey.CACHE_AWEME, awemeListJson);
            }
        }
    }
}
