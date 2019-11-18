package com.game.share.service;

import com.game.Local.component.LocalUtil;
import com.game.account.domain.AccountInfo;
import com.game.common.vo.ResultVO;
import com.game.favogame.domain.FavoGameVO;
import com.game.favogame.service.FavoGameService;
import com.game.game.domain.Game;
import com.game.game.domain.GameAllInfo;
import com.game.game.domain.GameVO;
import com.game.game.domain.GameVersion;
import com.game.game.service.GameService;
import com.game.login.service.LoginService;
import com.game.record.domain.TopGameRecord;
import com.game.record.service.RecordService;
import com.game.share.domain.AccountScorePage;
import com.game.share.domain.GameScorePage;
import com.game.share.domain.ShareHomeVO;
import com.game.share.domain.ShareRVO;
import com.game.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

@Service
public class ShareService {
    private static final Logger logger = LoggerFactory.getLogger(ShareService.class);
    private static final int TOP_GAME_MAX_NUM = 20;
    @Autowired
    LoginService loginService;
    @Autowired
    FavoGameService favoGameService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    GameService gameService;
    @Autowired
    RecordService recordService;
    @Autowired
    LocalUtil localUtil;

    //是否要验证用户token，提示用户页面过期
    public ResultVO homePageData(ShareRVO rvo) {
        /*int accountId = rvo.getAccountId();
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object token = valueOperations.get(Constant.REDIS_KEY_PREFIX_ACCOUNT_TOKEN + Constant.APP_TYPE_GAME_PLATFORM + "_"  + accountId);
        if (token == null) {
            logger.info("token expire, account id:" + accountId);
            return ResultVO.error("token expire", Constant.RNT_CODE_TOKEN_EXPIRE);
        }

        String tokens = rvo.getgToken() + "_" + rvo.getUuid() + "_" + rvo.getClientId();
        if (! tokens.equals(token)) {
            logger.info("token diff, account id:" + accountId + ", tokens:" + tokens);
            return ResultVO.error("token diff", Constant.RNT_CODE_TOKEN_EXPIRE);
        }*/

        AccountInfo accountInfo = loginService.getAccountInfo(rvo.getAccountId());
        if (accountInfo == null) {
            logger.debug("user not exist");
            return ResultVO.error("user not exist", Constant.RNT_CODE_EXCEPTION);
        }

        ShareHomeVO homeVO = ShareHomeVO.valueOf(accountInfo);
        homeVO.setButtonText(localUtil.getLocalMessage(LocalUtil.Lan_TogetherGame_Key, rvo));
        homeVO.setExtDesc(localUtil.getLocalMessage(LocalUtil.Lan_MoreGame_Key, rvo));
        homeVO.setGames(topGame());

        return ResultVO.success(homeVO);
    }

    public ResultVO scoreData(ShareRVO rvo) {
        int type = rvo.getType();

        //非连胜
        if (type == 0) {
            GameAllInfo game = gameService.queryGame(rvo.getGameId());
            if (game == null) {
                logger.warn("share score page, param error, game id:" + rvo.getGameId());
                return ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR);
            }

            GameScorePage gameScorePage = new GameScorePage();
            String gameName = game.getVersions().get(0).getgLan().get(Constant.LANGUAGE_DEFAULT).getName();
            gameScorePage.setGameIcon(game.getVersions().get(0).getgLan().get(Constant.LANGUAGE_DEFAULT).getIcon());
            gameScorePage.setGameName(gameName);

            int rankType = game.getRankType();

            String desc = localUtil.getLocalMessage(LocalUtil.Lan_GameDesc_Key, rvo).replace("GAMENAME", gameName);
            String score = "";
            if (rankType == Constant.RANKING_TYPE_SCORE) {
                score = rvo.getScore() + localUtil.getScoreUnit(Constant.RANKING_TYPE_SCORE, rvo);
            }
            if (rankType == Constant.RANKING_TYPE_LEVEL) {
                score = rvo.getScore() + localUtil.getScoreUnit(Constant.RANKING_TYPE_LEVEL, rvo);
            }
            //兼容游戏分享，0分置空
            if (rvo.getScore() == 0) {
                desc = "";
                score = "";
            }

            String buttonText = localUtil.getLocalMessage(LocalUtil.Lan_TogetherGame_Key, rvo);

            gameScorePage.setDesc(desc);
            gameScorePage.setScore(score);
            gameScorePage.setButtonText(buttonText);
            gameScorePage.setExtDesc(localUtil.getLocalMessage(LocalUtil.Lan_MoreGame_Key, rvo));
            gameScorePage.setGames(topGame());

            return ResultVO.success(gameScorePage);
        }

