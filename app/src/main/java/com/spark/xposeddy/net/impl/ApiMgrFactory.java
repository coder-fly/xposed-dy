package com.spark.xposeddy.net.impl;

import android.content.Context;

import com.spark.xposeddy.net.IApiMgr;
import com.spark.xposeddy.persist.IPersist;
import com.spark.xposeddy.persist.impl.PersistFactory;

public class ApiMgrFactory {

    private static IApiMgr mgr;

    public static IApiMgr getInstance(Context context) {
        if (mgr == null) {
            synchronized (ApiMgrFactory.class) {
                if (mgr == null) {
                    IPersist persist = PersistFactory.getInstance(context.getApplicationContext());
                    mgr = new ApiMgr(persist);
                }
            }
        }
        return mgr;
    }

}
