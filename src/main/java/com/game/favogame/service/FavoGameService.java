package com.game.favogame.service;

import com.game.common.vo.ResultVO;
import com.game.favogame.domain.AccountGameRecord;
import com.game.favogame.domain.FavoGameRVO;
import com.game.favogame.domain.FavoGameVO;
import com.game.game.domain.Game;
import com.game.game.domain.GameAllInfo;
import com.game.game.domain.GameVersion;
import com.game.game.service.GameService;
import com.game.record.service.RecordService;
import com.game.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class FavoGameService {
    private static final Logger logger = LoggerFactory.getLogger(FavoGameService.class);
    private static final int FAVO_GAME_MAX_NUM = 30;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    GameService gameService;
    @Autowired
    private RecordService recordService;

    public ResultVO favorite(int accountId, boolean isThird, FavoGameRVO favoGameRVO) {
        List<GameAllInfo> allGames = new ArrayList<>();
        Map<String, Integer> gameCount = new HashMap<>();

        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> games = zSetOperations.reverseRangeWithScores(Constant.REDIS_KEY_PREFIX_FAVORITE_GAME + accountId, 0, FAVO_GAME_MAX_NUM);
        if (games == null || games.isEmpty()) {
            logger.info("redis no data, accountId:" + accountId);
            List<AccountGameRecord> list = recordService.queryAccountGameRecord(accountId, FAVO_GAME_MAX_NUM);
            if (list.isEmpty()) {
                logger.debug("favo game form db empty");
                return ResultVO.error("no data", Constant.RNT_CODE_NO_DATA);
            }

            for(AccountGameRecord record : list){
                GameAllInfo gameAllInfo = gameService.queryGame(record.getGameId());
                if (gameAllInfo == null) {
                    continue;
                }
                allGames.add(gameAllInfo);
                gameCount.put(record.getGameId(), record.getCount());
            }

            setCache(accountId, list);
        } else {
            for (ZSetOperations.TypedTuple<String> value : games) {
                logger.debug("favorite game:" + value.getScore() + "," + value.getValue());
                GameAllInfo gameAllInfo = gameService.queryGame(value.getValue());
                if (gameAllInfo == null) {
                    continue;
                }
                allGames.add(gameAllInfo);
                gameCount.put(value.getValue(), Integer.parseInt(new DecimalFormat("0").format(value.getScore())));
            }
        }

        //TODO 三方不需要过滤规则
        List<Game> filterGames = gameService.filterGames(allGames, favoGameRVO);
        if (filterGames.isEmpty()) {
            logger.debug("favo game filter empty");
            return ResultVO.error("no data", Constant.RNT_CODE_NO_DATA);
        }

        List<FavoGameVO> favoGameVOS = new ArrayList<>();
        for (Game game : filterGames) {
            FavoGameVO gameVO = new FavoGameVO();
            BeanUtils.copyProperties(game, gameVO);
            gameVO.setCount(gameCount.get(game.getGameId()));

            favoGameVOS.add(gameVO);
        }

        return ResultVO.success(favoGameVOS);
    }

    /**
     * 设置缓存（如果数据库都没有数据，那么增加一条默认值(-1,0)到缓存）
     * @param accountId
     */
    private void setCache(int accountId, List<AccountGameRecord> records) {
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(Constant.REDIS_KEY_PREFIX_FAVORITE_GAME)
                .append(accountId);
        String key = keyBuilder.toString();
        if (records.isEmpty()) {
            zSetOperations.add(key, String.valueOf(-1), 0);
        } else {
            Set<ZSetOperations.TypedTuple> set = new LinkedHashSet<>();
            for(AccountGameRecord record : records){
                set.add(new DefaultTypedTuple(record.getGameId(), Double.valueOf(record.getCount())));
            }
            zSetOperations.add(key, set);
        }
    }
}
