package com.fanfan.robot.local.listener.base.recog;

import android.os.Bundle;
import android.util.ArrayMap;

import com.fanfan.robot.local.listener.base.local.IRecogListener;
import com.fanfan.robot.local.model.local.Asr;
import com.fanfan.robot.local.model.local.Cw;
import com.fanfan.robot.local.model.local.Ws;
import com.fanfan.robot.local.utils.GsonUtil;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.robot.seabreeze.log.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecogEventAdapter implements RecognizerListener, NulState {

    private IRecogListener listener;

    private ArrayMap<Integer, String> mIatResults;

    private StringBuilder sbL1;
    private StringBuilder sbL2;
    private StringBuilder sbL3;
    private StringBuilder sbL4;

    public RecogEventAdapter(IRecogListener listener) {
        this.listener = listener;
        mIatResults = new ArrayMap<>();

        sbL1 = new StringBuilder();
        sbL2 = new StringBuilder();
        sbL3 = new StringBuilder();
        sbL4 = new StringBuilder();
    }


    @Override
    public void onVolumeChanged(int volume, byte[] bytes) {
        listener.onAsrVolume(bytes.length, volume);
    }

    @Override
    public void onBeginOfSpeech() {
        listener.onAsrBegin();
    }

    @Override
    public void onEndOfSpeech() {
        listener.onAsrEnd();
    }

    @Override
    public void onResult(RecognizerResult recognizerResult, boolean isLast) {

        Log.e("onResult ++ : " + recognizerResult.getResultString());

        Asr local = GsonUtil.GsonToBean(recognizerResult.getResultString(), Asr.class);

        if (local.getSc() > GrammerUtils.THRESHOLD) {

            sbL1.delete(0, sbL1.length());
            sbL2.delete(0, sbL2.length());
            sbL3.delete(0, sbL3.length());
            sbL4.delete(0, sbL4.length());

            List<Ws> wsList = local.getWs();
            for (int i = 0; i < wsList.size(); i++) {

                if (i == 0) {
                    getKeyword(wsList, i, sbL1);
                } else if (i == 1) {
                    getKeyword(wsList, i, sbL2);
                } else if (i == 2) {
                    getKeyword(wsList, i, sbL3);
                } else if (i == 3) {
                    getKeyword(wsList, i, sbL4);
                }

            }

            listener.onAsrLocalFinalResult(sbL1.toString(), sbL2.toString(), sbL3.toString(), sbL4.toString());
        } else {
            listener.onAsrLocalDegreeLow(local, local.getSc());
        }

    }

    private void getKeyword(List<Ws> wsList, int i, StringBuilder sb) {

        Ws ws = wsList.get(i);
        List<Cw> cwList = ws.getCw();

        Set<Cw> cwSet = new HashSet<>(cwList);

        for (Cw cw : cwSet) {
            if (cw.getSc() > GrammerUtils.THRESHOLD) {
                sb.append(cw.getW());
            }
        }
    }

    @Override
    public void onError(SpeechError speechError) {
        listener.onAsrFinishError(speechError.getErrorCode(), speechError.getErrorDescription());
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {
        Log.i("onEvent i : " + i + " , i1 : " + i1 + " , i2 : " + i2);
    }
}
