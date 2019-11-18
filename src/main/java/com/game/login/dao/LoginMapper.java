package com.game.login.dao;

import com.game.login.domain.AppAccountLogin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginMapper {

    AppAccountLogin queryAppAndAccountId(@Param("accountId") int accountId, @Param("appId") int appId);

    void saveAppAccountLogin(AppAccountLogin appAccountLogin);

    void updateAppAccountLogin(AppAccountLogin appAccountLogin);

}
