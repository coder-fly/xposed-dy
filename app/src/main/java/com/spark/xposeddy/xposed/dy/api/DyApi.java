package com.spark.xposeddy.xposed.dy.api;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.spark.xposeddy.repository.bean.CommentBean;
import com.spark.xposeddy.util.TraceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedHelpers;

/**
 * 反射调用 dy ProfileApi、AwemeApi、CommentApi，直接解析api返回的结果
 * 有的结果返回比较慢，增加更快、更通用的DyApiRes，直接从网络层拦截结果
 */
public class DyApi {

    private ClassLoader classLoader;

    public DyApi(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * getProfileByUserId的同步方法
     * 通过handle的方式会丢失结果，用callback替换handle
     *
     * @param userId
     * @return
     */
    public Result getProfileByUserIdSync(String userId) {
        TraceUtil.e("getProfileByUserId req userId = " + userId);

        try {
            Class ProfileApiCall = XposedHelpers.findClass("com.ss.android.ugc.aweme.profile.api.q", classLoader);
            String url = "https://aweme.snssdk.com/aweme/v1/user/profile/other/?user_id=" + userId + "&address_book_access=2&from=0&source=UserProfileFragment_initUserData&publish_video_strategy_type=2&user_avatar_shrink=188_188&user_cover_shrink=750_422";
            Object profileApi = XposedHelpers.newInstance(ProfileApiCall, new Class[]{String.class, String.class}, url, null);
            Object res = XposedHelpers.callMethod(profileApi, "call");
            String body = JSON.toJSONString(res);
            JSONObject user = new JSONObject(body).optJSONObject("user");
            if (user != null) {
                String nickname = user.optString("nickname");
                String short_id = user.optString("shortId");
                String unique_id = user.optString("uniqueId");
                String uid = user.optString("uid");
                String sec_uid = user.optString("secUid");
                TraceUtil.e("getProfileByUserId res success, nickname = " + nickname + ", short_id = " + short_id + ", unique_id = " + unique_id + ", uid = " + uid + ", sec_uid = " + sec_uid);
                return Result.ok(sec_uid);
            } else {
                TraceUtil.e("getProfileByUserId res data is null");
            }
        } catch (Exception e) {
            TraceUtil.e("getProfileByUserId res exception: " + e.getMessage() + ", userId = " + userId);
        }
        return Result.error(Result.ResultCode.PARSE_ERR, "getProfileByUserId res parse error, userId = " + userId);
    }

    /**
     * getProfileByUserId的异步方法
     *
     * @param userId
     * @param callback
     */
    public void getProfileByUserId(String userId, ApiCallback callback) {
        TraceUtil.e("getProfileByUserId req userId = " + userId);

        try {
            Handler handler = new WeakRefHandler((Handler.Callback) msg -> {
                if (msg.what == 0) {
                    try {
                        String body = JSON.toJSONString(msg.obj);
                        JSONObject user = new JSONObject(body).optJSONObject("user");
                        if (user != null) {
                            String nickname = user.optString("nickname");
                            String short_id = user.optString("shortId");
                            String unique_id = user.optString("uniqueId");
                            String uid = user.optString("uid");
                            String sec_uid = user.optString("secUid");
                            TraceUtil.e("getProfileByUserId res success, nickname = " + nickname + ", short_id = " + short_id + ", unique_id = " + unique_id + ", uid = " + uid + ", sec_uid = " + sec_uid);
                            callback.setResult(Result.ok(sec_uid));
                            return true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                TraceUtil.e("getProfileByUserId res parse error, userId = " + userId);
                callback.setResult(Result.error(Result.ResultCode.PARSE_ERR, "getProfileByUserId res parse error, userId = " + userId));
                return true;
            }, Looper.getMainLooper());
            String url = "https://aweme.snssdk.com/aweme/v1/user/profile/other/?user_id=" + userId + "&address_book_access=2&from=0&source=UserProfileFragment_initUserData&publish_video_strategy_type=2&user_avatar_shrink=188_188&user_cover_shrink=750_422";
            Class ProfileManager = XposedHelpers.findClass("com.ss.android.ugc.aweme.profile.api.o", classLoader);
            Object profileManagerObj = XposedHelpers.callStaticMethod(ProfileManager, "a");
            XposedHelpers.callMethod(profileManagerObj, "a", new Class[]{Handler.class, String.class, String.class}, handler, url, null);
        } catch (Exception e) {
            TraceUtil.e("getProfileByUserId req exception: " + e.getMessage());
            callback.setResult(Result.error(Result.ResultCode.REQ_ERR, "getProfileByUserId req exception: " + e.getMessage()));
        }
    }

    /**
     * getAwemeList的同步方法
     * <p>
     * XposedHelpers.callStaticMethod(AwemeApi, "a", true...)，在没有网的时候会crash，报错如下
     * de.robv.android.xposed.XposedHelpers$InvocationTargetError: java.io.IOException
     * 修改为XposedHelpers.findMethodBestMatch、method.invoke的组合，如果在第一次请求的时候没有网也会crash，但是后面正常
     *
     * @param secUserId
     * @param maxCursor
     * @param count
     * @return
     */
    public Result getAwemeListSync(String secUserId, int maxCursor, int count) {
        TraceUtil.e("getAwemeList req secUserId = " + secUserId);

        try {
            Class AwemeApi = XposedHelpers.findClass("com.ss.android.ugc.aweme.profile.api.AwemeApi", classLoader);
            Method method = XposedHelpers.findMethodBestMatch(AwemeApi, "a", true, "", secUserId, 0, maxCursor, count, "", 0, 0, Integer.valueOf(2000));
            Object feedItemList = method.invoke(null, true, "", secUserId, 0, maxCursor, count, "", 0, 0, Integer.valueOf(2000));
            // Object feedItemList = XposedHelpers.callStaticMethod(AwemeApi, "a", true, "", secUserId, 0, maxCursor, count, "", 0, 0, Integer.valueOf(2000));
            if (feedItemList != null) {
                Class JSON = XposedHelpers.findClass("com.alibaba.fastjson.JSON", classLoader);
                String str = (String) XposedHelpers.callStaticMethod(JSON, "toJSONString", feedItemList);
                JSONArray arr = new JSONObject(str).optJSONArray("aweme_list");
                if (arr != null && arr.length() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject aweme = arr.optJSONObject(i);
                        String aweme_id = aweme.optString("aweme_id");
                        sb.append(aweme_id).append(";");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    TraceUtil.e("getAwemeList res success, list = " + sb.toString());
                    return Result.ok(sb.toString());
                } else {
                    TraceUtil.e("getAwemeList res data is null");
                }
            } else {
                TraceUtil.e("getAwemeList res feedItemList is null");
            }
        } catch (Exception e) {
            TraceUtil.e("getAwemeList res exception: " + e.getMessage() + ", secUserId = " + secUserId);
        }

        return Result.error(Result.ResultCode.PARSE_ERR, "getAwemeList res exception, secUserId = " + secUserId);
    }

    /**
     * getCommentList的同步方法
     *
     * @param awemeId
     * @param cursor
     * @param count
     * @return
     */
    public Result getCommentListSync(String awemeId, int cursor, int count) {
        ApiCallback callback = new ApiCallback();
        getCommentList(awemeId, cursor, count, callback);
        return callback.waitResult();
    }

    /**
     * getCommentList的异步方法
     *
     * @param awemeId
     * @param cursor
     * @param count
     * @param callback
     */
    public void getCommentList(String awemeId, int cursor, int count, ApiCallback callback) {
        TraceUtil.e("getCommentList req awemeId = " + awemeId);
        try {
            String secUserId = "";
            Class CommentApi = XposedHelpers.findClass("com.ss.android.ugc.aweme.comment.api.CommentApi", classLoader);
            Object task = XposedHelpers.callStaticMethod(CommentApi, "a",
                    awemeId, cursor, count, null, 2, 1, null, 0, "510100", 0, 0, Integer.valueOf(0), Integer.valueOf(0), 0, secUserId, 0);

            Class Continuation = XposedHelpers.findClass("bolts.Continuation", classLoader);
            Object continuationObj = Proxy.newProxyInstance(classLoader, new Class[]{Continuation}, new ContinuationHandler(awemeId, callback));
            XposedHelpers.callMethod(task, "continueWith", continuationObj);
        } catch (Exception e) {
            TraceUtil.e("getCommentList req exception: " + e.getMessage());
            callback.setResult(Result.error(Result.ResultCode.REQ_ERR, "getCommentList req exception: " + e.getMessage()));
        }
    }

    private class WeakRefHandler extends Handler {
        private WeakReference<Callback> mWeakReference;

        public WeakRefHandler(Callback callback) {
            mWeakReference = new WeakReference<>(callback);
        }

        public WeakRefHandler(Callback callback, Looper looper) {
            super(looper);
            mWeakReference = new WeakReference<>(callback);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mWeakReference != null && mWeakReference.get() != null) {
                Callback callback = mWeakReference.get();
                callback.handleMessage(msg);
            }
        }
    }

    public class ContinuationHandler implements InvocationHandler {
        private String awemeId;
        private ApiCallback callback;

        public ContinuationHandler(String awemeId, ApiCallback callback) {
            this.awemeId = awemeId;
            this.callback = callback;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                Object commentItemList = XposedHelpers.callMethod(args[0], "getResult");
                Class JSONCls = XposedHelpers.findClass("com.alibaba.fastjson.JSON", classLoader);
                String str = (String) XposedHelpers.callStaticMethod(JSONCls, "toJSONString", commentItemList);
                JSONArray arr = new JSONObject(str).optJSONArray("comments");
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
                    TraceUtil.e("getCommentList success, size = " + list.size() + ", data = " + list);
                    this.callback.setResult(Result.ok(listJson));
                    return null;
                } else {
                    TraceUtil.e("getCommentList res data is null");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            TraceUtil.e("getCommentList res parse error, awemeId = " + awemeId);
            this.callback.setResult(Result.error(Result.ResultCode.PARSE_ERR, "getCommentList res parse error, awemeId = " + awemeId));
            return null;
        }
    }
}
