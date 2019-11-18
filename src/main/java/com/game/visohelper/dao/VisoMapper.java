package com.game.visohelper.dao;

import com.game.visohelper.domain.VisoInputDomain;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VisoMapper {

    int insertVisoMsg(VisoInputDomain domain);
}
