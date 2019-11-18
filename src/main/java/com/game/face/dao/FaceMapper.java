package com.game.face.dao;

import com.game.face.domain.Face;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FaceMapper {
    List<Face> queryAll();
}
