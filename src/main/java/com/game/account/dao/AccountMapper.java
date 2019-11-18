package com.game.account.dao;

import com.game.account.domain.AccountInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccountMapper {
    AccountInfo queryByOpenId(@Param("openId") String openId, @Param("platform") int platform);

    AccountInfo queryById(@Param("accountId") int accountId);

    void saveAccount(AccountInfo accountInfo);

    void updateAccountInfo(AccountInfo accountInfo);

    List<AccountInfo> queryByFBOpenId(@Param("ids") List<String> ids);

    List<AccountInfo> queryByPage(@Param("accountId") int accountId, @Param("offset") int offset, @Param("limit") int limit);

    AccountInfo queryVisitorAccount(@Param("uuid") String uuid, @Param("type") int type);

    List<String> queryAvatarRand(@Param("limit") int limit);
}
