package com.spark.xposeddy.xposed;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.widget.Toast;

import com.spark.xposeddy.util.TraceUtil;
import com.spark.xposeddy.xposed.dy.HookDy;
import com.spark.xposeddy.xposed.phone.HookPhone;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * xposed的入口函数
 */
public class HookMain implements IXposedHookLoadPackage {
    // 包名
    public static final String PACKAGE_ID_NORM = "com.ss.android.ugc.aweme"; // 抖音短视频

    //是否已经HOOK
    private boolean isHook = false;

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.appInfo == null || (lpparam.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
            return;
        }
        final String packageName = lpparam.packageName;
        final String processName = lpparam.processName;
        if (PACKAGE_ID_NORM.equals(packageName)) {
            try {
                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        final Context context = (Context) param.args[0];
                        ClassLoader appClassLoader = context.getClassLoader();
                        if (PACKAGE_ID_NORM.equals(processName) && !isHook) {
                            isHook = true;
                            TraceUtil.xe("助手初始化成功");
                            Toast.makeText(context, "助手初始化成功", Toast.LENGTH_LONG).show();
                            new HookDy().hook(appClassLoader, context);
                            new HookPhone().hook(appClassLoader, context);
                        }
                    }
                });
            } catch (Throwable e) {
                TraceUtil.xe(e.getMessage());
            }
        }
    }
}
