package com.fanfan.robot.local.model;

import com.fanfan.robot.local.model.base.BaseItemData;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class LocalBean extends BaseItemData {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "saveTime")
    private long saveTime;
    @Property(nameInDb = "showTitle")
    private String showTitle;
    @Property(nameInDb = "showDetail")
    private String showDetail;
    @Property(nameInDb = "lat")
    private double lat;
    @Property(nameInDb = "lng")
    private double lng;
    @Property(nameInDb = "telephone")
    private String telephone;
    @Property(nameInDb = "type")
    private int type;
    @Property(nameInDb = "business")
    private String business;
    @Generated(hash = 1858052248)
    public LocalBean(Long id, long saveTime, String showTitle, String showDetail,
            double lat, double lng, String telephone, int type, String business) {
        this.id = id;
        this.saveTime = saveTime;
        this.showTitle = showTitle;
        this.showDetail = showDetail;
        this.lat = lat;
        this.lng = lng;
        this.telephone = telephone;
        this.type = type;
        this.business = business;
    }
    @Generated(hash = 140313795)
    public LocalBean() {
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
    public String getShowDetail() {
        return this.showDetail;
    }
    public void setShowDetail(String showDetail) {
        this.showDetail = showDetail;
    }
    public double getLat() {
        return this.lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLng() {
        return this.lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
    public String getTelephone() {
        return this.telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

}
