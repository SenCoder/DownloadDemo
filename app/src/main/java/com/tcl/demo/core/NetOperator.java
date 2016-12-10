package com.tcl.demo.core;

/**
 * Created by shengyuan on 16-12-5.
 */

public class NetOperator {

    // the background task in task.
    public void operator() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
