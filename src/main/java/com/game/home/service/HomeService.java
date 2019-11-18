package com.game.home.service;

import com.game.album.service.AlbumService;
import com.game.banner.domain.BannerInfo;
import com.game.banner.service.BannerService;
import com.game.common.vo.DataVO;
import com.game.game.dao.GameMapper;
import com.game.game.domain.Game;
import com.game.game.domain.GameAllInfo;
import com.game.game.domain.GameVO;
import com.game.game.service.GameService;
import com.game.home.dao.HomeMapper;
import com.game.home.domain.*;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class HomeService {
    private static final Logger logger = LoggerFactory.getLogger(HomeService.class);
    @Autowired
    HomeMapper homeMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    AlbumService albumService;
    @Autowired
    BannerService bannerService;
    @Autowired
    GameService gameService;
    @Value("${album.new.version}")
    private int newAlbumVersion;
    @Value("${album.title.show}")
    private boolean albumTitleISShow;

    /**
     * 首页专辑列表
     * @param pageVO
     * @return
     */
    public DataVO home(HomePageVO pageVO) {
        List<HomeRule> lists = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        ValueOperations operations = redisTemplate.opsForValue();
        Object jsonList = operations.get(Constant.REDIS_KEY_HOME_ALBUM_CFG);
        long endTime = System.currentTimeMillis();
        logger.info("get home album cfg redis, cost:" + (endTime - startTime) + "ms");
        Gson gson = new Gson();
        if (jsonList != null) {
            lists = gson.fromJson((String)jsonList, new TypeToken<List<HomeRule>>() {
            }.getType());
        } else {
            long startTimeFormDB = System.currentTimeMillis();
            lists = homeMapper.queryAllAndRule();
            operations.set(Constant.REDIS_KEY_HOME_ALBUM_CFG, gson.toJson(lists), 1, TimeUnit.HOURS);
            long endTimeFormDB = System.currentTimeMillis();
            logger.info("get home album cfg db, cost:" + (endTimeFormDB - startTimeFormDB) + "ms");
        }

        if (lists.isEmpty()) {
            logger.info("home album no data");
            return DataVO.error("no data", Constant.RNT_CODE_NO_DATA);
        }

        //开始过滤
        filterBannerOrAlbum(lists, pageVO);

        if (lists.isEmpty()) {
            logger.info("home album filter empty");
            return DataVO.error("no data", Constant.RNT_CODE_NO_DATA);
        }

        int page = pageVO.getPage();
        if (page == 0) {
            logger.info("home page empty");
            page = 1;
        }

        int limit = pageVO.getLimit();
        if (limit == 0) {
            logger.info("home limit empty");
            limit = 2;
        }

        int offset = (page - 1) * limit;
        if (offset >= lists.size()) {
            return DataVO.error("no data", Constant.RNT_CODE_NO_DATA);
        }

        int toIndex = lists.size();
        if ((offset + limit) < lists.size()) {
            toIndex = offset + limit;
        }

        //查询banner配置数据:针对新版本
        Map<String, Map<Integer, BannerInfo>> bannerMap = new HashMap<>();
        if (pageVO.getAppVer() >= newAlbumVersion) {
            bannerMap = bannerService.fetchBanner(pageVO);
        }

        List<Object> result = new ArrayList<>();
        List<HomeRule> data = lists.subList(offset, toIndex);
        for (HomeRule homeRule : data) {
            // 缓存
            List<String> gameIds = albumService.getGameIdByAlbumId(homeRule.getAlbumId(), 1, homeRule.getNumLimit());
            if (gameIds.isEmpty()) {
                logger.warn("album not conf game, id:" + homeRule.getAlbumId());
                continue;
            }

            //补充游戏资源数据
            long startTimeQueryGame = System.currentTimeMillis();
            List<GameAllInfo> games = gameService.queryGameByIds(gameIds);
            long endTimeQueryGame = System.currentTimeMillis();
            logger.info("query games cost:" + (endTimeQueryGame - startTimeQueryGame) + "ms");
            if (games.isEmpty()) {
                logger.info("home page, get album game empty, id:" + homeRule.getAlbumId());
                continue;
            }

            //过滤game
            List<Game> filterGame = gameService.filterGames(games, pageVO);

            Map<String, Game> gameMap = filterGame.stream().collect(Collectors.toMap(Game::getGameId, game -> game));

            // 查询游戏在线人数
            List<String> redisKeys = new ArrayList<>();
            for (String id : gameIds) {
                redisKeys.add(Constant.REDIS_KEY_PREFIX_GAME_ONLINE + id);
            }
            List<String> onlineCnt = operations.multiGet(redisKeys);
            logger.info("redis multi get:" + gson.toJson(onlineCnt));
            Map<String, Integer> gameOnline = new HashMap<>();
            for (int i=0; i < gameIds.size(); i++) {
                gameOnline.put(gameIds.get(i), onlineCnt.get(i) == null ? 0 : Integer.valueOf(onlineCnt.get(i)));
            }

            HomeVO homeVO = new HomeVO();
            homeVO.setId(homeRule.getAlbumId());
            homeVO.setTitle(homeRule.getTitle());
            homeVO.setMore(homeRule.getMore());
            List<HomeCard> gameVOS = new ArrayList<>();
            for (String gameId : gameIds) {
                if (! gameMap.containsKey(gameId)) {
                    continue;
                }
                HomeGameVO gameVO = new HomeGameVO();
                BeanUtils.copyProperties(gameMap.get(gameId), gameVO);
                gameVO.setOnline(CommonFunUtil.getOnlineGamer(gameOnline.get(gameId)));
                gameVOS.add(gameVO);
            }

            //banner插入到对应的位置
            if (pageVO.getAppVer() >= newAlbumVersion) {
                if (bannerMap.containsKey(homeRule.getAlbumId())) {
                    for (Map.Entry<Integer, BannerInfo> bannerInfo : bannerMap.get(homeRule.getAlbumId()).entrySet()) {
                        int pos = bannerInfo.getKey();
                        if (pos > gameVOS.size()) {
                            pos = gameVOS.size();
                        }
                        if (pos % 2 == 0) {
                            pos = pos + 1;
                        }
                        gameVOS.add(pos - 1, bannerInfo.getValue());
                    }
                }

                //是否显示title
                if (! albumTitleISShow) {
                    homeVO.setTitle("");
                }
            }

            homeVO.setGames(gameVOS);

            result.add(homeVO);
        }

        if (result.isEmpty()) {
            return DataVO.error("no data", Constant.RNT_CODE_NO_DATA);
        }

        return DataVO.success(lists.size(), result);
    }

    public void filterBannerOrAlbum(List<? extends Rule> lists, CommParam commParam) {
        String country = CommonFunUtil.getCountryByLan(commParam.getLan());
        String product = commParam.getDevModel();
        Iterator<? extends Rule> it = lists.iterator();
        while (it.hasNext()) {
            Rule homeRule = it.next();
            //国家黑名单
            if (! TextUtils.isEmpty(homeRule.getShieldCountry()) && homeRule.getShieldCountry().contains(country)) {
                logger.debug("filter country black, country:" + country);
                it.remove();
                continue;
            }

            //国家白名单
            if (! TextUtils.isEmpty(homeRule.getSpecifyCountry()) && ! homeRule.getSpecifyCountry().contains(country)) {
                logger.debug("filter country white, country:" + country);
                it.remove();
                continue;
            }

            //机型黑名单
            if (! TextUtils.isEmpty(homeRule.getShieldPhoneType()) && !TextUtils.isEmpty(product) && homeRule.getShieldPhoneType().contains(product)) {
                logger.debug("filter product black, product:" + product);
                it.remove();
                continue;
            }

            //机型白名单
            if (! TextUtils.isEmpty(homeRule.getSpecifyPhoneType()) && !TextUtils.isEmpty(product) && ! homeRule.getSpecifyPhoneType().contains(product)) {
                logger.debug("filter product white, product:" + product);
                it.remove();
            }
        }
    }
}
