package com.game.share.domain;

import com.game.account.domain.AccountInfo;
import com.game.favogame.domain.FavoGameVO;
import com.game.util.Constant;

import java.util.List;

public class ShareHomeVO {
    private int accountId;
    private String nickName;
    private String avatar;
    private String appName;
    private String appIcon;
    private String appUrl;
    private String buttonText;
    private String extDesc;
    private Object games;

    public static ShareHomeVO valueOf(AccountInfo accountInfo) {
        ShareHomeVO homeVO = new ShareHomeVO();
        homeVO.setAccountId(accountInfo.getId());
        homeVO.setNickName(accountInfo.getNickName());
        homeVO.setAvatar(accountInfo.getHeaderImg());
        homeVO.setAppName(Constant.SHARE_APP_NAME);
        homeVO.setAppIcon(Constant.SHARE_APP_ICON);
        homeVO.setAppUrl(Constant.SHARE_APP_URL);

        return homeVO;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getNickName() {
        return nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public Object getGames() {
        return games;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setGames(Object games) {
        this.games = games;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getExtDesc() {
        return extDesc;
    }

    public void setExtDesc(String extDesc) {
        this.extDesc = extDesc;
    }
}
