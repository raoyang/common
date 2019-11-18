package com.game.device.service;

import com.game.common.vo.ResultVO;
import com.game.device.dao.DeviceMapper;
import com.game.home.domain.CommParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceService {
    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    DeviceMapper deviceMapper;

    @Transactional(rollbackFor = Exception.class)
    public ResultVO saveDeviceInfo(CommParam commParam) {
        try {
            if (deviceMapper.countByUUId(commParam.getUuid()) == 0) {
                deviceMapper.saveDeviceInfo(commParam);
            }

        } catch (Exception e) {
            logger.info("save device info exception, msg:" + e.getMessage());
        }

        return ResultVO.success();
    }
}
