package com.spark.xposeddy.floatwindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import org.json.JSONObject;

import java.util.Locale;

abstract class AbsFloatWindow {

    protected Context mContext;
    protected FloatWindowMgr mFloatWindowMgr;
    protected WindowManager mWindowManager;
    protected WindowManager.LayoutParams mLayoutParams;
    protected Point mScreenSize;
    private View mRootView;
    private LayoutInflater mInflater;
    private boolean mIsShow = false;

    public AbsFloatWindow(FloatWindowMgr mgr) {
        this.mFloatWindowMgr = mgr;
    }

    public void onCreate(Context ctx) {
        mContext = ctx.getApplicationContext();
        mInflater = LayoutInflater.from(ctx);
        mWindowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);

        mScreenSize = new Point(0, 0);
        mWindowManager.getDefaultDisplay().getSize(mScreenSize);

        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.gravity = Gravity.START | Gravity.BOTTOM;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 系统提示window
        mLayoutParams.format = PixelFormat.TRANSLUCENT;// 支持透明
        //mParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 焦点
        mLayoutParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mLayoutParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mLayoutParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;//窗口的宽和高
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        onCreate();
    }

    public void onDestroy() {
        hideWindow();
    }

    public void onLanguageChanged(Locale newConfig) {
        // 移除旧界面
        hideWindow();
        // 重新建立界面
        onCreate();
    }

    protected View setRootView(int resId) {
        mRootView = mInflater.inflate(resId, null);
        // showWindow();
        return mRootView;
    }

    @Nullable
    protected View getViewById(int resId) {
        if (mRootView != null) {
            return mRootView.findViewById(resId);
        }
        return null;
    }

    @Nullable
    protected View getRootView() {
        return mRootView;
    }

    protected boolean isShow() {
        return mIsShow;
    }

    protected void showWindow() {
        if (!mIsShow) {
            mIsShow = true;
            mWindowManager.addView(mRootView, mLayoutParams);
        }
    }

    protected void hideWindow() {
        if (mIsShow) {
            mIsShow = false;
            try {
                mWindowManager.removeView(mRootView);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    protected void updateWindow() {
        if (mIsShow) {
            mWindowManager.updateViewLayout(mRootView, mLayoutParams);
        }
    }

    protected abstract void onCreate();

    public void onDataUpdate(JSONObject data) {

    }
}
