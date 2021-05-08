package com.spark.xposeddy.xposed.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.spark.xposeddy.repository.DbMgr;
import com.spark.xposeddy.repository.bean.CommentBean;
import com.spark.xposeddy.util.TraceUtil;

public class DbProvider extends ContentProvider {

    private static final String AUTHORITY = "com.ss.android.ugc.aweme.DbProvider";
    private static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    private static final String KEY = "key";
    private DbMgr mDbMgr;

    public static boolean insertComment(Context context, CommentBean comment) {
        if (comment == null) {
            return true;
        }

        ContentValues values = new ContentValues();
        values.put(KEY, JSON.toJSONString(comment));
        context.getContentResolver().insert(AUTHORITY_URI, values);
        return true;
    }

    public static CommentBean readComment(Context context, String awemeId) {
        String result = "";

        Cursor cursor = context.getContentResolver().query(AUTHORITY_URI, null, awemeId, null, null);
        if (cursor != null && cursor.moveToNext()) {
            result = cursor.getString(0);
        }
        cursor.close();
        TraceUtil.d("DbProvider readData, awemeId = " + awemeId + ", data = " + result);

        if (TextUtils.isEmpty(result)) {
            return null;
        }
        return JSON.parseObject(result, CommentBean.class);
    }


    @Override
    public boolean onCreate() {
        mDbMgr = DbMgr.getSingleInstance(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (!TextUtils.isEmpty(selection)) {
            CommentBean bean = mDbMgr.readComment(selection);
            String value = "";
            if (bean != null) {
                value = JSON.toJSONString(bean);
            }
            MatrixCursor cursor = new MatrixCursor(new String[]{"value"});
            cursor.addRow(new String[]{value});
            return cursor;
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (values != null) {
            String value = values.getAsString(KEY);
            CommentBean bean = JSON.parseObject(value, CommentBean.class);
            if (bean != null) {
                mDbMgr.insertComment(bean);
            }
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
