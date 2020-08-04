package com.dp.shell;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import java.lang.reflect.Method;


public class PluginServiceProvider extends ContentProvider {

    private Object mObj;

    @Override
    public boolean onCreate() {
        if (mObj == null) {
            mObj = DpLoadUtils.load(PluginHelper.getInstance().getContext(), "com.morgoo.droidplugin.PluginServiceProvider");
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return uri.getPath();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if (mObj != null) {
            Method method1 = null;
            try {
                method1 = mObj.getClass().getDeclaredMethod("onCreate", String.class, String.class, Bundle.class);
                method1.setAccessible(true);
                return (Bundle) method1.invoke(mObj, method, arg, extras);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
