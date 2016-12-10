package com.tcl.demo.database;

import android.os.Environment;

/**
 * Created by shengyuan on 16-12-8.
 */

public class DatabaseConst {

    public static final String[] FIELDS = {
            "Id",
            "AppName",
            "Url",
            "Size",
            "CompletedBytes",
            "PackageName",
    };

    public static final String SD_PATH = Environment.getExternalStorageDirectory().getPath();

}
