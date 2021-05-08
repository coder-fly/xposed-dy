package com.spark.xposeddy.util;

import android.os.Build;

import java.io.File;

public class ShellRootUtil {
    private ShellRootUtil() {
        /** cannot be instantiated **/
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 检查系统是否root
     *
     * @return
     */
    public static boolean isRoot() {
        if (checkSystemDebuggable()) {
            TraceUtil.e("checkSystemDebuggable true");
            return true;
        }
        if (checkSuFile()) {
            TraceUtil.e("checkSuFile true");
            return true;
        }
        if (checkWhichSu()) {
            TraceUtil.e("checkWhichSu true");
            return true;
        }

        return false;
    }

    /**
     * 查看app是否有root权限，app申请root权限
     * 这个方法可以用来查看root权限，但是会弹出提示框申请root权限，并且是阻塞的
     *
     * @return
     */
    public static boolean isRootAuth() {
        return Shell.execCommand("echo root", true).result == 0;
    }

    /**
     * 查看系统是否测试版
     * cat /system/build.prop | grep ro.build.tags
     *
     * @return
     */
    private static boolean checkSystemDebuggable() {
        String buildTags = Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }
        return false;
    }

    /**
     * 检查系统是否存在su相关文件
     *
     * @return
     */
    private static boolean checkSuFile() {
        String[] arr = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (int i = 0; i < arr.length; i++) {
            if (new File(arr[i]).exists()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 使用which命令查看系统是否存在su
     *
     * @return
     */
    private static boolean checkWhichSu() {
        return Shell.execCommand("which su", false).result == 0;
    }

}
