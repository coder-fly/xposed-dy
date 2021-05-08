package com.spark.xposeddy.util;

import android.text.TextUtils;

public class StringUtil {

    public static String getMidText(String str, String str2, String str3) {
        try {
            int start = str.indexOf(str2);
            if (start == -1) {
                return "";
            }

            int indexOf = start + str2.length();
            if (TextUtils.isEmpty(str3)) {
                return str.substring(indexOf);
            }
            return str.substring(indexOf, str.indexOf(str3, indexOf));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

}
