package com.spark.xposeddy.xposed.receiver;

import android.content.Context;
import android.content.Intent;

/**
 * app对xp发送的广播
 */
public class AppBroadcast {
    /**
     * 评论监听广播
     *
     * @param context
     * @param monitor
     */
    public static void sendCommentMonitor(Context context, boolean monitor) {
        Intent intent = new Intent(AppReceiver.RECEIVER_COMMENT_MONITOR_ACTION);
        intent.putExtra("monitor", monitor);
        context.sendBroadcast(intent);
    }

    /**
     * 直播监听广播
     *
     * @param context
     * @param monitor
     */
    public static void sendLiveMonitor(Context context, boolean monitor) {
        Intent intent = new Intent(AppReceiver.RECEIVER_LIVE_MONITOR_ACTION);
        intent.putExtra("monitor", monitor);
        context.sendBroadcast(intent);
    }

}
