package com.game.netty.handler;

import com.game.netty.message.BaseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<BaseMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMessage msg) throws Exception {
        System.out.println("收到客户端的请求, moduleId:" + msg.getModule() + ", cmd:" + msg.getCmd());
    }
}
