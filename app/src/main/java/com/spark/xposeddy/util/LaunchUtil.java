package com.spark.xposeddy.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class LaunchUtil {

    public static void startApp(Context context, String packName) {
        if (AndroidUtil.isInstall(context, packName)) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                // Toast.makeText(context, "没有安装" + packName, Toast.LENGTH_SHORT).show();
                TraceUtil.e("没有安装" + packName);
            }
        } else {
            // Toast.makeText(context, "没有安装" + packName, Toast.LENGTH_SHORT).show();
            TraceUtil.e("没有安装" + packName);
        }
    }

    public static void startApp(Context context, String packName, Bundle bundle) {
        if (AndroidUtil.isInstall(context, packName)) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                context.startActivity(intent);
            } else {
                // Toast.makeText(context, "没有安装" + packName, Toast.LENGTH_SHORT).show();
                TraceUtil.e("没有安装" + packName);
            }
        } else {
            // Toast.makeText(context, "没有安装" + packName, Toast.LENGTH_SHORT).show();
            TraceUtil.e("没有安装" + packName);
        }
    }

}
