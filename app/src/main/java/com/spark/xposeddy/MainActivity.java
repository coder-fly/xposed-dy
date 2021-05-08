package com.spark.xposeddy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.spark.xposeddy.floatwindow.FloatWindowMgr;
import com.spark.xposeddy.net.Callback;
import com.spark.xposeddy.net.IApiMgr;
import com.spark.xposeddy.net.impl.ApiMgrFactory;
import com.spark.xposeddy.persist.IPersist;
import com.spark.xposeddy.persist.PersistKey;
import com.spark.xposeddy.persist.impl.PersistFactory;
import com.spark.xposeddy.component.ProgressUtil;
import com.spark.xposeddy.util.ShellRootUtil;
import com.spark.xposeddy.util.TraceUtil;
import com.spark.xposeddy.xposed.receiver.XpReceiver;
import com.spark.xposeddy.xposed.phone.PhoneMgr;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEditDomain;
    private EditText mEditDevice;
    private EditText mEditType;
    private EditText mEditCommentPage;
    private EditText mEditVideoCount;
    private EditText mEditSampleDiff;
    private EditText mEditIntervalTime;
    private EditText mEditAwemeSmallInterval;
    private EditText mEditAwemeLargeInterval;
    private TextView mTvSampleAccounts;
    private TextView mTvSampleVideos;
    private Button mBtnSave;

    private Activity mContext = this;
    private IPersist mPersist;
    private IApiMgr mApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPersist = PersistFactory.getInstance(this);
        if (TextUtils.isEmpty((String) mPersist.readData(PersistKey.PHONE_INFO, ""))) {
            mPersist.writeData(PersistKey.PHONE_INFO, JSON.toJSONString(PhoneMgr.getPhoneInfo(mContext)));
        }
        if (TextUtils.isEmpty((String) mPersist.readData(PersistKey.SAMPLE_ACCOUNTS, ""))) {
            mPersist.writeData(PersistKey.SAMPLE_ACCOUNTS, "94448819751;94766512160;1041964956135549;87755879771;1587337261757357;88090808523;59118017879;2862721033306827;75121210472;60701509911");
        }
        if (TextUtils.isEmpty((String) mPersist.readData(PersistKey.SAMPLE_VIDEOS, ""))) {
            mPersist.writeData(PersistKey.SAMPLE_VIDEOS, "6925391908025994499");
        }
        initView();

        if (FloatWindowMgr.requestFloatWindowPermission(mContext)) {
            FloatWindowMgr.getSingleInstance(mContext).showMenu();
        }
        mApi = ApiMgrFactory.getInstance(this);
        XpReceiver.registerReceiver(mContext);
        registerReceiver();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterReceiver();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (ShellRootUtil.isRoot()) {
            if (!ShellRootUtil.isRootAuth()) {
                Toast.makeText(mContext, "APP需要root权限，请授权root权限！", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(mContext, "APP需要root权限，请root手机！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (resId == R.id.btn_save) {
            saveParam();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FloatWindowMgr.onActivityResult(mContext, requestCode, resultCode, data);
    }

    private void initView() {
        setContentView(R.layout.activity_main);

        mEditDomain = findViewById(R.id.edit_domain);
        mEditDevice = findViewById(R.id.edit_device);
        mEditType = findViewById(R.id.edit_type);
        mEditCommentPage = findViewById(R.id.edit_comment_page);
        mEditVideoCount = findViewById(R.id.edit_video_count);
        mEditSampleDiff = findViewById(R.id.edit_sample_diff);
        mEditIntervalTime = findViewById(R.id.edit_interval_time);
        mEditAwemeSmallInterval = findViewById(R.id.edit_aweme_small_interval);
        mEditAwemeLargeInterval = findViewById(R.id.edit_aweme_large_interval);
        mTvSampleAccounts = findViewById(R.id.tv_sample_account);
        mTvSampleVideos = findViewById(R.id.tv_sample_video);

        mBtnSave = findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(this);

        mEditDomain.setText((String) mPersist.readData(PersistKey.DOMAIN, ""));
        mEditDevice.setText((String) mPersist.readData(PersistKey.DEVICE_ID, ""));
        mEditType.setText((String) mPersist.readData(PersistKey.TYPE_ID, ""));
        mEditCommentPage.setText((String) mPersist.readData(PersistKey.COMMENT_PAGE, "1"));
        mEditVideoCount.setText((String) mPersist.readData(PersistKey.VIDEO_COUNT, "5"));
        mEditSampleDiff.setText((String) mPersist.readData(PersistKey.SAMPLE_DIFF, "300"));
        mEditIntervalTime.setText((String) mPersist.readData(PersistKey.INTERVAL_TIME, "5"));
        mEditAwemeSmallInterval.setText((String) mPersist.readData(PersistKey.AWEME_SMALL_INTERVAL_CNT, "1000"));
        mEditAwemeLargeInterval.setText((String) mPersist.readData(PersistKey.AWEME_LARGE_INTERVAL_CNT, "10000"));
        mTvSampleAccounts.setText((String) mPersist.readData(PersistKey.SAMPLE_ACCOUNTS, ""));
        mTvSampleVideos.setText((String) mPersist.readData(PersistKey.SAMPLE_VIDEOS, ""));

        try {
            PackageInfo packInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            ApplicationInfo appInfo = mContext.getApplicationInfo();
            setTitle(mContext.getResources().getString(appInfo.labelRes) + packInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveParam() {
        String domain = mEditDomain.getText().toString().trim();
        String device = mEditDevice.getText().toString().trim();
        String type = mEditType.getText().toString().trim();
        String commentPage = mEditCommentPage.getText().toString().trim();
        String videoCount = mEditVideoCount.getText().toString().trim();
        String sampleDiff = mEditSampleDiff.getText().toString().trim();
        String intervalTime = mEditIntervalTime.getText().toString().trim();
        String awemeSmallInterval = mEditAwemeSmallInterval.getText().toString().trim();
        String awemeLargeInterval = mEditAwemeLargeInterval.getText().toString().trim();
//        if (TextUtils.isEmpty(domain)) {
//            Toast.makeText(mContext, "服务器地址不能为空", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        mPersist.writeData(PersistKey.DOMAIN, domain);
//
//        if (TextUtils.isEmpty(device)) {
//            Toast.makeText(mContext, "设备编码不能为空", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        mPersist.writeData(PersistKey.DEVICE_ID, device);
//
//        if (TextUtils.isEmpty(type)) {
//            Toast.makeText(mContext, "类型编码不能为空", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        mPersist.writeData(PersistKey.TYPE_ID, type);

        if (TextUtils.isEmpty(commentPage)) {
            Toast.makeText(mContext, "评论前N页不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mPersist.writeData(PersistKey.COMMENT_PAGE, commentPage);

        if (TextUtils.isEmpty(videoCount)) {
            Toast.makeText(mContext, "达人前N条不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mPersist.writeData(PersistKey.VIDEO_COUNT, videoCount);

        if (TextUtils.isEmpty(sampleDiff)) {
            Toast.makeText(mContext, "采集时间差不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mPersist.writeData(PersistKey.SAMPLE_DIFF, sampleDiff);

        if (TextUtils.isEmpty(intervalTime)) {
            Toast.makeText(mContext, "执行间隔不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mPersist.writeData(PersistKey.INTERVAL_TIME, intervalTime);

        if (TextUtils.isEmpty(awemeSmallInterval)) {
            Toast.makeText(mContext, "拉取小间隔不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mPersist.writeData(PersistKey.AWEME_SMALL_INTERVAL_CNT, awemeSmallInterval);

        if (TextUtils.isEmpty(awemeLargeInterval)) {
            Toast.makeText(mContext, "拉取大间隔不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mPersist.writeData(PersistKey.AWEME_LARGE_INTERVAL_CNT, awemeLargeInterval);

        getDeviceTask(device);
    }

    private void getDeviceTask(String device) {
        ProgressUtil.showProgressHUD(mContext, "", false, null);
        mApi.getDeviceTask(device, new Callback<String>() {
            @Override
            public void onSuccess(String data) {
                ProgressUtil.dismissProgressHUD();
                TraceUtil.e("getDeviceTask success, data = " + data);
                try {
                    JSONObject obj = new JSONObject(data);
                    if (obj.has("info")) {
                        JSONObject info = obj.optJSONObject("info");
                        String dyIds = info.optString("dy_ids");
                        String videoUrls = info.optString("video_urls");
                        mPersist.writeData(PersistKey.SAMPLE_ACCOUNTS, dyIds);
                        mPersist.writeData(PersistKey.SAMPLE_VIDEOS, videoUrls);
                        mPersist.writeData(PersistKey.CACHE_PROFILE, "");
                        mPersist.writeData(PersistKey.CACHE_AWEME, "");
                        mTvSampleAccounts.setText(dyIds);
                        mTvSampleVideos.setText(videoUrls);

                        updateDeviceTaskState(device);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error) {
                ProgressUtil.dismissProgressHUD();
                TraceUtil.e("getDeviceTask fail, error = " + error);
                Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDeviceTaskState(String device) {
        mApi.updateDeviceTaskState(device, new Callback<String>() {
            @Override
            public void onSuccess(String data) {
                TraceUtil.e("updateDeviceTaskState success, data = " + data);
            }

            @Override
            public void onFailure(String error) {
                TraceUtil.e("updateDeviceTaskState fail, error = " + error);
            }
        });
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (XpReceiver.RECEIVER_NEW_TASK_ACTION.equals(action)) {
                String sampleAccounts = intent.getStringExtra("sampleAccounts");
                String sampleVideos = intent.getStringExtra("sampleVideos");
                mPersist.writeData(PersistKey.SAMPLE_ACCOUNTS, sampleAccounts);
                mPersist.writeData(PersistKey.SAMPLE_VIDEOS, sampleVideos);
                mTvSampleAccounts.setText(sampleAccounts);
                mTvSampleVideos.setText(sampleVideos);
            }
        }
    };

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(XpReceiver.RECEIVER_NEW_TASK_ACTION);
        mContext.registerReceiver(mReceiver, filter);
    }

    private void unRegisterReceiver() {
        mContext.unregisterReceiver(mReceiver);
    }
}
