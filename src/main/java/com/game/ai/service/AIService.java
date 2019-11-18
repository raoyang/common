package com.game.ai.service;

import com.game.ai.domain.PlayerInfo;
import com.game.util.Constant;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class AIService {
    private static final Logger logger = LoggerFactory.getLogger(AIService.class);
    private static final Map<String, List<Integer>> rankConf;
    static
    {
        rankConf = new HashMap<String, List<Integer>>();
        rankConf.put("1_1", Lists.newArrayList(1, 2));
        rankConf.put("1_2", Lists.newArrayList(1, 2));
        rankConf.put("1_3", Lists.newArrayList(2, 3));
        rankConf.put("2_1", Lists.newArrayList(1,2));
        rankConf.put("2_2", Lists.newArrayList(2, 3));
        rankConf.put("2_3", Lists.newArrayList(3, 4));
        rankConf.put("3_1", Lists.newArrayList(1, 2));
        rankConf.put("3_2", Lists.newArrayList(3, 4));
        rankConf.put("3_3", Lists.newArrayList(4, 5));
        rankConf.put("4_1", Lists.newArrayList(3, 4));
        rankConf.put("4_2", Lists.newArrayList(4, 5));
        rankConf.put("4_3", Lists.newArrayList(5));
        rankConf.put("5_1", Lists.newArrayList(4, 5));
        rankConf.put("5_2", Lists.newArrayList(5));
        rankConf.put("5_3", Lists.newArrayList(5));
    }

    @Autowired
    RedisTemplate redisTemplate;
    @Value("${match.ai.rank}")
    private int defaultAIRank;

    public int getAIRank(int accountId, String gameId) {
        //获取玩家信息
        PlayerInfo playerInfo = getPlayerInfo(accountId, gameId);
        if (playerInfo == null) {
            logger.debug("no player info, go default, accountId:" + accountId + ", gameId:" + gameId);
            return defaultAIRank;
        }

        int rank = dealAIRank(playerInfo);
        logger.debug("done AI rank, accountId:" + accountId + ", gameId:" + gameId + ", rank:" + rank + ", player info:" + playerInfo.toString());

        return rank;
    }

    private PlayerInfo getPlayerInfo(int accountId, String gameId) {
        PlayerInfo playerInfo = new PlayerInfo();
        String resultKey = Constant.REDIS_KEY_PREFIX_ACCOUNT_GAME_RESULT + accountId + ":" + gameId;
        HashOperations hashOperations = redisTemplate.opsForHash();
        Map<String, String> resultMap = hashOperations.entries(resultKey);
        if (resultMap.isEmpty()) {
            return null;
        }

        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            if (entry.getKey().equals(Constant.TOTAL_COUNT_KEY)) {
                playerInfo.setTotal(Integer.valueOf(entry.getValue()));
            }
            if (entry.getKey().equals(Constant.TOTAL_WIN_COUNT_KEY)) {
                playerInfo.setWin(Integer.valueOf(entry.getValue()));
            }
            if (entry.getKey().equals(Constant.IS_P_WIN_KEY)) {
                playerInfo.setpWin(Integer.valueOf(entry.getValue()));
            }
            if (entry.getKey().equals(Constant.IS_P_LOSS_KEY)) {
                playerInfo.setpLoss(Integer.valueOf(entry.getValue()));
            }
        }

        if (playerInfo.getTotal() == 0) {
            return null;
        }

        int rate = 0;
        if (playerInfo.getWin() != 0) {
            float rateDeci = new BigDecimal(((float) playerInfo.getWin() / playerInfo.getTotal())).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            rate = (int)(rateDeci * 100);
        }
        playerInfo.setRate(rate);

        playerInfo.setWeight(getPlayerWeight(playerInfo.getTotal()));

        //根据连胜、连败动态调整玩家等级
        int level = getPlayerLevel(rate);
        if (playerInfo.getpWin() > 3 && level < 5) {
            logger.debug("win more 3 times, level + 1, ori level:" + level);
            level += 1;
        }
        if (playerInfo.getpLoss() > 3 && level > 0) {
            logger.debug("loss more 3 times, level - 1, ori level:" + level);
            level -= 1;
        }

        playerInfo.setLevel(level);

        return playerInfo;
    }

    /**
     * 查询玩家级别
     * @param rateWin
     * @return
     */
    private int getPlayerLevel(int rateWin) {
        int level = 0;
        if (rateWin >= 1 && rateWin <= 20) {
            level = 1;
        } else if (rateWin >= 21 && rateWin <= 40) {
            level = 2;
        } else if (rateWin >= 41 && rateWin <= 60) {
            level = 3;
        } else if (rateWin >= 61 && rateWin <= 80) {
            level = 4;
        } else if (rateWin >= 81 && rateWin <= 100) {
            level = 5;
        }

        return level;
    }

    /**
     * 查询玩家段位
     * @param total
     * @return
     */
    private int getPlayerWeight(int total) {
        int weight = 1;
        if (total >= 0 && total <= 10) {
            weight = 1;
        } else if(total >= 11 && total <= 50) {
            weight = 2;
        } else if (total >= 51) {
            weight = 3;
        }

        return weight;
    }

    private int dealAIRank(PlayerInfo playerInfo) {
        if (playerInfo.getLevel() == 0) {
            return defaultAIRank;
        }

        List<Integer> rankRange = rankConf.get(playerInfo.getLevel() + "_" + playerInfo.getWeight());

        return rankRange.get(new Random().nextInt(rankRange.size()));
    }
}
