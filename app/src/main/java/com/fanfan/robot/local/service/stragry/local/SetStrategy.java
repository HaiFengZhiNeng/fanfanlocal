package com.fanfan.robot.local.service.stragry.local;

import com.fanfan.robot.local.R;
import com.fanfan.robot.local.service.stragry.SpecialStrategy;
import com.fanfan.robot.local.service.stragry.SpecialType;
import com.fanfan.robot.local.service.stragry.Strategy;

/**
 * Created by android on 2018/2/6.
 */

public class SetStrategy extends Strategy implements SpecialStrategy {

    @Override
    public SpecialType specialLocal(String speakTxt) {
        if (txtInTxt(speakTxt, R.string.LocalSpeech)) {
            return SpecialType.LocalSpeech;
        } else if (txtInTxt(speakTxt, R.string.LocalImport)) {
            return SpecialType.LocalImport;
        } else if (txtInTxt(speakTxt, R.string.FileImport)) {
            return SpecialType.FileImport;
        } else if (txtInTxt(speakTxt, R.string.SetPassword)) {
            return SpecialType.SetPassword;
        }
        return SpecialType.NoSpecial;
    }
}
