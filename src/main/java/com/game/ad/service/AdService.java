package com.game.ad.service;

import com.game.ad.dao.AdMapper;
import com.game.ad.domain.AdConf;
import com.game.ad.domain.DefaultAdConf;
import com.game.chat.util.JsonUtils;
import com.game.gconf.domain.GConf;
import com.game.gconf.service.GConfService;
import com.game.util.Constant;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.http.util.TextUtils;
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
public class AdService {
    private static final Logger logger = LoggerFactory.getLogger(AdService.class);

    @Autowired
    GConfService gConfService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    AdMapper adMapper;

    public List<AdConf> getAdConf(String gameId) {
        List<AdConf> adConfs = new ArrayList<>();

        List<GConf> gConfs = gConfService.allConf();
        if (gConfs.isEmpty()) {
            return adConfs;
        }

        DefaultAdConf defaultAdConf = null;
        for (GConf gConf : gConfs) {
            //游戏广告配置类型
            if (gConf.getType() == 5) {
                defaultAdConf = JsonUtils.stringToObject(gConf.getValue(), DefaultAdConf.class);
                break;
            }
        }

        if (defaultAdConf == null) {
            logger.error("no conf default ad");
            return adConfs;
        }

        if (TextUtils.isEmpty(defaultAdConf.getAppId())) {
            logger.error("no conf default ad appId");
            return adConfs;
        }

        String appId = defaultAdConf.getAppId();

        return queryByGame(gameId, appId);
    }

    public List<AdConf> queryByGame(String gameId, String appId) {
        List<AdConf> list = new ArrayList<>();

        Gson gson = new Gson();
        ValueOperations operations = redisTemplate.opsForValue();
        Object jsonList = operations.get(Constant.REDIS_KEY_PREFIX_GAME_AD_CONFIG + gameId + ":" + appId);
        if (jsonList != null) {
            list = gson.fromJson((String) jsonList, new TypeToken<List<AdConf>>() {
            }.getType());
        } else {
            list = adMapper.queryByGame(gameId, appId);
            operations.set(Constant.REDIS_KEY_PREFIX_GAME_AD_CONFIG, gson.toJson(list), 1, TimeUnit.HOURS);
        }

        return list;
    }
}
