package com.fanfan.robot.local.listener.base.local;

import com.fanfan.robot.local.model.local.Asr;
import com.fanfan.robot.local.model.local.Trans;

public interface IRecogListener {

    void onAsrBegin();

    void onAsrPartialResult(Asr recogResult, String results[]);

    void onAsrOnlineNluResult(int type, String nluResult);

    void onAsrFinalResult(String result);

    void onAsrEnd();

    void onAsrVolume(int volumePercent, int volume);

    void onAsrFinishError(int errorCode, String errorMessage);

    void onTrans(Trans trans);

    void onAsrLocalFinalResult(String key1, String key2, String key3, String key4);

    void onAsrLocalDegreeLow(Asr local, int degree);

    void onAsrTranslateError(int errorCode);
}
