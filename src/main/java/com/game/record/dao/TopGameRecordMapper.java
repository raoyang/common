package com.game.record.dao;

import com.game.record.domain.TopGameRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TopGameRecordMapper {

    List<TopGameRecord> queryTopGameRecord(@Param("limit") int limit);
}
