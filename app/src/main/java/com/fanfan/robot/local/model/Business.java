package com.fanfan.robot.local.model;

public class Business {

    private String business;
    private int type;

    public Business(String business, int type) {
        this.business = business;
        this.type = type;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Business) {
            Business business = (Business) obj;
            return business.type == this.type && business.business.equals(this.business);
        }
        return false;
    }
}
