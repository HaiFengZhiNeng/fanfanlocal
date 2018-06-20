package com.fanfan.robot.local.app;

import android.content.Context;

import com.fanfan.robot.local.app.common.Constants;
import com.fanfan.robot.local.utils.system.PreferencesUtils;
import com.iflytek.cloud.SpeechConstant;
import com.robot.seabreeze.log.Log;

/**
 * Created by android on 2018/1/5.
 */

public class RobotInfo {

    private volatile static RobotInfo instance;

    private RobotInfo() {
    }

    public static RobotInfo getInstance() {
        if (instance == null) {
            synchronized (RobotInfo.class) {
                if (instance == null) {
                    instance = new RobotInfo();
                }
            }
        }
        return instance;
    }

    public RobotInfo init(Context context) {
        setTtsLocalTalker(PreferencesUtils.getString(context, Constants.IAT_LOCAL_LANGUAGE_TALKER, "xiaoyan"));
        setIatLocalHear(PreferencesUtils.getString(context, Constants.IAT_LOCAL_LANGUAGE_HEAR, "xiaoyan"));
        setLineSpeed(PreferencesUtils.getInt(NovelApp.getInstance().getApplicationContext(), Constants.LINE_SPEED, 60));
        setLineVolume(PreferencesUtils.getInt(NovelApp.getInstance().getApplicationContext(), Constants.LINE_VOLUME, 100));
        isInitialization = PreferencesUtils.getBoolean(context, Constants.IS_INITIALIZATION, false);
        setPassword(PreferencesUtils.getString(context, Constants.PASSWORD, ""));
        return getInstance();
    }

    private String password;

    public void setPassword(String password) {
        this.password = password;
        PreferencesUtils.putString(NovelApp.getInstance().getApplicationContext(), Constants.PASSWORD, password);
    }

    public String getPassword() {
        return password;
    }

    //IAT_LOCAL_BUILD
    private boolean localBuild;

    public boolean isLocalBuild() {
        if (localBuild)
            return true;
        return PreferencesUtils.getBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_LOCAL_BUILD, false);
    }

    public void setLocalBuild() {
        Log.e("本地语法构建成功");
        this.localBuild = true;
        PreferencesUtils.putBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_LOCAL_BUILD, true);
    }

    //IAT_LOCAL_UPDATELEXICON
//    private boolean localUpdatelexicon;
//
//    public boolean isLocalUpdatelexicon() {
//        if (localUpdatelexicon)
//            return true;
//        return PreferencesUtils.getBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_LOCAL_UPDATELEXICON, false);
//    }
//
//    public void setLocalUpdatelexicon() {
//        Log.e("本地词典更新成功");
//        this.localUpdatelexicon = true;
//        PreferencesUtils.putBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IAT_LOCAL_UPDATELEXICON, true);
//    }

    //离线的发言人
    private String ttsLocalTalker;

    public String getTtsLocalTalker() {
        return ttsLocalTalker;
    }

    public void setTtsLocalTalker(String ttsLocalTalker) {
        this.ttsLocalTalker = ttsLocalTalker;
        PreferencesUtils.putString(NovelApp.getInstance().getApplicationContext(), Constants.IAT_LOCAL_LANGUAGE_TALKER, ttsLocalTalker);
    }

    //离线的监听人
    private String iatLocalHear;

    public String getIatLocalHear() {
        return iatLocalHear;
    }

    public void setIatLocalHear(String iatLocalHear) {
        this.iatLocalHear = iatLocalHear;
    }

    //在线语言（zh_cn， en_us）
    private String lineLanguage;

    public String getLineLanguage() {
        return lineLanguage;
    }

    public void setLineLanguage(String iatLineLanguage) {
        if (iatLineLanguage.equals("en_us")) {
            lineLanguage = "en_us";
            setTranslateEnable(true);
        } else {
            lineLanguage = "zh_cn";
            setTranslateEnable(false);
        }
    }

    //需要翻译
    private boolean translateEnable;

    public boolean isTranslateEnable() {
        return translateEnable;
    }

    public void setTranslateEnable(boolean translateEnable) {
        this.translateEnable = translateEnable;
    }

    //是否需要中转英
    private boolean queryLanage;

    public boolean isQueryLanage() {
        return queryLanage;
    }

    public void setQueryLanage(boolean queryLanage) {
        this.queryLanage = queryLanage;
        PreferencesUtils.getBoolean(NovelApp.getInstance(), Constants.QUERYLANAGE, queryLanage);
    }


    //是否已经构建语法，上传热词等
    private boolean isInitialization;

    public boolean isInitialization() {
        return isInitialization;
    }

    public void setInitialization(boolean initialization) {
        isInitialization = initialization;
        PreferencesUtils.putBoolean(NovelApp.getInstance().getApplicationContext(), Constants.IS_INITIALIZATION, isInitialization);
    }

    private int lineSpeed;

    public int getLineSpeed() {
        return lineSpeed;
    }

    public void setLineSpeed(int speed) {
        lineSpeed = speed;
        PreferencesUtils.putInt(NovelApp.getInstance().getApplicationContext(), Constants.LINE_SPEED, speed);
    }

    private int lineVolume;

    public int getLineVolume() {
        return lineVolume;
    }

    public void setLineVolume(int volume) {
        lineVolume = volume;
        PreferencesUtils.putInt(NovelApp.getInstance().getApplicationContext(), Constants.LINE_VOLUME, volume);
    }

}
