package com.spark.xposeddy.floatwindow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.spark.xposeddy.util.TraceUtil;

import org.json.JSONObject;

public class FloatWindowMgr {
    public static final String LOG_STATUS = "log_status";
    public static final String LOG_TARGET = "log_target";
    public static final String LOG_AWEME_TARGET = "log_aweme_target";
    public static final String LOG_COUNTS = "log_counts";
    public static final String LOG_AWEME_COUNTS = "log_aweme_counts";
    public static final String LOG_SAMPLE = "log_sample";
    public static final String LOG_COMMENT_UPLOAD = "log_comment_upload";
    public static final String LOG_LIVE_UPLOAD = "log_live_upload";
    public static final String LOG_DY_DEVICE_ID = "log_dy_device_id";
    private static final int REQUEST_CODE = 1001;
    private static FloatWindowMgr mInstance;

    private Context mContext;
    private MenuFloatWindow mMenuFloatWindow;
    private LogFloatWindow mLogFloatWindow;

    private FloatWindowMgr(Context context) {
        mContext = context.getApplicationContext();
        mMenuFloatWindow = new MenuFloatWindow(this);
        mMenuFloatWindow.onCreate(context);
        mLogFloatWindow = new LogFloatWindow(this);
        mLogFloatWindow.onCreate(context);
    }

    public static FloatWindowMgr getSingleInstance(Context context) {
        if (mInstance == null) {
            synchronized (FloatWindowMgr.class) {
                if (mInstance == null) {
                    mInstance = new FloatWindowMgr(context);
                }
            }
        }
        return mInstance;
    }

    public static boolean requestFloatWindowPermission(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(act)) {
                TraceUtil.e("申请悬浮窗权限！");
                askForDrawOverlay(act);
                return false;
            } else {
                TraceUtil.e("已获取悬浮窗权限！");
            }
        } else {
            TraceUtil.e("系统版本小于23，不需要申请悬浮窗权限！");
        }
        return true;
    }

    private static void askForDrawOverlay(Activity act) {
        AlertDialog alertDialog = new AlertDialog.Builder(act)
                .setTitle("允许显示悬浮框")
                .setMessage("为了APP正常工作，请允许这项权限")
                .setPositiveButton("去设置", (dialog, which) -> {
                    act.startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + act.getPackageName())), REQUEST_CODE);
                    dialog.dismiss();
                })
                .setNegativeButton("稍后再说", (dialog, which) -> dialog.dismiss())
                .create();

        alertDialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        alertDialog.show();
    }

    public static void onActivityResult(Activity act, int requestCode, int resultCode, @Nullable Intent data) {
        TraceUtil.e("requestCode = " + requestCode + ", resultCode = " + resultCode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == REQUEST_CODE) {
                if (!Settings.canDrawOverlays(act)) {
                    TraceUtil.e("授权失败！");
                } else {
                    TraceUtil.e("授权成功！");
                    FloatWindowMgr.getSingleInstance(act).showMenu();
                }
            }
        }
    }

    public void showMenu() {
        mLogFloatWindow.hideWindow();
        mMenuFloatWindow.showWindow();
    }

    public void showLog() {
        mMenuFloatWindow.hideWindow();
        mLogFloatWindow.showWindow();
    }

    public void closeFloatWindow() {
        mMenuFloatWindow.hideWindow();
        mLogFloatWindow.hideWindow();
    }

    public void updateLogData(JSONObject data) {
        // if (mLogFloatWindow.isShow()) {
            mLogFloatWindow.onDataUpdate(data);
        // }
    }
}
