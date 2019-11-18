package com.game.config;

import com.game.config.thread.ThreadPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {
    @Bean(name = "threadMatch")
    public ExecutorService getMatchThreadPool(){
        //默认线程数
        return Executors.newFixedThreadPool(1);
    }

    @Bean(name = "threadHttp")
    public ExecutorService getHttpThreadPool() {
        //默认线程数
        return Executors.newFixedThreadPool(5);
    }

    @Bean(name = "localThread")
    public ThreadPool getLocalThreadPool() {
        return new ThreadPool(1);
    }
}
