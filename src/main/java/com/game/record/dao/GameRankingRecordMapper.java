package com.game.record.dao;

import com.game.record.domain.GameRankingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameRankingRecordMapper {

    List<GameRankingRecord> queryGameRankingRecord(@Param("gameId") String gameId, @Param("limit") int limit);
}
