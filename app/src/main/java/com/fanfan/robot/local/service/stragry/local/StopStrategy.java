package com.fanfan.robot.local.service.stragry.local;

import com.fanfan.robot.local.R;
import com.fanfan.robot.local.service.stragry.SpecialStrategy;
import com.fanfan.robot.local.service.stragry.SpecialType;
import com.fanfan.robot.local.service.stragry.Strategy;

/**
 * Created by android on 2018/2/6.
 */

public class StopStrategy extends Strategy implements SpecialStrategy {

    @Override
    public SpecialType specialLocal(String speakTxt) {
        return txtInTxt(speakTxt, R.string.StopListener) ? SpecialType.StopListener : SpecialType.NoSpecial;
    }
}
