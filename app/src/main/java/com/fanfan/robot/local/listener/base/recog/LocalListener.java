package com.fanfan.robot.local.listener.base.recog;

import com.fanfan.robot.local.listener.base.local.IRecogListener;
import com.fanfan.robot.local.model.local.Asr;
import com.fanfan.robot.local.model.local.Trans;
import com.robot.seabreeze.log.Log;

public class LocalListener implements IRecogListener, NulState {


    protected static final String TAG = "LocalListener ";

    private long speechEndTime;

    @Override
    public void onAsrBegin() {
        speechEndTime = System.currentTimeMillis();
        Log.e(TAG + "监听已启动，检测用户说话");
    }

    @Override
    public void onAsrPartialResult(Asr recogResult, String[] results) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < results.length; i++) {
            buffer.append(results[i]);
        }
        Log.i(TAG + "临时识别结果，结果是“" + buffer.toString() + "” , sc : " + recogResult.getSc() + " , ls" + recogResult.getLs());
        Log.i(recogResult);
    }

    @Override
    public void onAsrOnlineNluResult(int type, String nluResult) {
        if (nluResult != null) {
            Log.i(TAG + "原始语义识别 type " + type + " , 结果json：" + nluResult);
        }
    }

    @Override
    public void onAsrFinalResult(String result) {
        Log.i(TAG + "识别结束，结果是“" + result + "”");
        long diffTime = System.currentTimeMillis() - speechEndTime;
        Log.i(TAG + "说话结束到识别结束耗时【" + diffTime + "ms】");
    }

    @Override
    public void onAsrEnd() {
        Log.i(TAG + "检测到用户说话结束");
    }

    @Override
    public void onAsrVolume(int volumePercent, int volume) {
//        Log.i("用户说话音量 ： volumePercent ： " + volumePercent + " , volume : " + volume);
    }

    @Override
    public void onAsrFinishError(int errorCode, String errorMessage) {
        Log.i("errorCode : " + errorCode + " , errorMessage : " + errorMessage);
    }

    @Override
    public void onTrans(Trans trans) {

    }

    @Override
    public void onAsrLocalFinalResult(String key1, String key2, String key3, String key4) {
        Log.e("本地语音识别结果 ： (" + key1 + ")  (" + key2 + ")  (" + key3 + ")  (" + key4 + ")");
    }

    @Override
    public void onAsrLocalDegreeLow(Asr local, int degree) {

        Log.i("本地识别置信度小 degree ： " + degree + " , local" + local);
    }

    @Override
    public void onAsrTranslateError(int errorCode) {
        Log.i("翻译出错 errorCode : " + errorCode);
        onAsrFinishError(errorCode, "translate error");
    }
}
