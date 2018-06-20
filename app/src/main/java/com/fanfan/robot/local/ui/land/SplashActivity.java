package com.fanfan.robot.local.ui.land;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fanfan.robot.local.app.RobotInfo;
import com.fanfan.robot.local.app.common.base.BaseActivity;
import com.fanfan.robot.local.service.SerialService;
import com.fanfan.robot.local.ui.main.MainActivity;
import com.fanfan.robot.local.R;
import com.fanfan.robot.local.app.common.Constants;
import com.fanfan.robot.local.app.common.base.BaseHandler;
import com.fanfan.robot.local.utils.bitmap.ImageLoader;
import com.fanfan.robot.local.utils.permiss.HiPermission;
import com.fanfan.robot.local.utils.permiss.PermissionCallback;
import com.robot.seabreeze.log.Log;

import java.lang.reflect.Method;


/**
 * 闪屏
 */
public class SplashActivity extends BaseActivity implements BaseHandler.HandleMessage {


    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    //权限
    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数
    private static final String PACKAGE_URL_SCHEME = "package:"; // 方案

    //handler
    private static final int REFRESH_COMPLETE = 0X153;

    //标志位确定SDK是否初始化，避免客户SDK未初始化的情况，实现可重入的init操作
    private static boolean isSDKInit = false;

    private Handler handler = new BaseHandler<>(SplashActivity.this);

    private static final int OVERLAY_PERMISSION_REQ_CODE = 600;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void initData() {
        ImageView ivSplash = (ImageView) findViewById(R.id.iv_splash);


        ImageLoader.loadImage(this, ivSplash, R.mipmap.splash_bg, false, 1000);

        @SuppressLint("WrongConstant") WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        Constants.displayWidth = wm.getDefaultDisplay().getWidth();
        Constants.displayHeight = wm.getDefaultDisplay().getHeight();

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）
        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        Log.e("屏幕宽度 width : " + width);
        Log.e("屏幕高度 height : " + height);
        Log.e("屏幕密度 density : " + density);
        Log.e("屏幕密度DPI densityDpi : " + densityDpi);

//        if (!Settings.canDrawOverlays(this)) {
//            showToast("当前需开启悬浮框权限，请授权！");
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//            intent.setData(Uri.parse("package:" + getPackageName()));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
//        } else {
            checkPermissions();
//        }
    }

    private void checkPermissions() {
        HiPermission.create(this)
                .checkMutiPermission(PERMISSIONS, new PermissionCallback() {
                    @Override
                    public void onClose() {
                        showMissingPermissionDialog();
                    }

                    @Override
                    public void onFinish() {
                        init();
                    }

                    @Override
                    public void onDeny(String permission, int position) {
                        Log.e("onDeny");
                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        Log.e("onGuarantee");
                    }
                });

    }

    @Override
    protected void setListener() {

    }

    private void init() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navToHome();
            }
        }, 1300);
    }

    // 显示缺失权限提示
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle(R.string.help);
        builder.setMessage(R.string.string_help_text);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
            }
        });

        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });

        builder.show();
    }

    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }


    public void navToHome() {

        startService(new Intent(SplashActivity.this, SerialService.class));
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case REFRESH_COMPLETE:
                finish();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                showToast("授权后请重启应用，否则无法开启悬浮窗");
            } else {
                showToast("权限授予成功！");
                checkPermissions();
            }
        }
    }
}
