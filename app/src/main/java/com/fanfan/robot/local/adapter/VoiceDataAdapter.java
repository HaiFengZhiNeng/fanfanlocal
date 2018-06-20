package com.fanfan.robot.local.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.local.R;
import com.fanfan.robot.local.model.VoiceBean;
import com.fanfan.robot.local.utils.TimeUtils;
import com.fanfan.robot.local.utils.bitmap.ImageLoader;

import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public class VoiceDataAdapter extends BaseQuickAdapter<VoiceBean, BaseViewHolder> {

    public VoiceDataAdapter(@Nullable List<VoiceBean> data) {
        super(R.layout.item_voice_data, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VoiceBean item) {
        helper.setText(R.id.show_title, item.getShowTitle());
        helper.setText(R.id.save_time, TimeUtils.getShortTime(item.getSaveTime()));
        helper.setText(R.id.voice_answer, item.getVoiceAnswer());
    }
}
