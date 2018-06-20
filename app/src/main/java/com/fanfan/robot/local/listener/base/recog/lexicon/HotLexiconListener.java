package com.fanfan.robot.local.listener.base.recog.lexicon;

public interface HotLexiconListener {

    void onCloudLexiconUpdatedSuccess();

    void onLocalLexiconUpdatedSuccess();

    void onLexiconUpdatedError(int errorCode, String errorDescription);
}
