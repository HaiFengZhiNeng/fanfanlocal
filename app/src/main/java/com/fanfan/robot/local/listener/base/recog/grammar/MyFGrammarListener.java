package com.fanfan.robot.local.listener.base.recog.grammar;

import com.fanfan.robot.local.app.RobotInfo;
import com.robot.seabreeze.log.Log;

public class MyFGrammarListener implements FGrammarListener {

    @Override
    public void onGrammarBuildSuccess() {
        Log.i("完成构建");
    }

    @Override
    public void onCloudGrammarBuildSuccess() {
        Log.i("在线语法构建成功");
    }

    @Override
    public void onLocalGrammarBuildSuccess() {
        Log.i("本地语法构建成功");
        RobotInfo.getInstance().setInitialization(true);
    }

    @Override
    public void onGrammarBuildError(int errorCode, String errorDescription) {

        Log.i("构建语法失败,错误码 ：" + errorCode + " 错误详情 : " + errorDescription);
    }
}
