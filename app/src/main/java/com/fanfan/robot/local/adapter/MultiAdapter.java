package com.fanfan.robot.local.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fanfan.robot.local.R;
import com.fanfan.robot.local.model.Business;
import com.fanfan.robot.local.model.LocalBean;
import com.fanfan.robot.local.model.MultiBean;
import com.fanfan.robot.local.model.VoiceBean;

import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public class MultiAdapter extends BaseMultiItemQuickAdapter<MultiBean, BaseViewHolder> {

    public MultiAdapter(List<MultiBean> data) {
        super(data);
        addItemType(MultiBean.TYPE_VOICE, R.layout.item_voice_simple);
        addItemType(MultiBean.TYPE_LOCAL_DETAIL, R.layout.item_local_simple);
        addItemType(MultiBean.TYPE_LOCAL_TYPE, R.layout.item_local_type);
    }


    @Override
    protected void convert(BaseViewHolder helper, MultiBean item) {
        switch (helper.getItemViewType()) {
            case MultiBean.TYPE_VOICE:
                VoiceBean voiceBean = item.getVoiceBean();
                if (voiceBean != null) {
                    helper.setText(R.id.tv_showtitle, voiceBean.getShowTitle());
                }
                break;
            case MultiBean.TYPE_LOCAL_DETAIL:
                LocalBean localBean = item.getLocalBean();
                if (localBean != null) {
                    helper.setText(R.id.tv_showtitle, localBean.getShowTitle());
                    helper.setText(R.id.tv_showdetail, localBean.getShowDetail());
                }
                break;
            case MultiBean.TYPE_LOCAL_TYPE:
                Business business = item.getBusiness();
                if (business != null) {
                    helper.setText(R.id.tvTitle, business.getBusiness());
                    ImageView iv = helper.getView(R.id.im_tiwenrw);
                    int type = business.getType();
                    if (type == 1) {
                        iv.setImageResource(R.mipmap.ic_population);
                    } else if (type == 2) {
                        iv.setImageResource(R.mipmap.ic_intersection);
                    } else if (type == 3) {
                        iv.setImageResource(R.mipmap.ic_entryexit);
                    } else {
                        iv.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    public void addData(VoiceBean voiceBean) {
        MultiBean data = new MultiBean();
        data.setItemtype(MultiBean.TYPE_VOICE);
        data.setVoiceBean(voiceBean);
        addData(data);
    }

    public void addData(LocalBean localBean) {
        MultiBean data = new MultiBean();
        data.setItemtype(MultiBean.TYPE_LOCAL_DETAIL);
        data.setLocalBean(localBean);
        addData(data);
    }

    public void addData(int type) {
        String business = "";
        if (type == 1) {
            business = "人口户政";
        } else if (type == 2) {
            business = "交管";
        } else if (type == 3) {
            business = "出入境";
        } else {
        }
        Business b = new Business(business, type);
        MultiBean data = new MultiBean();
        data.setItemtype(MultiBean.TYPE_LOCAL_TYPE);
        data.setBusiness(b);
        addData(data);
    }

}
