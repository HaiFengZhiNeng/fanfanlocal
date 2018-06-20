package com.fanfan.robot.local.listener.base.recog.grammar;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fanfan.robot.local.app.RobotInfo;
import com.fanfan.robot.local.app.common.Constants;
import com.fanfan.robot.local.listener.base.recog.GrammerUtils;
import com.fanfan.robot.local.listener.base.recog.lexicon.LexiconEventAdapter;
import com.fanfan.robot.local.listener.base.recog.lexicon.MyHotLexiconListener;
import com.fanfan.robot.local.utils.FucUtil;
import com.fanfan.robot.local.utils.system.AppUtil;
import com.fanfan.robot.local.utils.system.FileUtil;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.robot.seabreeze.log.Log;

import java.util.List;

import static com.fanfan.robot.local.listener.base.recog.GrammerUtils.STANDARD_TEXT_ENCODING;

public class GrammerEventManager implements GrammarListener {

//
//    private static final String GRAMMAR_BNF = "bnf";
//
//    public static final String LOCAL_GRAMMAR_NAME = "call";
//    private static final String GRAMMAR_LOCAL_FILE_NAME = "call.bnf";
//
//    public static final String STANDARD_TEXT_ENCODING = "utf-8";

    private Context context;
    private SpeechRecognizer recognizer;
    private FGrammarListener listener;

//    private LexiconEventAdapter adapter;

    public GrammerEventManager(Context context, SpeechRecognizer recognizer, final FGrammarListener listener) {
        this.context = context;
        this.recognizer = recognizer;
        this.listener = listener;
//        MyHotLexiconListener lexiconListener = new MyHotLexiconListener() {
//
//            @Override
//            public void onLocalLexiconUpdatedSuccess() {
//                super.onLocalLexiconUpdatedSuccess();
//                listener.onLocalGrammarBuildSuccess();
//
//            }
//        };
//        adapter = new LexiconEventAdapter(lexiconListener);
    }

    public void structure() {

        recognizer = GrammerUtils.getRecognizer(context, recognizer);

        String localGrammar = GrammerUtils.getLocalGrammar(context);

        StringBuilder sb = GrammerUtils.insertList(new StringBuilder(localGrammar), AppUtil.getLocalStrings(), GrammerUtils.getIndexL1(localGrammar));

        Log.e(sb.toString());
        recognizer.buildGrammar(GrammerUtils.GRAMMAR_BNF, sb.toString(), this);
    }

    @Override
    public void onBuildFinish(String s, SpeechError error) {
        if (error == null) {
//            if (RobotInfo.getInstance().isLocalUpdatelexicon()) {
//                RobotInfo.getInstance().setLocalBuild();
//                listener.onLocalGrammarBuildSuccess();
//            } else {
//                String lexiconContents = AppUtil.words2Contents();
//                updateLocation("local1", lexiconContents);
//            }
            listener.onLocalGrammarBuildSuccess();
        } else {
            listener.onGrammarBuildError(error.getErrorCode(), error.getErrorDescription());
        }
    }

    /**
     * 本项目暂不需要词典更新
     */
//    private void updateLocation(String lexiconName, String lexiconContents) {
//        recognizer.setParameter(SpeechConstant.PARAMS, null);
//        recognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
//        recognizer.setParameter(ResourceUtil.ASR_RES_PATH, FucUtil.getResAsrPath(context));
//        recognizer.setParameter(ResourceUtil.GRM_BUILD_PATH, Constants.GRM_PATH);
//        recognizer.setParameter(SpeechConstant.GRAMMAR_LIST, LOCAL_GRAMMAR_NAME);
//        recognizer.setParameter(SpeechConstant.TEXT_ENCODING, STANDARD_TEXT_ENCODING);
//        recognizer.updateLexicon(lexiconName, lexiconContents, adapter);
//    }
}
