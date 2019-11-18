package com.game.firebase.dao;


import com.game.firebase.domain.FirebaseInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface FirebaseInfoDao {

    FirebaseInfo queryByAccount(@Param("accountId") int accountId);

    void insert(FirebaseInfo info);

    void update(FirebaseInfo info);

    void delete(@Param("accountId") int accountId);
}
