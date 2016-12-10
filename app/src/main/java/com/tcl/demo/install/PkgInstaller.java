package com.tcl.demo.install;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import java.io.File;

/**
 * Created by shengyuan on 16-12-9.
 */

public class PkgInstaller {

    public static void installApk(Context context, String apkFile) {
        Log.d("PkgInstaller", apkFile);
        Uri uri = Uri.fromFile(new File(apkFile));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri,
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static String packageName(Context context, String apkFile) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkFile, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            return info.applicationInfo.packageName;
        }
        return null;
    }

    /**
     * todo: we should check the unInstallApk result and then return true or false.
     * @param context for startActivity.
     * @param packageName should be get from database.
     * @return boolean
     */
    public static boolean unInstallApk(Context context, String packageName) {

        if (! checkApplication(context, packageName)) {
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_DELETE);
        Uri uri = Uri.parse(packageName);
        intent.setData(uri);
        context.startActivity(intent);
        return true;
    }

    public static boolean checkApplication(Context context, String packageName) {

        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
