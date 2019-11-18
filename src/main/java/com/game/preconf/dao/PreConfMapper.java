package com.game.preconf.dao;

import com.game.preconf.domain.PreConf;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PreConfMapper {

    List<PreConf> queryAll();
}
