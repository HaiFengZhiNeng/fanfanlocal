package com.fanfan.robot.local.service.stragry.local;

import com.fanfan.robot.local.R;
import com.fanfan.robot.local.service.stragry.SpecialStrategy;
import com.fanfan.robot.local.service.stragry.SpecialType;
import com.fanfan.robot.local.service.stragry.Strategy;

/**
 * Created by android on 2018/2/6.
 */

public class HomeStrategy extends Strategy implements SpecialStrategy {

    @Override
    public SpecialType specialLocal(String speakTxt) {

        if (txtInTxt(speakTxt, R.string.HomePage)) {
            return SpecialType.HomePage;
        } else if (txtInTxt(speakTxt, R.string.ConsultingPosition)) {
            return SpecialType.ConsultingPosition;
        } else if (txtInTxt(speakTxt, R.string.MoreProblems)) {
            return SpecialType.MoreProblems;
        } else if (txtInTxt(speakTxt, R.string.SetUp)) {
            return SpecialType.SetUp;
        }
        return SpecialType.NoSpecial;
    }
}
