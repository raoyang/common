package com.game.device.dao;

import com.game.home.domain.CommParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeviceMapper {
    void saveDeviceInfo(CommParam commParam);
    int countByUUId(@Param("uuid") String uuid);
    void updateDiviceInfo(int accountId, String uuid);
}
