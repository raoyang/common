package com.game.badge.dao;

import com.game.badge.domain.Badge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BadgeMapper {
    List<Badge> queryByAccountId(@Param("accountId") int accountId);
}
