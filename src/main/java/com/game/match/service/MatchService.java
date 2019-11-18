package com.game.match.service;

import com.game.account.domain.AccountInfo;
import com.game.ai.service.AIService;
import com.game.chat.annotation.Protocol;
import com.game.chat.message.S2CMessage;
import com.game.common.vo.ResultVO;
import com.game.config.thread.ApiTask;
import com.game.config.thread.TaskIds;
import com.game.config.thread.ThreadPool;
import com.game.friend.service.FriendService;
import com.game.game.domain.GameAllInfo;
import com.game.game.service.GameService;
import com.game.login.service.LoginService;
import com.game.match.domain.*;
import com.game.netty.constant.MatchCmd;
import com.game.netty.constant.Module;
import com.game.netty.manager.ChannelManager;
import com.game.record.domain.Record;
import com.game.record.service.RecordService;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import com.google.gson.Gson;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

@Service
public class MatchService {
    private static MatchService instance = null;
    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);
    private static final int QUEUE_LENGTH = 10000;
    private static final int EVERY_MATCH_MAX_NUM = 20;
    private static final int ROOM_SIZE = 2;
    private static final int ROOM_EXPIRE_TIME = 7;
    private static final int FRIEND_INVITE_EXPIRE_TIME = 1; //好友邀请对战过期时间
    private static final String ROOM_CREATE_TIME = "create_time";
    private static final int MATCH_READY_TIMEOUT = 20000; //ms
    private static final int AI_ACCOUNT_NUM_BASE = 1000000;
    public static final int ACCOUNT_TYPE_AI = 1;
    private volatile Map<String, ArrayBlockingQueue<Integer>> matchPool = new HashMap<>();
    private static Set<Integer> preMatchList = new HashSet<>();
    private static Random random = new Random();
    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    @Autowired
    ThreadPool threadMatch;
    @Autowired
    ExecutorService threadHttp;
    @Autowired
    LoginService loginService;
    @Autowired
    private FriendService friendService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    RecordService recordService;
    @Autowired
    AIService aiService;
    @Autowired
    GameService gameService;

    @Value("${ai.account.total}")
    private int AIAccountTotal;
    @Value("${match.timer}")
    private int matchTimer;
    @Value("${match.fix.timer}")
    private int matchFixTimer;
    @Value("${match.ai.exit.time}")
    private int matchAIExitTime;
    @Value("${match.ai.exit.base.time}")
    private int matchAIExitBaseTime;
    @Value("${match.ai.refuse.time}")
    private int matchAIRefuseTime;
    @Value("${match.ai.refuse.base.time}")
    private int matchAIRefuseBaseTime;
    @Value("${match.ai.accept.time}")
    private int matchAIAcceptTime;
    @Value("${match.ai.accept.base.time}")
    private int matchAIAcceptBaseTime;
    @Value("${is.test}")
    private boolean isTest;
    @Value("${broadcast.invite.enable}")
    private boolean broadcastInviteEnable;
    @Value("${broadcast.invite.limit}")
    private int broadcastInviteLimit;

    @PostConstruct
    private void init(){
        instance = this;
    }

    public static MatchService getInstance(){
        return instance;
    }

    /**
     * 双人对战匹配
     */
    public void dealMatch() {
        logger.debug("start match");
        if (matchPool.isEmpty()) {
            logger.debug("no match data");
            return;
        }

        for (Map.Entry<String, ArrayBlockingQueue<Integer>> entry : matchPool.entrySet()) {
            logger.debug("start match game:" + entry.getKey());
            List<Integer> readyMatchList = new ArrayList<>();
            entry.getValue().drainTo(readyMatchList, EVERY_MATCH_MAX_NUM);
            if (readyMatchList.isEmpty()) {
                logger.debug("game " + entry.getKey() + ", no match data");
            } else if (readyMatchList.size() == 1) {
                logger.debug("game " + entry.getKey() + ", match only one");
                //匹配失败，不创建房间
                dealMatchFail(entry.getKey(), readyMatchList.get(0));
            } else if (readyMatchList.size() == ROOM_SIZE) {
                logger.debug("game " + entry.getKey() + ", match only two");
                //匹配成功，需创建房间
                dealMatchSuccess(entry.getKey(), readyMatchList);
            } else {
                //TODO 双人对战匹配，目前按照顺序两两返回，后续使用匹配算法
                logger.debug("game " + entry.getKey() + ", match much:" + new Gson().toJson(readyMatchList));
                double loop = Math.ceil((double) readyMatchList.size() / ROOM_SIZE);
                logger.debug("match loop:" + loop);
                for (int page = 0; page < loop; page ++) {
                    int offset = page * ROOM_SIZE;
                    int toIndex = offset + ROOM_SIZE;
                    if (toIndex >= readyMatchList.size()) {
                        toIndex = readyMatchList.size();
                    }
                    List<Integer> players = readyMatchList.subList(offset, toIndex);
                    if (players.size() == ROOM_SIZE) {
                        dealMatchSuccess(entry.getKey(), players);
                    } else {
                        dealMatchFail(entry.getKey(), players.get(0));
                    }
                }
            }

        }
    }

    //指定玩家立即匹配AI
    private void matchAtOnce(String gameId, int accountId) {
        logger.debug("time done, do match AI");
        if (matchPool.get(gameId).contains(accountId)) {
            matchPool.get(gameId).remove(accountId);
            //TODO 管理定时任务则考虑删除任务关系
            dealMatchFail(gameId, accountId);
        }
    }

    private void dealMatchFail(String gameId, int accountId) {
        int AIAccountId = random.nextInt(AIAccountTotal) + AI_ACCOUNT_NUM_BASE;
        logger.debug("match fail, do AI, real accountId:" + accountId + ", AI:" + AIAccountId);

        List<PlayerVO> playerVOS = new ArrayList<>();
        AccountInfo accountInfo = loginService.getAccountInfo(accountId);
        PlayerVO playerVO = PlayerVO.valueOf(accountInfo);
        playerVOS.add(playerVO);

        AccountInfo aiAccountInfo = loginService.getAccountInfo(AIAccountId);
        PlayerVO playerAI = PlayerVO.valueOf(aiAccountInfo);

        //根据玩家级别计算AI级别
        int AIRank = aiService.getAIRank(accountId, gameId);
        playerAI.setAiRank(AIRank);
        playerVOS.add(playerAI);

        //为AI打点：
        recordAI(AIAccountId, gameId, Constant.RECORD_EVENT_GAME_START);

        sendMatchMessage(gameId, playerVOS);
    }

    private void dealMatchSuccess(String gameId, List<Integer> accountIds) {
        List<PlayerVO> playerVOS = new ArrayList<>();
        //补充用户信息
        for (int accountId : accountIds) {
            AccountInfo accountInfo = loginService.getAccountInfo(accountId);
            PlayerVO playerVO = PlayerVO.valueOf(accountInfo);
            playerVOS.add(playerVO);
        }

        sendMatchMessage(gameId, playerVOS);
    }

    private void sendMatchMessage(String gameId, List<PlayerVO> players) {
        S2CMessage resultMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.MATCH_RESULT_S2C);
        MatchResultVO content = new MatchResultVO();
        content.setGameId(gameId);
        String roomId = CommonFunUtil.buildGameRoomId(players, gameId);
        content.setRoomId(roomId);
        content.setPlayers(players);
        resultMessage.setResult(ResultVO.success(content));

        for (PlayerVO account : players) {
            if (account.getAi() != ACCOUNT_TYPE_AI) {
                ChannelManager.sendMessage(account.getAccountId(), resultMessage);
            }
        }

        logger.debug("match done, room id:" + roomId);

        // redis保留房间信息
        HashOperations operations = redisTemplate.opsForHash();
        Map<String, String> roomPlayer = new HashMap<>();
        for (PlayerVO account : players) {
            if (account.getAi() == ACCOUNT_TYPE_AI) {
                roomPlayer.put(String.valueOf(account.getAccountId()), "1");
            } else {
                roomPlayer.put(String.valueOf(account.getAccountId()), "0");
            }
        }
        roomPlayer.put(ROOM_CREATE_TIME, String.valueOf(new Date().getTime()));

        String redisKey = Constant.REDIS_KEY_PREFIX_ROOM_INFO + roomId;
        operations.putAll(redisKey, roomPlayer);
        //设置过期
        redisTemplate.expire(redisKey, ROOM_EXPIRE_TIME, TimeUnit.DAYS);
    }

    private void dealMatchReadySuccess(String gameId, String roomId, Set<String> accountIds) {
        long curTime = System.currentTimeMillis();
        S2CMessage resultMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.MATCH_READY_RESULT_S2C);
        MatchResultVO content = new MatchResultVO();
        content.setGameId(gameId);
        content.setRoomId(roomId);
        content.setCurTime(curTime);
        resultMessage.setResult(ResultVO.success(content));
        logger.debug("match ready done, room id:" + roomId);

        for (String accountId : accountIds) {
            if (! accountId.equals(ROOM_CREATE_TIME)) {
                ChannelManager.sendMessage(Integer.valueOf(accountId), resultMessage);
            }
        }
    }

    private void dealMatchReadyFail(Map<String, String> rooms) {
        S2CMessage resultMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.MATCH_READY_RESULT_S2C);
        resultMessage.setResult(ResultVO.error("match ready timeout", Constant.RNT_CODE_MATCH_READY_TIMEOUT));

        for (String key : rooms.keySet()) {
            if (key.equals(ROOM_CREATE_TIME)) {
                continue;
            }
            logger.debug("match ready timeout, accountId:" + key);
            ChannelManager.sendMessage(Integer.parseInt(key), resultMessage);
        }
    }

    @Protocol(module = Module.MATCH, cmd = MatchCmd.MATCH_C2S)
    public void addEleToMatchPool(MatchMessage matchMessage) {
        logger.info("add match id:" + matchMessage.getAccountId());
        //从预匹配队列移除
        rmPreMatchList(matchMessage.getAccountId());
        threadMatch.getExecutor(TaskIds.MATCH).execute(new ApiTask(TaskIds.MATCH, ()->{
            try {
                boolean isAdd = false;
                if (matchPool.containsKey(matchMessage.getGameId())) {
                    if (matchPool.get(matchMessage.getGameId()).contains(matchMessage.getAccountId())) {
                        logger.warn("queue contain id, gameId:" + matchMessage.getGameId() + ", accountId:" + matchMessage.getAccountId());
                        return;
                    }
                    //put阻塞
                    isAdd = matchPool.get(matchMessage.getGameId()).offer(matchMessage.getAccountId());
                } else {
                    ArrayBlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<Integer>(QUEUE_LENGTH);
                    isAdd = blockingQueue.offer(matchMessage.getAccountId());
                    matchPool.put(matchMessage.getGameId(), blockingQueue);
                }
                if (! isAdd) {
                    logger.error("add matchPool fail, pool size:" + matchPool.get(matchMessage.getGameId()).size());
                    return;
                }

                ArrayBlockingQueue<Integer> queue = matchPool.get(matchMessage.getGameId());
                //队列仅一个，开始计时匹配，超时匹配机器人
                if (queue.size() == 1) {
                    scheduledExecutorService.schedule(()->match(matchMessage.getGameId(), matchMessage.getAccountId()), CommonFunUtil.genMatchTimeout(matchTimer, matchFixTimer), TimeUnit.SECONDS);
                    //发群邀请
                    broadcastGameInvite(matchMessage.getGameId());
                }
                //队列存在大于一个，判断和其他玩家是否拉黑关系，是进入倒计时，否直接匹配
                if (queue.size() >= 2) {
                    boolean isMatch = false;
                    Iterator<Integer> iterator = queue.iterator();
                    while (iterator.hasNext()) {
                        int targetId = iterator.next();
                        if (targetId != matchMessage.getAccountId()) {
                            if (! isBlackPlayer(targetId, matchMessage.getAccountId())) {
                                List<Integer> ids = new ArrayList<>(Arrays.asList(targetId, matchMessage.getAccountId()));
                                matchPool.get(matchMessage.getGameId()).removeAll(ids);
                                dealMatchSuccess(matchMessage.getGameId(), ids);
                                isMatch = true;
                                break;
                            }
                        }
                    }
                    if (! isMatch) {
                        scheduledExecutorService.schedule(()->match(matchMessage.getGameId(), matchMessage.getAccountId()), CommonFunUtil.genMatchTimeout(matchTimer, matchFixTimer), TimeUnit.SECONDS);
                        //发群邀请
                        broadcastGameInvite(matchMessage.getGameId());
                    }
                }
            } catch (Exception e) {
                logger.error("add match pool exception, msg:" + e.getMessage());
            }
        }));
    }

    //玩家是否有拉黑对方
    private boolean isBlackPlayer(int id1, int id2){
        if(friendService.isBlack(id1, id2)){
            return true;
        }

        if(friendService.isBlack(id2, id1)){
            return true;
        }

        return false;
    }

    /**
     * 针对机器人做随机反应
     * @param matchMessage
     */
    private void fixAIPlayerAction(MatchMessage matchMessage, List<Integer> accounts) {
        Random random = new Random();
        int aiAction = 2;
        if (! isTest) {
            aiAction = random.nextInt(3);
        }

        if (aiAction == 0) {
            int rejectTime = random.nextInt(matchAIRefuseTime) + matchAIRefuseBaseTime;
            scheduledExecutorService.schedule(()->noticeRefuse(matchMessage, accounts), rejectTime, TimeUnit.SECONDS);
        } else if (aiAction == 1) {
            int exitTime = random.nextInt(matchAIExitTime) + matchAIExitBaseTime;
            scheduledExecutorService.schedule(()->noticeExitRoom(matchMessage, accounts), exitTime, TimeUnit.SECONDS);
        } else if (aiAction == 2) {
            int acceptTime = random.nextInt(matchAIAcceptTime) + matchAIAcceptBaseTime;
            scheduledExecutorService.schedule(()->noticeMatchSuccess(matchMessage, accounts), acceptTime, TimeUnit.SECONDS);
        }
    }

    @Protocol(module = Module.MATCH, cmd = MatchCmd.MATCH_CANCEL_C2S)
    public void rmEleFromMatchPool(MatchMessage matchMessage) {
        logger.info("rm match id:" + matchMessage.getAccountId());
        threadMatch.getExecutor(TaskIds.MATCH_CANCEL).execute(new ApiTask(TaskIds.MATCH_CANCEL, ()->{
            if (matchPool.containsKey(matchMessage.getGameId())) {
                matchPool.get(matchMessage.getGameId()).remove(matchMessage.getAccountId());
                //TODO 取消定时任务
            }
        }));
    }

    /**
     * 自动准备
     * @param matchMessage
     */

    @Protocol(module = Module.MATCH, cmd = MatchCmd.MATCH_READY_C2S)
    public void dealMatchReady(MatchMessage matchMessage) {
        logger.info("deal match ready");
        if (TextUtils.isEmpty(matchMessage.getRoomId())) {
            logger.debug("match ready, roomId empty");
            return;
        }

        threadMatch.getExecutor(TaskIds.READY).execute(new ApiTask(TaskIds.READY, () -> {
            HashOperations operations = redisTemplate.opsForHash();
            String redisKey = Constant.REDIS_KEY_PREFIX_ROOM_INFO + matchMessage.getRoomId();
            Map<String, String> rooms = operations.entries(redisKey);
            if (rooms.isEmpty()) {
                logger.warn("room:" + matchMessage.getRoomId() + " not exist");
                return;
            }
            //是否超时
            String createTime = rooms.get(ROOM_CREATE_TIME);
            Long nowTime = new Date().getTime();
            if ((nowTime - Long.valueOf(createTime)) > MATCH_READY_TIMEOUT) {
                logger.info("ready timeout:" + (nowTime - Long.valueOf(createTime)));
                dealMatchReadyFail(rooms);

                //暂不解散房间，防止其他用户ready
                return;
            }

            Boolean isReady = true;
            String accountId = String.valueOf(matchMessage.getAccountId());
            for (String key : rooms.keySet()) {
                if (key.equals(ROOM_CREATE_TIME)) {
                    continue;
                }
                if (!key.equals(accountId) && rooms.get(key).equals("0")) {
                    logger.debug("accountId:" + accountId + " not ready");
                    isReady = false;
                    break;
                }
            }

            if (isReady) {
                Set<String> accountIds = operations.keys(redisKey);
                dealMatchReadySuccess(matchMessage.getGameId(), matchMessage.getRoomId(), accountIds);
            } else {
                logger.info("用户准备消息:" + matchMessage);
                //加一个定时，10s之后，如果房间内的其它人没有准备，则直接让准备过的人开始游戏
                scheduledExecutorService.schedule(() -> {
                    readyTimeout(matchMessage);
                }, 10, TimeUnit.SECONDS);
            }

            operations.put(redisKey, accountId, "1");
        }));
    }

    private void readyTimeout(MatchMessage matchMessage){
        logger.info("10s超时执行任务:" + matchMessage);
        HashOperations operations = redisTemplate.opsForHash();
        String key = Constant.REDIS_KEY_PREFIX_ROOM_INFO + matchMessage.getRoomId();
        Map<String, String> trooms = operations.entries(key);
        if (trooms.isEmpty()) {
            return;
        }
        boolean allReady = true;
        for (String roomKey : trooms.keySet()) {
            if (roomKey.equals(ROOM_CREATE_TIME)) {
                continue;
            }
            if (! roomKey.equals(String.valueOf(matchMessage.getAccountId())) && trooms.get(roomKey).equals("0")) {
                logger.debug("accountId:" + matchMessage.getAccountId() + " not ready");
                allReady = false;
                break;
            }
        }
        if(!allReady){
            //Set<String> accountIds = operations.keys(key);
            Set<String> accountIds = trooms.keySet();
            logger.info("提交10s超时定时任务.");
            dealMatchReadySuccess(matchMessage.getGameId(), matchMessage.getRoomId(), accountIds);
        }
    }

    /***
     * 换个对手
     * @param matchMessage
     */
    @Protocol(module = Module.MATCH, cmd = MatchCmd.MATCH_CHANGE_OPPONENT_C2S)
    public void changeOpponent(MatchMessage matchMessage){
        logger.info("player:" + matchMessage.getAccountId() + ", change opponent.");
        threadMatch.getExecutor(TaskIds.CHANGE_OPPONENT).execute(new ApiTask(TaskIds.CHANGE_OPPONENT, () -> {
            // 1. 将玩家从房间移除
            playerExitFromRoom(matchMessage.getRoomId(), matchMessage.getAccountId(), matchMessage.getGameId());
            addEleToMatchPool(matchMessage);
        }));
    }

    /***
     * 再来一局
     * @param matchMessage
     */
    @Protocol(module = Module.MATCH, cmd = MatchCmd.MATCH_ONCE_AGAIN_C2S)
    public void onceAgain(MatchMessage matchMessage){
        logger.info("player:" + matchMessage.getAccountId() + ", once again.");
        threadMatch.getExecutor(TaskIds.CHANGE_OPPONENT).execute(new ApiTask(TaskIds.ONCE_AGAIN, () ->{
            S2CMessage resultMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.MATCH_ONCE_AGAIN_S2C);
            List<Integer> accountInfos = getRoomAccountInfo(matchMessage.getRoomId());
            if(accountInfos == null){
                return;
            }
            if(!accountInfos.contains(matchMessage.getAccountId())){
                logger.info("room not contain account:" + matchMessage.getAccountId());
                return;
            }

            if(accountInfos.size() == 1){
                logger.info("room only player himself. account:" + matchMessage.getAccountId());
                return;
            }else{
                //判断除了自己，其它的人是不是都是机器人
                boolean allRebot = true;
                for(Integer account : accountInfos){
                    if(account != matchMessage.getAccountId() && loginService.getAccountInfo(account).getPlatform() != Constant.ACCOUNT_PLATFORM_AI){
                        allRebot = false;
                    }
                }
                if(! allRebot){
                    resultMessage.setResult(ResultVO.success(matchMessage));
                    for(Integer target : accountInfos){
                        if(loginService.getAccountInfo(target) != null && target != matchMessage.getAccountId()){
                            ChannelManager.sendMessage(target, resultMessage);
                        }
                    }
                }else{
                    //机器人的情况：
                    fixAIPlayerAction(matchMessage, accountInfos);
                    //matchSuccess(accountInfos, matchMessage);
                }
            }
        }));
    }

    @Protocol(module = Module.MATCH, cmd = MatchCmd.MATCH_AGAIN_CANCEL_C2S)
    public void onceAgainCancel(MatchMessage matchMessage){
        logger.info("player:" + matchMessage.getAccountId() + ", once again cancel.");
        threadMatch.getExecutor(TaskIds.AGAIN_CANCEL).execute(new ApiTask(TaskIds.AGAIN_CANCEL, () -> {
            List<Integer> accountInfos = getRoomAccountInfo(matchMessage.getRoomId());
            if(accountInfos == null){
                return;
            }
            if(!accountInfos.contains(matchMessage.getAccountId())){
                logger.info("room not contain account:" + matchMessage.getAccountId());
                return;
            }
            //消息转发
            if(accountInfos.size() == 2){
                S2CMessage s2CMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.MATCH_AGAIN_CANCEL_S2C);
                s2CMessage.setResult(ResultVO.success(matchMessage));
                for(Integer account : accountInfos){
                    if(account != matchMessage.getAccountId()){
                        ChannelManager.sendMessage(account, s2CMessage);
                    }
                }
            }
        }));
    }

    /***
     * 加入邀请
     * @param matchMessage
     */
    @Protocol(module = Module.MATCH, cmd = MatchCmd.MATCH_ENTER_INVITE_C2S)
    public void enterInvite(MatchMessage matchMessage){
        logger.info("player:" + matchMessage.getAccountId() + ", enter invite.");
        threadMatch.getExecutor(TaskIds.ENTER_INVITE).execute(new ApiTask(TaskIds.ENTER_INVITE, () -> {
            List<Integer> accountInfos = getRoomAccountInfo(matchMessage.getRoomId());
            if(accountInfos == null){
                return;
            }
            if(!accountInfos.contains(matchMessage.getAccountId())){
                logger.info("room not contain account:" + matchMessage.getAccountId());
                return;
            }

            // 暂时只针对二人场处理
            if(accountInfos.size() == 2){
                matchSuccess(accountInfos, matchMessage);
            }
        }));
    }

    @Protocol(module = Module.MATCH, cmd = MatchCmd.MATCH_EXIT_ROOM_C2S)
    public void exitRoom(MatchMessage matchMessage){
        logger.info("player:" + matchMessage.getAccountId() + ", exit room.");
        threadMatch.getExecutor(TaskIds.EXIT_ROOM).execute(new ApiTask(TaskIds.EXIT_ROOM, () -> {
            playerExitFromRoom(matchMessage.getRoomId(), matchMessage.getAccountId(), matchMessage.getGameId());
        }));

        addPreMatchList(matchMessage.getAccountId());
    }

    @Protocol(module = Module.MATCH, cmd = MatchCmd.MATCH_REFUSE_INVITE_C2S)
    public void refusePlayAgain(MatchMessage matchMessage){
        logger.info("player:" + matchMessage.getAccountId() + ", refuse.");
        threadMatch.getExecutor(TaskIds.REFUSE_INVITE).execute(new ApiTask(TaskIds.REFUSE_INVITE, () -> {
            List<Integer> accountInfos = getRoomAccountInfo(matchMessage.getRoomId());
            if(accountInfos == null){
                return;
            }
            if(!accountInfos.contains(matchMessage.getAccountId())){
                logger.info("room not contain account:" + matchMessage.getAccountId());
                return;
            }
            //消息转发
            if(accountInfos.size() == 2){
                S2CMessage s2CMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.MATCH_REFUSE_INVITE_S2C);
                s2CMessage.setResult(ResultVO.success(matchMessage));
                for(Integer account : accountInfos){
                    if(account != matchMessage.getAccountId()){
                        ChannelManager.sendMessage(account, s2CMessage);
                    }
                }
            }
        }));
    }

    /***
     * 换个游戏
     * @param matchMessage
     */
    @Protocol(module = Module.MATCH, cmd = MatchCmd.MATCH_CHANGE_ANOTHER_GAME_C2S)
    public void changeAnotherGame(MatchMessage matchMessage){
        logger.info("player:" + matchMessage.getAccountId() + ", change another game:" + matchMessage.getGameId());
        threadMatch.getExecutor(TaskIds.CHANGE_ANOTHER_GAME).execute(new ApiTask(TaskIds.CHANGE_ANOTHER_GAME,() ->{
            S2CMessage resultMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.MATCH_CHANGE_ANOTHER_GAME_S2C);
            List<Integer> accountInfos = getRoomAccountInfo(matchMessage.getRoomId());
            if(accountInfos == null){
                return;
            }
            if(!accountInfos.contains(matchMessage.getAccountId())){
                logger.info("room not contain account:" + matchMessage.getAccountId());
                return;
            }

            if(accountInfos.size() == 1){
                logger.info("room only player himself. account:" + matchMessage.getAccountId());
                return;
            }else{
                //判断除了自己，其它的人是不是都是机器人
                boolean allRebot = true;
                for(Integer account : accountInfos){
                    if(account != matchMessage.getAccountId() && loginService.getAccountInfo(account).getPlatform() != Constant.ACCOUNT_PLATFORM_AI){
                        allRebot = false;
                    }
                }
                if(! allRebot){
                    resultMessage.setResult(ResultVO.success(matchMessage));
                    for(Integer target : accountInfos){
                        if(loginService.getAccountInfo(target).getPlatform() != Constant.ACCOUNT_PLATFORM_AI && target != matchMessage.getAccountId()){
                            ChannelManager.sendMessage(target, resultMessage);
                        }
                    }
                }else{
                    //机器人情况：
                    fixAIPlayerAction(matchMessage, accountInfos);
                    //matchSuccess(accountInfos, matchMessage);
                }
            }
        }));
    }

    /***
     * 邀请好友对战
     * @param inviteMsg
     */
    @Protocol(module = Module.MATCH, cmd = MatchCmd.INVITE_FRIEND_MATCH_C2S)
    public void inviteFriendMatch(MatchMessage inviteMsg){
        logger.info("player:" + inviteMsg.getAccountId() + ", invite Friend:" + inviteMsg.getFriendId());
        //通知好友对战
        if(friendService.isBlack(inviteMsg.getAccountId(), inviteMsg.getFriendId())) {
            S2CMessage s2CMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.INVITE_FREND_REFUSE_BECAUSE_BLACK);
            ResultVO vo = ResultVO.error("blacked.", 1);
            vo.setData(inviteMsg);
            s2CMessage.setResult(vo);
            ChannelManager.sendMessage(inviteMsg.getAccountId(), s2CMessage);
            logger.info("用户:" + inviteMsg.getAccountId() + "约战黑名单用户:" + inviteMsg.getFriendId());
            return;
        }else if(friendService.isBlack(inviteMsg.getFriendId(), inviteMsg.getAccountId())){
            S2CMessage s2CMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.INVITE_FREND_REFUSE_BECAUSE_BLACK);
            ResultVO vo = ResultVO.error("blacked.", 2);
            vo.setData(inviteMsg);
            s2CMessage.setResult(vo);
            ChannelManager.sendMessage(inviteMsg.getAccountId(), s2CMessage);
            logger.info("用户:" + inviteMsg.getAccountId() + "约战黑名单用户:" + inviteMsg.getFriendId());
            return;
        }

        ValueOperations operations = redisTemplate.opsForValue();
        String key = Constant.REDIS_KEY_FRIEND_INVITE_INFO + inviteMsg.getAccountId();
        operations.set(key, String.valueOf(inviteMsg.getFriendId()));
        redisTemplate.expire(key, FRIEND_INVITE_EXPIRE_TIME, TimeUnit.DAYS);
        int friendId = inviteMsg.getFriendId();
        if(ChannelManager.getInstance().isOnline(friendId)){
            inviteMsg.setFriendId(inviteMsg.getAccountId());
            S2CMessage s2CMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.INVITE_FRIEND_MATCH_S2C);
            s2CMessage.setResult(ResultVO.success(inviteMsg));
            ChannelManager.sendMessage(friendId, s2CMessage);
        }
    }

    @Protocol(module = Module.MATCH, cmd = MatchCmd.INVITE_FRIEND_MATCH_CANCEL_C2S)
    public void inviteFriendMatchCancel(MatchMessage inviteMsg){
        logger.info("player:" + inviteMsg.getAccountId() + ", cancel invite Friend:" + inviteMsg.getFriendId());
        String key = Constant.REDIS_KEY_FRIEND_INVITE_INFO + inviteMsg.getAccountId();
        redisTemplate.delete(key);
        int friendId = inviteMsg.getFriendId();
        if(friendService.isBlack(inviteMsg.getAccountId(), inviteMsg.getFriendId()) ||
                friendService.isBlack(inviteMsg.getFriendId(), inviteMsg.getAccountId())){
            return;
        }
        //通知好友，取消邀请
        if(ChannelManager.getInstance().isOnline(friendId)){
            inviteMsg.setFriendId(inviteMsg.getAccountId());
            S2CMessage s2CMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.INVITE_FRIEND_MATCH_CANCEL_S2C);
            s2CMessage.setResult(ResultVO.success(inviteMsg));
            ChannelManager.sendMessage(friendId, s2CMessage);
        }
    }

    /***
     * 拒绝好友的约战邀请
     * @param inviteMsg
     */
    @Protocol(module = Module.MATCH, cmd = MatchCmd.FRIEND_REFUSE_MATCH_C2S)
    public void inviteFriendMatchRefuse(MatchMessage inviteMsg){
        logger.info("player:" + inviteMsg.getAccountId() + ", refuse Friend:" + inviteMsg.getFriendId() + " invite.");
        String key = Constant.REDIS_KEY_FRIEND_INVITE_INFO + inviteMsg.getFriendId();
        redisTemplate.delete(key);
        //通知好友，邀请被拒绝
        int friendId = inviteMsg.getFriendId();
        if(ChannelManager.getInstance().isOnline(friendId)){
            inviteMsg.setFriendId(inviteMsg.getAccountId());
            S2CMessage s2CMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.FRIEND_REFUSE_MATCH_S2C);
            s2CMessage.setResult(ResultVO.success(inviteMsg));
            ChannelManager.sendMessage(friendId, s2CMessage);
        }
    }

    @Protocol(module = Module.MATCH, cmd = MatchCmd.FRIEND_ENTER_MATCH_C2S)
    public void enterFriendMatch(MatchMessage inviteMsg){
        logger.info("player:" + inviteMsg.getAccountId() + ", enter Friend:" + inviteMsg.getFriendId() + " invite.");
        ValueOperations operations = redisTemplate.opsForValue();
        String key = Constant.REDIS_KEY_FRIEND_INVITE_INFO + inviteMsg.getFriendId();
        String value = (String)operations.get(key);
        if(!value.equals(String.valueOf(inviteMsg.getAccountId()))){
            logger.info("acccountId:"+ inviteMsg.getAccountId() +"进入未邀请的好友:"+inviteMsg.getFriendId()+"对战，不处理");
            return;
        }
        List<Integer> list = new ArrayList<>();
        list.add(inviteMsg.getFriendId());
        list.add(inviteMsg.getAccountId());
        dealMatchSuccess(inviteMsg.getGameId(), list);
    }

    /***
     * 获取房间内所有真实玩家id
     * @param roomId
     * @return
     */
    private List<Integer> getRoomAccountInfo(String roomId){
        String roomKey = Constant.REDIS_KEY_PREFIX_ROOM_INFO + roomId;
        HashOperations hashOperations = redisTemplate.opsForHash();
        Map<String, String> room = hashOperations.entries(roomKey);
        if(room.isEmpty()){
            return null;
        }
        List<Integer> result = new ArrayList<>();
        for(Map.Entry<String, String> entry : room.entrySet()){
            String key = entry.getKey();
            if(key.equals(ROOM_CREATE_TIME)){
                continue;
            }
            try {
                int accountId = Integer.parseInt(key);
                result.add(accountId);
            }catch (Exception e){
            }
        }
        return result;
    }

    private void playerExitFromRoom(String roomId, int accountId, String gameId){
        String roomKey = Constant.REDIS_KEY_PREFIX_ROOM_INFO + roomId;
        HashOperations hashOperations = redisTemplate.opsForHash();
        Map<String, String> room = hashOperations.entries(roomKey);
        if(room.isEmpty()){
            logger.info("player:" + accountId + ", exit room, but room is empty");
            return;
        }
        boolean change = false;
        List<Integer> broadCastPlayer = new ArrayList<>();
        for(Map.Entry<String, String> entry : room.entrySet()){
            if(entry.getKey().equals(ROOM_CREATE_TIME)){
                continue;
            }
            if(entry.getKey().equals(String.valueOf(accountId))){
                change = true;
            }else{
                try {
                    int anotherAccount = Integer.parseInt(entry.getKey());
                    broadCastPlayer.add(anotherAccount);
                }catch (Exception e){
                }
            }
        }
        if(change){
            for(Integer target : broadCastPlayer){
                //如果target是机器人，可以不用处理，就算发送也会失败
                broadcastPlayerExitRoom(roomId, target, gameId, accountId);
            }
        }

    }

    private void broadcastPlayerExitRoom(String roomId, int accountId, String gameId, int exitAccount){
        S2CMessage resultMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.MATCH_EXIT_ROOM_S2C);
        MatchMessage content = new MatchMessage();
        content.setAccountId(exitAccount);
        content.setRoomId(roomId);
        content.setGameId(gameId);
        resultMessage.setResult(ResultVO.success(content));
        ChannelManager.sendMessage(accountId, resultMessage);
    }

    private void addPoolAccount(String gameId, int accountId){
        // 2. 重新放入匹配池(最好把下面的代码包装成一个函数调用)
        try {
            if (matchPool.containsKey(gameId)) {
                matchPool.get(gameId).put(accountId);
            } else {
                ArrayBlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<Integer>(QUEUE_LENGTH);
                blockingQueue.put(accountId);
                matchPool.put(gameId, blockingQueue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 直接匹配成功
     * @param accounts
     * @param matchMessage
     */
    private void matchSuccess(Collection<Integer> accounts, MatchMessage matchMessage){
        List<PlayerVO> playerVOS = new ArrayList<>();
        for(Integer target : accounts){
            AccountInfo accountInfo = loginService.getAccountInfo(target);
            PlayerVO playerVO = PlayerVO.valueOf(accountInfo);
            if (playerVO.getAi() == ACCOUNT_TYPE_AI) {
                //根据玩家级别计算AI级别
                int AIRank = aiService.getAIRank(matchMessage.getAccountId(), matchMessage.getGameId());
                playerVO.setAiRank(AIRank);

                //为AI打点
                recordAI(playerVO.getAccountId(), matchMessage.getGameId(), Constant.RECORD_EVENT_GAME_START);
            }

            playerVOS.add(playerVO);
        }
        S2CMessage s2CMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.MATCH_RESULT_S2C);
        MatchResultVO content = new MatchResultVO();
        content.setGameId(matchMessage.getGameId());
        content.setRoomId(matchMessage.getRoomId());
        content.setPlayers(playerVOS);
        s2CMessage.setResult(ResultVO.success(content));

        //刷新房间创建时间，用于ready超时处理
        flushRoomInfo(matchMessage.getRoomId(), playerVOS);

        for(Integer target : accounts){
            if(loginService.getAccountInfo(target) != null){
                ChannelManager.sendMessage(target, s2CMessage);
            }
        }
    }

    /***
     * 数据转发通道
     * @param msg
     */

    @Protocol(module = Module.MATCH, cmd = MatchCmd.COMMON_TRANSFER_PIPLINE_C2S)
    public void transferCommonMessage(CommonMessage msg, int accountId){
        String roomId = msg.getRoomId();
        HashOperations operations = redisTemplate.opsForHash();
        String redisKey = Constant.REDIS_KEY_PREFIX_ROOM_INFO + roomId;
        Map<String, String> room = operations.entries(redisKey);
        if (room.isEmpty()) {
            logger.warn("room:" + roomId + " not exist");
            return;
        }
        for(Map.Entry<String, String> entry : room.entrySet()){
            if(entry.getKey().equals(ROOM_CREATE_TIME)){
                continue;
            }

            try {
                int id = Integer.parseInt(entry.getKey());
                if(id != accountId){
                    S2CMessage s2CMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.COMMON_TRANSFER_PIPLINE_S2C);
                    CommonMessageVO vo = new CommonMessageVO();
                    vo.setData(msg.getData());
                    s2CMessage.setResult(ResultVO.success(vo));
                    ChannelManager.sendMessage(id, s2CMessage);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Protocol(module = Module.MATCH, cmd = MatchCmd.BROADCAST_ACCEPT_GAME_INVITE_C2S)
    public void acceptBroadcastInvite(MatchMessage matchMessage){
        logger.debug("accept broadcast invite, accountId:" + matchMessage.getAccountId());
        addEleToMatchPool(matchMessage);
    }

    private void flushRoomInfo(String roomId, List<PlayerVO> list) {
        String roomKey = Constant.REDIS_KEY_PREFIX_ROOM_INFO + roomId;
        HashOperations hashOperations = redisTemplate.opsForHash();

        Map<String, String> roomPlayer = new HashMap<>();
        for (PlayerVO playerVO : list) {
            if (playerVO.getAi() == ACCOUNT_TYPE_AI) {
                roomPlayer.put(String.valueOf(playerVO.getAccountId()), "1");
            } else {
                roomPlayer.put(String.valueOf(playerVO.getAccountId()), "0");
            }
        }
        roomPlayer.put(ROOM_CREATE_TIME, String.valueOf(new Date().getTime()));

        hashOperations.putAll(roomKey, roomPlayer);
        //设置过期
        redisTemplate.expire(roomKey, ROOM_EXPIRE_TIME, TimeUnit.DAYS);
    }

    public void match(String gameId, int accountId) {
        threadMatch.getExecutor(TaskIds.MATCH_METHOD).execute(new ApiTask(TaskIds.MATCH_METHOD, ()->{
            //匹配逻辑
            matchAtOnce(gameId, accountId);
        }));
    }

    private void noticeExitRoom(MatchMessage matchMessage, List<Integer> accounts) {
        logger.debug("notice exit room, accountId:" + matchMessage.getAccountId());
        threadMatch.getExecutor(TaskIds.NOTICE_EXIT_ROOM).execute(new ApiTask(TaskIds.NOTICE_EXIT_ROOM, ()->{
            String roomKey = Constant.REDIS_KEY_PREFIX_ROOM_INFO + matchMessage.getRoomId();
            HashOperations hashOperations = redisTemplate.opsForHash();
            for(Integer id : accounts){
                if (id == matchMessage.getAccountId()) {
                    continue;
                }
                broadcastPlayerExitRoom(matchMessage.getRoomId(), matchMessage.getAccountId(), matchMessage.getGameId(), id);
                hashOperations.delete(roomKey, String.valueOf(id));
            }
        }));
    }

    private void noticeRefuse(MatchMessage matchMessage, List<Integer> accounts) {
        logger.debug("notice refuse, accountId:" + matchMessage.getAccountId());
        threadMatch.getExecutor(TaskIds.NOTICE_REFUSE).execute( new ApiTask(TaskIds.NOTICE_REFUSE, ()-> {
            for(Integer id : accounts){
                if (id == matchMessage.getAccountId()) {
                    continue;
                }

                S2CMessage s2CMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.MATCH_REFUSE_INVITE_S2C);
                MatchMessage content = new MatchMessage();
                content.setAccountId(id);
                content.setRoomId(matchMessage.getRoomId());
                s2CMessage.setResult(ResultVO.success(content));
                ChannelManager.sendMessage(matchMessage.getAccountId(), s2CMessage);
            }
        }));
    }

    private void noticeMatchSuccess(MatchMessage matchMessage, List<Integer> accounts) {
        logger.debug("notice match success, accountId:" + matchMessage.getAccountId());
        threadMatch.getExecutor(TaskIds.NOTICE_MATCH_SUCCESS).execute( new ApiTask(TaskIds.NOTICE_MATCH_SUCCESS, () -> {
            matchSuccess(accounts, matchMessage);
        }));
    }

    private void broadcastGameInvite(String gameId) {
        threadHttp.execute(()->{
            if (!broadcastInviteEnable) {
                return;
            }
            if (preMatchList.isEmpty()) {
                return;
            }
            logger.info("preMatchList size:" + preMatchList.size());

            GameAllInfo game = gameService.queryGame(gameId);
            if (game == null) {
                return;
            }
            String gameName = game.getVersions().get(0).getgLan().get(Constant.LANGUAGE_DEFAULT).getName();

            S2CMessage s2CMessage = S2CMessage.valueOf(Module.MATCH, MatchCmd.BROADCAST_GAME_INVITE_S2C);
            BroadcastMessage content = new BroadcastMessage();
            content.setGameId(gameId);
            content.setGameName(gameName);
            s2CMessage.setResult(ResultVO.success(content));

            List<Integer> pushPlayers = new ArrayList<>();
            pushPlayers.addAll(preMatchList);
            if (preMatchList.size() > broadcastInviteLimit) {
                Collections.shuffle(pushPlayers);
                pushPlayers = pushPlayers.subList(0, broadcastInviteLimit);
            }

            for (Integer id : pushPlayers) {
                ChannelManager.sendMessage(id, s2CMessage);
            }
            logger.debug("preMatchList:" + pushPlayers.toString());
        });
    }

    public synchronized void addPreMatchList(int accountId) {
        if (broadcastInviteEnable) {
            preMatchList.add(accountId);
        }
    }

    public synchronized void rmPreMatchList(int accountId) {
        if (broadcastInviteEnable) {
            preMatchList.remove(accountId);
        }
    }

    private void recordAI(int accountId, String gameId, int eventType) {
        Record record = new Record();
        record.setEvent(eventType);
        record.setAccountId(accountId);
        record.setGameId(gameId);

        recordService.record(record, null);
    }
}
