package com.game.game;

import com.game.game.dao.GameMapper;
import com.game.game.domain.Game;
import com.game.util.Constant;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeValue2HashTest {

    /*
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private GameMapper gameMapper;

    @Test
    public void test(){
        List<Game> games = new ArrayList<>();
        HashOperations<String, String, String> operations = redisTemplate.opsForHash();
        Map<String, String> map = operations.entries(Constant.REDIS_KEY_ALL_GAME);
        Gson gson = new Gson();
        if(map != null && !map.isEmpty()){
            for(String game:map.values()){
                games.add(gson.fromJson(game, Game.class));
            }
        }else{
            games = gameMapper.queryAll();
            map = new HashMap<>();
            for(Game game : games){
                StringBuilder sb = new StringBuilder();
                sb.append(game.getGameId())
                        .append("_")
                        .append(game.getVersionCode());
                map.put(sb.toString(), gson.toJson(game));
            }
            operations.putAll(Constant.REDIS_KEY_ALL_GAME, map);
        }

    }

     */
}
