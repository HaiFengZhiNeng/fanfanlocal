package com.fanfan.robot.local.listener.base.recog.grammar;

public interface FGrammarListener {

    void onGrammarBuildSuccess();

    void onCloudGrammarBuildSuccess();

    void onLocalGrammarBuildSuccess();

    void onGrammarBuildError(int errorCode, String errorDescription);

}
