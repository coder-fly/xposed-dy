package com.spark.xposeddy.util;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONObjectPack {
    private JSONObject jsonObject;

    public JSONObjectPack() {
        jsonObject = new JSONObject();
    }

    public JSONObjectPack putValue(String key, Object obj) {
        try {
            jsonObject.put(key, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JSONObject getJSONObject() {
        return jsonObject;
    }

    public static JSONObject getJsonObject(String key, Object obj) {
        JSONObject object = new JSONObject();
        try {
            object.put(key, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }
}
