package com.game.banner.service;

import com.game.banner.dao.BannerMapper;
import com.game.banner.domain.*;
import com.game.chat.util.JsonUtils;
import com.game.common.vo.ResultVO;
import com.game.game.domain.Game;
import com.game.game.domain.GameAllInfo;
import com.game.game.domain.GameVO;
import com.game.game.service.GameService;
import com.game.home.domain.CommParam;
import com.game.home.service.HomeService;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class BannerService {
    private static final Logger logger = LoggerFactory.getLogger(BannerService.class);
    @Autowired
    BannerMapper bannerMapper;
    @Autowired
    GameService gameService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    HomeService homeService;

    public ResultVO queryBanner(CommParam commParam) {
        List<Banner> lists = queryBanner();
        if (lists.isEmpty()) {
            logger.info("banner no conf data");
            return ResultVO.error("no data", Constant.RNT_CODE_NO_DATA);
        }

        //start filter
        homeService.filterBannerOrAlbum(lists, commParam);

        List<BannerVO> bannerVOS = new ArrayList<>();
        for (Banner b : lists) {
            BannerVO vo = new BannerVO();
            vo.setName(b.getName());
            vo.setIcon(b.getIcon());
            vo.setLinkType(b.getLinkType());
            vo.setLinkTo(b.getLinkTo());
            if (b.getLinkType() == Constant.BANNER_TYPE_GAME_RECOMMEND) {
                GameAllInfo game = gameService.queryGame(b.getLinkTo());
                if (game == null) {
                    logger.info("banner, recommend game null");
                    continue;
                }
                //过滤game
                List<GameAllInfo> games = new ArrayList<>(Arrays.asList(game));
                List<Game> filterGame = gameService.filterGames(games, commParam);
                logger.debug("banner filter game:" + JsonUtils.objectToString(filterGame));
                if (filterGame.isEmpty()) {
                    logger.debug("banner, filter game empty");
                    continue;
                }

                GameVO gameVO = new GameVO();
                BeanUtils.copyProperties(filterGame.get(0), gameVO);
                vo.setGameInfo(gameVO);
            } else {
                logger.info("no support type, linkType:" + b.getLinkType());
            }

            bannerVOS.add(vo);
        }

        return ResultVO.success(bannerVOS);
    }

    //为专辑提供banner数据
    public Map<String, Map<Integer, BannerInfo>> fetchBanner(CommParam commParam) {
        Map<String, Map<Integer, BannerInfo>> bannerMap = new HashMap<>();
        List<Banner> lists = queryBanner();
        if (lists.isEmpty()) {
            logger.info("banner no conf data");
            return bannerMap;
        }

        homeService.filterBannerOrAlbum(lists, commParam);
        if (lists.isEmpty()) {
            logger.debug("banner filter empty from fetch banner");
            return bannerMap;
        }

        BannerInfo bannerInfo = null;
        for (Banner banner : lists) {
            int albumPos = banner.getAlbumPos();
            String albumId = banner.getAlbumId();
            if (TextUtils.isEmpty(albumId) || albumPos == 0) {
                continue;
            }

            int linkType = banner.getLinkType();
            if (linkType == Constant.BANNER_TYPE_GAME_RECOMMEND) {
                GameAllInfo game = gameService.queryGame(banner.getLinkTo());
                if (game == null) {
                    logger.info("banner, recommend game null, gameId:" + banner.getLinkTo());
                    continue;
                }

                //过滤game
                List<GameAllInfo> games = new ArrayList<>(Arrays.asList(game));
                List<Game> filterGame = gameService.filterGames(games, commParam);
                if (filterGame.isEmpty()) {
                    logger.debug("banner, filter game empty, gameId:" + banner.getLinkTo());
                    continue;
                }

                //补充在线人数
                int onlineCnt = 0;
                ValueOperations valueOperations = redisTemplate.opsForValue();
                Object onlineObj = valueOperations.get(Constant.REDIS_KEY_PREFIX_GAME_ONLINE + banner.getLinkTo());
                if (onlineObj != null) {
                    onlineCnt = Integer.parseInt((String)onlineObj);
                }
                bannerInfo = BannerGame.valueOf(filterGame.get(0), CommonFunUtil.getOnlineGamer(onlineCnt), banner);
            } else if(linkType == Constant.BANNER_TYPE_H5) {
                bannerInfo = BannerH5.valueOf(banner);
            } else {
                logger.info("no support type, linkType:" + banner.getLinkType());
            }

            if (bannerInfo != null) {
                if (bannerMap.containsKey(albumId)) {
                    if (bannerMap.get(albumId).containsKey(albumPos)) {
                        logger.warn("insert album same pos, bannerId:" + banner.getAlbumId());
                    } else {
                        bannerMap.get(albumId).put(albumPos, bannerInfo);
                    }
                } else {
                    Map<Integer, BannerInfo> albumPosMap = new LinkedHashMap<>();
                    albumPosMap.put(albumPos, bannerInfo);
                    bannerMap.put(albumId, albumPosMap);
                }
            }
        }

        logger.debug("insert album banner info:" + JsonUtils.objectToString(bannerMap));

        return bannerMap;
    }

    private List<Banner> queryBanner() {
        Gson gson = new Gson();
        ValueOperations operations = redisTemplate.opsForValue();
        Object banners = operations.get(Constant.REDIS_KEY_HOME_BANNER);
        List<Banner> lists = new ArrayList<>();
        if (banners == null) {
            lists = bannerMapper.queryAllAndRule();
            operations.set(Constant.REDIS_KEY_HOME_BANNER, gson.toJson(lists), 1, TimeUnit.HOURS);
        } else {
            lists = gson.fromJson((String)banners, new TypeToken<List<Banner>>() {
            }.getType());
        }

        return lists;
    }
}
