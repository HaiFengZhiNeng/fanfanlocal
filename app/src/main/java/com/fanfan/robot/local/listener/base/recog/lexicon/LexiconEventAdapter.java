package com.fanfan.robot.local.listener.base.recog.lexicon;

import com.fanfan.robot.local.app.RobotInfo;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.robot.seabreeze.log.Log;

public class LexiconEventAdapter implements LexiconListener {

    private HotLexiconListener listener;

    public LexiconEventAdapter(HotLexiconListener listener) {
        this.listener = listener;
    }

    @Override
    public void onLexiconUpdated(String s, SpeechError error) {
        if (error == null) {

            listener.onLocalLexiconUpdatedSuccess();
        } else {
            Log.e("词典更新失败,错误码：" + error.getErrorCode() + error.getErrorDescription());
            listener.onLexiconUpdatedError(error.getErrorCode(), error.getErrorDescription());
        }
    }
}
