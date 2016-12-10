package com.tcl.demo.core;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import com.tcl.demo.database.DatabaseConst;

import java.io.File;


public class DownloadParam {

    private String mName;
    private String mStrUrl;
    private int mProgress;  // in percent
    private long mCompletedBytes;
    private long mSize;
    private Status mStatus = Status.PENDING;
    private String mExtension = ".apk";
    private ProgressListener mProgressListener;

    public DownloadParam(String name, String strUrl, int progress) {
        mName = name;
        mStrUrl = strUrl;
        mProgress = progress;
    }

    private DownloadParam(String name, String strUrl, long completedBytes, long size, Status status, int progress) {
        mName = name;
        mStrUrl = strUrl;
        mCompletedBytes = completedBytes;
        mSize = size;
        mStatus = status;
        mProgress = progress;
    }

    void setProgressListener(ProgressListener progressListener) {
        mProgressListener = progressListener;
    }

    public ProgressListener getProgressListener() {
        return mProgressListener;
    }

    public String getStrUrl() {
        return mStrUrl;
    }

    void setProgress(int progress) {
        mProgress = progress;
        mProgressListener.onProgressUpdate();
    }

    void setSize(long size) {
        this.mSize = size;
    }

    public long getSize() {
        return mSize;
    }

    public int getProgress() {
        return mProgress;
    }

    public String getName() {
        return mName;
    }

    String getExtension() {
        return mExtension;
    }

    void setCompletedBytes(long completedBytes) {
        mCompletedBytes = completedBytes;
    }

    public long getCompletedBytes() {
        return mCompletedBytes;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

    public enum Status {
        /**
         * before start
         */
        PENDING,
        /**
         * downloading
         */
        RUNNING,
        /**
         * pause button press
         */
        PAUSED,
        /**
         * finish downloading
         */
        FINISHED,
        /**
         *
         */
        INSTALLED,
    }

    public static DownloadParam toDownloadParam(Cursor cursor) {

        String appName = cursor.getString(cursor.getColumnIndex(DatabaseConst.FIELDS[1]));

        String url = cursor.getString(cursor.getColumnIndex(DatabaseConst.FIELDS[2]));
        long size = cursor.getLong(cursor.getColumnIndex(DatabaseConst.FIELDS[3]));
        long completedBytes = cursor.getLong(cursor.getColumnIndex(DatabaseConst.FIELDS[4]));
        Log.d("DocumentParam", " comp = " + completedBytes + " size =  " + size);
        Status status = null;
        int progress = 0;
        if (completedBytes < size && completedBytes > 0) {
            status = Status.PAUSED;
            progress = (int)(completedBytes * 100 / size);
        }
        else if(completedBytes == 0) {
            status = Status.PENDING;
        }
        else if(completedBytes == size) {
            status = Status.FINISHED;
            progress = 100;
        }
        DownloadParam param = new DownloadParam(appName, url, completedBytes, size, status, progress);

        return apkCheck(null, param) ? param:null;
    }

    public static String apkPath(Context context, DownloadParam param) {

        return  DatabaseConst.SD_PATH + "/" + param.getName() + param.getExtension();
//        return  context.getCacheDir().getPath() + "/" + param.getName() + param.getExtension();
    }

    public static boolean apkCheck(Context context, DownloadParam param) {
        File target = new File(apkPath(context, param));
        if (! target.exists()) {
            Log.d("DownloadParam", param.getName() + " restore fail due to apk missing");
            return false;
        }
        if (target.length() != param.getCompletedBytes() || param.getCompletedBytes() > param.getSize()) {
            Log.d("DocumentParam", target.length() + " comp = " + param.getCompletedBytes() + " size =  " + param.getSize());
            Log.d("DownloadParam", param.getName() + " restore fail due to incorrect information");
            target.delete();
            return false;
        }
        return true;
    }

}
