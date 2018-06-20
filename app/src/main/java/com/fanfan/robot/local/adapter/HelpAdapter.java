package com.fanfan.robot.local.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.local.R;
import com.fanfan.robot.local.model.VoiceBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public class HelpAdapter extends BaseQuickAdapter<VoiceBean, BaseViewHolder> {

    public HelpAdapter(@Nullable List<VoiceBean> data) {
        super(R.layout.item_help_simple, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VoiceBean item) {
        helper.setText(R.id.tv_showtitle, item.getShowTitle());
    }

}
