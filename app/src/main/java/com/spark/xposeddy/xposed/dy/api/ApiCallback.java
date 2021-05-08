package com.spark.xposeddy.xposed.dy.api;

import com.spark.xposeddy.util.TraceUtil;

public class ApiCallback {
    private volatile Result res = null;

    public Result getResult() {
        return this.res;
    }

    /**
     * 同步5s超时等待结果
     *
     * @return
     */
    public Result waitResult() {
        if (this.res != null) {
            return this.res;
        }

        // 5s超时等待
        for (int i = 0; i < 25; i++) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (this.res != null) {
                return this.res;
            }
        }

        TraceUtil.e("result timeout");
        this.res = Result.error(Result.ResultCode.TIME_OUT, "");
        return this.res;
    }

    public void setResult(Result res) {
        // TraceUtil.e("result res = " + res);
        this.res = res;
    }

}
