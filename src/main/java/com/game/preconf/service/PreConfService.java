package com.game.preconf.service;

import com.game.common.vo.ResultVO;
import com.game.gconf.domain.GConf;
import com.game.gconf.domain.GConfVO;
import com.game.gconf.service.GConfService;
import com.game.preconf.dao.PreConfMapper;
import com.game.preconf.domain.PreConf;
import com.game.preconf.domain.PreConfVO;
import com.game.util.Constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PreConfService {
    private static final Logger logger = LoggerFactory.getLogger(PreConfService.class);
    @Autowired
    PreConfMapper preConfMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    GConfService gConfService;

    public ResultVO queryConf() {
        List<PreConf> lists = new ArrayList<>();
        Gson gson = new Gson();

        //TODO 考虑分网络类型缓存数据
        ValueOperations operations = redisTemplate.opsForValue();
        Object jsonList = operations.get(Constant.REDIS_KEY_ALL_PRECONF);
        if (jsonList != null) {
            lists = gson.fromJson((String) jsonList, new TypeToken<List<PreConf>>() {
            }.getType());
        } else {
            lists = preConfMapper.queryAll();
            operations.set(Constant.REDIS_KEY_ALL_PRECONF, gson.toJson(lists), 1, TimeUnit.HOURS);
        }

        if (lists.size() == 0) {
            logger.warn(" query pre download conf, no data");
        }

        List<String> dataIds = new ArrayList<>();
        List<String> wifiIds = new ArrayList<>();
        List<String> defaultIds = new ArrayList<>();
        for (PreConf conf : lists) {
            if (conf.getNet() == Constant.NET_TYPE_ALL) {
                defaultIds.add(conf.getGameId());
                continue;
            }
            if (conf.getNet() == Constant.NET_TYPE_WIFI) {
                wifiIds.add(conf.getGameId());
                continue;
            }
            if (conf.getNet() == Constant.NET_TYPE_DATA) {
                dataIds.add(conf.getGameId());
            }
        }

        List<PreConfVO> preConfVOS = new ArrayList<>();
        if (! defaultIds.isEmpty()) {
            preConfVOS.add(PreConfVO.valueOf(Constant.NET_TYPE_ALL, defaultIds));
        }
        if (! wifiIds.isEmpty()) {
            preConfVOS.add(PreConfVO.valueOf(Constant.NET_TYPE_WIFI, wifiIds));
        }
        if (! dataIds.isEmpty()) {
            preConfVOS.add(PreConfVO.valueOf(Constant.NET_TYPE_DATA, dataIds));
        }

        List<GConf> gConfs = gConfService.allConf();

        GConfVO gConfVO = new GConfVO();
        gConfVO.setDownload(preConfVOS);
        gConfVO.setGlobal(gConfs);

        return ResultVO.success(gConfVO);
    }
}
