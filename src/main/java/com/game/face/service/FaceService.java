package com.game.face.service;

import com.game.chat.util.JsonUtils;
import com.game.face.dao.FaceMapper;
import com.game.face.domain.Face;
import com.game.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class FaceService {
    private static final Logger logger = LoggerFactory.getLogger(FaceService.class);
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    FaceMapper faceMapper;

    public List<Face> getFaceList() {
        ValueOperations operations = redisTemplate.opsForValue();
        String redisKey = Constant.REDIS_KEY_GAME_FACE;

        List<Face> faces = new ArrayList<>();
        Object obj = operations.get(redisKey);
        if (obj == null) {
            faces = faceMapper.queryAll();
            if (faces.isEmpty()) {
                return faces;
            }

            operations.set(redisKey, JsonUtils.objectToString(faces), 30, TimeUnit.DAYS);
        } else {
            faces = JsonUtils.stringToList((String)obj, Face[].class);
        }

        return faces;
    }
}
