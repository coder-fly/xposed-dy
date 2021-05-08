package com.spark.xposeddy.xposed.dy.api;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.spark.xposeddy.repository.bean.CommentBean;
import com.spark.xposeddy.util.TraceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.robv.android.xposed.XposedHelpers;

/**
 * 反射调用 dy ProfileApi、AwemeApi、CommentApi，直接从网络层拦截结果
 */
public class DyApiRes {
    private static final String fGetProfileTag = "/aweme/v1/user/profile/other/";
    private static final String fGetAwemeListTag = "/aweme/v1/aweme/post/";
    private static final String fGetCommentListTag = "/aweme/v2/comment/list/";
    private ClassLoader classLoader;
    private CallbackQueueMgr mCallbackQueueMgr;
    private ExecutorService executor;

    public DyApiRes(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.mCallbackQueueMgr = new CallbackQueueMgr();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void releaseThreadPool() {
        this.executor.shutdown();
        this.mCallbackQueueMgr.executor.shutdown();
    }

    public static boolean isContainsUrl(String url) {
        return url.contains(fGetAwemeListTag) || url.contains(fGetCommentListTag);
        // return url.contains(fGetProfileTag) || url.contains(fGetAwemeListTag) || url.contains(fGetCommentListTag);
    }

    public void setResponse(String url, String body) throws JSONException {
        String callUrl = mCallbackQueueMgr.matchUrl(url);
        ApiCallback callback = mCallbackQueueMgr.getCallback(callUrl);
        if (callback != null) {
            JSONObject jsonBody = new JSONObject(body);
            if (url.contains(fGetAwemeListTag)) {
                JSONArray arr = jsonBody.optJSONArray("aweme_list");
                if (arr != null && arr.length() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject aweme = arr.optJSONObject(i);
                        String aweme_id = aweme.optString("aweme_id");
                        String desc = aweme.optString("desc");
                        sb.append(aweme_id).append(";");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    TraceUtil.e("getAwemeList success, list = " + sb.toString());
                    callback.setResult(Result.ok(sb.toString()));
                    mCallbackQueueMgr.removeCallback(callUrl);
                    return;
                }
                TraceUtil.e("getAwemeList parse error, url = " + callUrl);
                callback.setResult(Result.error(Result.ResultCode.PARSE_ERR, "getAwemeList parse error, url = " + callUrl));
                mCallbackQueueMgr.removeCallback(callUrl);
            } else if (url.contains(fGetCommentListTag)) {
                JSONArray arr = jsonBody.optJSONArray("comments");
                if (arr != null && arr.length() > 0) {
                    List<CommentBean> list = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject comment = arr.optJSONObject(i);
                        String cid = comment.optString("cid");
                        long create_time = comment.optLong("create_time");
                        String text = comment.optString("text");
                        String aweme_id = comment.optString("aweme_id");

                        JSONObject user = comment.optJSONObject("user");
                        String uid = user.optString("uid");
                        String short_id = user.optString("short_id");
                        String nickname = user.optString("nickname");
                        String gender = user.optString("gender");
                        String birthday = user.optString("birthday");
                        String aweme_count = user.optString("aweme_count");
                        String unique_id = user.optString("unique_id");
                        birthday = TextUtils.isEmpty(birthday) ? "0" : birthday.split("-")[0];
                        short_id = TextUtils.isEmpty(unique_id) ? short_id : unique_id;

                        CommentBean bean = new CommentBean();
                        bean.setAwemeId(aweme_id).setUid(uid).setTypeCode("")
                                .setAcc(short_id).setNick(nickname).setSex(gender)
                                .setAge(birthday).setWorks_num(aweme_count).setContentID(cid)
                                .setContent(text).setRelevant(aweme_id).setCreate_time(create_time)
                                .setSource("2");
                        list.add(bean);
                    }

                    String listJson = JSON.toJSONString(list);
                    TraceUtil.d("getCommentList success, size = " + list.size() + ", data = " + listJson);
                    callback.setResult(Result.ok(listJson));
                    mCallbackQueueMgr.removeCallback(callUrl);
                    return;
                }
                TraceUtil.e("getCommentList parse error, url = " + callUrl);
                callback.setResult(Result.error(Result.ResultCode.PARSE_ERR, "getCommentList parse error, url = " + callUrl));
                mCallbackQueueMgr.removeCallback(callUrl);
            }
        } else {
            TraceUtil.e("callback is null, url = " + url);
        }
    }

