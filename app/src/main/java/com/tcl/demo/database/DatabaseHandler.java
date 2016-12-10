package com.tcl.demo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tcl.demo.core.DownloadParam;
import com.tcl.demo.install.PkgInstaller;

import org.xutils.common.util.LogUtil;

/**
 * Created by shengyuan on 16-12-8.
 */

public class DatabaseHandler {

    private static final String TAG = "DatabaseHandler";
    private Context mContext;
    private DownloadDBHelper mDBHelper;
    private static DatabaseHandler mInstance;

    private DatabaseHandler(Context context) {
        mContext = context;
        mDBHelper = new DownloadDBHelper(mContext);
    }

    public static DatabaseHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHandler(context);
        }
        return mInstance;
    }

    // 程序退出，之前的 task 对象被销毁
    public DownloadParam restoreTask(String appName) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = mDBHelper.getReadableDatabase();
            String sql = "select * from task where AppName = ?";
            cursor = db.rawQuery(sql, new String[]{appName});
            if (cursor.moveToNext()) {
                Log.d("DatabaseHandler", "restore task from database");
                return parseDownloadParam(cursor);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return null;
    }

    public void insertOrUpdateRecord(DownloadParam param) {

        if (isRecordExits(param)) {
            // update
            String sql = "update [task] set CompletedBytes = ? where AppName = ?";
            execSQL(sql, new Object[]{param.getCompletedBytes(), param.getName()});
        }
        else {
            // insert
            String sql = "insert into task (AppName, Url, Size, CompletedBytes) "
                    + "values (?, ?, ?, ?)";
            execSQL(sql, new Object[]{param.getName(),
                    param.getStrUrl(),
                    param.getSize(),
                    param.getCompletedBytes()});
        }
    }

    public void addPackageName(DownloadParam param) {
        String sql = "update [task] set PackageName = ? where AppName = ?";
        execSQL(sql, new Object[]{PkgInstaller.packageName(mContext, DownloadParam.apkPath(mContext, param)), param.getName()});
    }

    public boolean isRecordExits(DownloadParam param) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = mDBHelper.getReadableDatabase();
            String sql = "select * from task where AppName = ?";
            cursor = db.rawQuery(sql, new String[]{param.getName()});

            return cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    private void execSQL(String sql, Object[] bindArgs) {
        SQLiteDatabase db = null;
        try {
            if (sql.contains("select")){
                LogUtil.d("select sql should be processed by query method");
            }
            else if (sql.contains("insert") || sql.contains("update") || sql.contains("delete")){
                db = mDBHelper.getWritableDatabase();
                db.beginTransaction();
                db.execSQL(sql, bindArgs);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    public Context getContext() {
        return mContext;
    }

    public static DownloadParam parseDownloadParam(Cursor cursor) {

        return DownloadParam.toDownloadParam(cursor);
    }

}