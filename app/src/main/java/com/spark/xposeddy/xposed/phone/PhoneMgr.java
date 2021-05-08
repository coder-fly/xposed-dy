package com.spark.xposeddy.xposed.phone;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.alibaba.fastjson.JSON;
import com.spark.xposeddy.persist.PersistKey;
import com.spark.xposeddy.persist.impl.PersistFactory;
import com.spark.xposeddy.util.TraceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * [{"androidId":"63231c278558b0cd","board":"bbk17_td3001_ics2","bootloader":"unknown","brand":"vivo","buildID":"IMM76D","codename":"REL","density":"1.5","densityDpi":"240","device":"bbk17_td3001_ics2","display":"ALPS.ICS2.TDD.MP.V1.2","fingerprint":"vivo/bbk17_td3001_ics2/bbk17_td3001_ics2:4.0.4/IMM76D/1414057382:user/test-keys","getAccuracy":"53.21255","getBSSID":"d0:c7:c0:5a:95:3c","getBestProvider":"network","getCanonicalHostName":"localhost","getCellLocation":"[47101,66265930,-1]","getDataActivity":"0","getDeviceId":"861276029118852","getExtraInfo":"null","getHeight":"800","getHostAddress":"127.0.0.1","getHostName":"localhost","getIpAddress":"1879156928","getLine1Number":"","getLocalHost":"localhost/127.0.0.1","getLongitude":"114.208623","getMacAddress":"2c:28:2d:43:ac:27","getNetworkId":"1","getNetworkOperator":"46000","getNetworkOperatorName":"中国移动","getNetworkType":"8","getProvider":"network","getRadioVersion":"MAUI.11AMD.W12.36.SP.V8.F3, 2013/06/26 18:02","getReason":"null","getRotation":"0","getRssi":"-62","getSSID":"0000000000","getSimOperator":"46002","getSimOperatorName":"CMCC","getSimSerialNumber":"89860013161400060539","getSubscriberId":"460028392640539","getSubtype":"0","getSubtypeName":"","getType":"1","getTypeName":"WIFI","getWidth":"480","hardware":"mt6577","heightPixels":"800","host":"compiler017","incremental":"eng.compiler.1414057382","manufacturer":"BBK","model":"vivo S11t","product":"bbk17_td3001_ics2","release":"4.0.4","scaledDensity":"1.5","scanResultsBSSID":"d0:c7:c0:5a:95:3c","scanResultsCapabilities":"[WPA-PSK-CCMP][WPA2-PSK-CCMP][ESS]","scanResultsFrequency":"2437","scanResultsLevel":"-61","scanResultsSSID":"0000000000","sdk":"15","sdkInt":"15","serial":"BUBIPFCYKR5TROZ9","getLatitude":"35.600945","tags":"test-keys","time":"1414057602000","type":"user","user":"compiler","version":"78","widthPixels":"480"},{"androidId":"6f04f1956ae0218d","board":"2013023","bootloader":"unknown","brand":"Xiaomi","buildID":"HM2013023","codename":"REL","density":"2.0","densityDpi":"320","device":"HM2013023","display":"JHBCNBL30.0","fingerprint":"Xiaomi/2013023/HM2013023:4.2.2/HM2013023/JHBCNBL30.0:user/release-keys","getAccuracy":"43.386314","getBSSID":"ac:f1:df:00:37:dd","getBestProvider":"network","getCanonicalHostName":"localhost","getCellLocation":"[37278,16241,-1]","getDataActivity":"0","getDeviceId":"864645025303568","getExtraInfo":"\"xsw-1\"","getHeight":"1280","getHostAddress":"127.0.0.1","getHostName":"localhost","getIpAddress":"-1124030272","getLine1Number":"+8613572094803","getLocalHost":"localhost/127.0.0.1","getLongitude":"108.920785","getMacAddress":"d4:97:0b:64:eb:8e","getNetworkId":"1","getNetworkOperator":"46000","getNetworkOperatorName":"中国移动","getNetworkType":"1","getProvider":"network","getRadioVersion":"MOLY.WR8.W1248.MD.WG.MP.V28.P38, 2014/04/22 20:00","getReason":"null","getRotation":"0","getRssi":"-56","getSSID":"\"xsw-1\"","getSimOperator":"46000","getSimOperatorName":"CMCC","getSimSerialNumber":"89860089261476363513","getSubscriberId":"460001963960368","getSubtype":"0","getSubtypeName":"","getType":"1","getTypeName":"WIFI","getWidth":"720","hardware":"mt6589","heightPixels":"1280","host":"wcc-miui-ota-bd05","incremental":"JHBCNBL30.0","manufacturer":"Xiaomi","model":"2013023","product":"2013023","release":"4.2.2","scaledDensity":"1.72","scanResultsBSSID":"ac:f1:df:00:37:dd","scanResultsCapabilities":"[WPA-PSK-TKIP+CCMP][ESS]","scanResultsFrequency":"2437","scanResultsLevel":"-63","scanResultsSSID":"xsw-1","sdk":"17","sdkInt":"17","serial":"ORBUD6AYUGT4P7TK","getLatitude":"34.1707","tags":"release-keys","time":"1428574802000","type":"user","user":"builder","version":"78","widthPixels":"720"},{"androidId":"3ce81b5e2df57159","board":"MSM8960","bootloader":"unknown","brand":"Xiaomi","buildID":"JRO03L","codename":"REL","density":"2.0","densityDpi":"320","device":"aries","display":"JRO03L","fingerprint":"Xiaomi/aries/aries:4.1.1/JRO03L/JLB52.0:user/release-keys","getAccuracy":"35.0","getBSSID":"b8:55:10:49:d1:a8","getBestProvider":"network","getCanonicalHostName":"localhost","getCellLocation":"[56993,328340,1637654,13831,6]","getDataActivity":"0","getDeviceId":"99000519543244","getExtraInfo":"null","getHeight":"1280","getHostAddress":"127.0.0.1","getHostName":"localhost","getIpAddress":"117483712","getLine1Number":"","getLocalHost":"localhost/127.0.0.1","getLongitude":"113.729108","getMacAddress":"8c:be:be:ba:57:89","getNetworkId":"0","getNetworkOperator":"46003","getNetworkOperatorName":"46003","getNetworkType":"6","getProvider":"network","getRadioVersion":"M9615A-CEFWMAZM-2.0.128017","getReason":"null","getRotation":"0","getRssi":"-51","getSSID":"TOTOLINK_49d1a8","getSimOperator":"46003","getSimOperatorName":"中国电信","getSimSerialNumber":"89860313907694880013","getSubscriberId":"460036291482109","getSubtype":"0","getSubtypeName":"","getType":"1","getTypeName":"WIFI","getWidth":"720","hardware":"qcom","heightPixels":"1280","host":"wcc-miui-ota-bd16.bj","incremental":"JLB52.0","manufacturer":"Xiaomi","model":"MI 2SC","product":"aries","release":"4.1.1","scaledDensity":"2.0","scanResultsBSSID":"c8:3a:35:55:28:78","scanResultsCapabilities":"[WPA-PSK-CCMP][WPA2-PSK-CCMP][ESS]","scanResultsFrequency":"2422","scanResultsLevel":"-69","scanResultsSSID":"123456789","sdk":"16","sdkInt":"16","serial":"ca56c43b","getLatitude":"22.8023","tags":"release-keys","time":"1422851016000","type":"user","user":"builder","version":"0","widthPixels":"720"},{"androidId":"9987280afd6cca3b","board":"C8815","bootloader":"unknown","brand":"Huawei","buildID":"HuaweiC8815","codename":"REL","density":"1.5","densityDpi":"240","device":"hwC8815","display":"C8815V100R001C92B140","fingerprint":"Huawei/C8815/hwC8815:4.1.2/HuaweiC8815/C92B140:user/ota-rel-keys,release-keys","getAccuracy":"40.0","getBSSID":"b0:c7:45:2e:24:a6","getBestProvider":"network","getCanonicalHostName":"localhost","getCellLocation":"[15285,461335,1711470,14174,12]","getDataActivity":"0","getDeviceId":"A0000043EF143B","getExtraInfo":"null","getHeight":"960","getHostAddress":"127.0.0.1","getHostName":"localhost","getIpAddress":"1678485696","getLine1Number":"","getLocalHost":"localhost/127.0.0.1","getLongitude":"118.854023","getMacAddress":"08:7a:4c:76:fa:54","getNetworkId":"52","getNetworkOperator":"46003","getNetworkOperatorName":"中国电信","getNetworkType":"6","getProvider":"network","getRadioVersion":"3110","getReason":"null","getRotation":"0","getRssi":"-72","getSSID":"Buffalo-G-24A6","getSimOperator":"46003","getSimOperatorName":"中国电信","getSimSerialNumber":"89860313900251158626","getSubscriberId":"460036511825671","getSubtype":"0","getSubtypeName":"","getType":"1","getTypeName":"WIFI","getWidth":"540","hardware":"huawei","heightPixels":"960","host":"huawei-desktop","incremental":"C92B140","manufacturer":"HUAWEI","model":"HUAWEI C8815","product":"C8815","release":"4.1.2","scaledDensity":"1.5","scanResultsBSSID":"ec:26:ca:1c:18:9e","scanResultsCapabilities":"[WPA-PSK-CCMP][WPA2-PSK-CCMP][ESS]","scanResultsFrequency":"2462","scanResultsLevel":"-87","scanResultsSSID":"TP-LINK_189E","sdk":"16","sdkInt":"16","serial":"087A4C76E2E3","getLatitude":"32.034776","tags":"ota-rel-keys,release-keys","time":"1415607936000","type":"user","user":"huawei","version":"0","widthPixels":"540"}]
 * [
 * {"androidId":"63231c278558b0cd","board":"bbk17_td3001_ics2","bootloader":"unknown","brand":"vivo","buildID":"IMM76D","codename":"REL","density":"1.5","densityDpi":"240","device":"bbk17_td3001_ics2","display":"ALPS.ICS2.TDD.MP.V1.2","fingerprint":"vivo/bbk17_td3001_ics2/bbk17_td3001_ics2:4.0.4/IMM76D/1414057382:user/test-keys","getAccuracy":"53.21255","getBSSID":"d0:c7:c0:5a:95:3c","getBestProvider":"network","getCanonicalHostName":"localhost","getCellLocation":"[47101,66265930,-1]","getDataActivity":"0","getDeviceId":"861276029118852","getExtraInfo":"null","getHeight":"800","getHostAddress":"127.0.0.1","getHostName":"localhost","getIpAddress":"1879156928","getLine1Number":"","getLocalHost":"localhost/127.0.0.1","getLongitude":"114.208623","getMacAddress":"2c:28:2d:43:ac:27","getNetworkId":"1","getNetworkOperator":"46000","getNetworkOperatorName":"中国移动","getNetworkType":"8","getProvider":"network","getRadioVersion":"MAUI.11AMD.W12.36.SP.V8.F3, 2013/06/26 18:02","getReason":"null","getRotation":"0","getRssi":"-62","getSSID":"0000000000","getSimOperator":"46002","getSimOperatorName":"CMCC","getSimSerialNumber":"89860013161400060539","getSubscriberId":"460028392640539","getSubtype":"0","getSubtypeName":"","getType":"1","getTypeName":"WIFI","getWidth":"480","hardware":"mt6577","heightPixels":"800","host":"compiler017","incremental":"eng.compiler.1414057382","manufacturer":"BBK","model":"vivo S11t","product":"bbk17_td3001_ics2","release":"4.0.4","scaledDensity":"1.5","scanResultsBSSID":"d0:c7:c0:5a:95:3c","scanResultsCapabilities":"[WPA-PSK-CCMP][WPA2-PSK-CCMP][ESS]","scanResultsFrequency":"2437","scanResultsLevel":"-61","scanResultsSSID":"0000000000","sdk":"15","sdkInt":"15","serial":"BUBIPFCYKR5TROZ9","getLatitude":"35.600945","tags":"test-keys","time":"1414057602000","type":"user","user":"compiler","version":"78","widthPixels":"480"},
 * {"androidId":"6f04f1956ae0218d","board":"2013023","bootloader":"unknown","brand":"Xiaomi","buildID":"HM2013023","codename":"REL","density":"2.0","densityDpi":"320","device":"HM2013023","display":"JHBCNBL30.0","fingerprint":"Xiaomi/2013023/HM2013023:4.2.2/HM2013023/JHBCNBL30.0:user/release-keys","getAccuracy":"43.386314","getBSSID":"ac:f1:df:00:37:dd","getBestProvider":"network","getCanonicalHostName":"localhost","getCellLocation":"[37278,16241,-1]","getDataActivity":"0","getDeviceId":"864645025303568","getExtraInfo":"\"xsw-1\"","getHeight":"1280","getHostAddress":"127.0.0.1","getHostName":"localhost","getIpAddress":"-1124030272","getLine1Number":"+8613572094803","getLocalHost":"localhost/127.0.0.1","getLongitude":"108.920785","getMacAddress":"d4:97:0b:64:eb:8e","getNetworkId":"1","getNetworkOperator":"46000","getNetworkOperatorName":"中国移动","getNetworkType":"1","getProvider":"network","getRadioVersion":"MOLY.WR8.W1248.MD.WG.MP.V28.P38, 2014/04/22 20:00","getReason":"null","getRotation":"0","getRssi":"-56","getSSID":"\"xsw-1\"","getSimOperator":"46000","getSimOperatorName":"CMCC","getSimSerialNumber":"89860089261476363513","getSubscriberId":"460001963960368","getSubtype":"0","getSubtypeName":"","getType":"1","getTypeName":"WIFI","getWidth":"720","hardware":"mt6589","heightPixels":"1280","host":"wcc-miui-ota-bd05","incremental":"JHBCNBL30.0","manufacturer":"Xiaomi","model":"2013023","product":"2013023","release":"4.2.2","scaledDensity":"1.72","scanResultsBSSID":"ac:f1:df:00:37:dd","scanResultsCapabilities":"[WPA-PSK-TKIP+CCMP][ESS]","scanResultsFrequency":"2437","scanResultsLevel":"-63","scanResultsSSID":"xsw-1","sdk":"17","sdkInt":"17","serial":"ORBUD6AYUGT4P7TK","getLatitude":"34.1707","tags":"release-keys","time":"1428574802000","type":"user","user":"builder","version":"78","widthPixels":"720"},
 * {"androidId":"3ce81b5e2df57159","board":"MSM8960","bootloader":"unknown","brand":"Xiaomi","buildID":"JRO03L","codename":"REL","density":"2.0","densityDpi":"320","device":"aries","display":"JRO03L","fingerprint":"Xiaomi/aries/aries:4.1.1/JRO03L/JLB52.0:user/release-keys","getAccuracy":"35.0","getBSSID":"b8:55:10:49:d1:a8","getBestProvider":"network","getCanonicalHostName":"localhost","getCellLocation":"[56993,328340,1637654,13831,6]","getDataActivity":"0","getDeviceId":"99000519543244","getExtraInfo":"null","getHeight":"1280","getHostAddress":"127.0.0.1","getHostName":"localhost","getIpAddress":"117483712","getLine1Number":"","getLocalHost":"localhost/127.0.0.1","getLongitude":"113.729108","getMacAddress":"8c:be:be:ba:57:89","getNetworkId":"0","getNetworkOperator":"46003","getNetworkOperatorName":"46003","getNetworkType":"6","getProvider":"network","getRadioVersion":"M9615A-CEFWMAZM-2.0.128017","getReason":"null","getRotation":"0","getRssi":"-51","getSSID":"TOTOLINK_49d1a8","getSimOperator":"46003","getSimOperatorName":"中国电信","getSimSerialNumber":"89860313907694880013","getSubscriberId":"460036291482109","getSubtype":"0","getSubtypeName":"","getType":"1","getTypeName":"WIFI","getWidth":"720","hardware":"qcom","heightPixels":"1280","host":"wcc-miui-ota-bd16.bj","incremental":"JLB52.0","manufacturer":"Xiaomi","model":"MI 2SC","product":"aries","release":"4.1.1","scaledDensity":"2.0","scanResultsBSSID":"c8:3a:35:55:28:78","scanResultsCapabilities":"[WPA-PSK-CCMP][WPA2-PSK-CCMP][ESS]","scanResultsFrequency":"2422","scanResultsLevel":"-69","scanResultsSSID":"123456789","sdk":"16","sdkInt":"16","serial":"ca56c43b","getLatitude":"22.8023","tags":"release-keys","time":"1422851016000","type":"user","user":"builder","version":"0","widthPixels":"720"},
 * {"androidId":"9987280afd6cca3b","board":"C8815","bootloader":"unknown","brand":"Huawei","buildID":"HuaweiC8815","codename":"REL","density":"1.5","densityDpi":"240","device":"hwC8815","display":"C8815V100R001C92B140","fingerprint":"Huawei/C8815/hwC8815:4.1.2/HuaweiC8815/C92B140:user/ota-rel-keys,release-keys","getAccuracy":"40.0","getBSSID":"b0:c7:45:2e:24:a6","getBestProvider":"network","getCanonicalHostName":"localhost","getCellLocation":"[15285,461335,1711470,14174,12]","getDataActivity":"0","getDeviceId":"A0000043EF143B","getExtraInfo":"null","getHeight":"960","getHostAddress":"127.0.0.1","getHostName":"localhost","getIpAddress":"1678485696","getLine1Number":"","getLocalHost":"localhost/127.0.0.1","getLongitude":"118.854023","getMacAddress":"08:7a:4c:76:fa:54","getNetworkId":"52","getNetworkOperator":"46003","getNetworkOperatorName":"中国电信","getNetworkType":"6","getProvider":"network","getRadioVersion":"3110","getReason":"null","getRotation":"0","getRssi":"-72","getSSID":"Buffalo-G-24A6","getSimOperator":"46003","getSimOperatorName":"中国电信","getSimSerialNumber":"89860313900251158626","getSubscriberId":"460036511825671","getSubtype":"0","getSubtypeName":"","getType":"1","getTypeName":"WIFI","getWidth":"540","hardware":"huawei","heightPixels":"960","host":"huawei-desktop","incremental":"C92B140","manufacturer":"HUAWEI","model":"HUAWEI C8815","product":"C8815","release":"4.1.2","scaledDensity":"1.5","scanResultsBSSID":"ec:26:ca:1c:18:9e","scanResultsCapabilities":"[WPA-PSK-CCMP][WPA2-PSK-CCMP][ESS]","scanResultsFrequency":"2462","scanResultsLevel":"-87","scanResultsSSID":"TP-LINK_189E","sdk":"16","sdkInt":"16","serial":"087A4C76E2E3","getLatitude":"32.034776","tags":"ota-rel-keys,release-keys","time":"1415607936000","type":"user","user":"huawei","version":"0","widthPixels":"540"}
 * ]
 */
