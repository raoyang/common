package com.game.record.service;

import com.game.account.domain.AccountInfo;
import com.game.account.domain.GodInfo;
import com.game.account.service.AccountService;
import com.game.chat.util.JsonUtils;
import com.game.common.vo.ResultVO;
import com.game.favogame.domain.AccountGameRecord;
import com.game.friend.domain.RecommendFriend;
import com.game.game.domain.GameAllInfo;
import com.game.game.service.GameService;
import com.game.login.service.LoginService;
import com.game.record.dao.AccountGameRecordMapper;
import com.game.record.dao.GameRankingRecordMapper;
import com.game.record.dao.TopGameRecordMapper;
import com.game.record.domain.*;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class RecordService {
    private static final Logger logger = LoggerFactory.getLogger(RecordService.class);
    private static final Logger eventLog = LoggerFactory.getLogger("event");
    private static final Logger adLog = LoggerFactory.getLogger("ad");

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private AccountGameRecordMapper recordMapper;
    @Autowired
    GameService gameService;
    @Autowired
    TopGameRecordMapper topGameRecordMapper;
    @Autowired
    GameRankingRecordMapper rankingRecordMapper;
    @Autowired
    private LoginService loginService;
    @Autowired
    AccountService accountService;
    @Value("${share.add.lottery.times}")
    private int shareADDMaxTimes;
    @Value("${is.test}")
    private boolean isTest;
    @Value("${add.account.2.god.enable}")
    private boolean addGodEnable;

    private static final int GAME_GOD_COUNT = 4;

    /** 排行榜长度 **/
    private static final int GAME_GOD_RANK_COUNT = 50;
    private static final int GAME_SUPER_GOD_COUNT = 20; //超级大神分水岭

    public ResultVO record(Record record, String ip) {
        String gameId = record.getGameId();
        int eventType = record.getEvent();
        int accountId = record.getAccountId();

        if (eventType == Constant.RECORD_EVENT_GAME_ACTIVITY) {
            recordShareLog(record);
            return ResultVO.success();
        }

        if (gameId.equals("") || eventType == 0 || accountId == 0) {
            logger.info("param error");
            return ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR);
        }

        GameAllInfo game = gameService.queryGame(gameId);
        if (game == null) {
            logger.error("record: game not exist, id:" + gameId);
            return ResultVO.error("game not exist", Constant.RNT_CODE_PARAM_ERROR);
        }
        String gameName = game.getVersions().get(0).getgLan().get(Constant.LANGUAGE_DEFAULT).getName();

        //游戏开始：在线人数+1、用户开局次数+1
        String onlineGameKey = Constant.REDIS_KEY_PREFIX_GAME_ONLINE + gameId;
        String favoriteGameKey = Constant.REDIS_KEY_PREFIX_FAVORITE_GAME + accountId;
        String topGameKey = Constant.REDIS_KEY_TOP_GAME;
        String grayNumKey = Constant.REDIS_KEY_PREFIX_GRAY_NUM + gameId + "_" + record.getgVer();
        String queueGameStartKey = Constant.REDIS_KEY_QUEUE_GAME_START;
        String queueGameScoreKey = Constant.REDIS_KEY_QUEUE_GAME_SCORE;
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        ValueOperations valueOperations = redisTemplate.opsForValue();
        if (eventType == Constant.RECORD_EVENT_GAME_START) {
            valueOperations.increment(onlineGameKey, 1);
            //因游戏结束事件不准确，设置在线玩家计数器每天0点过期
            redisTemplate.expire(onlineGameKey, CommonFunUtil.getRemainSecondsOneDay(new Date()), TimeUnit.SECONDS);
            //TODO 切换灰度时清零
            valueOperations.increment(grayNumKey, 1);
            zSetOperations.incrementScore(favoriteGameKey, gameId, 1);
            zSetOperations.incrementScore(topGameKey, gameId, 1);

            Map<String, String> eventData = new HashMap<>();
            eventData.put("accountId", String.valueOf(record.getAccountId()));
            eventData.put("gameId", record.getGameId());

            sendLog2RedisList(queueGameStartKey, JsonUtils.objectToString(eventData));

            addESRedis(record, ip, gameName);
        }

        //若出现负数，跟踪打点
        if (eventType == Constant.RECORD_EVENT_GAME_FINISH) {
            valueOperations.increment(onlineGameKey, -1);
        }

        //零分也记录
        int rPoints = record.getScore();
        int rType = game.getRankType();
        String accountIdStr = String.valueOf(accountId);
        if (eventType == Constant.RECORD_EVENT_RANKING_REPORT) {
            String rankingKey = Constant.REDIS_KEY_PREFIX_RANKING + gameId;
            if (rType == Constant.RANKING_TYPE_SCORE || rType == Constant.RANKING_TYPE_LEVEL) {
                Double score = zSetOperations.score(rankingKey, accountIdStr);
                if (score == null) {
                    zSetOperations.add(rankingKey, accountIdStr, rPoints);
                    //分值，设置周过期（暂时取消）
                    /*if (rType == Constant.RANKING_TYPE_SCORE) {
                        redisTemplate.expire(rankingKey, CommonFunUtil.netWeekLeftTimeSecond(), TimeUnit.SECONDS);
                    }*/
                    /*Long size = zSetOperations.zCard(rankingKey);
                    if (size > Constant.RANKING_MAX) {
                        zSetOperations.removeRange(rankingKey, 0, (size - Constant.RANKING_MAX - 1));
                    }*/
                } else if(score < rPoints) {
                    zSetOperations.add(rankingKey, accountIdStr, rPoints);
                }
            } else {
                logger.warn("no support ranking type:" + rType);
            }

            Map<String, String> eventData = new HashMap<>();
            eventData.put("accountId", String.valueOf(record.getAccountId()));
            eventData.put("gameId", record.getGameId());
            eventData.put("rankType", String.valueOf(rType));
            eventData.put("score", String.valueOf(rPoints));

            sendLog2RedisList(queueGameScoreKey, JsonUtils.objectToString(eventData));
        }

        if (eventType == Constant.RECORD_EVENT_GAME_RESULT) {
            int totalInc = 0;
            int winInc = 0;
            int isPWin = 0;
            int isPLoss = 0;
            int resultType = record.getResult();
            if (resultType == Constant.RECORD_EVENT_GAME_RESULT_WIN) {
                totalInc += 1;
                winInc += 1;
                isPWin += 1;
                isPLoss = 0;
            } else if (resultType == Constant.RECORD_EVENT_GAME_RESULT_DRAW) {
                totalInc += 1;
                isPWin = 0;
                isPLoss = 0;
            } else if ( resultType == Constant.RECORD_EVENT_GAME_RESULT_LOSS) {
                totalInc += 1;
                isPWin = 0;
                isPLoss += 1;
            }

            if (totalInc == 1) {
                String resultKey = Constant.REDIS_KEY_PREFIX_ACCOUNT_GAME_RESULT + accountId + ":" + gameId;
                HashOperations hashOperations = redisTemplate.opsForHash();

                if (redisTemplate.hasKey(resultKey) ){
                    hashOperations.increment(resultKey, Constant.TOTAL_COUNT_KEY, totalInc);
                    if (winInc == 1) {
                        hashOperations.increment(resultKey, Constant.TOTAL_WIN_COUNT_KEY, winInc);
                    }
                    if (isPWin == 1) {
                        hashOperations.increment(resultKey, Constant.IS_P_WIN_KEY, isPWin);
                    } else {
                        hashOperations.put(resultKey, Constant.IS_P_WIN_KEY, "0");
                    }
                    if (isPLoss == 1) {
                        hashOperations.increment(resultKey, Constant.IS_P_LOSS_KEY, isPLoss);
                    } else {
                        hashOperations.put(resultKey, Constant.IS_P_LOSS_KEY, "0");
                    }
                } else {
                    Map<String, String> resultMap = new HashMap<>();
                    resultMap.put(Constant.TOTAL_COUNT_KEY, String.valueOf(totalInc));
                    resultMap.put(Constant.TOTAL_WIN_COUNT_KEY, String.valueOf(winInc));
                    resultMap.put(Constant.IS_P_WIN_KEY, String.valueOf(isPWin));
                    resultMap.put(Constant.IS_P_LOSS_KEY, String.valueOf(isPLoss));
                    hashOperations.putAll(resultKey, resultMap);
                }
            }

            addESRedis(record, ip, gameName);
        }

        //所有事件都计入日志
        StringBuilder sb = new StringBuilder();
        sb.append(accountId).append("|")
                .append(gameId).append("|")
                .append(eventType).append("|")
                .append(record.getResult()).append("|")
                .append(record.getRoomId()).append("|")
                .append(rType).append("|")
                .append(rPoints).append("|")
                .append(record.getAppVer()).append("|")
                .append(record.getDevModel()).append("|")
                .append(record.getCh()).append("|")
                .append(record.getClientId()).append("|")
                .append(ip);

        String msg = sb.toString();
        eventLog.info(msg);

        return ResultVO.success();
    }

    /**
     * 写入导入ES的redis
     * @param record
     * @param ip
     * @param gameName
     */
    private void addESRedis(Record record, String ip, String gameName) {
        GameResultLog resultLog = new GameResultLog();
        BeanUtils.copyProperties(record, resultLog);
        resultLog.setGameName(gameName);
        resultLog.setIp(ip);
        sendLog2RedisList(Constant.REDIS_KEY_QUEUE_GAME_RESULT, JsonUtils.objectToString(resultLog));
    }

    private void recordShareLog(Record record) {
        int accountId = record.getAccountId();
        RecordExt ext = JsonUtils.stringToObject(record.getExt(), RecordExt.class);
        //点击分享，增加一次抽奖次数
        if (record.getType() == Constant.RECORD_EVENT_ACTIVITY_SHARE) {
            String activityTimeKey = Constant.REDIS_KEY_PREFIX_ACTIVITY_TIMES + ext.getaId() + ":" + accountId;
            String activityLoginKey = Constant.REDIS_KEY_PREFIX_ACTIVITY_LOGIN_FIRST + ext.getaId() + ":" + accountId;
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Object tObj = valueOperations.get(activityLoginKey);
            if (tObj != null && Integer.valueOf((String)tObj) < shareADDMaxTimes) {
                valueOperations.increment(activityTimeKey, 1);
                valueOperations.increment(activityLoginKey, 1);
                redisTemplate.expire(activityLoginKey, CommonFunUtil.getRemainSecondsOneDay(new Date()), TimeUnit.SECONDS);
                int total = Integer.valueOf((String)tObj) + 1;
                logger.debug("add lottery times, accountId:" + accountId + ", add total:" + total);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(accountId).append("|")
                .append(record.getEvent()).append("|")
                .append(record.getType()).append("|")
                .append(record.getContent()).append("|")
                .append(record.getAppVer()).append("|")
                .append(record.getDevModel()).append("|")
                .append(record.getCh()).append("|")
                .append(record.getClientId());

        String msg = sb.toString();
        eventLog.info(msg);
    }

    public ResultVO recordADLog(Record record, String ip) {
        int accountId = record.getAccountId();
        String time = CommonFunUtil.getNowDay();

        StringBuilder sb = new StringBuilder();
        sb.append(accountId).append("|")
                .append(record.getGameId()).append("|")
                .append(record.getAdId()).append("|")
                .append(record.getEvent()).append("|")
                .append(record.getAppVer()).append("|")
                .append(record.getDevModel()).append("|")
                .append(record.getCh()).append("|")
                .append(record.getLan()).append("|")
                .append(record.getResult()).append("|")
                .append(time).append("|")
                .append(ip);

        String msg = sb.toString();
        adLog.info(msg);

        //存储redis，json格式
        AdLog adLog = new AdLog();
        BeanUtils.copyProperties(record, adLog);
        adLog.setTime(time);
        adLog.setIp(ip);
        sendLog2RedisList(Constant.REDIS_KEY_QUEUE_GAME_AD, JsonUtils.objectToString(adLog));

        return ResultVO.success();
    }

    public void recordAppLog(AppRecord record, String ip) {
        List<String> lists = record.getContent();
        if (lists.isEmpty()) {
            return;
        }

        List<String> events = new ArrayList<>();
        for (String event : lists) {
            AppRecordDetail detail = JsonUtils.stringToObject(event, AppRecordDetail.class);
            detail.setApp_ver(record.getAppVer());
            detail.setCh(record.getCh());
            detail.setDev_brand(record.getDevBrand());
            detail.setDev_model(record.getDevModel());
            detail.setDev_mem(record.getDevMem());
            detail.setLan(record.getLan());
            detail.setRegion(record.getRegion());
            detail.setOs_ver(record.getOsVer());
            detail.setNet_type(record.getNetType());
            detail.setAccount_id(record.getAccountId());
            detail.setUuid(record.getUuid());
            detail.setIp(ip);

            String msg = JsonUtils.objectToString(detail);
            events.add(msg);
            logger.debug("add app log:" + msg);
        }

        if (!isTest) {
            ListOperations listOperations = redisTemplate.opsForList();
            listOperations.rightPushAll(Constant.REDIS_KEY_QUEUE_APP_RECORD, events);
        }
    }

    private void sendLog2RedisList(String redisKey, String content) {
        if (!isTest) {
            ListOperations listOperations = redisTemplate.opsForList();
            listOperations.rightPush(redisKey, content);
        }
    }

    /**
     * 定时写入日志文件：跨天分隔
     */
    @Scheduled(cron = "0 5 0 * * ?")
    public void fixEventLogFile() {
        String msg = "0|0|0|0";
        eventLog.info(msg);
    }

    public List<AccountGameRecord> queryAccountGameRecord(int accountId, int maxNum) {
        return recordMapper.queryAccountGameRecord(accountId, maxNum);
    }

    public List<TopGameRecord> queryTopGameRecord(int maxNum) {
        return topGameRecordMapper.queryTopGameRecord(maxNum);
    }

    public List<GameRankingRecord> queryRankingRecord(String gameId, int maxNum) {
        return rankingRecordMapper.queryGameRankingRecord(gameId, maxNum);
    }

    /***
     * 随机获取游戏大神列表
     * @return
     */
    public ResultVO getRandomGameGod(){
        String key = Constant.REDIS_KEY_GAME_GOD_RANK;
        ZSetOperations operations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> set = operations.reverseRangeWithScores(key, 0, GAME_GOD_RANK_COUNT);
        List<RecommendFriend> result = calRandomGameGod(set);
        if (result.isEmpty()) {
            return ResultVO.error("no data", Constant.RNT_CODE_NO_DATA);
        }

        return ResultVO.success(result);
    }

    private List<RecommendFriend> calRandomGameGod(Set<ZSetOperations.TypedTuple<String>> set){
        List<RecommendFriend> list = new ArrayList<>();

        List<GameGoldRankObj> allData = addGodAIData(set);
        if (allData.isEmpty()) {
            return list;
        }

        Collections.shuffle(allData);
        int len = allData.size() < GAME_GOD_COUNT ? allData.size() : GAME_GOD_COUNT;
        for(int i = 0 ; i < len ; i ++){
            GameGoldRankObj obj = allData.get(i);
            AccountInfo info = loginService.getAccountInfo(obj.getAccountId());
            if(info != null){
                list.add(RecommendFriend.valueOf(info, obj.getGodType(), obj.getWinPer(), obj.getTotalCnt()));
            }
        }
        return list;
    }

    private void buildRecommendFriendList(List<RecommendFriend> list, Set<ZSetOperations.TypedTuple<String>> set){
        for(ZSetOperations.TypedTuple<String> typedTuple : set){
            String str = typedTuple.getValue();
            double score = typedTuple.getScore();
            GameGoldRankObj obj = JsonUtils.stringToObject(str, GameGoldRankObj.class);
            obj.setWinPer(score);
            int accountId = obj.getAccountId();
            AccountInfo info = loginService.getAccountInfo(accountId);
            if(info != null){
                list.add(RecommendFriend.valueOf(info, RecommendFriend.SUPER_GAME_GOD, score, obj.getTotalCnt()));
            }
        }
    }

    private static class GameGoldRankObj implements Comparator<GameGoldRankObj>{

        private double winPer;
        private int accountId;
        private int totalCnt; //总局数
        private int godType; //1 大神 2 超级大神

        public int getAccountId() {
            return accountId;
        }
        public void setAccountId(int accountId) {
            this.accountId = accountId;
        }
        public int getTotalCnt() {
            return totalCnt;
        }
        public void setTotalCnt(int totalCnt) {
            this.totalCnt = totalCnt;
        }
        public double getWinPer() {
            return winPer;
        }
        public void setWinPer(double winPer) {
            this.winPer = winPer;
        }
        public int getGodType() {
            return godType;
        }
        public void setGodType(int godType) {
            this.godType = godType;
        }

        @Override
        public int compare(GameGoldRankObj o1, GameGoldRankObj o2) {
            if(o1.getWinPer() > o2.getWinPer()){
                return -1;
            }else if(o1.getWinPer() < o2.getWinPer()){
                return 1;
            }else{
                if(o1.getTotalCnt() > o2.getTotalCnt()){
                    return -1;
                }else if(o1.getTotalCnt() < o2.getTotalCnt()){
                    return 1;
                }
                return 0;
            }
        }
    }

    //补充大神数据
    private List<GameGoldRankObj> addGodAIData(Set<ZSetOperations.TypedTuple<String>> set) {
        List<GameGoldRankObj> rankList = new ArrayList<>();

        Set<Integer> realAccount = new HashSet<>();
        if(set != null && set.size() != 0){
            for(ZSetOperations.TypedTuple<String> typedTuple : set){
                String str = typedTuple.getValue();
                double score = typedTuple.getScore();
                GameGoldRankObj obj = JsonUtils.stringToObject(str, GameGoldRankObj.class);
                obj.setWinPer(score);
                rankList.add(obj);
                realAccount.add(obj.getAccountId());
            }
        }

        List<GodInfo> godInfos = accountService.queryAIGodList();
        if (! godInfos.isEmpty()) {
            if (rankList.size() < GAME_GOD_RANK_COUNT) {
                for (GodInfo godInfo : godInfos) {
                    if (realAccount.contains(godInfo.getAccountId())) {
                        continue;
                    }
                    GameGoldRankObj rankObj = new GameGoldRankObj();
                    BeanUtils.copyProperties(godInfo, rankObj);
                    rankList.add(rankObj);
                }
            } else if (addGodEnable) {
                for (GodInfo godInfo : godInfos) {
                    if (godInfo.getType() == 0) {
                        continue;
                    }
                    if (realAccount.contains(godInfo.getAccountId())) {
                        continue;
                    }
                    GameGoldRankObj rankObj = new GameGoldRankObj();
                    BeanUtils.copyProperties(godInfo, rankObj);
                    rankList.add(rankObj);
                }
            }
        }

        if (rankList.isEmpty()) {
            logger.debug("add AI God data: no data");
            return rankList;
        }

        rankList.sort(new GameGoldRankObj());

        List<GameGoldRankObj> resultList = new ArrayList<>();
        int rankLength = rankList.size();
        if (rankLength > GAME_GOD_RANK_COUNT) {
            rankLength = GAME_GOD_RANK_COUNT;
        }
        for(int i = 0 ; i < rankLength; i ++){
            GameGoldRankObj obj = rankList.get(i);
            if(i < GAME_SUPER_GOD_COUNT){
                obj.setGodType(RecommendFriend.SUPER_GAME_GOD);
            }else{
                obj.setGodType(RecommendFriend.GAME_GOD);
            }
            resultList.add(obj);
        }

        return resultList;
    }
}
