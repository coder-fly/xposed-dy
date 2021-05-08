package com.spark.xposeddy.xposed;

import android.text.TextUtils;

import com.spark.xposeddy.util.TraceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpHelper {
    public static JSONObject post(String url, Map map) {
        HttpURLConnection urlConnection;
        JSONObject result = new JSONObject();
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();
            String json = map2Json(map);
            if (!TextUtils.isEmpty(((CharSequence) json))) {
                PrintWriter writer = new PrintWriter(urlConnection.getOutputStream());
                writer.print(json);
                writer.flush();
                writer.close();
            }

            int code = urlConnection.getResponseCode();
            TraceUtil.d("get code from response. code is " + code);
            if (code == 200) {
                inputStream = urlConnection.getInputStream();
                json = in2String(inputStream);
                TraceUtil.d("Response Data is " + json);
                result = new JSONObject(json);
            } else {
                result.put("code", code);
                result.put("msg", "response data error.");
            }

            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TraceUtil.d("http post result:" + result.toString());
        return result;
    }

    public static JSONObject post(String url, Object body) {
        HttpURLConnection urlConnection;
        JSONObject result = new JSONObject();
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();
            String json = body.toString();
            if (!TextUtils.isEmpty(((CharSequence) json))) {
                PrintWriter writer = new PrintWriter(urlConnection.getOutputStream());
                writer.print(json);
                writer.flush();
                writer.close();
            }

            int code = urlConnection.getResponseCode();
            TraceUtil.d("get code from response. code is " + code);
            if (code == 200) {
                inputStream = urlConnection.getInputStream();
                json = in2String(inputStream);
                TraceUtil.d("Response Data is " + json);
                result = new JSONObject(json);
            } else {
                result.put("code", code);
                result.put("msg", "response data error.");
            }

            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TraceUtil.d("http post result:" + result.toString());
        return result;
    }

    public static JSONObject get(String url, Map map) {
        HttpURLConnection urlConnection;
        JSONObject result = new JSONObject();
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) new URL(url + map2UrlParam(map)).openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            String json = "";
            int code = urlConnection.getResponseCode();
            TraceUtil.d("get code from response. code is " + code);
            if (code == 200) {
                inputStream = urlConnection.getInputStream();
                json = in2String(inputStream);
                TraceUtil.d("Response Data is " + json);
                result = new JSONObject(json);
            } else {
                result.put("code", code);
                result.put("msg", "response data error.");
            }

            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TraceUtil.d("http post result:" + result.toString());
        return result;
    }

    private static String map2Json(Map<String, String> map) {
        String str = "{}";
        if (!(map == null || map.size() == 0)) {
            try {
                JSONObject jSONObject = new JSONObject();
                for (Map.Entry entry : map.entrySet()) {
                    if (!TextUtils.isEmpty((CharSequence) entry.getValue())) {
                        jSONObject.put((String) entry.getKey(), entry.getValue());
                    }
                }
                return jSONObject.toString();
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("format params error. err=");
                sb.append(e.getMessage());
                TraceUtil.e(sb.toString());
            }
        }
        return str;
    }

    private static String map2UrlParam(Map<String, String> map) {
        if (!(map == null || map.size() == 0)) {
            try {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry entry : map.entrySet()) {
                    if (!TextUtils.isEmpty((CharSequence) entry.getValue())) {
                        sb.append("&");
                        sb.append(entry.getKey());
                        sb.append("=");
                        sb.append(entry.getValue());
                    }
                }
                sb.delete(0, 1);
                sb.insert(0, "?");
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private static String in2String(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                sb.append(readLine);
            } else {
                bufferedReader.close();
                return sb.toString();
            }
        }
    }
}
