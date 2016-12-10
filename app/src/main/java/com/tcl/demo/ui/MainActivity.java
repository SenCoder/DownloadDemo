package com.tcl.demo.ui;

import android.app.Activity;
import android.os.Bundle;

import com.tcl.asynctaskdemo.R;

public class MainActivity extends Activity {

    private DownloadFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        initView();
    }

    private void initView() {

        mFragment = (DownloadFragment) getFragmentManager().findFragmentById(R.id.fragment_download);

    }

}
