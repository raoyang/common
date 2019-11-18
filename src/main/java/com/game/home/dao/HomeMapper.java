package com.game.home.dao;

import com.game.home.domain.Home;
import com.game.home.domain.HomeRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HomeMapper {

    List<HomeRule> queryAllAndRule();
}
