package com.spark.xposeddy;

import android.app.Application;

import com.spark.xposeddy.util.TraceUtil;

import org.xutils.x;

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 打开APP日志
        TraceUtil.setDebug(true);
        TraceUtil.setWriter(true);
        // xUtils初始化
        x.Ext.init(this);
    }
}
