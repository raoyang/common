package com.game.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheLogger {

    private static final Logger logger = LoggerFactory.getLogger("cache");

    public static void cacheLogger(String msg){
        logger.info(msg);
    }
}
