package com.fanfan.robot.local.app;

import com.fanfan.robot.local.BuildConfig;
import com.fanfan.robot.local.R;
import com.fanfan.robot.local.app.RobotInfo;
import com.fanfan.robot.local.app.common.Constants;
import com.fanfan.robot.local.app.common.base.BaseApplication;
import com.fanfan.robot.local.app.common.lifecycle.Foreground;
import com.fanfan.robot.local.db.base.BaseManager;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.robot.seabreeze.log.Log;
import com.robot.seabreeze.log.inner.ConsoleTree;
import com.robot.seabreeze.log.inner.FileTree;
import com.robot.seabreeze.log.inner.LogcatTree;
import com.squareup.leakcanary.LeakCanary;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class NovelApp extends BaseApplication {

    private static NovelApp instance;

    public static NovelApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initLogger(this);
        Foreground.init(this);

        //初始化数据库
        BaseManager.initOpenHelper(this);
        //初始化讯飞
        initXf();
        //初始化本机
        RobotInfo.getInstance().init(this);
        initOkGo();

        //内存泄漏检测
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
    }


    private void initLogger(NovelApp context) {
        if (BuildConfig.DEBUG) {
            Log.getLogConfig().configAllowLog(true).configShowBorders(false);
            Log.plant(new FileTree(context, Constants.PRINT_LOG_PATH));
            Log.plant(new ConsoleTree());
            Log.plant(new LogcatTree());
        }
    }

    private void initXf() {
        String param = "appid=" +
                getString(R.string.app_id) +
                "," +
                // 设置使用v5+
                SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC;
        SpeechUtility.createUtility(this, param);
    }

    private void initOkGo() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //超时时间设置，默认60秒
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //全局的连接超时时间
        OkGo.getInstance().init(this)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
        ;
    }
}
