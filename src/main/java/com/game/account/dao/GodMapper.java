package com.game.account.dao;

import com.game.account.domain.GodInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GodMapper {

    List<GodInfo> queryAll();
}
