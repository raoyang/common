package com.game.account.component;

import com.game.common.vo.ResultVO;
import com.game.home.domain.CommParam;
import com.game.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class GToken {
    private static final Logger logger = LoggerFactory.getLogger(GToken.class);

    @Autowired
    RedisTemplate redisTemplate;

    //查询gToken
    public ResultVO checkGToken(CommParam commParam, String appId) {
        int accountId = commParam.getAccountId();
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object token = valueOperations.get(Constant.REDIS_KEY_PREFIX_ACCOUNT_TOKEN + appId + "_"  + accountId);
        if (token == null) {
            logger.info("token expire, account id:" + accountId);
            return ResultVO.error("token expire", Constant.RNT_CODE_TOKEN_EXPIRE);
        }

        String tokens = commParam.getgToken() + "_" + commParam.getUuid() + "_" + commParam.getClientId();
        if (! tokens.equals(token)) {
            logger.info("token diff, account id:" + accountId + ", tokens:" + tokens);
            return ResultVO.error("token diff", Constant.RNT_CODE_TOKEN_EXPIRE);
        }

        return ResultVO.success();
    }
}
