package com.spark.xposeddy.floatwindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.spark.xposeddy.R;
import com.spark.xposeddy.xposed.receiver.AppBroadcast;
import com.spark.xposeddy.xposed.receiver.XpBroadcast;
import com.spark.xposeddy.xposed.receiver.XpReceiver;
import com.spark.xposeddy.net.Callback;
import com.spark.xposeddy.net.impl.ApiMgrFactory;
import com.spark.xposeddy.persist.IPersist;
import com.spark.xposeddy.persist.PersistKey;
import com.spark.xposeddy.persist.impl.PersistFactory;
import com.spark.xposeddy.util.FileUtil;
import com.spark.xposeddy.util.LaunchUtil;
import com.spark.xposeddy.util.ShellFileUtil;
import com.spark.xposeddy.util.ShellUtil;
import com.spark.xposeddy.util.TraceUtil;
import com.spark.xposeddy.xposed.dy.HookDy;
import com.spark.xposeddy.xposed.HookMain;
import com.spark.xposeddy.xposed.phone.PhoneMgr;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import static com.spark.xposeddy.floatwindow.FloatWindowMgr.LOG_AWEME_COUNTS;
import static com.spark.xposeddy.floatwindow.FloatWindowMgr.LOG_AWEME_TARGET;
import static com.spark.xposeddy.floatwindow.FloatWindowMgr.LOG_COUNTS;
import static com.spark.xposeddy.floatwindow.FloatWindowMgr.LOG_DY_DEVICE_ID;
import static com.spark.xposeddy.floatwindow.FloatWindowMgr.LOG_LIVE_UPLOAD;
import static com.spark.xposeddy.floatwindow.FloatWindowMgr.LOG_SAMPLE;
import static com.spark.xposeddy.floatwindow.FloatWindowMgr.LOG_STATUS;
import static com.spark.xposeddy.floatwindow.FloatWindowMgr.LOG_TARGET;
import static com.spark.xposeddy.floatwindow.FloatWindowMgr.LOG_COMMENT_UPLOAD;

class LogFloatWindow extends AbsFloatWindow implements View.OnClickListener, View.OnTouchListener, CompoundButton.OnCheckedChangeListener {

    private LinearLayout mLayoutLog;
    private Switch mSwComment;
    private Switch mSwLive;
    private ImageView mImgClose;
    private Button mBtnStart;
    private Button mBtnStop;
    private Button mBtnNewPhone;
    private Button mBtnIP;
    private TextView mTxtDeviceNum;
    private TextView mTxtStatus;
    private TextView mTxtTarget;
    private TextView mTxtAwemeTarget;
    private TextView mTxtCounts;
    private TextView mTxtAwemeCounts;
    private TextView mTxtSample;
    private TextView mTxtCommentUpload;
    private TextView mTxtLiveUpload;
    private TextView mTxtDeviceIP;
    private TextView mTxtDyDeviceID;
    private TextView mTxtDeviceInfo;
    private ProgressBar mProgressBar;

