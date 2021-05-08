package com.spark.xposeddy.util;

public class ShellUtil {
    private ShellUtil() {
        /** cannot be instantiated **/
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isAppRunning(String packageName) {
        return Shell.execCommand("ps | grep " + packageName, false).result == 0;
    }

    public static boolean exitApp(String packageName) {
        return Shell.execCommand("am force-stop " + packageName, true).result == 0;
    }

    public static boolean openSELinux() {
        if (Shell.execCommand("setenforce 1", true).result == 0) {
            if (isSELinux()) {
                return true;
            }
        }
        return false;
    }

    public static boolean closeSELinux() {
        if (Shell.execCommand("setenforce 0", true).result == 0) {
            if (!isSELinux()) {
                return true;
            }
        }
        return false;
    }

    static boolean isSELinux() {
        Shell.CommandResult result = Shell.execCommand("getenforce", true);
        if (result.result == 0) {
            if ("Permissive".equals(result.successMsg)) {
                TraceUtil.e("SELinux is Permissive");
                return false;
            } else if ("Enforcing".equals(result.successMsg)) {
                TraceUtil.e("SELinux is Enforcing");
                return true;
            }
        }
        TraceUtil.e("SELinux is unknown");
        return true;
    }

}
