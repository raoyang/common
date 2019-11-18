package com.game.open.service;

import com.game.account.domain.AccountInfo;
import com.game.account.service.AccountService;
import com.game.common.vo.DataVO;
import com.game.common.vo.ResultVO;
import com.game.face.domain.Face;
import com.game.face.domain.FaceVO;
import com.game.face.service.FaceService;
import com.game.game.domain.Game;
import com.game.game.domain.GameAllInfo;
import com.game.game.domain.GameCfg;
import com.game.game.domain.GameSecret;
import com.game.game.service.GameService;
import com.game.login.domain.Account;
import com.game.login.service.LoginService;
import com.game.open.domain.*;
import com.game.util.*;
import com.game.util.pic.MD5Util;
import com.google.common.collect.Maps;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class OpenService {
    private static final Logger logger = LoggerFactory.getLogger(OpenService.class);
    private static final Long GAME_TOKEN_EXPIRE_TIME = 365L;
    @Autowired
    AccountService accountService;
    @Autowired
    GameService gameService;
    @Autowired
    LoginService loginService;
    @Autowired
    FaceService faceService;
    @Autowired
    RedisTemplate redisTemplate;
    @Value("${avatar.default.male}")
    private String maleAvatar;

    public ResultVO login(Account account) {
        String openId = account.getOpenId();
        if (!CheckInput.isPhone(openId)) {
            logger.warn("openId error, id:" + openId);
            return ResultVO.error("id error", Constant.RNT_CODE_PARAM_ERROR);
        }
        String nickName = account.getNickName();
        if (TextUtils.isEmpty(nickName)) {
            nickName = openId;
        }
        int platform = Constant.ACCOUNT_PLATFORM_TEST;

        int accountId = 0;
        AccountInfo accountInfo = accountService.queryByOpenId(openId, platform);
        if (accountInfo == null) {
            AccountInfo info = new AccountInfo();
            info.setOpenId(openId);
            info.setHeaderImg(maleAvatar);
            info.setType(Constant.ACCOUNT_TYPE_FORMAL);
            info.setNickNameByte(nickName.getBytes(StandardCharsets.UTF_8));
            info.setPlatform(platform);
            accountService.addAccount(info);
            accountId = info.getId();
        } else {
            accountId = accountInfo.getId();
        }

        String token = genToken(openId, platform);
        loginService.setRedisToken(Constant.APP_TYPE_GAME_PLATFORM, accountId, account.getUuid(), account.getClientId(), token);

        OLoginVO vo = new OLoginVO();
        vo.setAccountId(accountId);
        vo.setgToken(token);

        return ResultVO.success(vo);
    }

    private String genToken(String openId, int platform) {
        String md5 = MD5Util.getMD5String(openId + platform + Constant.APP_TYPE_GAME_PLATFORM);
        Long time = new Date().getTime();
        return Constant.APP_TYPE_GAME_PLATFORM + md5 + String.valueOf(time);
    }

    public ResultVO authToken(String gameId, int uId, String token, String sign) {
        GameAllInfo game = gameService.queryGame(gameId);
        if (game == null) {
            logger.error("game not exist, id:" + gameId);
            return ResultVO.error("game not exist", Constant.RNT_CODE_EXCEPTION);
        }

        GameSecret gameSecret = gameService.queryGameSecret(gameId);
        if (gameSecret == null) {
            logger.error("game not conf, please conf, id:" + gameId);
            return ResultVO.error("conf exception", Constant.RNT_CODE_EXCEPTION);
        }

        if (TextUtils.isEmpty(gameSecret.getSecret()) || TextUtils.isEmpty(gameSecret.getSignKey())) {
            logger.error("game not conf secret or sign, id:" + gameId);
            return ResultVO.error("conf exception", Constant.RNT_CODE_EXCEPTION);
        }

        try {
            Map<String, String> params = Maps.newHashMap();
            params.put("gameId", gameId);
            params.put("uId", String.valueOf(uId));
            params.put("token", token);
            params.put("sign", sign);
            if (!SignUtil.validateSignByKey(params, gameSecret.getSignKey())) {
                return ResultVO.error("sign check fail", Constant.RNT_CODE_SIGN_ERROR);
            }
        } catch (Exception e) {
            logger.error("open auth token exception, msg:" + e.getMessage());
            return ResultVO.error("sign check fail", Constant.RNT_CODE_SIGN_ERROR);
        }

        String secretHex = StringUtil.String2HexString(gameSecret.getSecret());
        if (! JwtUtil.validateJwt(token, secretHex)) {
            logger.error("game token jwt error, gameId:" + gameId);
            return ResultVO.error("game token error", Constant.RNT_CODE_PARAM_ERROR);
        }

        Object gameIdJwt = JwtUtil.getKey(token, "gId", secretHex);
        if (gameIdJwt == null || !gameIdJwt.equals(gameId)) {
            logger.error("check game token: game id error, gameId:" + gameId);
            return ResultVO.error("game id error", Constant.RNT_CODE_TOKEN_CHECK_FAIL);
        }

        Object uIdJwt = JwtUtil.getKey(token, "uId", secretHex);
        if (uIdJwt == null || (int)uIdJwt != uId) {
            logger.error("check game token: uid error, gameId:" + gameId);
            return ResultVO.error("uid error", Constant.RNT_CODE_TOKEN_CHECK_FAIL);
        }

        Object cTime = JwtUtil.getKey(token, "ct", secretHex);
        if (cTime == null) {
            logger.error("check game token: createTime error, gameId:" + gameId);
            return ResultVO.error("ct error", Constant.RNT_CODE_TOKEN_CHECK_FAIL);
        }

        Long tNow = new Date().getTime();
        Long expireTime = GAME_TOKEN_EXPIRE_TIME * 24 * 60 * 60 * 1000;
        logger.debug("now time:" + tNow + ", cTime:" + (Long)cTime + ", expire Time:" + expireTime);
        if ((tNow - (Long)cTime) > expireTime) {
            logger.warn("check game token: token expire, gameId:" + gameId);
            return ResultVO.error("token expire", Constant.RNT_CODE_TOKEN_EXPIRE);
        }

        AccountInfo accountInfo = loginService.getAccountInfo(uId);
        if (accountInfo == null) {
            logger.warn("uId no valid, uId:" + uId);
            return ResultVO.error("uId no valid", Constant.RNT_CODE_PARAM_ERROR);
        }

        OpenAccountVO accountVO = OpenAccountVO.valueOf(accountInfo);

        return ResultVO.success(accountVO);
    }

    public ResultVO gameDataCfg(GameDataRVO dataRVO) {
        String gameId = dataRVO.getGd().getGameId();
        if (TextUtils.isEmpty(gameId)) {
            return ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR);
        }

        String config = gameService.queryGameDataCfg(gameId);
        logger.debug("game:" + gameId + ", data conf:" + config);
        if (TextUtils.isEmpty(config)) {
            return ResultVO.error("no conf", Constant.RNT_CODE_EXCEPTION);
        }

        GameDataConfVO confVO = new GameDataConfVO();
        confVO.setConfig(config);

        return ResultVO.success(confVO);
    }

    public ResultVO gameFace() {
        List<Face> list = faceService.getFaceList();
        if (list.isEmpty()) {
            return ResultVO.error("no data", Constant.RNT_CODE_NO_DATA);
        }

        List<FaceVO> faceVOS = new ArrayList<>();
        for (Face face : list) {
            faceVOS.add(FaceVO.valueOf(face));
        }

        return ResultVO.success(faceVOS);
    }

    public ResultVO gameRank(GameDataRVO dataRVO) {
        int accountId = dataRVO.getAccountId();
        String gameId = dataRVO.getGd().getGameId();
        int rPoints = dataRVO.getGd().getScore();

        AccountInfo accountInfo = loginService.getAccountInfo(accountId);
        if (accountInfo == null) {
            logger.debug("gameRank, account exception, accountId:" + accountId);
            return ResultVO.error("account exception", Constant.RNT_CODE_EXCEPTION);
        }

        GameAllInfo game = gameService.queryGame(gameId);
        if (game == null) {
            logger.debug("gameRank, game no valid, gameId:" + gameId);
            return ResultVO.error("game no valid", Constant.RNT_CODE_EXCEPTION);
        }

        int rankType = game.getRankType();
        String rankingKey = Constant.REDIS_KEY_PREFIX_RANKING + gameId;
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        String accountIdStr = String.valueOf(accountId);
        if (rankType == Constant.RANKING_TYPE_SCORE || rankType == Constant.RANKING_TYPE_LEVEL) {
            Double score = zSetOperations.score(rankingKey, accountIdStr);
            if (score == null) {
                zSetOperations.add(rankingKey, accountIdStr, rPoints);
                //分值，设置周过期（暂时取消）
                /*if (rankType == Constant.RANKING_TYPE_SCORE) {
                    redisTemplate.expire(rankingKey, CommonFunUtil.netWeekLeftTimeSecond(), TimeUnit.SECONDS);
                }*/
            } else if(score < rPoints) {
                zSetOperations.add(rankingKey, accountIdStr, rPoints);
            }

            int rankingId = zSetOperations.reverseRank(rankingKey, accountIdStr).intValue() + 1;
            if (rankingId > Constant.RANKING_GAME_MAX) {
                logger.warn("no ranking, accountId:" + accountIdStr + ", gameId:" + gameId + ", rankId:" + rankingId);
                ResultVO.error("rank id too big", Constant.RNT_CODE_GAME_RANK_TOO_BIG);
            }

            GameRankVO rankVO = GameRankVO.valueOf(rankingId);
            return ResultVO.success(rankVO);
        }

        logger.warn("no support ranking type:" + rankType);
        return ResultVO.error("game rankType no valid", Constant.RNT_CODE_EXCEPTION);
    }
}
