package com.spark.xposeddy.xposed.receiver;

import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

/**
 * xp对外发送的广播
 */
public class XpBroadcast {
    /**
     * 达人信息、视频信息更新广播
     *
     * @param context
     * @param sampleAccounts
     * @param sampleVideos
     */
    public static void sendDeviceTask(Context context, String sampleAccounts, String sampleVideos) {
        Intent intent = new Intent(XpReceiver.RECEIVER_NEW_TASK_ACTION);
        intent.putExtra("sampleAccounts", sampleAccounts);
        intent.putExtra("sampleVideos", sampleVideos);
        context.sendBroadcast(intent);
    }

    /**
     * 日志悬浮窗更新广播
     *
     * @param context
     * @param log
     */
    public static void sendFloatWindowLog(Context context, JSONObject log) {
        String str = log.toString();
        Intent intent = new Intent(XpReceiver.RECEIVER_LOGS_ACTION);
        intent.putExtra("logs", str);
        context.sendBroadcast(intent);
    }

    /**
     * dy风控广播
     *
     * @param context
     * @param risk
     */
    public static void sendRisk(Context context, String risk) {
        Intent intent = new Intent(XpReceiver.RECEIVER_RISK_ACTION);
        intent.putExtra("risk", risk);
        context.sendBroadcast(intent);
    }
}
