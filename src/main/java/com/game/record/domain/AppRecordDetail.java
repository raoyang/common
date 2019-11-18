package com.game.record.domain;

public class AppRecordDetail{
    //事件名称
    private String event;
    //打点的账号ID
    private String accountID;
    //server记录的ID
    private int account_id;
    //1：没有操作关闭应用;2:登录失败了之后关闭应用
    private int account_null_type;
    //0:未登录设备 1：登录设备
    private int is_logined_device;
    //1.facebook,2:Google,6:访客登录
    private int login_type;
    //1.facebook,2:Google，6:访客登录
    private int register_type;
    //1.facebook,2:Google,0:平台登录，6:访客登录
    private String error_type;
    //结果为接口请求时返回的错误码(没有错误码传空)
    private String error_code;
    //失败的信息
    private String error_msg;
    //游戏名称
    private String game_name;
    //游戏ID
    private String game_id;
    //0:未完游戏，1：玩过游戏
    private int result;
    //0：未连接 1：连接
    private int is_net_work_ok;
    //游戏时长毫秒
    private int play_time;
    //产生时间
    private String utc;
    //uuid
    private String uuid;

    //公共信息
    private int app_ver;
    private String ch;
    private String dev_model;
    private String dev_brand;
    private int dev_mem;
    private String os_ver;
    private int net_type;
    private String lan;
    private String region;
    private String ip;

    public String getEvent() {
        return event;
    }

    public int getAccount_null_type() {
        return account_null_type;
    }

    public int getIs_logined_device() {
        return is_logined_device;
    }

    public int getLogin_type() {
        return login_type;
    }

    public int getRegister_type() {
        return register_type;
    }

    public String getError_type() {
        return error_type;
    }

    public String getError_code() {
        return error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public String getGame_name() {
        return game_name;
    }

    public String getGame_id() {
        return game_id;
    }

    public int getResult() {
        return result;
    }

    public int getIs_net_work_ok() {
        return is_net_work_ok;
    }

    public int getPlay_time() {
        return play_time;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public void setAccount_null_type(int account_null_type) {
        this.account_null_type = account_null_type;
    }

    public void setIs_logined_device(int is_logined_device) {
        this.is_logined_device = is_logined_device;
    }

    public void setLogin_type(int login_type) {
        this.login_type = login_type;
    }

    public void setRegister_type(int register_type) {
        this.register_type = register_type;
    }

    public void setError_type(String error_type) {
        this.error_type = error_type;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setIs_net_work_ok(int is_net_work_ok) {
        this.is_net_work_ok = is_net_work_ok;
    }

    public void setPlay_time(int play_time) {
        this.play_time = play_time;
    }

    public int getApp_ver() {
        return app_ver;
    }

    public String getCh() {
        return ch;
    }

    public String getDev_model() {
        return dev_model;
    }

    public String getDev_brand() {
        return dev_brand;
    }

    public int getDev_mem() {
        return dev_mem;
    }

    public String getOs_ver() {
        return os_ver;
    }

    public int getNet_type() {
        return net_type;
    }

    public String getLan() {
        return lan;
    }

    public String getRegion() {
        return region;
    }

    public void setApp_ver(int app_ver) {
        this.app_ver = app_ver;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    public void setDev_model(String dev_model) {
        this.dev_model = dev_model;
    }

    public void setDev_brand(String dev_brand) {
        this.dev_brand = dev_brand;
    }

    public void setDev_mem(int dev_mem) {
        this.dev_mem = dev_mem;
    }

    public void setOs_ver(String os_ver) {
        this.os_ver = os_ver;
    }

    public void setNet_type(int net_type) {
        this.net_type = net_type;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUtc() {
        return utc;
    }

    public void setUtc(String utc) {
        this.utc = utc;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
