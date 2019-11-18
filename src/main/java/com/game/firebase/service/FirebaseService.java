package com.game.firebase.service;

import com.game.firebase.dao.FirebaseInfoDao;
import com.game.firebase.domain.FirebaseInfo;
import com.game.util.CollectionUtil;
import com.game.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class FirebaseService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseService.class);

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    FirebaseInfoDao firebaseInfoDao;

    @Resource(name = "threadHttp")
    ExecutorService executorService;

    private String appId = "101";

    private String toTokenKey(String appId, String accountId) {
        StringBuilder keyBuilder = new StringBuilder(Constant.REDIS_KEY_PREFIX_FIREBASE_TOKEN).append(appId);
        keyBuilder.append("_").append(accountId);
        return keyBuilder.toString();
    }

    private List<String> toTokenKeys(String appId, List<String> recipients) {
        List<String> tokenKeys = new ArrayList<>();

        recipients.forEach((r)->{
            String key = toTokenKey(appId, r);
            tokenKeys.add(key);
        });

        return tokenKeys;
    }

    private String toIdKey(String appId, String firebaseId) {
        StringBuilder keyBuilder = new StringBuilder(Constant.REDIS_KEY_PREFIX_FIREBASE_ID).append(appId);
        keyBuilder.append("_").append(firebaseId);
        return keyBuilder.toString();
    }


    public List<String> findTokens(List<String> recipientIds) {
        List<String> tokenKeys = toTokenKeys(appId, recipientIds);
        if (!CollectionUtil.isEmpty(tokenKeys)) {
            ValueOperations<String,String> operations = redisTemplate.opsForValue();
            return operations.multiGet(tokenKeys);
        }

        return null;
    }

    public String findToken(int accountId) {
        String key = toTokenKey(appId, String.valueOf(accountId));
        ValueOperations operations = redisTemplate.opsForValue();
        Object value = operations.get(key);
        if (value instanceof String) {
            return (String)value;
        } else {
            FirebaseInfo info = firebaseInfoDao.queryByAccount(accountId);
            if (info != null && StringUtils.isEmpty(info.getFirebaseToken())) {
                operations.setIfAbsent(key, info.getFirebaseToken());
                return info.getFirebaseToken();
            }
        }

        return "";
    }

    public void insertOrUpdateFirebaseTokenAsync(int accountId, String firebaseId, String firebaseToken) {
        executorService.execute(()-> insertOrUpdateFirebaseToken(accountId,firebaseId,firebaseToken));
    }

    public void deleteFirebaseTokenAsync(int accountId) {
//        executorService.execute(()-> deleteFirebaseToken(accountId));
    }

    public int deleteFirebaseToken(int accountId) {
        if (accountId <= 0) {
            logger.warn("the account id is empty");
            return -1;
        }

        String key = toTokenKey(appId,String.valueOf(accountId));
        Boolean deleted = redisTemplate.delete(key);
        if (deleted != null) {
            return deleted.booleanValue() ? 1 : 0;
        }

        return 0;
    }

    public int insertOrUpdateFirebaseToken(int accountId, String firebaseId, String firebaseToken) {

        if (StringUtils.isEmpty(firebaseToken) || StringUtils.isEmpty(firebaseId)) {
            logger.warn("the firebase id or token is empty");
            return -1;
        }

        if (accountId < 0) {
            logger.warn("the account id is empty");
            return -1;
        }

        logger.debug("the account id is " + accountId);

        /**
         * 如果没有上传账户ID，则认为是临时
         */
        if (accountId == 0) {
            logger.debug("the account is 0");
            String key = toIdKey(appId, firebaseId);
            ValueOperations operations = redisTemplate.opsForValue();
            operations.set(key, firebaseToken, 30L, TimeUnit.DAYS);

        } else {
            String key = toTokenKey(appId,String.valueOf(accountId));
            String idkey = toIdKey(appId, firebaseId);

            ValueOperations operations = redisTemplate.opsForValue();
            Object value = operations.get(key);
            if (firebaseToken.equals(value)) {
                return 0;
            } else {
                operations.set(key, firebaseToken);
                redisTemplate.delete(idkey);
                FirebaseInfo info = new FirebaseInfo(accountId, firebaseId, firebaseToken);
                if (value == null) {
                    firebaseInfoDao.insert(info);
                } else {
                    firebaseInfoDao.update(info);
                }
                return 1;
            }
        }
        return 0;
    }


}
