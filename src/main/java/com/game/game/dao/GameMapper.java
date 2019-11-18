package com.game.game.dao;

import com.game.game.domain.Game;
import com.game.game.domain.GameAllInfo;
import com.game.game.domain.GameCfg;
import com.game.game.domain.GameLan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameMapper {

    GameAllInfo queryById(@Param("gameId") String gameId);

    List<GameLan> queryLanByGameVersion(@Param("gameId") String gameId, @Param("versionCode") int versionCode);

    GameCfg queryGameCfg(@Param("gameId") String gameId);
}
