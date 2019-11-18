package com.game.visohelper.service;

import com.game.common.vo.ResultVO;
import com.game.visohelper.dao.VisoMapper;
import com.game.visohelper.domain.VisoInputDomain;
import com.game.visohelper.domain.VisoInputVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VisoService {

    private static final Logger logger = LoggerFactory.getLogger(VisoService.class);

    @Autowired
    private VisoMapper visoMapper;

    @Transactional(rollbackFor = Exception.class)
    public ResultVO leaveMessage(VisoInputVO vo){

        if(vo.getMsg() == null || vo.getMsg().equals("")){
            return ResultVO.error("invalid message");
        }
        visoMapper.insertVisoMsg(VisoInputDomain.valueOf(vo));
        return ResultVO.success();
    }
}