public class PhoneMgr {
    private static final String PHONE_NUM = "phone_num";
    private static final List<PhoneInfo> mPhoneList;
    private static final String[] mPhoneInfos = new String[]{
            "{\"androidId\":\"63231c278558b0cd\",\"board\":\"bbk17_td3001_ics2\",\"bootloader\":\"unknown\",\"brand\":\"vivo\",\"buildID\":\"IMM76D\",\"codename\":\"REL\",\"density\":\"1.5\",\"densityDpi\":\"240\",\"device\":\"bbk17_td3001_ics2\",\"display\":\"ALPS.ICS2.TDD.MP.V1.2\",\"fingerprint\":\"vivo/bbk17_td3001_ics2/bbk17_td3001_ics2:4.0.4/IMM76D/1414057382:user/test-keys\",\"getAccuracy\":\"53.21255\",\"getBSSID\":\"d0:c7:c0:5a:95:3c\",\"getBestProvider\":\"network\",\"getCanonicalHostName\":\"localhost\",\"getCellLocation\":\"[47101,66265930,-1]\",\"getDataActivity\":\"0\",\"getDeviceId\":\"861276029118852\",\"getExtraInfo\":\"null\",\"getHeight\":\"800\",\"getHostAddress\":\"127.0.0.1\",\"getHostName\":\"localhost\",\"getIpAddress\":\"1879156928\",\"getLine1Number\":\"\",\"getLocalHost\":\"localhost/127.0.0.1\",\"getLongitude\":\"114.208623\",\"getMacAddress\":\"2c:28:2d:43:ac:27\",\"getNetworkId\":\"1\",\"getNetworkOperator\":\"46000\",\"getNetworkOperatorName\":\"中国移动\",\"getNetworkType\":\"8\",\"getProvider\":\"network\",\"getRadioVersion\":\"MAUI.11AMD.W12.36.SP.V8.F3, 2013/06/26 18:02\",\"getReason\":\"null\",\"getRotation\":\"0\",\"getRssi\":\"-62\",\"getSSID\":\"0000000000\",\"getSimOperator\":\"46002\",\"getSimOperatorName\":\"CMCC\",\"getSimSerialNumber\":\"89860013161400060539\",\"getSubscriberId\":\"460028392640539\",\"getSubtype\":\"0\",\"getSubtypeName\":\"\",\"getType\":\"1\",\"getTypeName\":\"WIFI\",\"getWidth\":\"480\",\"hardware\":\"mt6577\",\"heightPixels\":\"800\",\"host\":\"compiler017\",\"incremental\":\"eng.compiler.1414057382\",\"manufacturer\":\"BBK\",\"model\":\"vivo S11t\",\"product\":\"bbk17_td3001_ics2\",\"release\":\"4.0.4\",\"scaledDensity\":\"1.5\",\"scanResultsBSSID\":\"d0:c7:c0:5a:95:3c\",\"scanResultsCapabilities\":\"[WPA-PSK-CCMP][WPA2-PSK-CCMP][ESS]\",\"scanResultsFrequency\":\"2437\",\"scanResultsLevel\":\"-61\",\"scanResultsSSID\":\"0000000000\",\"sdk\":\"15\",\"sdkInt\":\"15\",\"serial\":\"BUBIPFCYKR5TROZ9\",\"getLatitude\":\"35.600945\",\"tags\":\"test-keys\",\"time\":\"1414057602000\",\"type\":\"user\",\"user\":\"compiler\",\"version\":\"78\",\"widthPixels\":\"480\"}",
            "{\"androidId\":\"6f04f1956ae0218d\",\"board\":\"2013023\",\"bootloader\":\"unknown\",\"brand\":\"Xiaomi\",\"buildID\":\"HM2013023\",\"codename\":\"REL\",\"density\":\"2.0\",\"densityDpi\":\"320\",\"device\":\"HM2013023\",\"display\":\"JHBCNBL30.0\",\"fingerprint\":\"Xiaomi/2013023/HM2013023:4.2.2/HM2013023/JHBCNBL30.0:user/release-keys\",\"getAccuracy\":\"43.386314\",\"getBSSID\":\"ac:f1:df:00:37:dd\",\"getBestProvider\":\"network\",\"getCanonicalHostName\":\"localhost\",\"getCellLocation\":\"[37278,16241,-1]\",\"getDataActivity\":\"0\",\"getDeviceId\":\"864645025303568\",\"getExtraInfo\":\"\\\"xsw-1\\\"\",\"getHeight\":\"1280\",\"getHostAddress\":\"127.0.0.1\",\"getHostName\":\"localhost\",\"getIpAddress\":\"-1124030272\",\"getLine1Number\":\"+8613572094803\",\"getLocalHost\":\"localhost/127.0.0.1\",\"getLongitude\":\"108.920785\",\"getMacAddress\":\"d4:97:0b:64:eb:8e\",\"getNetworkId\":\"1\",\"getNetworkOperator\":\"46000\",\"getNetworkOperatorName\":\"中国移动\",\"getNetworkType\":\"1\",\"getProvider\":\"network\",\"getRadioVersion\":\"MOLY.WR8.W1248.MD.WG.MP.V28.P38, 2014/04/22 20:00\",\"getReason\":\"null\",\"getRotation\":\"0\",\"getRssi\":\"-56\",\"getSSID\":\"\\\"xsw-1\\\"\",\"getSimOperator\":\"46000\",\"getSimOperatorName\":\"CMCC\",\"getSimSerialNumber\":\"89860089261476363513\",\"getSubscriberId\":\"460001963960368\",\"getSubtype\":\"0\",\"getSubtypeName\":\"\",\"getType\":\"1\",\"getTypeName\":\"WIFI\",\"getWidth\":\"720\",\"hardware\":\"mt6589\",\"heightPixels\":\"1280\",\"host\":\"wcc-miui-ota-bd05\",\"incremental\":\"JHBCNBL30.0\",\"manufacturer\":\"Xiaomi\",\"model\":\"2013023\",\"product\":\"2013023\",\"release\":\"4.2.2\",\"scaledDensity\":\"1.72\",\"scanResultsBSSID\":\"ac:f1:df:00:37:dd\",\"scanResultsCapabilities\":\"[WPA-PSK-TKIP+CCMP][ESS]\",\"scanResultsFrequency\":\"2437\",\"scanResultsLevel\":\"-63\",\"scanResultsSSID\":\"xsw-1\",\"sdk\":\"17\",\"sdkInt\":\"17\",\"serial\":\"ORBUD6AYUGT4P7TK\",\"getLatitude\":\"34.1707\",\"tags\":\"release-keys\",\"time\":\"1428574802000\",\"type\":\"user\",\"user\":\"builder\",\"version\":\"78\",\"widthPixels\":\"720\"}",
            "{\"androidId\":\"3ce81b5e2df57159\",\"board\":\"MSM8960\",\"bootloader\":\"unknown\",\"brand\":\"Xiaomi\",\"buildID\":\"JRO03L\",\"codename\":\"REL\",\"density\":\"2.0\",\"densityDpi\":\"320\",\"device\":\"aries\",\"display\":\"JRO03L\",\"fingerprint\":\"Xiaomi/aries/aries:4.1.1/JRO03L/JLB52.0:user/release-keys\",\"getAccuracy\":\"35.0\",\"getBSSID\":\"b8:55:10:49:d1:a8\",\"getBestProvider\":\"network\",\"getCanonicalHostName\":\"localhost\",\"getCellLocation\":\"[56993,328340,1637654,13831,6]\",\"getDataActivity\":\"0\",\"getDeviceId\":\"99000519543244\",\"getExtraInfo\":\"null\",\"getHeight\":\"1280\",\"getHostAddress\":\"127.0.0.1\",\"getHostName\":\"localhost\",\"getIpAddress\":\"117483712\",\"getLine1Number\":\"\",\"getLocalHost\":\"localhost/127.0.0.1\",\"getLongitude\":\"113.729108\",\"getMacAddress\":\"8c:be:be:ba:57:89\",\"getNetworkId\":\"0\",\"getNetworkOperator\":\"46003\",\"getNetworkOperatorName\":\"46003\",\"getNetworkType\":\"6\",\"getProvider\":\"network\",\"getRadioVersion\":\"M9615A-CEFWMAZM-2.0.128017\",\"getReason\":\"null\",\"getRotation\":\"0\",\"getRssi\":\"-51\",\"getSSID\":\"TOTOLINK_49d1a8\",\"getSimOperator\":\"46003\",\"getSimOperatorName\":\"中国电信\",\"getSimSerialNumber\":\"89860313907694880013\",\"getSubscriberId\":\"460036291482109\",\"getSubtype\":\"0\",\"getSubtypeName\":\"\",\"getType\":\"1\",\"getTypeName\":\"WIFI\",\"getWidth\":\"720\",\"hardware\":\"qcom\",\"heightPixels\":\"1280\",\"host\":\"wcc-miui-ota-bd16.bj\",\"incremental\":\"JLB52.0\",\"manufacturer\":\"Xiaomi\",\"model\":\"MI 2SC\",\"product\":\"aries\",\"release\":\"4.1.1\",\"scaledDensity\":\"2.0\",\"scanResultsBSSID\":\"c8:3a:35:55:28:78\",\"scanResultsCapabilities\":\"[WPA-PSK-CCMP][WPA2-PSK-CCMP][ESS]\",\"scanResultsFrequency\":\"2422\",\"scanResultsLevel\":\"-69\",\"scanResultsSSID\":\"123456789\",\"sdk\":\"16\",\"sdkInt\":\"16\",\"serial\":\"ca56c43b\",\"getLatitude\":\"22.8023\",\"tags\":\"release-keys\",\"time\":\"1422851016000\",\"type\":\"user\",\"user\":\"builder\",\"version\":\"0\",\"widthPixels\":\"720\"}",
            "{\"androidId\":\"9987280afd6cca3b\",\"board\":\"C8815\",\"bootloader\":\"unknown\",\"brand\":\"Huawei\",\"buildID\":\"HuaweiC8815\",\"codename\":\"REL\",\"density\":\"1.5\",\"densityDpi\":\"240\",\"device\":\"hwC8815\",\"display\":\"C8815V100R001C92B140\",\"fingerprint\":\"Huawei/C8815/hwC8815:4.1.2/HuaweiC8815/C92B140:user/ota-rel-keys,release-keys\",\"getAccuracy\":\"40.0\",\"getBSSID\":\"b0:c7:45:2e:24:a6\",\"getBestProvider\":\"network\",\"getCanonicalHostName\":\"localhost\",\"getCellLocation\":\"[15285,461335,1711470,14174,12]\",\"getDataActivity\":\"0\",\"getDeviceId\":\"A0000043EF143B\",\"getExtraInfo\":\"null\",\"getHeight\":\"960\",\"getHostAddress\":\"127.0.0.1\",\"getHostName\":\"localhost\",\"getIpAddress\":\"1678485696\",\"getLine1Number\":\"\",\"getLocalHost\":\"localhost/127.0.0.1\",\"getLongitude\":\"118.854023\",\"getMacAddress\":\"08:7a:4c:76:fa:54\",\"getNetworkId\":\"52\",\"getNetworkOperator\":\"46003\",\"getNetworkOperatorName\":\"中国电信\",\"getNetworkType\":\"6\",\"getProvider\":\"network\",\"getRadioVersion\":\"3110\",\"getReason\":\"null\",\"getRotation\":\"0\",\"getRssi\":\"-72\",\"getSSID\":\"Buffalo-G-24A6\",\"getSimOperator\":\"46003\",\"getSimOperatorName\":\"中国电信\",\"getSimSerialNumber\":\"89860313900251158626\",\"getSubscriberId\":\"460036511825671\",\"getSubtype\":\"0\",\"getSubtypeName\":\"\",\"getType\":\"1\",\"getTypeName\":\"WIFI\",\"getWidth\":\"540\",\"hardware\":\"huawei\",\"heightPixels\":\"960\",\"host\":\"huawei-desktop\",\"incremental\":\"C92B140\",\"manufacturer\":\"HUAWEI\",\"model\":\"HUAWEI C8815\",\"product\":\"C8815\",\"release\":\"4.1.2\",\"scaledDensity\":\"1.5\",\"scanResultsBSSID\":\"ec:26:ca:1c:18:9e\",\"scanResultsCapabilities\":\"[WPA-PSK-CCMP][WPA2-PSK-CCMP][ESS]\",\"scanResultsFrequency\":\"2462\",\"scanResultsLevel\":\"-87\",\"scanResultsSSID\":\"TP-LINK_189E\",\"sdk\":\"16\",\"sdkInt\":\"16\",\"serial\":\"087A4C76E2E3\",\"getLatitude\":\"32.034776\",\"tags\":\"ota-rel-keys,release-keys\",\"time\":\"1415607936000\",\"type\":\"user\",\"user\":\"huawei\",\"version\":\"0\",\"widthPixels\":\"540\"}"
    };

