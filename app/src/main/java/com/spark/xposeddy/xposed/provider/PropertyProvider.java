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

import com.spark.xposeddy.persist.IPersist;
import com.spark.xposeddy.persist.impl.PersistFactory;
import com.spark.xposeddy.util.TraceUtil;

public class PropertyProvider extends ContentProvider {

    private static final String AUTHORITY = "com.ss.android.ugc.aweme.PropertyProvider";
    private static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    private IPersist mPersist;

    public static boolean writeData(Context context, String key, String value) {
        ContentValues values = new ContentValues();
        values.put(key, value);
        context.getContentResolver().insert(AUTHORITY_URI, values);
        return true;
    }

    public static String readData(Context context, String key) {
        String result = "";

        Cursor cursor = context.getContentResolver().query(AUTHORITY_URI, null, key, null, null);
        if (cursor != null && cursor.moveToNext()) {
            result = cursor.getString(0);
        }
        cursor.close();
        TraceUtil.d("PropertyProvider readData, key = " + key + ", data = " + result);
        return result;
    }

    @Override
    public boolean onCreate() {
        mPersist = PersistFactory.getInstance(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (!TextUtils.isEmpty(selection)) {
            String value = (String) mPersist.readData(selection, "");
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
            for (String key : values.keySet()) {
                mPersist.writeData(key, values.get(key));
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
