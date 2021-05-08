package com.spark.xposeddy.net;

import android.support.annotation.NonNull;

import com.spark.xposeddy.repository.bean.CommentBean;

import java.util.List;

public interface IApiMgr {

    boolean uploadComment(List<CommentBean> commentList, @NonNull Callback<String> callback);

    boolean getDeviceTask(String deviceNum, @NonNull Callback<String> callback);

    boolean updateDeviceTaskState(String deviceNum, @NonNull Callback<String> callback);

    boolean getIP(@NonNull Callback<String> callback);

}
