package com.spark.xposeddy.floatwindow;

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.spark.xposeddy.R;

class MenuFloatWindow extends AbsFloatWindow implements View.OnClickListener, View.OnTouchListener {
    private LinearLayout mLayoutMenu;
    private int x;
    private int y;

    public MenuFloatWindow(FloatWindowMgr mgr) {
        super(mgr);
    }

    @Override
    protected void onCreate() {
        mLayoutParams.gravity = Gravity.CENTER | Gravity.RIGHT;

        setRootView(R.layout.float_menu);
        mLayoutMenu = (LinearLayout) getViewById(R.id.layout_menu);
        mLayoutMenu.setOnClickListener(this);
        mLayoutMenu.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        mFloatWindowMgr.showLog();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getRawX();
                y = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int nowX = (int) event.getRawX();
                int nowY = (int) event.getRawY();
                int movedX = nowX - x;
                int movedY = nowY - y;
                x = nowX;
                y = nowY;

                // 更新悬浮窗控件布局
                mLayoutParams.x = mLayoutParams.x - movedX;
                mLayoutParams.y = mLayoutParams.y + movedY;
                updateWindow();
                break;
            default:
                break;
        }
        return false;
    }
}
