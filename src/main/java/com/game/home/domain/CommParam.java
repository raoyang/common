package com.game.home.domain;

public class CommParam {
    private int appVer;
    private String ch = "";
    private String devModel = "";
    private String devBrand = "";
    private int devMem;
    private int isAbroad;
    private String osVer = "";
    private String cpuId = "";
    private String aId = "";
    private int netType;
    private String clientId = "";
    private String lan =  "";
    private String region = "";
    private int accountId = 0;
    private String gToken = "";
    private String uuid = "";
    private float D;
    private int W;
    private int H;
    private String longitude;
    private String latitude;

    public int getAppVer() {
        return appVer;
    }

    public void setAppVer(int appVer) {
        this.appVer = appVer;
    }

    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDevModel() {
        return devModel;
    }

    public void setDevModel(String devModel) {
        this.devModel = devModel;
    }

    public int getIsAbroad() {
        return isAbroad;
    }

    public void setIsAbroad(int isAbroad) {
        this.isAbroad = isAbroad;
    }

    public int getNetType() {
        return netType;
    }

    public void setNetType(int netType) {
        this.netType = netType;
    }

    public String getOsVer() {
        return osVer;
    }

    public void setOsVer(String osVer) {
        this.osVer = osVer;
    }

    public String getCpuId() {
        return cpuId;
    }

    public void setCpuId(String cpuId) {
        this.cpuId = cpuId;
    }

    public String getaId() {
        return aId;
    }

    public void setaId(String aId) {
        this.aId = aId;
    }

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }

    public String getDevBrand() {
        return devBrand;
    }

    public void setDevBrand(String devBrand) {
        this.devBrand = devBrand;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getgToken() {
        return gToken;
    }

    public void setgToken(String gToken) {
        this.gToken = gToken;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public float getD() {
        return D;
    }

    public int getW() {
        return W;
    }

    public int getH() {
        return H;
    }

    public void setD(float d) {
        D = d;
    }

    public void setW(int w) {
        W = w;
    }

    public void setH(int h) {
        H = h;
    }

    public int getDevMem() {
        return devMem;
    }

    public void setDevMem(int devMem) {
        this.devMem = devMem;
    }
}
