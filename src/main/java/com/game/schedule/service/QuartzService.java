package com.game.schedule.service;

import com.game.match.service.MatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class QuartzService implements ApplicationRunner{
    private static Logger logger = LoggerFactory.getLogger(QuartzService.class);

    @Autowired
    ExecutorService threadMatch;
    @Autowired
    MatchService matchService;
    @Value("${match.timer}")
    private int matchTimer;
    @Value("${match.enable}")
    private boolean matchEnable;

    public void match() {
        threadMatch.execute(new Runnable() {
            @Override
            public void run() {
                //匹配逻辑
                matchService.dealMatch();
            }
        });
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        /*if (matchEnable) {
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(()->match(), 0, matchTimer, TimeUnit.SECONDS);
        }*/
    }
}
