package com.game.device.domain;

public class Device {
    private int id;
    private int accountId;
    private int appVer;
    private String ch;
    private String clientId;
    private String product;
    private String brand;
    private int isAbroad;
    private String osVer;
    private String cpuId;
    private String region;
    private String aid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setCpuId(String cpuId) {
        this.cpuId = cpuId;
    }

    public String getCpuId() {
        return cpuId;
    }

    public void setOsVer(String osVer) {
        this.osVer = osVer;
    }

    public String getOsVer() {
        return osVer;
    }

    public void setIsAbroad(int isAbroad) {
        this.isAbroad = isAbroad;
    }

    public int getIsAbroad() {
        return isAbroad;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public String getCh() {
        return ch;
    }

    public void setAppVer(int appVer) {
        this.appVer = appVer;
    }

    public int getAppVer() {
        return appVer;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }
}
