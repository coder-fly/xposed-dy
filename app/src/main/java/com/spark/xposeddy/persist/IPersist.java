package com.spark.xposeddy.persist;

public interface IPersist {

    String FILE_NAME = "persist_data";

    boolean writeData(String key, Object value);

    Object readData(String key, Object defaultValue);

    boolean clearData(String key);

    boolean clearDatas();
}
