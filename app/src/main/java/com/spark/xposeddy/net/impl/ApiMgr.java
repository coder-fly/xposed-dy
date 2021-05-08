package com.spark.xposeddy.net.impl;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.spark.xposeddy.net.Callback;
import com.spark.xposeddy.net.IApiMgr;
import com.spark.xposeddy.persist.IPersist;
import com.spark.xposeddy.persist.PersistKey;
import com.spark.xposeddy.repository.bean.CommentBean;
import com.spark.xposeddy.util.JSONObjectPack;
import com.spark.xposeddy.util.StringUtil;
import com.spark.xposeddy.util.TraceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.HttpException;

import java.util.HashMap;
import java.util.List;

class ApiMgr implements IApiMgr {
    private static final String NO_DATA = "no data";
    private static final String PARSE_ERROR = "parse error";
    private static final String REQUEST_ERROR = "request error";
    private static final String REQUEST_CANCEL = "request cancel";

    private IPersist mPersist;
    private String mDomain;

    ApiMgr(IPersist persist) {
        mPersist = persist;
    }

    @Override
    public boolean uploadComment(List<CommentBean> commentList, @NonNull Callback<String> callback) {
        mDomain = (String) mPersist.readData(PersistKey.DOMAIN, "");

        String str = JSON.toJSONString(commentList);
        JSONArray list = new JSONArray();
        try {
            list = new JSONArray(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject obj = new JSONObjectPack()
                .putValue("jsonArr", list)
                .getJSONObject();

        XUtil.postJson(ApiUrl.uploadComment(mDomain), "", obj, new ApiCallback(new Callback<Object>() {
            @Override
            public void onSuccess(Object data) {
                callback.onSuccess(data == null ? "" : data.toString());
            }

            @Override
            public void onFailure(String errMsg) {
                callback.onFailure(errMsg);
            }
        }));

        return true;
    }

    @Override
    public boolean getDeviceTask(String deviceNum, @NonNull Callback<String> callback) {
        mDomain = (String) mPersist.readData(PersistKey.DOMAIN, "");

        HashMap<String, String> map = new HashMap<>();
        map.put("deviceNum", deviceNum);
        XUtil.get(ApiUrl.getDeviceTask(mDomain), map, new ApiCallback(new Callback<Object>() {
            @Override
            public void onSuccess(Object data) {
                callback.onSuccess(data == null ? "" : data.toString());
            }

            @Override
            public void onFailure(String errMsg) {
                callback.onFailure(errMsg);
            }
        }));

        return true;
    }

    @Override
    public boolean updateDeviceTaskState(String deviceNum, @NonNull Callback<String> callback) {
        mDomain = (String) mPersist.readData(PersistKey.DOMAIN, "");

        HashMap<String, String> map = new HashMap<>();
        map.put("deviceNum", deviceNum);
        XUtil.get(ApiUrl.updateDeviceTaskState(mDomain), map, new ApiCallback(new Callback<Object>() {
            @Override
            public void onSuccess(Object data) {
                callback.onSuccess(data == null ? "" : data.toString());
            }

            @Override
            public void onFailure(String errMsg) {
                callback.onFailure(errMsg);
            }
        }));

        return true;
    }

    @Override
    public boolean getIP(@NonNull Callback<String> callback) {
        XUtil.get("http://pv.sohu.com/cityjson?ie=utf-8", null, new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                TraceUtil.d("result = " + result);
                try {
                    if (!TextUtils.isEmpty(result)) {
                        String json = "{" + StringUtil.getMidText(result, "{", "}") + "}";
                        JSONObject jsObj = new JSONObject(json);
                        String ip = jsObj.optString("cip");
                        TraceUtil.d("ip = " + ip);
                        callback.onSuccess(ip);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onFailure(PARSE_ERROR);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                callback.onFailure(REQUEST_ERROR);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                callback.onFailure(REQUEST_CANCEL);
            }

            @Override
            public void onFinished() {

            }
        });

        return true;
    }

    private class ApiCallback implements org.xutils.common.Callback.CommonCallback<String> {
        private Callback<Object> mCallback;

        ApiCallback(Callback<Object> callback) {
            this.mCallback = callback;
        }

        @Override
        public void onSuccess(String result) {
            boolean isResult = false;
            String errMsg = NO_DATA;

            try {
                TraceUtil.d("result = " + result);
                JSONObject obj = new JSONObject(result);
                boolean success = obj.optBoolean("success");
                if (success) {
                    isResult = true;
                    // JSONObject data = obj.optJSONObject("data");
                    // 这里不能用optJSONObject，因为获取到的可能是JSONObject、也可能是JSONArray
                    Object data = obj.opt("data");
                    mCallback.onSuccess(data);
                } else {
                    String msg = obj.optString("error");
                    if (msg != null) {
                        errMsg = msg;
                    }
                }
            } catch (JSONException e) {
                errMsg = PARSE_ERROR;
                e.printStackTrace();
            }

            if (!isResult) {
                mCallback.onFailure(errMsg);
            }
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            if (ex instanceof HttpException) {
                mCallback.onFailure(ex == null ? REQUEST_ERROR : ((HttpException) ex).getResult());
                return;
            }

            mCallback.onFailure(ex == null ? REQUEST_ERROR : ex.getMessage());
        }

        @Override
        public void onCancelled(CancelledException cex) {
            mCallback.onFailure(REQUEST_CANCEL);
        }

        @Override
        public void onFinished() {

        }
    }
}

