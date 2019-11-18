package com.game.game.service;

import com.game.ad.domain.AdConf;
import com.game.ad.service.AdService;
import com.game.chat.util.JsonUtils;
import com.game.common.vo.ResultVO;
import com.game.game.dao.GameMapper;
import com.game.game.dao.GameSecretMapper;
import com.game.game.domain.*;
import com.game.home.domain.CommParam;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import com.game.util.JwtUtil;
import com.game.util.StringUtil;
import com.google.gson.Gson;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    @Autowired
    GameMapper gameMapper;
    @Autowired
    GameSecretMapper gameSecretMapper;
    @Autowired
    AdService adService;
    @Autowired
    RedisTemplate redisTemplate;

    public GameAllInfo queryGame(String id) {
        GameAllInfo game = null;
        Gson gson = new Gson();

        ValueOperations valueOperations = redisTemplate.opsForValue();
        String redisKey = Constant.REDIS_KEY_PREFIX_GAME_ALL_INFO + id;
        Object gameObj = valueOperations.get(redisKey);
        if (gameObj == null) {
            game = gameMapper.queryById(id);
            if (game == null) {
                return null;
            }

            List<GameVersion> versions = game.getVersions();
            if (versions.isEmpty()) {
                return null;
            }

            boolean isValidLan = false;
            for (GameVersion version : versions) {
                List<GameLan> gameLans = gameMapper.queryLanByGameVersion(id, version.getVersionCode());
                if (gameLans.isEmpty()) {
                    logger.error("no valid game lan package, gameId:" + id);
                    continue;
                }

                Map<String, GameLan> gameLanMap = gameLans.stream().collect(Collectors.toMap(GameLan::getLan, gameLan -> gameLan));
                version.setgLan(gameLanMap);
                isValidLan = true;
            }

            if (!isValidLan) {
                logger.error("game:" + id + ", no valid language package");
                return null;
            }

            //增加广告ID配置
            List<AdConf> adConfs = adService.getAdConf(id);
            game.setAd(adConfs);

            //增加游戏秘钥
            GameSecret gameSecret = queryGameSecret(id);
            if (gameSecret != null) {
                game.setGameSecret(gameSecret.getGameSecret());
            }

            valueOperations.set(redisKey, gson.toJson(game), 30, TimeUnit.DAYS);
        } else {
            game = gson.fromJson((String)gameObj, GameAllInfo.class);
        }

        return game;
    }

    public List<GameAllInfo> queryGameByIds(List<String> gameIds) {
        List<GameAllInfo> allGames = new ArrayList<>();
        for (String id : gameIds) {
            GameAllInfo gameAllInfo = queryGame(id);
            if (gameAllInfo != null) {
                allGames.add(gameAllInfo);
            }
        }

        return allGames;
    }

    //游戏资源过滤（国家，机型，下发量，指定账户等）
    public List<Game> filterGames(List<GameAllInfo> games, CommParam commParam) {
        List<Game> filterGame = new ArrayList<>();

        String country = CommonFunUtil.getCountryByLan(commParam.getLan());
        String product = commParam.getDevModel();
        Iterator<GameAllInfo> it = games.iterator();
        while (it.hasNext()) {
            GameAllInfo gameAllInfo = it.next(); //所有有效版本,最多两个，一个是正式发布，一个是测试发布
            List<GameVersion> versions = gameAllInfo.getVersions();
            if (versions == null) {
                logger.error("game version null, gameId:" + gameAllInfo.getGameId());
                continue;
            }

            if (versions.isEmpty() || versions.size() > 2) {
                logger.error("game version conf error, gameId:" + gameAllInfo.getGameId());
                continue;
            }

            //平台版本检测
            if (gameAllInfo.getAppVerLimit() != 0 && commParam.getAppVer() != 0) {
                if (commParam.getAppVer() < gameAllInfo.getAppVerLimit()) {
                    logger.debug("filter game app ver limit, app ver:" + commParam.getAppVer() + ", conf ver:" + gameAllInfo.getAppVerLimit());
                    continue;
                }
            }

            //内存检测
            if (gameAllInfo.getMemLimit() != 0 && commParam.getDevMem() != 0) {
                if ( commParam.getDevMem() < gameAllInfo.getMemLimit()) {
                    logger.debug("filter game mem limit, devMem:" + commParam.getDevMem() + ", mem limit:" + gameAllInfo.getMemLimit());
                    continue;
                }
            }

            for (GameVersion version : versions) {
                //国家黑名单
                if (! TextUtils.isEmpty(version.getShieldCountry()) && version.getShieldCountry().contains(country)) {
                    logger.debug("filter game country black, country:" + country + ", gameId:" + gameAllInfo.getGameId());
                    continue;
                }

                //国家白名单
                if (! TextUtils.isEmpty(version.getSpecifyCountry()) && ! version.getSpecifyCountry().contains(country)) {
                    logger.debug("filter game country white, country:" + country + ", gameId:" + gameAllInfo.getGameId());
                    continue;
                }

                //机型黑名单
                if (! TextUtils.isEmpty(version.getShieldPhoneType()) && version.getShieldPhoneType().contains(product)) {
                    logger.debug("filter game product black, product:" + product + ", gameId:" + gameAllInfo.getGameId());
                    continue;
                }

                //机型白名单
                if (! TextUtils.isEmpty(version.getSpecifyPhoneType()) && ! version.getSpecifyPhoneType().contains(product)) {
                    logger.debug("filter game product white, product:" + product + ", gameId:" + gameAllInfo.getGameId());
                    continue;
                }

                //指定账户（针对测试发布）
                if (version.getStatus() == Constant.GAME_STATUS_TEST && ! TextUtils.isEmpty(version.getAccountIds())) {
                    if (! Arrays.asList(version.getAccountIds().split(",")).contains(String.valueOf(commParam.getAccountId()))) {
                        logger.debug("filter game test accountIds, id:" + commParam.getAccountId() + ", gameId:" + gameAllInfo.getGameId());
                        continue;
                    }
                }

                //限量（针对测试发布）
                if (version.getStatus() == Constant.GAME_STATUS_TEST && version.getTestNum() != 0) {
                    ValueOperations operations = redisTemplate.opsForValue();
                    int serNum = 0;
                    Object obj = operations.get(Constant.REDIS_KEY_PREFIX_GRAY_NUM + gameAllInfo.getGameId() + "_" + version.getVersionCode());
                    if (obj != null) {
                        serNum = Integer.parseInt((String)obj);
                    }

                    if (serNum >= version.getTestNum() ) {
                        logger.debug("filter game test num, id:" + serNum + ", gameId:" + gameAllInfo.getGameId());
                        continue;
                    }
                }

                //只下发一个满足要求的版本，找到即break
                //处理多语言
                String language = Arrays.asList(commParam.getLan().split("-")).get(0);
                GameLan gameLan = version.getgLan().get(language);
                if (gameLan == null) {
                    //默认英文
                    gameLan = version.getgLan().get(Constant.LANGUAGE_DEFAULT);
                }

                filterGame.add(Game.valueOf(gameAllInfo, version, gameLan));
                break;
            }

        }

        return filterGame;
    }

    public ResultVO genToken(TokenRVO tokenRVO) {
        String gameId = tokenRVO.getGameId();

        GameSecret gameSecret = queryGameSecret(gameId);
        if (gameSecret == null || TextUtils.isEmpty(gameSecret.getSecret())) {
            logger.warn("gen game token:game not conf, id:" + gameId);
            return ResultVO.error("exception", Constant.RNT_CODE_EXCEPTION);
        }

        String token = JwtUtil.generateJwt(gameId, tokenRVO.getAccountId(), StringUtil.String2HexString(gameSecret.getSecret()));

        GTokenVO gTokenVO = new GTokenVO();
        gTokenVO.setToken(token);

        return ResultVO.success(gTokenVO);
    }

    public GameSecret queryGameSecret(String gameId) {
        ValueOperations operations = redisTemplate.opsForValue();
        String redisKey = Constant.REDIS_KEY_PREFIX_GAME_SECRET + gameId;

        GameSecret gameSecret = null;
        Object secretObj = operations.get(redisKey);
        if (secretObj == null) {
            gameSecret = gameSecretMapper.queryByGameId(gameId);
            if (gameSecret != null) {
                operations.set(redisKey, JsonUtils.objectToString(gameSecret), 30, TimeUnit.DAYS);
            }
        } else {
            gameSecret = JsonUtils.stringToObject((String)secretObj, GameSecret.class);
        }

        return gameSecret;
    }

    public String queryGameDataCfg(String gameId) {
        ValueOperations operations = redisTemplate.opsForValue();
        String redisKey = Constant.REDIS_KEY_PREFIX_GAME_DATA_CONF + gameId;

        String dataConfig = "";
        Object obj = operations.get(redisKey);
        if (obj == null) {
            GameCfg gameCfg = gameMapper.queryGameCfg(gameId);
            if (gameCfg != null && !TextUtils.isEmpty(gameCfg.getDataConfig())) {
                operations.set(redisKey, gameCfg.getDataConfig(), 30, TimeUnit.DAYS);
                dataConfig = gameCfg.getDataConfig();
            }
        } else {
            dataConfig = (String)obj;
        }

        return dataConfig;
    }

}
