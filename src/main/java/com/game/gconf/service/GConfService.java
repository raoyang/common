package com.game.gconf.service;

import com.game.gconf.dao.GConfMapper;
import com.game.gconf.domain.GConf;
import com.game.util.Constant;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GConfService {
    private static final Logger logger = LoggerFactory.getLogger(GConfService.class);
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    GConfMapper gConfMapper;

    public List<GConf> allConf() {
        List<GConf> lists = new ArrayList<>();

        Gson gson = new Gson();
        ValueOperations operations = redisTemplate.opsForValue();
        Object jsonList = operations.get(Constant.REDIS_KEY_GLOBAL_CONF);
        if (jsonList != null) {
            lists = gson.fromJson((String) jsonList, new TypeToken<List<GConf>>() {
            }.getType());
        } else {
            lists = gConfMapper.queryAll();
            operations.set(Constant.REDIS_KEY_GLOBAL_CONF, gson.toJson(lists), 1, TimeUnit.HOURS);
        }

        return lists;
    }
}
