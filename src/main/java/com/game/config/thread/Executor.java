package com.game.config.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Executor extends AbstractExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(Executor.class);

    private BlockingQueue<Runnable> queue = null;

    private volatile boolean shutdown = false;

    private volatile boolean terminate = false;

    private class worker implements Runnable{

        @Override
        public void run() {
            while (true){
                try{
                    Runnable task = queue.take();
                    if(task == null){
                        continue;
                    }
                    if(task instanceof shutdown){
                        break;
                    }
                    task.run();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            logger.info("线程:" + Thread.currentThread().getName() + "已经关闭.");
            shutdown = true;
        }
    }

    public Executor(){
        queue = new LinkedBlockingQueue<>();
        Runnable worker = this.new worker();
        Thread thread = new Thread(worker);
        thread.start();
    }

    private class shutdown implements Runnable{
        @Override
        public void run() {
        }
    }

    @Override
    public void shutdown() {
        queue.offer(new shutdown());
        terminate = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public boolean isTerminated() {
        return terminate;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void execute(Runnable command) {
        if(terminate){
            throw new RuntimeException("thread wait shutdown.");
        }
        queue.offer(command);
    }
}
