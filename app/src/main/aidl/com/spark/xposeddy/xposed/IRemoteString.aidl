// IRemoteString.aidl
package com.spark.xposeddy.xposed;

// 跨进程大文本传输
interface IRemoteString {
    String getStr();
}