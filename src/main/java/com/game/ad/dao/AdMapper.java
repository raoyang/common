package com.game.ad.dao;

import com.game.ad.domain.AdConf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdMapper {
    List<AdConf> queryByGame(@Param("gameId") String gameId, @Param("appId") String appId);
}
