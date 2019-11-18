package com.game.config.thread;

public class ThreadPool {

    private Executor[] executors;

    public ThreadPool(int num){
        if(num <= 0){
            throw  new RuntimeException("thread num is incorrect:" + num);
        }
        executors = new Executor[num];
        for(int i = 0 ; i < num ; i ++){
            executors[i] = new Executor();
        }
    }

    public Executor getExecutor(int id){
        if(executors == null){
            return null;
        }
        int index = id & (executors.length - 1);
        return executors[index];
    }
}
