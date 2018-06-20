package com.fanfan.robot.local.listener.base.synthesizer;

import com.fanfan.robot.local.R;
import com.fanfan.robot.local.app.NovelApp;
import com.robot.seabreeze.log.Log;

public class EarListener implements ISynthListener {

    protected static final String TAG = "EarListener ";

    @Override
    public void onSpeakBegin() {

        Log.i(TAG + "开始说话");
    }

    @Override
    public void onBufferProgress(int percent) {

        String format = String.format(NovelApp.getInstance().getString((R.string.tts_toast_format_buffer)), percent);
        Log.i(format);
    }

    @Override
    public void onSpeakPaused() {

        Log.i(TAG + "暂停播放");
    }

    @Override
    public void onSpeakResumed() {

        Log.i(TAG + "继续播放");
    }

    @Override
    public void onSpeakProgress(int percent) {

        String format = String.format(NovelApp.getInstance().getString((R.string.tts_toast_format_speak)), percent);
//        Log.i(TAG + format);
    }

    @Override
    public void onCompleted() {

        Log.i(TAG + "播放完成");
    }

    @Override
    public void onSpeakError(int errorCode, String errorDescription) {

        Log.i(TAG + "播放error speechError ： " + errorCode + " , message : " + errorDescription);
    }
}
