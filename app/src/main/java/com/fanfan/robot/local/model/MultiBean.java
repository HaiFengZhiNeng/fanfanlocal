package com.fanfan.robot.local.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * Created by android on 2018/2/23.
 */

public class MultiBean implements Serializable, MultiItemEntity {

    public static final int TYPE_VOICE = 1;
    public static final int TYPE_LOCAL_DETAIL = 2;
    public static final int TYPE_LOCAL_TYPE = 3;

    private int itemtype;

    private LocalBean localBean;
    private VoiceBean voiceBean;
    private Business business;

    @Override
    public int getItemType() {
        return itemtype;
    }

    public void setItemtype(int itemtype) {
        this.itemtype = itemtype;
    }

    public LocalBean getLocalBean() {
        return localBean;
    }

    public void setLocalBean(LocalBean localBean) {
        this.localBean = localBean;
    }

    public VoiceBean getVoiceBean() {
        return voiceBean;
    }

    public void setVoiceBean(VoiceBean voiceBean) {
        this.voiceBean = voiceBean;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }
}
