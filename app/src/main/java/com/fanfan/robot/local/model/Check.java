package com.fanfan.robot.local.model;

/**
 * Created by Administrator on 2018/3/27/027.
 */

public class Check {


    /**
     * check : {"id":"1","type":"1","versionCode":"8","versionName":"1.0.8","appName":"robot.apk","updateUrl":"files/robot.apk","upgradeInfo":"修改部分bug","updateTime":"2018-05-23 15:00:48"}
     * code : 0
     * msg : 最新
     */

    private CheckBean check;
    private int code;
    private String msg;

    public CheckBean getCheck() {
        return check;
    }

    public void setCheck(CheckBean check) {
        this.check = check;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class CheckBean {
        /**
         * id : 1
         * type : 1
         * versionCode : 8
         * versionName : 1.0.8
         * appName : robot.apk
         * updateUrl : files/robot.apk
         * upgradeInfo : 修改部分bug
         * updateTime : 2018-05-23 15:00:48
         */

        private String id;
        private String type;
        private int versionCode;
        private String versionName;
        private String appName;
        private String updateUrl;
        private String upgradeInfo;
        private String updateTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getUpdateUrl() {
            return updateUrl;
        }

        public void setUpdateUrl(String updateUrl) {
            this.updateUrl = updateUrl;
        }

        public String getUpgradeInfo() {
            return upgradeInfo;
        }

        public void setUpgradeInfo(String upgradeInfo) {
            this.upgradeInfo = upgradeInfo;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
