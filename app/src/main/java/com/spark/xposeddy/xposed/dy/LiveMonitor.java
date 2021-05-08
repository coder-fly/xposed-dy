package com.spark.xposeddy.xposed.dy;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.spark.xposeddy.floatwindow.FloatWindowMgr;
import com.spark.xposeddy.net.impl.ApiUrl;
import com.spark.xposeddy.persist.PersistKey;
import com.spark.xposeddy.repository.bean.CommentBean;
import com.spark.xposeddy.util.JSONObjectPack;
import com.spark.xposeddy.xposed.receiver.XpBroadcast;
import com.spark.xposeddy.xposed.HttpHelper;
import com.spark.xposeddy.xposed.provider.PropertyProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 直播间消息监听器
 */
public class LiveMonitor {

    private Context mContext;
    private ExecutorService executor;
    private volatile boolean monitor;
    private String mDomain, mTypeId;
    private long uploadCnt = 0;

    public LiveMonitor(Context context) {
        this.mContext = context;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void releaseThreadPool() {
        this.executor.shutdown();
    }

    /**
     * 设置监听状态
     *
     * @param monitor
     */
    public void setMonitor(boolean monitor) {
        if (monitor) {
            uploadCnt = 0;
        }
        this.monitor = monitor;
        this.mDomain = PropertyProvider.readData(mContext, PersistKey.DOMAIN);
        this.mTypeId = PropertyProvider.readData(mContext, PersistKey.TYPE_ID);
    }

    /**
     * 上传直播间消息
     *
     * @param bean
     */
    public void uploadLiveMsg(CommentBean bean) {
        if (!this.monitor) {
            return;
        }

        bean.setTypeCode(mTypeId);
        List<CommentBean> beanList = new ArrayList<>();
        beanList.add(bean);

        String str = JSON.toJSONString(beanList);
        JSONArray list = new JSONArray();
        try {
            list = new JSONArray(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject obj = new JSONObjectPack()
                .putValue("jsonArr", list)
                .getJSONObject();

        XpBroadcast.sendFloatWindowLog(mContext, JSONObjectPack.getJsonObject(FloatWindowMgr.LOG_LIVE_UPLOAD, String.valueOf(++uploadCnt)));
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                HttpHelper.post(ApiUrl.uploadComment(mDomain), obj);
            }
        });
    }
}