    public Result getAwemeListSync(String secUserId, int maxCursor, int count) {
        ApiCallback callback = new ApiCallback();
        getAwemeList(secUserId, maxCursor, count, callback);
        return callback.waitResult();
    }

    public void getAwemeList(String secUserId, int maxCursor, int count, ApiCallback callback) {
        mCallbackQueueMgr.addCallback("&max_cursor=" + maxCursor + "&sec_user_id=" + secUserId, callback);

        Class AwemeApi = XposedHelpers.findClass("com.ss.android.ugc.aweme.profile.api.AwemeApi", classLoader);
        executor.execute(() -> XposedHelpers.callStaticMethod(AwemeApi, "a", true, "", secUserId, 0, maxCursor, count, "", 0, 0, Integer.valueOf(2000)));
    }

    public Result getCommentListSync(String awemeId, int cursor, int count) {
        ApiCallback callback = new ApiCallback();
        getCommentList(awemeId, cursor, count, callback);
        return callback.waitResult();
    }

    public void getCommentList(String awemeId, int cursor, int count, ApiCallback callback) {
        mCallbackQueueMgr.addCallback(fGetCommentListTag + "?aweme_id=" + awemeId + "&cursor=" + cursor + "&count=" + count, callback);

        String secUserId = "";
        Class CommentApi = XposedHelpers.findClass("com.ss.android.ugc.aweme.comment.api.CommentApi", classLoader);
        XposedHelpers.callStaticMethod(CommentApi, "a",
                awemeId, cursor, count, null, 2, 1, null, 0, "510100", 0, 0, Integer.valueOf(0), Integer.valueOf(0), 0, secUserId, 0);
    }

    private class CallbackQueueMgr {
        private HashMap<String, ApiTickCallback> mCallbackQueue;
        private ScheduledExecutorService executor;

        private CallbackQueueMgr() {
            mCallbackQueue = new HashMap<>();
            executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleWithFixedDelay(() -> clearOverTimeCallback(), 5, 5, TimeUnit.SECONDS);
        }

        private synchronized boolean addCallback(String url, ApiCallback callback) {
            ApiTickCallback tickCallback = new ApiTickCallback(callback, System.currentTimeMillis());
            if (mCallbackQueue.containsKey(url)) {
                TraceUtil.e("同样的请求还在处理中, url = " + url);
                callback.setResult(Result.error(Result.ResultCode.EXISTS, "同样的请求还在处理中, url = " + url));
                return false;
            }
            mCallbackQueue.put(url, tickCallback);
            return true;
        }

        private synchronized String matchUrl(String url) {
            Iterator<Map.Entry<String, ApiTickCallback>> iterator = mCallbackQueue.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ApiTickCallback> item = iterator.next();
                String callUrl = item.getKey();
                if (url.contains(callUrl)) {
                    return callUrl;
                }
            }

            return null;
        }

        private synchronized ApiCallback getCallback(String url) {
            ApiTickCallback tickCallback = mCallbackQueue.get(url);
            return tickCallback == null ? null : tickCallback.apiCallback;
        }

        private synchronized void removeCallback(String url) {
            mCallbackQueue.remove(url);
        }

        private synchronized void clearOverTimeCallback() {
            long now = System.currentTimeMillis();
            Iterator<Map.Entry<String, ApiTickCallback>> iterator = mCallbackQueue.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ApiTickCallback> item = iterator.next();
                ApiTickCallback callback = item.getValue();
                if (now - callback.tick > 2000) { // 超时时间2s
                    TraceUtil.e("请求超时, url = " + item.getKey());
                    callback.apiCallback.setResult(Result.error(Result.ResultCode.TIME_OUT, "请求超时, url = " + item.getKey()));
                    iterator.remove();
                }
            }
        }

        private class ApiTickCallback {
            public ApiCallback apiCallback;
            public long tick;

            public ApiTickCallback(ApiCallback apiCallback, long tick) {
                this.apiCallback = apiCallback;
                this.tick = tick;
            }
        }
    }
}
