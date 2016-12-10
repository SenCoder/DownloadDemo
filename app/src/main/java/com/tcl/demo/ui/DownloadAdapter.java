package com.tcl.demo.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tcl.asynctaskdemo.R;
import com.tcl.demo.core.DownloadManager;
import com.tcl.demo.core.DownloadParam;
import com.tcl.demo.core.DownloadTask;
import com.tcl.demo.install.PkgInstaller;

import java.util.List;

/**
 * Created by shengyuan on 16-12-5.
 */

public class DownloadAdapter extends BaseAdapter {

    private Context mContext;
    private DownloadManager mDownloadManager;
    private List<DownloadTask> mDownloadTasks;

    public DownloadAdapter(Context context,
                           DownloadManager downloadManager) {
        mContext = context;
        mDownloadManager = downloadManager;
        mDownloadTasks = downloadManager.getDownloadTasks();
    }

    static class ViewHolder
    {
        public Button mButton;
        public ProgressBar mProgressBar;
        public TextView mTextView;
    }

    @Override
    public int getCount() {

        return mDownloadTasks.size();
    }

    @Override
    public Object getItem(int i) {

        return mDownloadTasks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        final DownloadParam param = mDownloadTasks.get(i).getDownloadParam();
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_download, null);
            viewHolder.mTextView = (TextView) view.findViewById(R.id.task_name);
            viewHolder.mButton = (Button) view.findViewById(R.id.btn_download_ctrl);
            viewHolder.mProgressBar= (ProgressBar) view.findViewById(R.id.progress_download);
            view.setTag(viewHolder);

            if (param.getSize() == param.getCompletedBytes() && param.getCompletedBytes() > 0) {
                viewHolder.mButton.setText("Install");
            }
            else {
                viewHolder.mButton.setText("Download");
            }

            viewHolder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    switch (param.getStatus()) {
                        case PENDING:
                            mDownloadManager.startTask(i);
                            viewHolder.mButton.setText("Pause");
                            break;
                        case RUNNING:
                            mDownloadManager.pauseTask(i);
                            viewHolder.mButton.setText("Download");
                            break;
                        case PAUSED:
                            mDownloadManager.resumeTask(i);
                            viewHolder.mButton.setText("Pause");
                            break;
                        case FINISHED:
                            // todo: text on the button does change before the installation begins.
                            PkgInstaller.installApk(mContext, DownloadParam.apkPath(mContext, param));
                            viewHolder.mButton.setText("Uninstall");
                            break;
                        case INSTALLED:
                            // todo: package name should be restored from database.
                            PkgInstaller.unInstallApk(mContext, PkgInstaller.packageName(mContext, DownloadParam.apkPath(mContext, param)));
                            viewHolder.mButton.setText("Download");
                            break;
                    }
                }
            });
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (param.getStatus() == DownloadParam.Status.FINISHED) {
            viewHolder.mButton.setText("Install");
        }
        viewHolder.mTextView.setText(param.getName() + " " + param.getProgress() + "%");
        viewHolder.mProgressBar.setProgress(param.getProgress());

        return view;
    }
}
