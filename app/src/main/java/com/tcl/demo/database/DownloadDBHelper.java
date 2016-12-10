package com.tcl.demo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shengyuan on 16-12-7.
 */

public class DownloadDBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "Task";
    // for convenience of debugging, we create db file on sd  card.
    // actually, we can just use DATABASE_NAME = "download.db".
    public static final String DATABASE_NAME = DatabaseConst.SD_PATH + "/download.db";

    public DownloadDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DownloadDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create table
        String sql = "create table if not exists " + TABLE_NAME +
                "(Id integer primary key autoincrement, " +
                "AppName varchar(128), " +
                "Url varchar(256), " +
                "Size integer, " +
                "CompletedBytes integer, " +
                "PackageName varchar(128))";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