        //连胜
        if (type == 1) {
            AccountInfo accountInfo = loginService.getAccountInfo(rvo.getAccountId());
            if (accountInfo == null) {
                logger.warn("share score page, param error, account id:" + rvo.getAccountId());
                return ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR);
            }

            AccountScorePage accountScorePage = new AccountScorePage();
            accountScorePage.setAccountId(accountInfo.getId());
            accountScorePage.setNickName(accountInfo.getNickName());
            accountScorePage.setAvatar(accountInfo.getHeaderImg());

            String desc = localUtil.getLocalMessage(LocalUtil.Lan_PersonDesc_Key, rvo);
            String score = rvo.getScore() + localUtil.getLocalMessage(LocalUtil.Lan_Win_Key, rvo);
            String buttonText = localUtil.getLocalMessage(LocalUtil.Lan_Challenge_Key, rvo);

            accountScorePage.setDesc(desc);
            accountScorePage.setScore(score);
            accountScorePage.setButtonText(buttonText);
            accountScorePage.setExtDesc(localUtil.getLocalMessage(LocalUtil.Lan_MoreGame_Key, rvo));
            accountScorePage.setGames(topGame());

            return ResultVO.success(accountScorePage);
        }

        return ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR);
    }

    private List<GameVO> topGame() {
        List<GameVO> list = new ArrayList<>();

        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> games = zSetOperations.reverseRangeWithScores(Constant.REDIS_KEY_TOP_GAME, 0, 20);
        if (games == null || games.isEmpty()) {
            logger.info("redis no top game");
            return loadFromDB();
        }

        for (ZSetOperations.TypedTuple<String> value : games) {
            logger.debug("top game:" + value.getScore() + "," + value.getValue());
            GameVO gameVO = new GameVO();
            GameAllInfo gameAllInfo = gameService.queryGame(value.getValue());
            if (gameAllInfo == null) {
                continue;
            }

            GameVersion defaultVersion = gameAllInfo.getVersions().get(0);
            Game game = Game.valueOf(gameAllInfo, defaultVersion, defaultVersion.getgLan().get(Constant.LANGUAGE_DEFAULT));

            BeanUtils.copyProperties(game, gameVO);
            list.add(gameVO);
        }

        return list;
    }

    private List<GameVO> loadFromDB() {
        List<TopGameRecord> list = recordService.queryTopGameRecord(TOP_GAME_MAX_NUM);
        List<GameVO> gameVOS = new ArrayList<>();
        for(TopGameRecord record : list){
            GameVO gameVO = new GameVO();
            GameAllInfo gameAllInfo = gameService.queryGame(record.getGameId());
            if (gameAllInfo == null) {
                continue;
            }

            GameVersion defaultVersion = gameAllInfo.getVersions().get(0);
            Game game = Game.valueOf(gameAllInfo, defaultVersion, defaultVersion.getgLan().get(Constant.LANGUAGE_DEFAULT));
            BeanUtils.copyProperties(game, gameVO);
            gameVOS.add(gameVO);
        }

        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        String key = Constant.REDIS_KEY_TOP_GAME;
        if (gameVOS.isEmpty()) {
            zSetOperations.add(key, String.valueOf(-1), 0);
        } else {
            Set<ZSetOperations.TypedTuple> set = new LinkedHashSet<>();
            for(TopGameRecord record : list){
                set.add(new DefaultTypedTuple(record.getGameId(), Double.valueOf(record.getCount())));
            }
            zSetOperations.add(key, set);
        }

        return gameVOS;
    }
}
