package com.game.badge.service;

import com.game.badge.dao.BadgeMapper;
import com.game.badge.domain.Badge;
import com.game.common.vo.ResultVO;
import com.game.home.domain.CommParam;
import com.game.util.Constant;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BadgeService {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    BadgeMapper badgeMapper;

    public List<Badge> getBadgeInfo(int accountId) {
        List<Badge> badges = new ArrayList<>();
        if (accountId == 0) {
            return badges;
        }

        Gson gson = new Gson();
        ValueOperations operations = redisTemplate.opsForValue();
        String redisKey = Constant.REDIS_KEY_PREFIX_BADGE + accountId;
        Object jsonList = operations.get(redisKey);
        if (jsonList != null) {
            badges = gson.fromJson((String) jsonList, new TypeToken<List<Badge>>() {
            }.getType());
        } else {
            badges = badgeMapper.queryByAccountId(accountId);
            operations.set(redisKey, gson.toJson(badges), 24, TimeUnit.HOURS);
        }

        return badges;
    }

    public ResultVO accountBadge(CommParam commParam) {
        int accountId = commParam.getAccountId();
        List<Badge> badges = getBadgeInfo(accountId);
        if (badges.isEmpty()) {
            return ResultVO.error("no data", Constant.RNT_CODE_NO_DATA);
        }

        return ResultVO.success(badges);
    }
}
