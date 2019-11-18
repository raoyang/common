package com.game.gconf.dao;

import com.game.gconf.domain.GConf;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GConfMapper {

    List<GConf> queryAll();
}
