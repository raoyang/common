package com.game.netty;

import com.game.netty.codec.MessageDecoder;
import com.game.netty.codec.MessageEncoder;
import com.game.netty.constant.NettyConstant;
import com.game.netty.handler.ServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

public class ServerHandlerInitializer extends ChannelInitializer<NioSocketChannel> {

    @Override
    protected void initChannel(NioSocketChannel ch) {
        ch.pipeline().addLast(new MessageDecoder());
        ch.pipeline().addLast(new MessageEncoder());
        ch.pipeline().addLast(new ReadTimeoutHandler(NettyConstant.READ_TIME_OUT, TimeUnit.SECONDS));
        ch.pipeline().addLast(new ServerHandler());
    }
}
