package com.game.netty.constant;

public class NettyConstant {

    public static final int BOSS_THREAD = 1;

    public static final int WORK_THREAD = Runtime.getRuntime().availableProcessors();

    //读超时，设置为120s
    public static final int READ_TIME_OUT = 300;
}
