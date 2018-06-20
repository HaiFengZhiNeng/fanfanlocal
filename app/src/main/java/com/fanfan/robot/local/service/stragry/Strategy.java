package com.fanfan.robot.local.service.stragry;

import com.fanfan.robot.local.app.NovelApp;

/**
 * Created by android on 2018/2/6.
 */

public class Strategy {

    protected boolean txtInTxt(String speakTxt, int res) {
        if (speakTxt.endsWith("。")) {
            speakTxt = speakTxt.substring(0, speakTxt.length() - 1);
        }
        return NovelApp.getInstance().getApplicationContext().getResources().getString(res).equals(speakTxt);
    }

}
