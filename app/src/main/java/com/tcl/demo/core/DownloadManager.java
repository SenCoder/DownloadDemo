package com.tcl.demo.core;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.tcl.demo.database.DatabaseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shengyuan on 16-12-5.
 */

public class DownloadManager {

    private List<DownloadTask> mDownloadTasks;
    // 所有 task 共享
    private ProgressListener mProgressListener;
    // 所有 task 共享
    private DownloadStateListener mDownloadStateListener;

    private DatabaseHandler mDatabaseHandler;

    private Context mContext;

    public DownloadManager(Context context) {

        mContext = context;
        mDownloadTasks = new ArrayList<>();
        mDatabaseHandler = DatabaseHandler.getInstance(context);
    }

    public void setProgressListener(ProgressListener progressListener) {
        mProgressListener = progressListener;
    }

    public void setDownloadStateListener(DownloadStateListener downloadStateListener) {
        mDownloadStateListener = downloadStateListener;
    }

    public List<DownloadTask> getDownloadTasks() {
        return mDownloadTasks;
    }

    public boolean addTask(DownloadTask downloadTask) {

        return downloadTask != null && mDownloadTasks.add(downloadTask);
    }

    public void startTask(int index) {
        DownloadTask task = mDownloadTasks.get(index);
        startTask(task);
    }

    public void startTask(DownloadTask task) {
        DownloadParam param = task.getDownloadParam();

        param.setProgressListener(mProgressListener);
        task.setDatabaseHandler(mDatabaseHandler);
        task.setDownloadStateListener(mDownloadStateListener);
        task.execute();
    }

    /**
     * when the user pause downloading.
     * @param index
     */
    public void pauseTask(int index) {
        DownloadParam param = mDownloadTasks.get(index).getDownloadParam();
        // 这里的状态设置必须在UI线程中
        param.setStatus(DownloadParam.Status.PAUSED);

    }

    /**
     * when the user pause downloading and continue.
     * @param index
     */
    public void resumeTask(int index) {

        DownloadTask task = mDownloadTasks.get(index);
        if (task.getDatabaseHandler() == null) {
            task.setDatabaseHandler(mDatabaseHandler);
        }
        if (task.getDownloadStateListener() == null) {
            task.setDownloadStateListener(mDownloadStateListener);
        }
        if (task.getDownloadParam().getProgressListener() == null) {
            task.getDownloadParam().setProgressListener(mProgressListener);
        }
        Log.d("DownloadManager", "resumeTask at " + task.getDownloadParam().getCompletedBytes());
        task.reExecute();
    }

    public void cancelTask(int index) {

        mDownloadStateListener.onCancel(index);
    }

    public boolean removeTask(DownloadTask downloadTask) {
        // delete task in database
        return mDownloadTasks.remove(downloadTask);
    }

    public void finishTask(int index) {

        DownloadParam param = mDownloadTasks.get(index).getDownloadParam();
        synchronized (param) {
            param.setStatus(DownloadParam.Status.FINISHED);
            mDatabaseHandler.insertOrUpdateRecord(param);
        }
        mDownloadStateListener.onFinish(index);
//        mDownloadTasks.remove(index);
    }

    public Context getContext() {
        return mContext;
    }
}
