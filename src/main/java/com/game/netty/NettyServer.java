package com.game.netty;

import com.game.netty.constant.NettyConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private static NioEventLoopGroup boss;

    private static NioEventLoopGroup worker;

    private static ServerBootstrap bootstrap;

    @Value("${netty.open}")
    private boolean open = false;

    @Value("${netty.port}")
    private int port;

    @PostConstruct
    private void init() throws Exception{
        if(open){
            boss = new NioEventLoopGroup(NettyConstant.BOSS_THREAD);
            worker = new NioEventLoopGroup(NettyConstant.WORK_THREAD);
            bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 500)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ServerHandlerInitializer());
            bootstrap.bind(port).sync();
            logger.info("######netty server start at:" + port + "######");
        }
    }

    /***
     * 关闭线程池
     */
    public void shutdown(){
        if(boss != null){
            boss.shutdownGracefully();
        }
        if(worker != null){
            worker.shutdownGracefully();
        }
    }
}
