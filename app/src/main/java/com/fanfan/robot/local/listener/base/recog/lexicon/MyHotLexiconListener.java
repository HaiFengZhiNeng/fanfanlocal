package com.fanfan.robot.local.listener.base.recog.lexicon;

import com.robot.seabreeze.log.Log;

public class MyHotLexiconListener implements HotLexiconListener {
    @Override
    public void onCloudLexiconUpdatedSuccess() {
        Log.i("在线热词上传成功");
    }

    @Override
    public void onLocalLexiconUpdatedSuccess() {
        Log.i("本地热词上传成功");
    }

    @Override
    public void onLexiconUpdatedError(int errorCode, String errorDescription) {
        Log.i("词典更新失败,错误码 ：" + errorCode + " 错误详情 : " + errorDescription);
    }
}
