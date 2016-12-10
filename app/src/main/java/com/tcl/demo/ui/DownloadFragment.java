package com.tcl.demo.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tcl.demo.core.DownloadTask;
import com.tcl.demo.core.DownloadManager;
import com.tcl.asynctaskdemo.R;
import com.tcl.demo.core.DownloadParam;
import com.tcl.demo.core.DownloadStateListener;
import com.tcl.demo.core.ProgressListener;
import com.tcl.demo.database.DatabaseHandler;
import com.tcl.demo.install.PkgInstaller;

/**
 * Created by shengyuan on 16-12-5.
 */

public class DownloadFragment extends Fragment {

    private ListView mListView;
    private DownloadAdapter mAdapter;
    private Context mContext;

    private DownloadManager mDownloadManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_download, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        mContext = getActivity();
        mDownloadManager = new DownloadManager(mContext);
        mAdapter = new DownloadAdapter(mContext, mDownloadManager);
        mListView = (ListView) view.findViewById(R.id.listview_download);
        mListView.setAdapter(mAdapter);

    }

    @Override
    public void onResume() {
        demo();
        super.onResume();
    }

    public void demo() {
        mDownloadManager.setProgressListener(new ProgressListener() {
            @Override
            public void onProgressUpdate() {
                mAdapter.notifyDataSetChanged();
            }
        });
        mDownloadManager.setDownloadStateListener(new DownloadStateListener() {
            @Override
            public void onStart() {
                Log.d("tag", "task onStart");
            }

            @Override
            public void onPause() {
                Log.d("tag", "task onPause");
            }

            @Override
            public void onConnectionLost(int taskIndex) {
                Log.d("tag", "task connection lost");
            }

            @Override
            public void onResume() {
                Log.d("tag", "task onResume");
            }

            @Override
            public void onCancel(int taskIndex) {
                Log.d("tag", "task onCancel");
            }

            @Override
            public void onFinish(int taskIndex) {
                Log.d("tag", "task onFinish");
            }
        });

        DownloadParam param = DatabaseHandler.getInstance(mContext).restoreTask("搜狗拼音");
        if (param == null) {
            param = new DownloadParam("搜狗拼音",
                "http://gdown.baidu.com/data/wisegame/5d43d88e91997402/sougoushurufa_610.apk",
                0);
        }
        mDownloadManager.addTask(new DownloadTask(param, mDownloadManager));

        param = DatabaseHandler.getInstance(mContext).restoreTask("爱奇艺");
        if (param == null) {
            param = new DownloadParam("爱奇艺",
                    "http://gdown.baidu.com/data/wisegame/2c5bd3160eb3dd4d/aiqiyi_80810.apk",
                    0);
        }
        mDownloadManager.addTask(new DownloadTask(param, mDownloadManager));

        param = DatabaseHandler.getInstance(mContext).restoreTask("ES文件浏览器");
        if (param == null) {
            param = new DownloadParam("ES文件浏览器",
                    "http://120.198.236.3/gdown.baidu.com/data/wisegame/7b9c96cc82a8dda4/ESFileExplorer_552.apk",
                    0);
        }
        mDownloadManager.addTask(new DownloadTask(param, mDownloadManager));

    }

}
