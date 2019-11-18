package com.game.config.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiTask implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(ApiTask.class);

    private int taskId;

    private Runnable task;

    public ApiTask(){}

    public ApiTask(int taskId, Runnable task){
        this.taskId = taskId;
        this.task = task;
    }

    @Override
    public void run() {
        if(task == null){
            return;
        }
        long start = System.currentTimeMillis();
        task.run();
        long end = System.currentTimeMillis();
        long realTime = end - start;
        if(realTime > 200){
            logger.warn("taskId:" + taskId + "execute timeout,real time is:" + realTime);
        }
    }
}
