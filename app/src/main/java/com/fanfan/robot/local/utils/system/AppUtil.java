package com.fanfan.robot.local.utils.system;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.fanfan.robot.local.R;
import com.fanfan.robot.local.app.NovelApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppUtil {

    public static String words2Contents() {
        StringBuilder sb = new StringBuilder();
        List<String> words = getLocalStrings();
        for (String anArrStandard : words) {
            sb.append(anArrStandard).append("\n");
        }
        return sb.toString();
    }

    @NonNull
    public static List<String> getLocalStrings() {

        List<String> words = new ArrayList<>();
        words.add(NovelApp.getInstance().getApplicationContext().getResources().getString(R.string.HomePage));
        words.add(NovelApp.getInstance().getApplicationContext().getResources().getString(R.string.ConsultingPosition));
        words.add(NovelApp.getInstance().getApplicationContext().getResources().getString(R.string.MoreProblems));
        words.add(NovelApp.getInstance().getApplicationContext().getResources().getString(R.string.SetUp));
        words.add(NovelApp.getInstance().getApplicationContext().getResources().getString(R.string.Determine));
        words.add(NovelApp.getInstance().getApplicationContext().getResources().getString(R.string.Cancel));
        words.add(NovelApp.getInstance().getApplicationContext().getResources().getString(R.string.LocalSpeech));
        words.add(NovelApp.getInstance().getApplicationContext().getResources().getString(R.string.LocalImport));
        words.add(NovelApp.getInstance().getApplicationContext().getResources().getString(R.string.FileImport));
        words.add(NovelApp.getInstance().getApplicationContext().getResources().getString(R.string.SetPassword));
        words.add(NovelApp.getInstance().getApplicationContext().getResources().getString(R.string.Back));
        words.add(NovelApp.getInstance().getApplicationContext().getResources().getString(R.string.StopListener));
        return words;
    }

    /**
     * 获取程序版本号
     *
     * @param context 上下文
     * @return 版本号
     */
    public static int getVersionCode(Context context) {
        int versionCode = -1;
        String pkName = context.getPackageName();
        try {
            versionCode = context.getPackageManager().getPackageInfo(pkName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionCode;
    }

    /**
     * 普通安装
     *
     * @param context
     * @param apkFile
     */
    public static void installNormal(Context context, File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //版本在7.0以上是不能直接通过uri访问的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            String authority = getPackageName(context) + ".fileprovider";
            Uri apkUri = FileProvider.getUriForFile(context, authority, apkFile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    /**
     * 获取当前程序包名
     *
     * @param context 上下文
     * @return 程序包名
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * 获取程序版本信息
     *
     * @param context 上下文
     * @return 版本名称
     */
    public static String getVersionName(Context context) {
        String versionName = null;
        String pkName = context.getPackageName();
        try {
            versionName = context.getPackageManager().getPackageInfo(pkName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

}
