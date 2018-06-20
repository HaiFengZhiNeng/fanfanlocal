package com.fanfan.robot.local.model;

import com.fanfan.robot.local.model.base.BaseItemData;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by android on 2017/12/20.
 */
@Entity
public class VoiceBean extends BaseItemData {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "saveTime")
    private long saveTime;
    @Property(nameInDb = "showTitle")
    private String showTitle;
    @Property(nameInDb = "voiceanswer")
    private String voiceAnswer;
    @Property(nameInDb = "keyword")
    private String keyword;
    @Property(nameInDb = "type")
    private int type;
    @Property(nameInDb = "business")
    private String business;
    @Property(nameInDb = "key1")
    private String key1;
    @Property(nameInDb = "key2")
    private String key2;
    @Property(nameInDb = "key3")
    private String key3;
    @Property(nameInDb = "key4")
    private String key4;

    @Generated(hash = 976608132)
    public VoiceBean(Long id, long saveTime, String showTitle, String voiceAnswer,
                     String keyword, int type, String business, String key1, String key2,
                     String key3, String key4) {
        this.id = id;
        this.saveTime = saveTime;
        this.showTitle = showTitle;
        this.voiceAnswer = voiceAnswer;
        this.keyword = keyword;
        this.type = type;
        this.business = business;
        this.key1 = key1;
        this.key2 = key2;
        this.key3 = key3;
        this.key4 = key4;
    }

    @Generated(hash = 1719036352)
    public VoiceBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getSaveTime() {
        return this.saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public String getShowTitle() {
        return this.showTitle;
    }

    public void setShowTitle(String showTitle) {
        this.showTitle = showTitle;
    }

    public String getVoiceAnswer() {
        return this.voiceAnswer;
    }

    public void setVoiceAnswer(String voiceAnswer) {
        this.voiceAnswer = voiceAnswer;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBusiness() {
        return this.business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getKey1() {
        return this.key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getKey2() {
        return this.key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }

    public String getKey3() {
        return this.key3;
    }

    public void setKey3(String key3) {
        this.key3 = key3;
    }

    public String getKey4() {
        return this.key4;
    }

    public void setKey4(String key4) {
        this.key4 = key4;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VoiceBean) {
            VoiceBean voiceBean = (VoiceBean) obj;
            return voiceBean.showTitle.equals(this.getShowTitle());
        }
        return false;
    }
}
