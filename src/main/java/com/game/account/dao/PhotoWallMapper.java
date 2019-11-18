package com.game.account.dao;

import com.game.account.domain.PhotoWallInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PhotoWallMapper {

    void insertPhotoUrl(PhotoWallInfo info);

    String getPhotoWalls(int accountId);

    void updatePhotoUrl(PhotoWallInfo info);
}
