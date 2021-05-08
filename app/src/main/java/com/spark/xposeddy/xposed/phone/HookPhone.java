package com.spark.xposeddy.xposed.phone;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;

import com.alibaba.fastjson.JSON;
import com.spark.xposeddy.persist.PersistKey;
import com.spark.xposeddy.util.TraceUtil;
import com.spark.xposeddy.xposed.provider.PropertyProvider;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * 一键新机的入口
 */
public class HookPhone {

    public void hook(ClassLoader appClassLoader, Context context) {
        try {
            String str = PropertyProvider.readData(context, PersistKey.PHONE_INFO);
            PhoneInfo info = JSON.parseObject(str, PhoneInfo.class);
            if (info != null && !TextUtils.isEmpty(info.getGetDeviceId()) && !TextUtils.isEmpty(info.getAndroidId())) {
                hookPhone(appClassLoader, context, info);
            } else {
                TraceUtil.xe("hookPhone err: info is null");
            }

        } catch (UnknownHostException e) {
            TraceUtil.xe("hookPhone err: " + e.getMessage());
        }
    }

    private void hookPhone(final ClassLoader classLoader, final Context context, PhoneInfo phoneInfo) throws UnknownHostException {
        TraceUtil.e("hookPhone");
        TraceUtil.e("phoneInfo = " + JSON.toJSONString(phoneInfo));
        setStaticField(Build.class, "TAGS", phoneInfo.getTags());
        setStaticField(Build.class, "HOST", phoneInfo.getHost());
        setStaticField(Build.class, "USER", phoneInfo.getUser());
        setStaticField(Build.class, "TIME", Long.parseLong(phoneInfo.getTime()));
        setStaticField(Build.class, "DISPLAY", phoneInfo.getDisplay());
        setStaticField(Build.class, "BOOTLOADER", phoneInfo.getBootloader());
        setStaticField(Build.class, "SERIAL", phoneInfo.getSerial());
        setStaticField(Build.class, "BOARD", phoneInfo.getBoard());
        setStaticField(Build.class, "BRAND", phoneInfo.getBrand());
        setStaticField(Build.class, "DEVICE", phoneInfo.getDevice());
        setStaticField(Build.class, "FINGERPRINT", phoneInfo.getFingerprint());
        setStaticField(Build.class, "HARDWARE", phoneInfo.getHardware());
        setStaticField(Build.class, "MANUFACTURER", phoneInfo.getManufacturer());
        setStaticField(Build.class, "TYPE", phoneInfo.getType());
        setStaticField(Build.class, "MODEL", phoneInfo.getModel());
        setStaticField(Build.class, "PRODUCT", phoneInfo.getProduct());
        setStaticField(Build.class, "ID", phoneInfo.getBuildID());
        setStaticField(Build.VERSION.class, "RELEASE", phoneInfo.getRelease());
        setStaticField(Build.VERSION.class, "INCREMENTAL", phoneInfo.getIncremental());
        setStaticField(Build.VERSION.class, "CODENAME", phoneInfo.getCodename());

        // 暂时屏蔽，避免SDK变化导致crash
//        String SDK_INT = phoneInfo.getSdkInt();
//        if (!isNullStr(SDK_INT)) {
//            setStaticField(Build.VERSION.class, "SDK", phoneInfo.getSdk());
//            setStaticField(Build.VERSION.class, "SDK_INT", Integer.parseInt(SDK_INT));
//        }
        setMethodRes(Build.class, "getRadioVersion", phoneInfo.getGetRadioVersion());

        Class SystemProperties = XposedHelpers.findClass("android.os.SystemProperties", classLoader);
        XposedHelpers.findAndHookMethod(SystemProperties, "native_get", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String name = (String) param.args[0];
                String result = (String) param.getResult();
                TraceUtil.d("native_get: " + name + " = " + result);
                if ("net.hostname".equals(name)) {
                    result = "android-" + phoneInfo.getAndroidId();
                    param.setResult(result);
                    TraceUtil.d("native_new: " + name + " = " + result);
                }
            }
        });

        setMethodRes(TelephonyManager.class, "getDeviceId", phoneInfo.getGetDeviceId());
        setMethodRes(TelephonyManager.class, "getNetworkOperator", phoneInfo.getGetNetworkOperator());
        setMethodRes(TelephonyManager.class, "getNetworkOperatorName", phoneInfo.getGetNetworkOperatorName());
        setMethodRes(TelephonyManager.class, "getNetworkType", Integer.parseInt(phoneInfo.getGetNetworkType()));
        setMethodRes(TelephonyManager.class, "getSimOperator", phoneInfo.getGetSimOperator());
        setMethodRes(TelephonyManager.class, "getSimOperatorName", phoneInfo.getGetSimOperatorName());
        setMethodRes(TelephonyManager.class, "getSubscriberId", phoneInfo.getGetSubscriberId());
        setMethodRes(TelephonyManager.class, "getDataActivity", Integer.parseInt(phoneInfo.getGetDataActivity()));
        setMethodRes(TelephonyManager.class, "getDeviceSoftwareVersion", phoneInfo.getVersion());
        setMethodRes(TelephonyManager.class, "getSimCountryIso", "cn");
        setMethodRes(TelephonyManager.class, "getSimState", 5);
        setMethodRes(TelephonyManager.class, "getLine1Number", isNullStr(phoneInfo.getGetLine1Number()) ? null : phoneInfo.getGetLine1Number());
        setMethodRes(TelephonyManager.class, "getSimSerialNumber", phoneInfo.getGetSimSerialNumber());

        setMethodRes(NetworkInfo.class, "getExtraInfo", isNullStr(phoneInfo.getGetExtraInfo()) ? null : phoneInfo.getGetExtraInfo());
        setMethodRes(NetworkInfo.class, "getReason", isNullStr(phoneInfo.getGetReason()) ? null : phoneInfo.getGetReason());
        setMethodRes(NetworkInfo.class, "getSubtype", Integer.parseInt(phoneInfo.getGetSubtype()));
        setMethodRes(NetworkInfo.class, "getSubtypeName", phoneInfo.getGetSubtypeName());
        setMethodRes(NetworkInfo.class, "getType", Integer.parseInt(phoneInfo.getGetType()));
        setMethodRes(NetworkInfo.class, "getTypeName", phoneInfo.getGetTypeName());

        setMethodRes(WifiInfo.class, "getMacAddress", phoneInfo.getGetMacAddress());
        setMethodRes(WifiInfo.class, "getBSSID", phoneInfo.getGetBSSID());
        setMethodRes(WifiInfo.class, "getIpAddress", Integer.parseInt(phoneInfo.getGetIpAddress()));
        setMethodRes(WifiInfo.class, "getNetworkId", Integer.parseInt(phoneInfo.getGetNetworkId()));
        setMethodRes(WifiInfo.class, "getSSID", phoneInfo.getGetSSID());
        setMethodRes(WifiInfo.class, "getRssi", Integer.parseInt(phoneInfo.getGetRssi()));
        setMethodRes(InetAddress.class, "getLocalHost", InetAddress.getByName(phoneInfo.getGetHostAddress()));

        XposedHelpers.findAndHookMethod(WifiManager.class, "getScanResults", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                ArrayList<ScanResult> sr = (ArrayList<ScanResult>) param.getResult();
                sr.get(0).BSSID = phoneInfo.getScanResultsBSSID();
                sr.get(0).capabilities = phoneInfo.getScanResultsCapabilities();
                sr.get(0).frequency = Integer.parseInt(phoneInfo.getScanResultsFrequency());
                sr.get(0).level = Integer.parseInt(phoneInfo.getScanResultsLevel());
                sr.get(0).SSID = phoneInfo.getScanResultsSSID();
                param.setResult(sr);
            }
        });

        if (!isNullStr(phoneInfo.getDensity()) && !"0".equals(phoneInfo.getDensity())) {
            setMethodRes(Display.class, "getWidth", Integer.parseInt(phoneInfo.getGetWidth()));
            setMethodRes(Display.class, "getHeight", Integer.parseInt(phoneInfo.getGetHeight()));
            setMethodRes(Display.class, "getRotation", Integer.parseInt(phoneInfo.getGetRotation()));
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.widthPixels = Integer.parseInt(phoneInfo.getWidthPixels());
            metrics.heightPixels = Integer.parseInt(phoneInfo.getHeightPixels());
            metrics.density = Float.parseFloat(phoneInfo.getDensity());
            metrics.densityDpi = Integer.parseInt(phoneInfo.getDensityDpi());
            metrics.scaledDensity = Float.parseFloat(phoneInfo.getScaledDensity());
            setMethodRes(Resources.class, "getDisplayMetrics", metrics);
        }

        XposedHelpers.findAndHookMethod(Settings.Secure.class, "getString", ContentResolver.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if ("android_id".equals(param.args[1])) {
                    param.setResult(phoneInfo.getAndroidId());
                }
            }
        });

        if (!isNullStr(phoneInfo.getGetCellLocation())) {
            String[] ss = phoneInfo.getGetCellLocation().replace("[", "").replace("]", "").split(",");
            if (ss.length == 5) {
                CdmaCellLocation cdma = new CdmaCellLocation();
                cdma.setCellLocationData(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]), Integer.parseInt(ss[3]), Integer.parseInt(ss[4]));
                setMethodRes(TelephonyManager.class, "getCellLocation", cdma);
            } else if (ss.length == 3) {
                Bundle b = new Bundle();
                b.putInt("lac", Integer.parseInt(ss[0]));
                b.putInt("cid", Integer.parseInt(ss[1]));
                b.putInt("psc", Integer.parseInt(ss[2]));
                setMethodRes(TelephonyManager.class, "getCellLocation", new GsmCellLocation(b));
            }
        }

        if (!isNullStr(phoneInfo.getGetBestProvider())) {
            setMethodRes(LocationManager.class, "getBestProvider", phoneInfo.getGetBestProvider());
            Location loc = new Location(phoneInfo.getGetProvider());
            loc.setLatitude(Double.parseDouble(phoneInfo.getGetLatitude()));
            loc.setLongitude(Double.parseDouble(phoneInfo.getGetLongitude()));
            loc.setAccuracy(Float.parseFloat(phoneInfo.getGetAccuracy()));
            loc.setProvider(phoneInfo.getGetProvider());
            setMethodRes(LocationManager.class, "getLastKnownLocation", loc);
            setMethodRes(Location.class, "getLatitude", Double.parseDouble(phoneInfo.getGetLatitude()));
            setMethodRes(Location.class, "getLongitude", Double.parseDouble(phoneInfo.getGetLongitude()));
        }
    }

    private boolean isNullStr(String value) {
        return (value == null || "NULL".equals(value) || "null".equals(value));
    }

    private void setStaticField(Class cls, String filed, Object value) {
        if (value == null) {
            return;
        }

        String type = value.getClass().getSimpleName();
        if ("Integer".equals(type)) {
            XposedHelpers.setStaticIntField(cls, filed, (Integer) value);
        } else if ("Boolean".equals(type)) {
            XposedHelpers.setStaticBooleanField(cls, filed, (Boolean) value);
        } else if ("Float".equals(type)) {
            XposedHelpers.setStaticFloatField(cls, filed, (Float) value);
        } else if ("Long".equals(type)) {
            XposedHelpers.setStaticLongField(cls, filed, (Long) value);
        } else if ("Double".equals(type)) {
            XposedHelpers.setStaticDoubleField(cls, filed, (Double) value);
        } else {
            XposedHelpers.setStaticObjectField(cls, filed, value);
        }
    }

    private void setMethodRes(Class cls, String method, Object value) {
        if (value == null) {
            return;
        }

        TraceUtil.d("hookPhone: " + method + " = " + value);
        XposedBridge.hookAllMethods(cls, method, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                param.setResult(value);
            }
        });
    }
}
