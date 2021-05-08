package com.spark.xposeddy.net.impl;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONObject;
import org.xutils.common.Callback.Cancelable;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;

public class XUtil {

    // 发送get请求
    static Cancelable get(String url, Map<String, String> map, CommonCallback callback) {
        RequestParams params = new RequestParams(url);
        if (null != map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                params.addQueryStringParameter(entry.getKey(), entry.getValue());
            }
        }
        return x.http().get(params, callback);
    }

    // 发送get请求
    static Cancelable get(String url, String token, Map<String, String> map, CommonCallback callback) {
        RequestParams params = new RequestParams(url);

        if (!TextUtils.isEmpty(token)) {
            params.addHeader("authorization", token);
        }

        if (null != map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                params.addQueryStringParameter(entry.getKey(), entry.getValue());
            }
        }
        return x.http().get(params, callback);
    }

    // 发送post请求
    static Cancelable post(String url, Map<String, String> map, CommonCallback callback) {
        RequestParams params = new RequestParams(url);
        if (null != map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                params.addParameter(entry.getKey(), entry.getValue());
            }
        }
        return x.http().post(params, callback);
    }

    // 发送post请求
    static Cancelable post(String url, Map<String, String> header, Map<String, String> map, CommonCallback callback) {
        RequestParams params = new RequestParams(url);
        if (null != header) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                params.addHeader(entry.getKey(), entry.getValue());
            }
        }

        if (null != map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                params.addParameter(entry.getKey(), entry.getValue());
            }
        }
        return x.http().post(params, callback);
    }

    // 发送post请求
    // 这里不能用JSONObject，因为获取到的可能是JSONObject、也可能是JSONArray
    static Cancelable postJson(String url, String token, @NonNull Object jsObjArr, CommonCallback callback) {
        RequestParams params = new RequestParams(url);

        if (!TextUtils.isEmpty(token)) {
            params.addHeader("authorization", token);
        }

        params.setAsJsonContent(true);
        params.setBodyContent(jsObjArr.toString());
        return x.http().post(params, callback);
    }

    // 上传文件
    static Cancelable uploadFile(String url, Map<String, Object> map, CommonCallback callback) {
        RequestParams params = new RequestParams(url);
        if (null != map) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                params.addParameter(entry.getKey(), entry.getValue());
            }
        }
        params.setConnectTimeout(100 * 1000);
        params.setReadTimeout(600 * 1000);
        params.setMultipart(true);
        return x.http().post(params, callback);
    }

    // 下载文件
    static Cancelable downloadFile(String url, String filepath, CommonCallback callback) {
        RequestParams params = new RequestParams(url);
        //设置断点续传
        params.setAutoResume(true);
        params.setSaveFilePath(filepath);
        return x.http().get(params, callback);
    }

    // 上传文件
    static String uploadFileSync(String url, Map<String, Object> map) {
        RequestParams params = new RequestParams(url);
        if (null != map) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                params.addParameter(entry.getKey(), entry.getValue());
            }
        }
        params.setConnectTimeout(100 * 1000);
        params.setReadTimeout(600 * 1000);
        params.setMultipart(true);

        String result = null;
        try {
            result = x.http().postSync(params, String.class);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return result;
    }
}

