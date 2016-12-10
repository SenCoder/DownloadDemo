package com.tcl.demo.core;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.tcl.demo.database.DatabaseConst;
import com.tcl.demo.database.DatabaseHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * AsynccTask 适用于轻量级的异步处理。
 * 使用 AsyncTask 需要3个泛型参数，Params, Progress, Result.
 * Params 为启动任务需要的参数，比如Http请求的URL
 * Progress 为任务进度
 * Result 为任务返回结果
 *
 * 我们在设计的时候应该考虑到一点：
 * Status 是不希望别人修改的，因此不应该提供 public setStatus 方法.
 */

/**
 * 在下载的时候，我们需要考虑到连接异常断开
 * SocketTimeoutException
 */

public class DownloadTask {

    private DownloadParam mDownloadParam;
    private DownloadStateListener mDownloadStateListener;
    private DatabaseHandler mDatabaseHandler;
    private DownloadAsyncTask mDownloadAsyncTask;
    private DownloadManager mDownloadManager;

    private int progressPercent = 0;

    public DownloadTask(DownloadParam param, DownloadManager mgr) {

        mDownloadManager = mgr;
        mDownloadParam = param;
        mDownloadAsyncTask = new DownloadAsyncTask();
    }

    public DownloadStateListener getDownloadStateListener() {
        return mDownloadStateListener;
    }

    public DatabaseHandler getDatabaseHandler() {
        return mDatabaseHandler;
    }

    public void setDatabaseHandler(DatabaseHandler databaseHandler) {
        mDatabaseHandler = databaseHandler;
    }

    public void setDownloadStateListener(DownloadStateListener downloadStateListener) {
        mDownloadStateListener = downloadStateListener;
    }

    public DownloadParam getDownloadParam() {
        return mDownloadParam;
    }

    public void execute() {
        mDownloadAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void reExecute() {
        mDownloadAsyncTask = new DownloadAsyncTask();
        mDownloadAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mDownloadStateListener.onResume();
    }

    private class DownloadAsyncTask extends AsyncTask<DownloadParam, Long, String> {

        @Override
        protected String doInBackground(DownloadParam... downloadParams) {

            HttpURLConnection connection = null;
            File file = null;
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            long progressBytes = 0;

            try {
                URL url = new URL(mDownloadParam.getStrUrl());
                connection = (HttpURLConnection) url.openConnection();

                if (mDownloadParam.getStatus() == DownloadParam.Status.PAUSED) {
                    long startPos = mDownloadParam.getCompletedBytes();
                    long endPos = mDownloadParam.getSize();
                    connection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
                    progressBytes = startPos;
                    Log.d("doInBackground", "resume at " + startPos);
                }
                else {
                    mDownloadParam.setSize(connection.getContentLength());
                }

                connection.setConnectTimeout(2000);
                connection.setReadTimeout(5000);

                mDatabaseHandler.insertOrUpdateRecord(mDownloadParam);

                file = new File(DownloadParam.apkPath(mDownloadManager.getContext(), mDownloadParam));

                connection.connect();
                mDownloadParam.setStatus(DownloadParam.Status.RUNNING);

                int stateCode = connection.getResponseCode();
                if (stateCode >= 400) {
                    inputStream = connection.getErrorStream();
                }
                else {
                    Log.d("doInBackground", "state update to " + mDownloadParam.getStatus());
                    inputStream = connection.getInputStream();
                }

                outputStream = new FileOutputStream(file, true);
                byte[] buffer = new byte[4096];
                int readLen = 0;
                progressPercent = (int)(progressBytes * 100/mDownloadParam.getSize());

                while ((readLen = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, readLen);
                    progressBytes += readLen;
                    int currentProgressPercent = (int)(progressBytes * 100/mDownloadParam.getSize());
                    if (currentProgressPercent > progressPercent) {
                        progressPercent = currentProgressPercent;
                        publishProgress(progressBytes);
                    }
                    // if the pause button is pressed.
                    if (mDownloadParam.getStatus() == DownloadParam.Status.PAUSED) {
                        cancel(true);
                        mDownloadParam.setCompletedBytes(progressBytes);
                        Log.d("DownloadTask", "pause at " + progressBytes);
                        mDatabaseHandler.insertOrUpdateRecord(mDownloadParam);
                        mDownloadStateListener.onPause();
                        return null;
                    }
                }
                mDownloadParam.setStatus(DownloadParam.Status.FINISHED);
                Log.d("DownloadTask", "status update to finish");
                mDownloadParam.setCompletedBytes(progressBytes);
                mDatabaseHandler.insertOrUpdateRecord(mDownloadParam);
                mDatabaseHandler.addPackageName(mDownloadParam);
                outputStream.flush();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        // This method is called by UI thread before task.
        @Override
        protected void onPreExecute() {
            mDownloadStateListener.onStart();
            super.onPreExecute();
        }

        // This method is called by UI thread after task.
        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(mDatabaseHandler.getContext(), "task finish", Toast.LENGTH_SHORT).show();
            super.onPostExecute(s);
        }

        // This method is called by UI thread when publishProgress is called.
        @Override
        protected void onProgressUpdate(Long... values) {
            long value = values[0];
            if (mDownloadParam.getSize() == 0) {
                mDownloadParam.setProgress((int)value);
            }
            else {
                mDownloadParam.setProgress((int) (value * 100L/mDownloadParam.getSize()));
            }
            super.onProgressUpdate(values);
        }
    }


/*

    @Override
    protected String doInBackground(DownloadParam... downloadParams) {
        return simulateDownload();
    }

    private String simulateDownload() {
        NetOperator netOperator = new NetOperator();
        int i = 0;
        while (i < 100) {
            i += new Random(System.currentTimeMillis()).nextInt(20);
            netOperator.operator();
            Log.d("tag", "thread -" + Thread.currentThread().getName() + ":downloading " + (i < 100 ? i:100) + "%");
            // modify DownloadParam
            // 调用 publishProgress 更新 UI. 千万不要在这里直接更新 UI. 因为 doInBackground 由非UI线程调用。
            // publishProgress 的本质是调用 handler 发送消息
            // handler 处理消息时，onProgressUpdate() 主动调用 onProgressUpdate
            publishProgress(i < 100 ? i:100);
        }
        return i + "";
    }
*/

}
