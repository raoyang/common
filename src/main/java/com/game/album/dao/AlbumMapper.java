package com.game.album.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlbumMapper {
    List<String> queryAlbumContent(@Param("albumId") String albumId, @Param("page") int page, @Param("limit") int limit);

    int countAlbumContent(@Param("albumId") String albumId);
}
