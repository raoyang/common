package com.game.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionLogger {

    private static final Logger logger = LoggerFactory.getLogger("exception");

    public static void exceptionLogger(String msg){
        logger.info(msg);
    }
}