    static {
        mPhoneList = new ArrayList<>();
        for (String info : mPhoneInfos) {
            mPhoneList.add(JSON.parseObject(info, PhoneInfo.class));
        }
    }

    public static PhoneInfo getPhoneInfo(Context context) {
        int cacheNum = getCacheNum(context);
        if (cacheNum == -1) {
            return getLocalInfo(context);
        }

        return getCacheInfo(cacheNum);
    }

    public static PhoneInfo newPhoneInfo(Context context) {
        int cacheNum = (Integer) PersistFactory.getInstance(context).readData(PHONE_NUM, -1);
        if (++cacheNum >= mPhoneList.size()) {
            cacheNum = -1;
        }
        PersistFactory.getInstance(context).writeData(PHONE_NUM, cacheNum);

        if (cacheNum == -1) {
            return getLocalInfo(context);
        }
        return getCacheInfo(cacheNum);
    }

    private static int getCacheNum(Context context) {
        return (Integer) PersistFactory.getInstance(context).readData(PHONE_NUM, -1);
    }

    private static PhoneInfo getCacheInfo(int num) {
        if (num >= mPhoneList.size()) {
            return null;
        }
        return mPhoneList.get(num);
    }

    private static PhoneInfo getLocalInfo(Context context) {
        PhoneInfo phoneInfo = new PhoneInfo();

        try {
            phoneInfo.setTags(Build.TAGS);
            phoneInfo.setHost(Build.HOST);
            phoneInfo.setUser(Build.USER);
            phoneInfo.setTime(String.valueOf(Build.TIME));
            phoneInfo.setDisplay(Build.DISPLAY);
            phoneInfo.setBootloader(Build.BOOTLOADER);
            phoneInfo.setSerial(Build.SERIAL);
            phoneInfo.setBoard(Build.BOARD);
            phoneInfo.setBrand(Build.BRAND);
            phoneInfo.setDevice(Build.DEVICE);
            phoneInfo.setFingerprint(Build.FINGERPRINT);
            phoneInfo.setHardware(Build.HARDWARE);
            phoneInfo.setManufacturer(Build.MANUFACTURER);
            phoneInfo.setType(Build.TYPE);
            phoneInfo.setModel(Build.MODEL);
            phoneInfo.setProduct(Build.PRODUCT);
            phoneInfo.setBuildID(Build.ID);
            phoneInfo.setRelease(Build.VERSION.RELEASE);
            phoneInfo.setIncremental(Build.VERSION.INCREMENTAL);
            phoneInfo.setCodename(Build.VERSION.CODENAME);
            phoneInfo.setSdk(Build.VERSION.SDK);
            phoneInfo.setSdkInt(String.valueOf(Build.VERSION.SDK_INT));
            phoneInfo.setGetRadioVersion(Build.getRadioVersion());

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            phoneInfo.setGetDeviceId(telephonyManager.getDeviceId());
            phoneInfo.setGetNetworkOperator(telephonyManager.getNetworkOperator());
            phoneInfo.setGetNetworkOperatorName(telephonyManager.getNetworkOperatorName());
            phoneInfo.setGetNetworkType(String.valueOf(telephonyManager.getNetworkType()));
            phoneInfo.setGetSimOperator(telephonyManager.getSimOperator());
            phoneInfo.setGetSimOperatorName(telephonyManager.getSimOperatorName());
            phoneInfo.setGetSubscriberId(telephonyManager.getSubscriberId());
            phoneInfo.setGetDataActivity(String.valueOf(telephonyManager.getDataActivity()));
            phoneInfo.setVersion(telephonyManager.getDeviceSoftwareVersion());
            phoneInfo.setGetLine1Number(telephonyManager.getLine1Number());
            phoneInfo.setGetSimSerialNumber(telephonyManager.getSimSerialNumber());
            phoneInfo.setGetCellLocation(telephonyManager.getCellLocation().toString());

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            phoneInfo.setGetExtraInfo(networkInfo.getExtraInfo());
            phoneInfo.setGetReason(networkInfo.getReason());
            phoneInfo.setGetSubtype(String.valueOf(networkInfo.getSubtype()));
            phoneInfo.setGetTypeName(networkInfo.getSubtypeName());
            phoneInfo.setGetType(String.valueOf(networkInfo.getType()));
            phoneInfo.setGetTypeName(networkInfo.getTypeName());

            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            phoneInfo.setGetMacAddress(wifiInfo.getMacAddress());
            phoneInfo.setGetBSSID(wifiInfo.getBSSID());
            phoneInfo.setGetIpAddress(String.valueOf(wifiInfo.getIpAddress()));
            phoneInfo.setGetNetworkId(String.valueOf(wifiInfo.getNetworkId()));
            phoneInfo.setGetSSID(wifiInfo.getSSID());
            phoneInfo.setGetRssi(String.valueOf(wifiInfo.getRssi()));
            // phoneInfo.setGetLocalHost(InetAddress.getLocalHost().toString());
            phoneInfo.setGetLocalHost("localhost/127.0.0.1");

            List<ScanResult> list = wifiManager.getScanResults();
            if (list != null && list.size() > 0) {
                ScanResult result = list.get(0);
                phoneInfo.setScanResultsBSSID(result.BSSID);
                phoneInfo.setScanResultsCapabilities(result.capabilities);
                phoneInfo.setScanResultsFrequency(String.valueOf(result.frequency));
                phoneInfo.setScanResultsLevel(String.valueOf(result.level));
                phoneInfo.setScanResultsSSID(result.SSID);
            }

            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            phoneInfo.setWidthPixels(String.valueOf(metrics.widthPixels));
            phoneInfo.setWidthPixels(String.valueOf(metrics.heightPixels));
            phoneInfo.setWidthPixels(String.valueOf(metrics.density));
            phoneInfo.setWidthPixels(String.valueOf(metrics.densityDpi));
            phoneInfo.setWidthPixels(String.valueOf(metrics.scaledDensity));

            phoneInfo.setAndroidId(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        } catch (Exception e) {
            TraceUtil.e("phoneRead err: " + e.getMessage());
        }
        return phoneInfo;
    }

}
