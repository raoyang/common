package com.game.banner.dao;

import com.game.banner.domain.Banner;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BannerMapper {

    List<Banner> queryAll();
    List<Banner> queryAllAndRule();
}
