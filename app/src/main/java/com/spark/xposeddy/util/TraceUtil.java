package com.spark.xposeddy.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.XposedBridge;

public class TraceUtil {
    private static boolean isDebug = true;
    private static boolean isWriter = true;
    private final static int LOG_MAX_LEN = 3000;
    private final static String MATCH = "%s->%s->%d";
    private final static String CONNECTOR = ":<--->:";
    private final static String sLogDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/log_hb/";
    private final static String sLogName = "log";
    private final static String TAG = "===========》";

    private static String buildHeader() {
        StackTraceElement stack = Thread.currentThread().getStackTrace()[4];
        String className = "";
        Pattern pattern = Pattern.compile(".[\\w\\$]+$");
        Matcher matcher = pattern.matcher(stack.getClassName());
        if (matcher.find()) {
            className = matcher.group(0);
            className = className.replace(".", "");
        }
        return String.format(Locale.getDefault(), MATCH, className, stack.getMethodName(), stack.getLineNumber()) + CONNECTOR;
    }

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    public static void setWriter(boolean writer) {
        isWriter = writer;
    }

    public static void e(String msg) {
        if (!isDebug) return;

        msg = buildHeader() + ": " + msg;
        for (int i = 0; i < msg.length(); i += LOG_MAX_LEN) {
            if (i + LOG_MAX_LEN < msg.length()) {
                Log.e("error" + TAG, msg.substring(i, i + LOG_MAX_LEN));
            } else {
                Log.e("error" + TAG, msg.substring(i, msg.length()));
            }
        }
    }

    public static void d(String msg) {
        if (!isDebug) return;

        msg = buildHeader() + ": " + msg;
        for (int i = 0; i < msg.length(); i += LOG_MAX_LEN) {
            if (i + LOG_MAX_LEN < msg.length()) {
                Log.d("debug" + TAG, msg.substring(i, i + LOG_MAX_LEN));
            } else {
                Log.d("debug" + TAG, msg.substring(i, msg.length()));
            }
        }
    }

    /**
     * xposed + logcat
     *
     * @param msg 日志
     */
    public static void xe(String msg) {
        if (!isDebug) return;

        msg = buildHeader() + ": " + msg;
        try {
            for (int i = 0; i < msg.length(); i += LOG_MAX_LEN) {
                if (i + LOG_MAX_LEN < msg.length()) {
                    Log.e("xerror" + TAG, msg.substring(i, i + LOG_MAX_LEN));
                    writeLog(msg.substring(i, i + LOG_MAX_LEN));
                } else {
                    Log.e("xerror" + TAG, msg.substring(i, msg.length()));
                    writeLog(msg.substring(i, msg.length()));
                }
            }
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
    }

    /**
     * log文件 + logcat
     *
     * @param msg 日志
     */
    public static void le(String msg) {
        if (!isDebug) return;

        msg = buildHeader() + ": " + msg;
        try {
            for (int i = 0; i < msg.length(); i += LOG_MAX_LEN) {
                if (i + LOG_MAX_LEN < msg.length()) {
                    Log.e("lerror" + TAG, msg.substring(i, i + LOG_MAX_LEN));
                    writeLog(msg.substring(i, i + LOG_MAX_LEN));
                } else {
                    Log.e("lerror" + TAG, msg.substring(i, msg.length()));
                    writeLog(msg.substring(i, msg.length()));
                }
            }
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
    }

    private static void writeLog(String msg) {
        if (!isWriter) return;

        File logDir = new File(sLogDir);// 如果没有log文件夹则新建该文件夹
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        try {
            SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            File file = new File(sLogDir, sLogName + data.format(new Date()) + ".txt");
            FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            bufWriter.write(sdf.format(new Date()) + ": " + msg);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
