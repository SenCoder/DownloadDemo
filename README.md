# DownloadDemo
App download demo with AsnycTask on Android Studio.

## OverView

![image](https://github.com/SenCoder/DownloadDemo/blob/master/states.png)

```java
public enum Status {
    /* before start */
    PENDING,
    /* downloading */
    RUNNING,
    /* pause button press */
    PAUSED,
    /* finish downloading */
    FINISHED,
    /* apk installed */
    INSTALLED,
}
```

## Usage
We need to include these permissions in the AndroidManifest.xml.

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

## Let's get started

```java
DownloadManager dm = new DownloadManager(getContext());
dm.setProgressListener(new ProgressListener() {
    @Override
    public void onProgressUpdate() {
        mAdapter.notifyDataSetChanged();
    }
});

dm.setDownloadStateListener(new DownloadStateListener() {
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
```

## Add download task

```java
DownloadParam param = DatabaseHandler.getInstance(mContext).restoreTask("搜狗拼音");
if (param == null) {
    param = new DownloadParam("搜狗拼音",
        "http://gdown.baidu.com/data/wisegame/5d43d88e91997402/sougoushurufa_610.apk",
        0);
}
dm.addTask(new DownloadTask(param, dm));
```
## Start downloading and control
```java
// start
dm.startTask(i);
// pause
dm.pauseTask(i);
// restart after pause
dm.resumeTask(i);
```
