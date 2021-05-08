package com.spark.xposeddy.net;

public interface Callback<T> {
    void onSuccess(T data);

    void onFailure(String error);
}
