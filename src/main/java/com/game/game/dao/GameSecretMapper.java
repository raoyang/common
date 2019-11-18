package com.game.game.dao;

import com.game.game.domain.GameSecret;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GameSecretMapper {
    GameSecret queryByGameId(@Param("gameId") String gameId);
}
