package com.game.friend.service;

import com.game.account.domain.AccountInfo;
import com.game.account.service.AccountService;
import com.game.chat.util.JsonUtils;
import com.game.common.vo.DataVO;
import com.game.common.vo.ResultVO;
import com.game.firebase.domain.FirebaseMessage;
import com.game.firebase.domain.MessageEntity;
import com.game.firebase.service.MessageService;
import com.game.friend.constant.FriendRedisKey;
import com.game.friend.dao.FriendDao;
import com.game.friend.domain.*;
import com.game.login.domain.Account;
import com.game.login.service.LoginService;
import com.game.util.Constant;
import com.game.util.HttpUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class FriendService {

    private static final Logger logger = LoggerFactory.getLogger(FriendService.class);

    private static final Logger cacheLogger = LoggerFactory.getLogger("cache");

    //用户操作锁列表
    private static Map<Integer, Object> locks = new ConcurrentHashMap<>();

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private FriendDao friendDao;

    @Autowired
    private LoginService loginService;

    @Autowired
    AccountService accountService;

    @Autowired
    MessageService messageService;

    @Value("${facebook.friend.list.url}")
    private String fbFriendUrl;
    @Value("${is.proxy}")
    private int isProxy;

    public DataVO friendRecommend(Account account) {
        List<Object> recommendFriends = new ArrayList<>();
        Set<String> friendFromSelf = getFriendList(account.getAccountId());
        Map<String, String> blackMap = getBlackList(account.getAccountId());
        List<String> blackList = new ArrayList<>(blackMap.keySet());
        friendFromSelf.addAll(blackList);
        Set<String> allFriend = friendFromSelf;
        //先Facebook好友
        if (account.getPlatform() == Constant.ACCOUNT_PLATFORM_FACEBOOK) {
            List<String> friendIds = friends(account);

            //去掉已是好友的账号(查找用户好友，从friendIds删除)
            List<AccountInfo> accountInfos = new ArrayList<>();
            if (! friendIds.isEmpty()) {
                accountInfos = accountService.batchQueryAccount(friendIds);
            }

            if (! friendFromSelf.isEmpty() && ! accountInfos.isEmpty()) {
                Iterator<AccountInfo> it = accountInfos.iterator();
                while (it.hasNext()) {
                    AccountInfo info = it.next();
                    if (friendFromSelf.contains(String.valueOf(info.getId()))) {
                        it.remove();
                    } else {
                        allFriend.add(String.valueOf(info.getId()));
                    }
                }
            }

            if (! accountInfos.isEmpty()) {
                for (AccountInfo accountInfo : accountInfos) {
                    recommendFriends.add(RecommendFriend.valueOf(accountInfo));
                }
            }

        }

        // 再附近的人（从缓存查询，无则不考虑）
        Map<String, Double> aroundPlayers = getAroundPlayer(account.getAccountId());
        Set<String> aroundIds = aroundPlayers.keySet();
        if (! aroundIds.isEmpty() && ! allFriend.isEmpty()) {
            for (String id : allFriend) {
                if (aroundIds.contains(id)) {
                    aroundIds.remove(id);
                }
            }
        }

        if (! aroundIds.isEmpty()) {
            for (String id : aroundIds) {
                AccountInfo accountInfo = loginService.getAccountInfo(Integer.valueOf(id));
                if (accountInfo == null) {
                    continue;
                }

                RecommendFriend friend = RecommendFriend.valueOf(accountInfo);
                friend.setType(1);
                Double distance = aroundPlayers.get(String.valueOf(accountInfo.getId()));
                friend.setDistance(new Double(distance).intValue());
                recommendFriends.add(friend);
            }
        }

        return DataVO.success(recommendFriends.size(), recommendFriends);
    }

    public DataVO aroundPlayer(AroundPlayerRVO playerRVO) {
        int accountId = playerRVO.getAccountId();

        List<Object> recommendPlayers = new ArrayList<>();
        Set<String> friendFromSelf = getFriendList(accountId);
        Map<String, String> blackMap = getBlackList(accountId);
        List<String> blackList = new ArrayList<>(blackMap.keySet());
        friendFromSelf.addAll(blackList);

        // 附近的人（从缓存查询，无则不考虑）
        Map<String, Double> aroundPlayers = getAroundPlayer(accountId);
        Set<String> aroundIds = aroundPlayers.keySet();
        if (! aroundIds.isEmpty() && ! friendFromSelf.isEmpty()) {
            for (String id : friendFromSelf) {
                if (aroundIds.contains(id)) {
                    aroundIds.remove(id);
                }
            }
        }

        if (aroundIds.isEmpty()) {
            return DataVO.error("no data", Constant.RNT_CODE_NO_DATA);
        }

        List<String> resultList = new ArrayList<>();
        resultList.addAll(aroundIds);

        int page = playerRVO.getPage();
        int limit = playerRVO.getLimit();
        if (page == 0) {
            page = 1;
        }
        if (limit == 0) {
            limit = 20;
        }

        int offset = (page - 1) * limit;
        int playerSize = resultList.size();
        if (offset >= playerSize) {
            return DataVO.error("no data", Constant.RNT_CODE_NO_DATA);
        }

        int toIndex = playerSize;
        if ((offset + limit) < playerSize) {
            toIndex = offset + limit;
        }

        List<String> resultPage = resultList.subList(offset, toIndex);
        for (String id : resultPage) {
            AccountInfo accountInfo = loginService.getAccountInfo(Integer.valueOf(id));
            if (accountInfo == null) {
                continue;
            }

            AroundPlayer player = AroundPlayer.valueOf(accountInfo);
            Double distance = aroundPlayers.get(String.valueOf(accountInfo.getId()));
            player.setDistance(new Double(distance).intValue());
            recommendPlayers.add(player);
        }

        return DataVO.success(playerSize, page, limit, recommendPlayers);
    }

    public DataVO friendFacebook(Account account) {
        List<Object> fbFriends = new ArrayList<>();
        Set<String> friendFromSelf = getFriendList(account.getAccountId());
        Map<String, String> blackMap = getBlackList(account.getAccountId());
        List<String> blackList = new ArrayList<>(blackMap.keySet());
        friendFromSelf.addAll(blackList);
        //Facebook好友
        if (account.getPlatform() == Constant.ACCOUNT_PLATFORM_FACEBOOK) {
            List<String> friendIds = friends(account);

            //去掉已是好友的账号(查找用户好友，从accountInfos删除)
            List<AccountInfo> accountInfos = new ArrayList<>();
            if (! friendIds.isEmpty()) {
                accountInfos = accountService.batchQueryAccount(friendIds);
            }

            if (! friendFromSelf.isEmpty() && ! accountInfos.isEmpty()) {
                Iterator<AccountInfo> it = accountInfos.iterator();
                while (it.hasNext()) {
                    AccountInfo info = it.next();
                    if (friendFromSelf.contains(String.valueOf(info.getId()))) {
                        it.remove();
                    }
                }
            }

            if (! accountInfos.isEmpty()) {
                for (AccountInfo accountInfo : accountInfos) {
                    fbFriends.add(RecommendFriend.valueOf(accountInfo));
                }
            }
        }

        return DataVO.success(fbFriends.size(), fbFriends);
    }

    private List<String> friends(Account account) {
        List<String> lists = new ArrayList<>();

        Map<String, String> params = new HashMap<>();
        params.put("access_token", account.getToken());
        String result = HttpUtil.sendGet(fbFriendUrl, params, "UTF-8", isProxy);
        logger.info("facebook friend list result:" + result);
        if (result.equals("")) {
            logger.warn("no friend, id:" + account.getOpenId());
        }

        FBFriendData friendData = JsonUtils.stringToObject(result, FBFriendData.class);
        if (friendData == null) {
            logger.error("get friend from FB, data parse json error");
            return lists;
        }

        if (friendData.getError() != null) {
            logger.error("facebook friend list ret err, msg:" + friendData.getError().getMessage());
            return lists;
        }

        List<FBFriend> fbFriends = friendData.getData();
        if (! fbFriends.isEmpty()) {
            for (FBFriend fbFriend : fbFriends) {
                lists.add(fbFriend.getId());
            }
        }

        return lists;
    }

    //只读缓存
    private Map<String, Double> getAroundPlayer(int accountId) {
        Map<String, Double> aroundPlayers = new HashMap<>();

        ValueOperations operations = redisTemplate.opsForValue();
        Object players = operations.get(Constant.REDIS_KEY_PREFIX_AROUND_PLAYER + accountId);
        if (players == null) {
            return aroundPlayers;
        }

        aroundPlayers = JsonUtils.stringToObject((String)players, Map.class);
        logger.debug("around " + accountId + ", player:" + aroundPlayers.toString());

        return aroundPlayers;
    }

    //1. 好友申请
    public ResultVO friendApply(FriendMessage msg){
        logger.info("account:" + msg.getAccountId() + ", apply friend:" + msg.getApplyId());
        /*AccountInfo info = loginService.getAccountInfo(msg.getApplyId());
        if(info == null){
            logger.info("no accountInfo found: "+ msg.getApplyId());
            return ResultVO.error("no friend found.");
        }*/
        //我是否在对方的申请列表中
        if(isApplyFriend(msg.getAccountId(), msg.getApplyId())){
            //重复申请
            return ResultVO.error("repeat apply.");
        }
        //判断对方是否是我的好友
        if(isFriend(msg.getApplyId(), msg.getAccountId())){
            return ResultVO.error("already friend.");
        }
        //被申请人在自己的黑名单列表，不能申请，只能先解除黑名单，再申请
        if(isBlack(msg.getApplyId(), msg.getAccountId())){
            return ResultVO.error("in black list.");
        }
        if(msg.getApplyId() == msg.getAccountId()){
            return ResultVO.error("yourself.");
        }
        //把我的申请信息，缓存到对方的列表中
        cacheApplyFriendMessage(msg.getAccountId(), msg.getApplyId());

        /**
         * add for application notification, tangmin
         */
        sendApplication(msg.getAccountId(), msg.getApplyId());

        return ResultVO.success();
    }

    //2. 同意添加好友
    public ResultVO agreeFriendApply(FriendMessage msg){
        logger.info("用户:" + msg.getAccountId() + ",准备添加好友:" + msg.getApplyId());
        Object lock = getLock(msg.getAccountId());
        //1. 判断是否有这个申请
        //if(!isApplyFriend(msg.getApplyId(), msg.getAccountId())){
        //   logger.info("用户:"+ msg.getAccountId() +"非法的添加不存在申请的用户:" + msg.getApplyId());
        //   return ResultVO.error("consent no apply account.");
        //}
        //2. 删除这个申请记录
        if (msg.getAccountId() == msg.getApplyId()) {
            return ResultVO.success("add himself:" + msg.getAccountId());
        }
        rmApplyFriendFromCache(msg.getApplyId(), msg.getAccountId());
        synchronized (lock) {
            //3.防止重复添加，查询对方是否是我的好友
            if (friendDao.isFriend(msg.getAccountId(), msg.getApplyId()) > 0) {
                logger.info("添加已经存在的好友:" + msg.getAccountId());
                return ResultVO.success();
            }

            //判断是否是自己的黑名单
            if (friendDao.isBlack(msg.getAccountId(), msg.getApplyId()) > 0) {
                //先删除黑名单，再添加好友
                rmBlack(msg.getApplyId(), msg.getAccountId());
            }

            if(friendDao.isBlack(msg.getApplyId(), msg.getAccountId()) > 0){
                rmBlack(msg.getAccountId(), msg.getApplyId());
            }

            //4. 好友列表新增一个人
            logger.info("用户:" + msg.getAccountId() + ",可以添加好友:" + msg.getApplyId());
            addFriend(msg.getApplyId(), msg.getAccountId());

            /**
             * add for agree notification, tangmin
             */
            agreeApplication(msg.getAccountId(), msg.getApplyId());
        }
        return ResultVO.success();
    }

    //3. 删除好友
    public ResultVO rmFriend(FriendMessage msg){
        logger.info("用户:" + msg.getAccountId() + ",准备删除好友:" + msg.getApplyId());
        //1. 判断是否是自己的好友
        if(!isFriend(msg.getApplyId(), msg.getAccountId())){
            logger.info("account:" + msg.getAccountId() + "删除不存在的好友:" + msg.getApplyId());
            //return ResultVO.error("delete not exist friend.");
            return ResultVO.success();
        }
        //防止用户持续性的发送不合法请求造成的数据库操作
        if(msg.getApplyId() == 0 || msg.getAccountId() == 0){
            return ResultVO.success();
        }
        logger.info("用户:" + msg.getAccountId() + ",可以删除好友:" + msg.getApplyId());
        rmFriend(msg.getApplyId(), msg.getAccountId());
        rmFriend(msg.getAccountId(), msg.getApplyId());
        return ResultVO.success();
    }

    //4. 加入黑名单
    public ResultVO addBlackList(FriendMessage msg) {
        logger.info("用户:" + msg.getAccountId() + ",准备拉黑用户:" + msg.getApplyId());
        Object lock = getLock(msg.getAccountId());
        synchronized (lock) {
            if (isBlack(msg.getApplyId(), msg.getAccountId())) {
                return ResultVO.success();
            }
            //判断是不是自己的好友
            if (isFriend(msg.getApplyId(), msg.getAccountId())) {
                blackedFriend(msg.getApplyId(), msg.getAccountId());
            } else {
                blackedStranger(msg.getApplyId(), msg.getAccountId());
            }
        }
        return ResultVO.success();
    }

    /***
     * 是否申请过好友
     * @param accountId 提交申请用户
     * @param targetId 被申请用户
     * @return
     */
    private boolean isApplyFriend(int accountId, int targetId){
        HashOperations operations = redisTemplate.opsForHash();
        String key = FriendRedisKey.FRIEND_APPLY + targetId;
        return operations.hasKey(key, String.valueOf(accountId));
    }

    /***
     * 删除一个申请信息
     *  备注：如果用户一直不处理申请记录，会越累计越多，应该如何处置
     * @param accountId
     * @param targetId
     */
    private void rmApplyFriendFromCache(int accountId, int targetId){
        HashOperations operations = redisTemplate.opsForHash();
        String key = FriendRedisKey.FRIEND_APPLY + targetId;
        operations.delete(key, String.valueOf(accountId));
    }

    /***
     * 增加一个申请消息
     * @param accountId
     * @param targetId
     */
    private void cacheApplyFriendMessage(int accountId, int targetId){
        //将消息缓存到玩家队列
        HashOperations operations = redisTemplate.opsForHash();
        String key = FriendRedisKey.FRIEND_APPLY + targetId;
        operations.put(key, String.valueOf(accountId), String.valueOf(System.currentTimeMillis()));
        redisTemplate.expire(key, FriendRedisKey.FRIEND_APPLY_EXPIRE,TimeUnit.DAYS);
    }

    /***
     * 给目标用户新增一个好友
     * @param accountId 好友
     * @param targetId 目标用户
     */
    private void addFriend(int accountId, int targetId){
        try{
            int result0 =  friendDao.addFriend(accountId, targetId, new Timestamp(System.currentTimeMillis()));
            //更新缓存
            if(result0 == 1){
                logger.info("用户:" + accountId + "添加:" + targetId + "为好友");
                addFriendListFromCache(accountId, targetId);
            }
            int result1 = friendDao.addFriend(targetId, accountId, new Timestamp(System.currentTimeMillis()));
            if(result1 == 1){
                //更新缓存
                logger.info("用户:" + targetId + "添加:" + accountId + "为好友");
                addFriendListFromCache(targetId, accountId);
            }
        }catch (Exception e){
            logger.error("add friend: "+accountId+" for target: "+ targetId +" failure.");
            e.printStackTrace();
            throw e;
        }
    }

    /***
     * 给目标用户删除一个好友
     * @param accountId
     * @param targetId
     */
    private void rmFriend(int accountId, int targetId){
        try {
            int result = friendDao.reFriend(targetId, accountId);
            if(result == 1 || result == 0){ //如果数据库删除了，或者数据库没有数据，缓存也要同步去做
                logger.info("用户:" + targetId + "删除好友:" + accountId);
                rmFriendListFromCache(accountId, targetId);
                return;
            }
        }catch (Exception e){
            logger.error("用户:" + targetId + ",删除好友accountId:" + accountId + "失败.");
            e.printStackTrace();
            throw e;
        }
    }

    private void rmBlack(int accountId, int targetId){
        try{
            int result = friendDao.reBlack(targetId, accountId);
            if(result == 1 || result == 0){
                rmBlackListFromCache(accountId, targetId);
            }
        }catch (Exception e){
            logger.error("用户:" + targetId + ",从黑名单移除用户accountId:" + accountId + "失败.");
            e.printStackTrace();
            throw e;
        }
    }

    /***
     * 目标用户拉黑好友
     * @param accountId
     * @param targetId
     */
    private void blackedFriend(int accountId, int targetId){
        try{
            long curTime = System.currentTimeMillis();
            int result = friendDao.friend2Black(targetId, accountId, new Timestamp(curTime));
            if(result == 1){
                logger.info("用户:" + targetId + "拉黑好友:" + accountId);
                //从好友列表移除
                rmFriendListFromCache(accountId, targetId);
                addBlackList(accountId, targetId, curTime);
                return;
            }
        }catch (Exception e){
            logger.error("用户:" + targetId + ",拉黑好友account:" + accountId + ",失败.");
            e.printStackTrace();
            throw e;
        }
    }

    /***
     * 目标用户拉黑路人
     * @param accountId
     * @param targetId
     */
    private void blackedStranger(int accountId, int targetId){
        try{
            long curTime = System.currentTimeMillis();
            int result = friendDao.addBlackList(targetId, accountId, new Timestamp(curTime));
            if(result == 1){
                logger.info("用户:" + targetId + "拉黑陌生人:" + accountId);
                addBlackList(accountId, targetId, curTime);
                return;
            }
        }catch (Exception e){
            logger.error("用户:" + targetId + ",拉黑好友account:" + accountId + ",失败.");
            e.printStackTrace();
            throw e;
        }
    }

    private void addBlackList(int accountId, int targetId, long time){
        getBlackList(targetId);
        HashOperations operations = redisTemplate.opsForHash();
        String key = FriendRedisKey.BLACK_LIST + targetId;
        operations.put(key, String.valueOf(accountId), String.valueOf(time));
        operations.delete(key, String.valueOf(0));
        redisTemplate.expire(key, FriendRedisKey.BLACK_LIST_EXPIRE, TimeUnit.DAYS);
    }


    /***
     * 目标用户成功持久化一个新增好友，更新缓存
     * @param accountId 好友
     * @param targetId 目标用户
     * 说明：为了防止玩家无好友导致频繁访问数据库，默认给好友列表添加一个id=0的未知用户
     */
    private void addFriendListFromCache(int accountId, int targetId){
        getFriendList(targetId);
        SetOperations operations = redisTemplate.opsForSet();
        String key = FriendRedisKey.FRIEND_LIST + targetId;
        operations.add(key, String.valueOf(accountId));
        operations.remove(key, String.valueOf(0));//移除0
        redisTemplate.expire(key, FriendRedisKey.FRIEND_LIST_EXPIRE, TimeUnit.DAYS);
    }

    private String[] getStringArray(List<Integer> list){
        if(list == null){
            return null;
        }
        String[] str = new String[list.size()];
        for(int i = 0 ; i < list.size() ; i ++){
            str[i] = String.valueOf(list.get(i));
        }
        return str;
    }

    private void rmFriendListFromCache(int accountId, int targetId){
        SetOperations operations = redisTemplate.opsForSet();
        String key = FriendRedisKey.FRIEND_LIST + targetId;
        operations.remove(key, String.valueOf(accountId));
        redisTemplate.expire(key, FriendRedisKey.FRIEND_LIST_EXPIRE, TimeUnit.DAYS);
    }

    private void rmBlackListFromCache(int accountId, int targetId){
        HashOperations operations = redisTemplate.opsForHash();
        String key = FriendRedisKey.BLACK_LIST + targetId;
        operations.delete(key, String.valueOf(accountId));
        redisTemplate.expire(key, FriendRedisKey.BLACK_LIST_EXPIRE, TimeUnit.DAYS);
    }

    /***
     * accountId是否是目标用户的好友
     * @param accountId
     * @param targetId
     * @return
     */
    public boolean isFriend(int accountId, int targetId){
        Set<String> friendList = getFriendList(targetId);
        return friendList.contains(String.valueOf(accountId));
    }

    /***
     *
     * @param accountId
     * @param targetId
     * @return
     */
    public boolean isBlack(int accountId, int targetId){
        Map<String, String> blackList = getBlackList(targetId);
        return blackList.containsKey(String.valueOf(accountId));
    }

    /***
     * 获取好友列表
     * @param targetId
     * @return
     */
    public Set<String> getFriendList(int targetId){
        SetOperations operations = redisTemplate.opsForSet();
        String key = FriendRedisKey.FRIEND_LIST + targetId;
        Set<String> friendList = operations.members(key);
        if(friendList == null || friendList.isEmpty()){
            List<Integer> list = friendDao.friends(targetId);
            if(list == null || list.isEmpty()){
                operations.add(key, String.valueOf(0));
            }else{
                operations.add(key, getStringArray(list));
                for(Integer  i : list){
                    friendList.add(String.valueOf(i));
                }
            }
        }
        return friendList;
    }

    public Set<FriendMessageRes> getFriendInfos(Set<String> set){
        Set<FriendMessageRes> result = new LinkedHashSet<>();
        for(String src : set){
            int id = Integer.parseInt(src);
            if(id != 0){
                AccountInfo info = loginService.getAccountInfo(id);
                if(info != null){
                    result.add(FriendMessageRes.valueOf(info));
                }
            }
        }
        return result;
    }

    public Set<BlackMessageRes> getBlackInfos(Map<String, String> map){
        Set<BlackMessageRes> result = new LinkedHashSet<>();
        for(Map.Entry<String, String> entry: map.entrySet()){
            int id =Integer.parseInt(entry.getKey());
            if(id != 0){
                AccountInfo info = loginService.getAccountInfo(id);
                if(info != null){
                    result.add(BlackMessageRes.valueOf(info, Long.parseLong(entry.getValue())));
                }
            }
        }
        return result;
    }

    /***
     * 获取黑名单列表
     * @param targetId
     * @return
     */
    public Map<String, String> getBlackList(int targetId){
        HashOperations operations = redisTemplate.opsForHash();
        String key = FriendRedisKey.BLACK_LIST + targetId;
        Map<String, String> map = operations.entries(key);
        if(map == null || map.isEmpty()){
            List<BlackData> list = friendDao.blackList(targetId);
            if(list == null || list.isEmpty()){
                operations.put(key, String.valueOf(0) , String.valueOf(0));
                Map<String, String> result = new HashMap<>();
                result.put(String.valueOf(0), String.valueOf(0));
                return result;
            }else{
                Map<String, String> temp = new HashMap<>();
                for(BlackData data : list){
                    temp.put(String.valueOf(data.getAccountId()), String.valueOf(data.getTime().getTime()));
                }
                operations.putAll(key, temp);
                return temp;
            }
        }
        return map;
    }

    /***
     * 获取申请消息列表
     * @param targetId
     * @return
     */
    public List<FriendMessageRes> getApplyList(int targetId){
        HashOperations operations = redisTemplate.opsForHash();
        String key = FriendRedisKey.FRIEND_APPLY + targetId;
        Map<String, String> map = operations.entries(key);
        if(map == null || map.isEmpty()){
            return new ArrayList<>();
        }
        List<FriendMessageRes> result = new ArrayList<>();
        for(String src : map.keySet()){
            int id = Integer.parseInt(src);
            AccountInfo info = loginService.getAccountInfo(id);
            if(info == null){
                logger.error("getApplyList: can't find account:" + id);
                continue;
            }
            FriendMessageRes res = new FriendMessageRes();
            res.setApplyAccount(id);
            res.setNickName(info.getNickName());
            res.setSex(info.getSex());
            res.setHeaderImg(info.getHeaderImg());
            result.add(res);
        }
        redisTemplate.delete(key);
        return result;
    }

    public ResultVO deleteBlack(FriendMessage msg){

        logger.info("用户:" + msg.getAccountId() + "移除黑名单:" + msg.getApplyId());
        if(!isBlack(msg.getApplyId(), msg.getAccountId())){
            return ResultVO.error("is not black.");
        }
        Object lock = getLock(msg.getAccountId());
        synchronized (lock){
            if(friendDao.beforeFriend(msg.getAccountId(), msg.getApplyId()) > 0){
                long curTime = System.currentTimeMillis();
                int result = friendDao.black2Friend(msg.getAccountId(), msg.getApplyId(), new Timestamp(curTime));
                if(result == 1){
                    rmBlackListFromCache(msg.getApplyId(), msg.getAccountId());
                    //好友列表加回来
                    addFriendListFromCache(msg.getApplyId(), msg.getAccountId());
                }
            }else{
                rmBlack(msg.getApplyId(), msg.getAccountId());
            }
        }
        return ResultVO.success();
    }

    public Object getLock(int accountId){
        Object object = locks.get(accountId);
        if(object == null){
            synchronized (this){
                object = locks.get(accountId);
                if(object == null){
                    object = new Object();
                    locks.put(accountId, object);
                }
            }
        }
        return object;
    }


    private FirebaseMessage buildApplicationFriendMessage(int source, int target, int cmd) {
        AccountInfo accountInfo = loginService.getAccountInfo(source);
        if (accountInfo != null) {
            FirebaseMessage.Builder fmb = new FirebaseMessage.Builder();
            fmb.source(source).target(target).isNotification(false).timeToLive(7 * 24 * 3600);

            ApplicationMessage friendMessage = new ApplicationMessage();
            friendMessage.setSourceId(source);
            friendMessage.setSourceName(accountInfo.getNickName());
            friendMessage.setThumbUrl(accountInfo.getHeaderImg());

            MessageEntity entity = new MessageEntity();
            entity.setCmd(cmd);
            entity.setVer(1);
            entity.setNotificationType(0x01);
            entity.setData(friendMessage);

            return fmb.entity(entity).build();
        } else {
            logger.warn("there is no account info for user id " + source + ", please check the id");
        }

        return null;
    }

    private void sendApplication(int source, int target) {
        FirebaseMessage fm = buildApplicationFriendMessage(source, target, MessageEntity.CMD_FRIEND_APPLY);
        if (fm != null) {
            messageService.toSingleUserAsync(fm);
        }
    }

    private void agreeApplication(int source, int target) {
        FirebaseMessage fm = buildApplicationFriendMessage(source, target, MessageEntity.CMD_FRIEND_AGREE);
        if (fm != null) {
            messageService.toSingleUserAsync(fm);
        }
    }
}
