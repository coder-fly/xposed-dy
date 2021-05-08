package com.spark.xposeddy.persist.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.spark.xposeddy.persist.IPersist;

class SharedPersist implements IPersist {

    private Context context;

    SharedPersist(Context context) {
        this.context = context;
    }

    @Override
    public boolean writeData(String key, Object value) {
        if (value == null) {
            return false;
        }

        String type = value.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if ("String".equals(type)) {
            editor.putString(key, (String) value);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) value);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) value);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) value);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) value);
        } else if ("Double".equals(type)) {
            editor.putString(key, String.valueOf(value));
        }
        editor.commit();
        return true;
    }

    @Override
    public Object readData(String key, Object defaultValue) {
        String type = defaultValue.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultValue);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultValue);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultValue);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultValue);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultValue);
        } else if ("Double".equals(type)) {
            String str = sp.getString(key, "");
            if (TextUtils.isEmpty(str)) {
                return defaultValue;
            } else {
                Double val = (Double) defaultValue;
                try {
                    val = Double.valueOf(str);
                } catch (NumberFormatException var8) {
                    var8.printStackTrace();
                }
                return val;
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean clearData(String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key).commit();
        return false;
    }

    @Override
    public boolean clearDatas() {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        return false;
    }


}
