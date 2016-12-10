package com.tcl.demo.core;

/**
 * Created by shengyuan on 16-12-7.
 */

public interface DownloadStateListener {

    /**
     * update ui
     */
    void onStart();

    /**
     * update database
     * update ui
     */
    void onPause();

    void onConnectionLost(int taskIndex);

    /**
     * update ui
     */
    void onResume();

    /**
     * update database
     * update ui
     */
    void onCancel(int taskIndex);

    /**
     * update database
     * update ui
     */
    void onFinish(int taskIndex);

}
