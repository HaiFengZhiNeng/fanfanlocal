package com.fanfan.robot.local.service.stragry.local;

import com.fanfan.robot.local.R;
import com.fanfan.robot.local.service.stragry.SpecialStrategy;
import com.fanfan.robot.local.service.stragry.SpecialType;
import com.fanfan.robot.local.service.stragry.Strategy;

public class SureStrategy extends Strategy implements SpecialStrategy {

    @Override
    public SpecialType specialLocal(String speakTxt) {
        if (txtInTxt(speakTxt, R.string.Determine)) {
            return SpecialType.Determine;
        } else if (txtInTxt(speakTxt, R.string.Cancel)) {
            return SpecialType.Cancel;
        }
        return SpecialType.NoSpecial;
    }

}
