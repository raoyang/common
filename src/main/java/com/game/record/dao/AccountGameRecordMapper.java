package com.game.record.dao;

import com.game.favogame.domain.AccountGameRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AccountGameRecordMapper {

    //1. 查询所有喜欢的游戏，返回list
    List<AccountGameRecord> queryAccountGameRecord(@Param("accountId") int accountId, @Param("limit") int limit);

    //2. 批量更新某个游戏的次数
    void updateAccountGameRecord(List<AccountGameRecord> list);

    void insertAccountGameRecord(AccountGameRecord record);
}
