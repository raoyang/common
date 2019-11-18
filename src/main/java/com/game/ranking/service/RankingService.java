package com.game.ranking.service;

import com.game.Local.component.LocalUtil;
import com.game.account.domain.AccountInfo;
import com.game.common.vo.RankingVO;
import com.game.friend.service.FriendService;
import com.game.game.domain.Game;
import com.game.game.domain.GameAllInfo;
import com.game.game.service.GameService;
import com.game.login.service.LoginService;
import com.game.ranking.domain.RankingData;
import com.game.ranking.domain.RankingMe;
import com.game.ranking.domain.RankingRVO;
import com.game.record.domain.GameRankingRecord;
import com.game.record.service.RecordService;
import com.game.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

@Service
public class RankingService {
    private static final Logger logger = LoggerFactory.getLogger(RankingService.class);

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    LoginService loginService;
    @Autowired
    FriendService friendService;
    @Autowired
    GameService gameService;
    @Autowired
    RecordService recordService;
    @Autowired
    LocalUtil localUtil;

    @Value("${ai.account.total}")
    private int AITotal;

    public RankingVO ranking(RankingRVO rankingRVO) {
        int category = rankingRVO.getCategory();
        if (category == 0) {
            logger.info("param error, category empty");
            return RankingVO.error("param error", Constant.RNT_CODE_PARAM_ERROR);
        }

        int accountId = rankingRVO.getAccountId();
        AccountInfo self = loginService.getAccountInfo(accountId);
        if (self == null) {
            logger.error("account not exist, id:" + accountId);
            return RankingVO.error("param error", Constant.RNT_CODE_PARAM_ERROR);
        }

        String gameId = rankingRVO.getGameId();
        GameAllInfo game = gameService.queryGame(gameId);
        if (game == null) {
            logger.error("game id not exist, id:" + gameId);
            return RankingVO.error("param error", Constant.RNT_CODE_PARAM_ERROR);
        }

        String rankingKey = Constant.REDIS_KEY_PREFIX_RANKING + gameId;
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        int page = rankingRVO.getPage();
        if (page == 0) {
            page = 1;
        }
        int limit = rankingRVO.getLimit();
        if (limit == 0) {
            limit = 20;
        }
        int offset = (page - 1) * limit;
        if (! redisTemplate.hasKey(rankingKey)) {
            logger.debug("redis no ranking data, game:" + gameId);

            //load data to redis from db
            List<GameRankingRecord> rankingRecords = recordService.queryRankingRecord(gameId, Constant.RANKING_MAX);
            if (rankingRecords.isEmpty()) {
                logger.debug("db no data, gameId:" + gameId);
                return RankingVO.error("no data", Constant.RNT_CODE_NO_DATA);
            }

            Set<ZSetOperations.TypedTuple> set = new LinkedHashSet<>();
            for(GameRankingRecord record : rankingRecords){
                set.add(new DefaultTypedTuple(record.getAccountId(), Double.valueOf(record.getScore())));
            }
            zSetOperations.add(rankingKey, set);
        }

        int total = zSetOperations.size(rankingKey).intValue();
        if (total > Constant.RANKING_MAX) {
            total = Constant.RANKING_MAX;
        }

        if (offset >= total) {
            logger.debug("no more ranking data, game:" + gameId);
            return RankingVO.error("no data", Constant.RNT_CODE_NO_DATA);
        }

        Double scoreSelf = zSetOperations.score(rankingKey, String.valueOf(accountId));
        int rankType = game.getRankType();

        List<RankingData> rankingData = new ArrayList<>();
        if (category == Constant.RANKING_CATEGORY_GLOBAL) {
            Set<ZSetOperations.TypedTuple<String>> accounts = zSetOperations.reverseRangeWithScores(rankingKey, offset, offset + limit - 1);

            for (ZSetOperations.TypedTuple<String> value : accounts) {
                logger.debug("game ranking:" + value.getScore() + "," + value.getValue());
                AccountInfo accountInfo = loginService.getAccountInfo(Integer.valueOf(value.getValue()));
                if (accountInfo == null) {
                    continue;
                }
                int score = Integer.parseInt(new DecimalFormat("0").format(value.getScore()));
                rankingData.add(RankingData.valueOf(score + localUtil.getScoreUnit(rankType, rankingRVO), accountInfo));
            }

            RankingMe me = null;
            if (page == 1 && scoreSelf != null) {
                int rankingId = zSetOperations.reverseRank(rankingKey, String.valueOf(accountId)).intValue() + 1;
                String rankScore = Integer.parseInt(new DecimalFormat("0").format(scoreSelf)) + localUtil.getScoreUnit(rankType, rankingRVO);
                me = RankingMe.valueOf(rankingId, rankScore, self);
            }

            //小于20个用户需补全
            /*if (page == 1 && rankingVOS.size() < 20) {
                int left = 20 - rankingVOS.size();
                logger.debug("game ranking, left ai:" + left);
                for (int i = 0; i < left; i ++) {
                    rankingVOS.add(RankingData.valueOf(getRandomScore(type), loginService.getAIAccountInfo(getRandomAccountId())));
                }

                total = 20;
            }*/

            /*//重新排序
            Collections.sort(rankingData, new Comparator<RankingData>(){
                public int compare(RankingData o1, RankingData o2) {
                    //降序排列
                    if(o1.getScore() < o2.getScore()){
                        return 1;
                    }
                    if(o1.getScore() == o2.getScore()){
                        return 0;
                    }
                    return -1;
                }
            });*/


            return RankingVO.success(total, page, limit, me, rankingData);
        }

        if (category == Constant.RANKING_CATEGORY_FRIEND) {
            Set<String> friendIds = friendService.getFriendList(accountId);
            //删除占位,添加自己
            friendIds.remove("0");
            friendIds.add(String.valueOf(accountId));

            if (friendIds.isEmpty()) {
                if (scoreSelf == null || page > 1) {
                    return RankingVO.error("no data", Constant.RNT_CODE_NO_DATA);
                }

                int scoreInt = Integer.parseInt(new DecimalFormat("0").format(scoreSelf));
                String score = scoreInt + localUtil.getScoreUnit(rankType, rankingRVO);

                rankingData.add(RankingData.valueOf(score, self));
                RankingMe me = RankingMe.valueOf(1, score, self);

                return RankingVO.success(1,page, limit, me, rankingData);
            }


            //好友榜翻页，排行数据变动问题
            Set<ZSetOperations.TypedTuple<String>> accountAll = zSetOperations.reverseRangeWithScores(rankingKey, 0, Constant.RANKING_MAX);

            Map<Integer, Integer> friendRanking = new LinkedHashMap<>();
            List<Integer> rankingList = new ArrayList<>();
            for (ZSetOperations.TypedTuple<String> value : accountAll) {
                if (friendIds.contains(value.getValue())) {
                    friendRanking.put(Integer.parseInt(value.getValue()), Integer.parseInt(new DecimalFormat("0").format(value.getScore())));
                    rankingList.add(Integer.parseInt(value.getValue()));
                }
            }

            if (! rankingList.contains(accountId) && scoreSelf != null) {
                rankingList.add(accountId);
                friendRanking.put(accountId, Integer.parseInt(new DecimalFormat("0").format(scoreSelf)));
            }

            if (offset >= rankingList.size()) {
                return RankingVO.error("no data", Constant.RNT_CODE_NO_DATA);
            }

            int toIndex = offset + limit;
            if (toIndex >= rankingList.size()) {
                toIndex = rankingList.size();
            }

            List<Integer> pageContent = rankingList.subList(offset, toIndex);
            for (Integer id : pageContent) {
                AccountInfo accountInfo = loginService.getAccountInfo(id);
                rankingData.add(RankingData.valueOf(friendRanking.get(id) + localUtil.getScoreUnit(rankType, rankingRVO), accountInfo));
            }

            RankingMe me = null;
            if (page == 1 && scoreSelf != null) {
                int rankingId = rankingList.indexOf(accountId) + 1;
                int rankScore = friendRanking.get(accountId);
                me = RankingMe.valueOf(rankingId, rankScore + localUtil.getScoreUnit(rankType, rankingRVO), self);
            }

            return RankingVO.success(rankingList.size(), page, limit, me, rankingData);
        }

        return RankingVO.error("no data", Constant.RNT_CODE_NO_DATA);
    }

    //获取随机分值（默认分值和关卡值待定）
    private int getRandomScore(int type) {
        Random random = new Random();
        int score = 0;
        if (type == Constant.RANKING_TYPE_SCORE) {
            score = random.nextInt(100) + 10;
        }
        if (type == Constant.RANKING_TYPE_LEVEL) {
            score = random.nextInt(10) + 1;
        }

        return score;
    }

    //随机AI账号ID
    private int getRandomAccountId() {
        Random random = new Random();
        return random.nextInt(AITotal) + 1000000;
    }
}
