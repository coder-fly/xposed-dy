package com.spark.xposeddy.xposed.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.spark.xposeddy.floatwindow.FloatWindowMgr;
import com.spark.xposeddy.util.LaunchUtil;
import com.spark.xposeddy.util.ShellUtil;
import com.spark.xposeddy.util.TraceUtil;
import com.spark.xposeddy.xposed.HookMain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * XpBroadcast广播的接收器
 */
public class XpReceiver extends BroadcastReceiver {

    public static final String RECEIVER_NEW_TASK_ACTION = "dy.aweme.new.task";
    public static final String RECEIVER_LOGS_ACTION = "dy.aweme.logs";
    public static final String RECEIVER_RISK_ACTION = "dy.aweme.risk";

    public static final String RISK_AWEME_LIST_MILD = "risk.aweme.list.mild"; // aweme list轻微风控
    public static final String RISK_AWEME_LIST_SEVERE = "risk.aweme.list.severe"; // aweme list严重风控
    public static final String RISK_COMMENT_LIST_MILD = "risk.comment.list.mild"; // 评论列表轻微风控
    public static final String RISK_COMMENT_LIST_SEVERE = "risk.comment.list.severe"; // 评论列表严重风控
    public static final String RISK_DY_CRASH = "risk.dy.crash"; // dy卡死或crash

    private FloatWindowMgr mFloatWindowMgr;

    public static void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(RECEIVER_LOGS_ACTION);
        filter.addAction(RECEIVER_RISK_ACTION);
        context.registerReceiver(new XpReceiver(context), filter);
    }

    private XpReceiver(Context context) {
        super();
        TraceUtil.le("XpReceiver创建成功！");
        mFloatWindowMgr = FloatWindowMgr.getSingleInstance(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(RECEIVER_LOGS_ACTION)) {
            String str = intent.getStringExtra("logs");
            // TraceUtil.e("receive logs: " + str);
            try {
                JSONObject obj = new JSONObject(str);
                mFloatWindowMgr.updateLogData(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (action.equals(RECEIVER_RISK_ACTION)) {
            String str = intent.getStringExtra("risk");
            TraceUtil.e("receive risk: " + str);
            if (RISK_AWEME_LIST_MILD.equals(str) || RISK_COMMENT_LIST_MILD.equals(str) || RISK_DY_CRASH.equals(str)) {
                ShellUtil.exitApp(HookMain.PACKAGE_ID_NORM);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Bundle bundle = new Bundle();
                bundle.putBoolean("monitor", true);
                LaunchUtil.startApp(context, HookMain.PACKAGE_ID_NORM, bundle);
            }
        }
    }
}
