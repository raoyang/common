package com.game.tool.dao;

import com.game.account.domain.AccountInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AIAccountMapper {
    void BatchSaveAIAccount(@Param("accounts")List<AccountInfo> accounts);

    AccountInfo queryById(@Param("accountId") int accountId);
}
