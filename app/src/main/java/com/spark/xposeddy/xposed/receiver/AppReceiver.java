package com.spark.xposeddy.xposed.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.spark.xposeddy.util.TraceUtil;
import com.spark.xposeddy.xposed.dy.CommentMonitor;
import com.spark.xposeddy.xposed.dy.LiveMonitor;
import com.spark.xposeddy.xposed.dy.api.DyApi;
import com.spark.xposeddy.xposed.dy.api.DyApiRes;

/**
 * AppBroadcast广播的接收器
 */
public class AppReceiver extends BroadcastReceiver {
    public static final String RECEIVER_COMMENT_MONITOR_ACTION = "dy.aweme.comment.monitor";
    public static final String RECEIVER_LIVE_MONITOR_ACTION = "dy.aweme.live.monitor";

    private CommentMonitor mCommentMonitor;
    private LiveMonitor mLiveMonitor;
    private DyApi mDyApi;
    private DyApiRes mDyApiRes;

    public static void registerReceiver(Context context, DyApi dyApi, DyApiRes dyApiRes, LiveMonitor liveMonitor) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(RECEIVER_COMMENT_MONITOR_ACTION);
        filter.addAction(RECEIVER_LIVE_MONITOR_ACTION);
        context.registerReceiver(new AppReceiver(dyApi, dyApiRes, liveMonitor), filter);
        TraceUtil.le("AppReceiver创建成功！");
    }

    public AppReceiver(DyApi dyApi, DyApiRes dyApiRes, LiveMonitor liveMonitor) {
        this.mLiveMonitor = liveMonitor;
        this.mDyApi = dyApi;
        this.mDyApiRes = dyApiRes;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(RECEIVER_COMMENT_MONITOR_ACTION)) {
            boolean monitor = intent.getBooleanExtra("monitor", false);
            TraceUtil.e("broadcast: comment monitor = " + monitor);
            if (monitor) {
                if (mCommentMonitor == null) {
                    TraceUtil.e("comment monitor 正在新建...");
                    mCommentMonitor = new CommentMonitor(context, mDyApi, mDyApiRes);
                    mCommentMonitor.startMonitor();
                } else {
                    TraceUtil.e("comment monitor 已经运行");
                }
            } else {
                if (mCommentMonitor != null) {
                    TraceUtil.e("comment monitor 停止中...");
                    mCommentMonitor.stopMonitor();
                    mCommentMonitor = null;
                } else {
                    TraceUtil.e("comment monitor 已经停止");
                }
            }
        } else if (action.equals(RECEIVER_LIVE_MONITOR_ACTION)) {
            boolean monitor = intent.getBooleanExtra("monitor", false);
            TraceUtil.e("broadcast: live monitor = " + monitor);
            mLiveMonitor.setMonitor(monitor);
        }
    }
}
