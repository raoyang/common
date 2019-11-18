package com.game.album.service;

import com.game.album.dao.AlbumMapper;
import com.game.album.domain.AlbumDetailRVO;
import com.game.common.vo.DataVO;
import com.game.game.domain.Game;
import com.game.game.domain.GameAllInfo;
import com.game.game.service.GameService;
import com.game.home.domain.HomeGameVO;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AlbumService {
    private static final Logger logger = LoggerFactory.getLogger(AlbumService.class);
    @Autowired
    AlbumMapper albumMapper;
    @Autowired
    GameService gameService;
    @Autowired
    RedisTemplate redisTemplate;

    public List<String> getGameIdByAlbumId(String albumId, int page, int limit) {
        return albumMapper.queryAlbumContent(albumId, page, limit);
    }

    public DataVO details(AlbumDetailRVO detailRVO) {
        String albumId = detailRVO.getId();
        if (TextUtils.isEmpty(albumId)) {
            logger.debug("param id empty");
            return DataVO.error("param id empty", Constant.RNT_CODE_PARAM_ERROR);
        }

        int page = detailRVO.getPage();
        if (page == 0) {
            page = 1;
        }

        int limit = detailRVO.getLimit();
        if (limit == 0) {
            limit = 20;
        }

        List<String> gameIds = albumMapper.queryAlbumContent(albumId, page, limit);
        if (gameIds.isEmpty()) {
            return DataVO.error("no data", Constant.RNT_CODE_NO_DATA);
        }

        //补充游戏资源数据
        List<GameAllInfo> games = gameService.queryGameByIds(gameIds);
        if (games.isEmpty()) {
            logger.info("album detail, get album game empty, id:" + albumId);
            return DataVO.error("no data", Constant.RNT_CODE_NO_DATA);
        }

        //过滤game
        List<Game> filterGame = gameService.filterGames(games, detailRVO);

        Map<String, Game> gameMap = filterGame.stream().collect(Collectors.toMap(Game::getGameId, game -> game));

        // 查询游戏在线人数
        List<String> redisKeys = new ArrayList<>();
        for (String id : gameIds) {
            redisKeys.add(Constant.REDIS_KEY_PREFIX_GAME_ONLINE + id);
        }
        ValueOperations operations = redisTemplate.opsForValue();
        List<String> onlineCnt = operations.multiGet(redisKeys);
        Map<String, Integer> gameOnline = new HashMap<>();
        for (int i=0; i < gameIds.size(); i++) {
            gameOnline.put(gameIds.get(i), onlineCnt.get(i) == null ? 0 : Integer.valueOf(onlineCnt.get(i)));
        }

        List<HomeGameVO> gameVOS = new ArrayList<>();
        for (String gameId : gameIds) {
            if (! gameMap.containsKey(gameId)) {
                continue;
            }
            gameVOS.add(HomeGameVO.valueOf(gameMap.get(gameId), CommonFunUtil.getOnlineGamer(gameOnline.get(gameId))));
        }

        int total = albumMapper.countAlbumContent(albumId);

        return DataVO.success(total, gameVOS);
    }

}
