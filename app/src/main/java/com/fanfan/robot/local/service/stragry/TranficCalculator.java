package com.fanfan.robot.local.service.stragry;


/**
 * Created by android on 2018/2/6.
 */

public class TranficCalculator {

    public SpecialType specialLocal(String speakTxt, SpecialStrategy mStrategy) {
        return mStrategy.specialLocal(speakTxt);
    }
}
