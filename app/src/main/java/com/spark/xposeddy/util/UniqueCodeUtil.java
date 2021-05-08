package com.spark.xposeddy.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.UUID;

public class UniqueCodeUtil {
    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机设备的IMEI
     * 非手机设备无法获取
     * 获取DEVICE_ID需要READ_PHONE_STATE权限，在Android 6.0存在权限问题
     *
     * @param context
     * @return 868234000200804
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        Log.w("getIMEI", "" + deviceId);
        return deviceId == null ? "" : deviceId;
    }

    /**
     * 获取wifi的设备标识
     * 如果WiFi没有打开过，是无法获取其Mac地址的
     *
     * @param context
     * @return 00:27:15:05:66:0b
     */
    public static String getWlanMac(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String deviceId = wm.getConnectionInfo().getMacAddress();

        Log.w("getWlanMac", deviceId);
        return deviceId == null ? "" : deviceId.replace(":", "");
    }

    /**
     * 获取AndroidId
     * ANDROID_ID是设备第一次启动时产生和存储的64bit的一个数，当设备被wipe后该数重置
     * 在主流厂商生产的设备上，有一个很经常的bug，就是每个设备都会产生相同的ANDROID_ID
     * 标准的UUID格式为：xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx (8-4-4-4-12)
     *
     * @param context
     * @return 6a3c5de7b2d5069e
     */
    public static String getAndroidId(Context context) {
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        Log.w("getAndroidId", deviceId);
        return deviceId == null ? "" : deviceId;
    }

    /**
     * 通过读取设备的ROM版本号、厂商名、CPU型号和其他硬件信息来组合出一串15位的号码和设备硬件序列号作为种子生成UUID。
     *
     * @param context
     * @return ffffffff-b79e-6d5d-0000-00003707bda6
     */
    public static String getUniqueID(Context context) {
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10;

        String serial = Build.SERIAL;
        if (TextUtils.isEmpty(serial)) serial = "serial";

        String deviceId = new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        Log.w("getUniqueID", deviceId);
        return deviceId;
    }

    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    /**
     * 获取自动生成的唯一码
     * 同一程序重新安装会生成一个新值
     * 标准的UUID格式为：xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx (8-4-4-4-12)
     *
     * @param context
     * @return 681a9d89-b591-4bc2-8778-7e7e944eca69
     */
    public synchronized static String getAutoUniqueID(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.apply();
            }
        }

        Log.w("getAutoUniqueID", uniqueID);
        return uniqueID;
    }

}