    private int x;
    private int y;
    private IPersist mPersist;
    private Handler mHandler;
    private long updateCnt = 0, lastUpdateCnt = 0, errCnt = 0;
    private Runnable mKeepAliveRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(mKeepAliveRunnable, 10000);
            if (mSwComment.isChecked() && !TextUtils.isEmpty((String) mPersist.readData(PersistKey.CACHE_AWEME, ""))) {
                TraceUtil.e("keepAlive check: errCnt = " + errCnt + ", updateCnt = " + updateCnt + ", lastUpdateCnt = " + lastUpdateCnt);
                if (updateCnt != lastUpdateCnt) {
                    errCnt = 0;
                    lastUpdateCnt = updateCnt;
                } else {
                    if (++errCnt > 2) {
                        updateCnt = lastUpdateCnt = errCnt = 0;
                        XpBroadcast.sendRisk(mContext, XpReceiver.RISK_DY_CRASH);
                    }
                }
            }
        }
    };

    public LogFloatWindow(FloatWindowMgr mgr) {
        super(mgr);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate() {
        mLayoutParams.gravity = Gravity.CENTER;
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        setRootView(R.layout.float_log);
        mLayoutLog = (LinearLayout) getViewById(R.id.layout_log);
        mLayoutLog.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
        });
        mSwComment = (Switch) getViewById(R.id.sw_comment);
        mSwLive = (Switch) getViewById(R.id.sw_live);
        mImgClose = (ImageView) getViewById(R.id.img_close);
        mBtnStart = (Button) getViewById(R.id.btn_start);
        mBtnStop = (Button) getViewById(R.id.btn_stop);
        mBtnNewPhone = (Button) getViewById(R.id.btn_new_phone);
        mBtnIP = (Button) getViewById(R.id.btn_ip);

        mTxtDeviceNum = (TextView) getViewById(R.id.txt_device_num);
        mTxtStatus = (TextView) getViewById(R.id.txt_status);
        mTxtTarget = (TextView) getViewById(R.id.txt_target);
        mTxtAwemeTarget = (TextView) getViewById(R.id.txt_aweme_target);
        mTxtCounts = (TextView) getViewById(R.id.txt_count);
        mTxtAwemeCounts = (TextView) getViewById(R.id.txt_aweme_count);
        mTxtSample = (TextView) getViewById(R.id.txt_sample);
        mTxtCommentUpload = (TextView) getViewById(R.id.txt_comment_upload);
        mTxtLiveUpload = (TextView) getViewById(R.id.txt_live_upload);
        mTxtDeviceIP = (TextView) getViewById(R.id.txt_device_ip);
        mTxtDyDeviceID = (TextView) getViewById(R.id.txt_dy_device_id);
        mTxtDeviceInfo = (TextView) getViewById(R.id.txt_device_info);
        mProgressBar = (ProgressBar) getViewById(R.id.progress);

        mSwComment.setOnTouchListener(this);  // 用来拦截onCheck的事件
        mSwLive.setOnTouchListener(this);
        mSwComment.setOnCheckedChangeListener(this);
        mSwLive.setOnCheckedChangeListener(this);
        mImgClose.setOnClickListener(this);
        mBtnStart.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
        mBtnNewPhone.setOnClickListener(this);
        mBtnIP.setOnClickListener(this);

        mPersist = PersistFactory.getInstance(mContext);
        mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(mKeepAliveRunnable, 10000);
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();

        switch (resId) {
            case R.id.img_close:
                mFloatWindowMgr.showMenu();
                break;
            case R.id.btn_start:
                LaunchUtil.startApp(mContext, HookMain.PACKAGE_ID_NORM);
                break;
            case R.id.btn_stop:
                mSwComment.setChecked(false);
                mSwLive.setChecked(false);
                ShellUtil.exitApp(HookMain.PACKAGE_ID_NORM);
                break;
            case R.id.btn_new_phone:
                mSwComment.setChecked(false);
                mSwLive.setChecked(false);
                new NewPhoneThread(mContext, mPersist).start();
                break;
            case R.id.btn_ip:
                updateIp();
                break;
            default:
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!TextUtils.isEmpty((CharSequence) mPersist.readData(PersistKey.SAMPLE_ACCOUNTS, ""))
                || !TextUtils.isEmpty((CharSequence) mPersist.readData(PersistKey.SAMPLE_VIDEOS, ""))) {
            return false;
        }

        Toast.makeText(mContext, "请先填写参数保存，获取采集数据", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        TraceUtil.e("onCheckedChanged isChecked = " + isChecked);
        int resId = buttonView.getId();
        if (resId == R.id.sw_comment) {
            AppBroadcast.sendCommentMonitor(mContext, isChecked);
            if (isChecked) {
                mTxtTarget.setText("0/0");
                mTxtAwemeTarget.setText(", 0/0");
                mTxtCounts.setText("0");
                mTxtAwemeCounts.setText(", 0");
                mTxtSample.setText("0");
                mTxtCommentUpload.setText("0");
            }
        } else {
            AppBroadcast.sendLiveMonitor(mContext, isChecked);
            if (isChecked) {
                mTxtLiveUpload.setText("0");
            }
        }
    }

    @Override
    protected void showWindow() {
        super.showWindow();
        mTxtDeviceNum.setText((String) mPersist.readData(PersistKey.DEVICE_ID, ""));
        mTxtDeviceInfo.setText((String) mPersist.readData(PersistKey.PHONE_INFO, ""));
        mTxtDyDeviceID.setText((String) mPersist.readData(PersistKey.DY_DEVICE_ID, ""));
        updateIp();
    }

    @Override
    public void onDataUpdate(JSONObject data) {
        super.onDataUpdate(data);
        updateCnt++;
        if (data.has(LOG_STATUS)) {
            mTxtStatus.setText(data.optString(LOG_STATUS));
        } else if (data.has(LOG_TARGET)) {
            mTxtTarget.setText(data.optString(LOG_TARGET));
        } else if (data.has(LOG_AWEME_TARGET)) {
            mTxtAwemeTarget.setText(", " + data.optString(LOG_AWEME_TARGET));
        } else if (data.has(LOG_COUNTS)) {
            mTxtCounts.setText(data.optString(LOG_COUNTS));
        } else if (data.has(LOG_AWEME_COUNTS)) {
            mTxtAwemeCounts.setText(", " + data.optString(LOG_AWEME_COUNTS));
        } else if (data.has(LOG_SAMPLE)) {
            mTxtSample.setText(data.optString(LOG_SAMPLE));
        } else if (data.has(LOG_COMMENT_UPLOAD)) {
            mTxtCommentUpload.setText(data.optString(LOG_COMMENT_UPLOAD));
        } else if (data.has(LOG_LIVE_UPLOAD)) {
            mTxtLiveUpload.setText(data.optString(LOG_LIVE_UPLOAD));
        } else if (data.has(LOG_DY_DEVICE_ID)) {
            mPersist.writeData(PersistKey.DY_DEVICE_ID, data.optString(LOG_DY_DEVICE_ID));
            mTxtDyDeviceID.setText(data.optString(LOG_DY_DEVICE_ID));
        }
    }

    private void updateIp() {
        ApiMgrFactory.getInstance(mContext).getIP(new Callback<String>() {
            @Override
            public void onSuccess(String data) {
                mTxtDeviceIP.setText(data);
            }

            @Override
            public void onFailure(String error) {
                mTxtDeviceIP.setText("ip获取失败");
            }
        });
    }

    class NewPhoneThread extends Thread {
        private Context context;
        private String packName;
        private IPersist persist;

        public NewPhoneThread(Context context, IPersist persist) {
            super();
            this.context = context;
            this.persist = persist;
            this.packName = HookMain.PACKAGE_ID_NORM;
            mProgressBar.setVisibility(View.VISIBLE);
            mBtnNewPhone.setEnabled(false);
        }

        @Override
        public void run() {
            super.run();
            persist.writeData(PersistKey.PHONE_INFO, JSON.toJSONString(PhoneMgr.newPhoneInfo(mContext)));

            TraceUtil.e("exitApp start tick: " + System.currentTimeMillis() / 1000);
            ShellUtil.exitApp(packName);
            TraceUtil.e("exitApp end tick: " + System.currentTimeMillis() / 1000);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            TraceUtil.e("clearAppData start tick: " + System.currentTimeMillis() / 1000);
            clearAppData(packName);
            TraceUtil.e("clearAppData end tick: " + System.currentTimeMillis() / 1000);

            ShellUtil.exitApp(packName);

            mHandler.post(() -> {
                mProgressBar.setVisibility(View.GONE);
                mBtnNewPhone.setEnabled(true);
                mTxtDeviceInfo.setText((String) mPersist.readData(PersistKey.PHONE_INFO, ""));
            });
        }

        private boolean clearAppData(String packageName) {
            String baseInner = "/data/user/0/" + packageName + "/";
            String baseExternal = "/storage/emulated/0/Android/data/" + packageName + "/";
            ShellFileUtil.deleteFile(new File(baseExternal));

            // 删除com.snssdk.api
            String snssdkPath = baseExternal.replace(packageName, "com.snssdk.api");
            FileUtil.deleteFile(new File(snssdkPath));

            File baseInnerFile = new File(baseInner);
            if (ShellFileUtil.exists(baseInnerFile)) {
                List<File> fileList = ShellFileUtil.listFiles(new File(baseInner));
                for (File file : fileList) {
                    if (!file.getPath().contains("lib ->")) {
                        ShellFileUtil.deleteFile(file);
                    }
                }
            }

            return true;
        }

    }
}
