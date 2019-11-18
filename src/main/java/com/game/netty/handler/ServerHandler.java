package com.game.netty.handler;

import com.game.match.service.MatchService;
import com.game.netty.constant.HeartBeatCmd;
import com.game.netty.constant.Module;
import com.game.netty.manager.ChannelManager;
import com.game.netty.message.BaseMessage;
import com.game.netty.user.MessageWorker;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

public class ServerHandler extends SimpleChannelInboundHandler<BaseMessage> {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    private MessageWorker worker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMessage msg) throws Exception {
        logger.debug("Recieve message module:" + msg.getModule() + ", cmd:" + msg.getCmd() + ",msg:" + new String(msg.getBody(), Charset.forName("utf-8")));
        if(worker.getAccount() != null){
            logger.debug("playerId:" + worker.getAccount().getId());
        }
        if(msg.getModule() == Module.HEART_BEAT && msg.getCmd() == HeartBeatCmd.C2S){
            BaseMessage result = BaseMessage.valueOf(Module.HEART_BEAT, HeartBeatCmd.S2C);
            msg.setBody(new byte[0]);
            ctx.channel().writeAndFlush(result);
            return;
        }
        worker.onMessage(msg);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        logger.debug("client connected:" + ctx.channel().remoteAddress());
        worker = MessageWorker.valueOf(ctx.channel());
        ChannelManager.getInstance().addMessageWorker(worker);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        logger.debug("cliend unconnected:" + ctx.channel().remoteAddress());
        ChannelManager.getInstance().rmMessageWorker(worker);
        MatchService.getInstance().rmPreMatchList(worker.getAccount().getId());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        logger.debug("user event triggered:" + evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        if(cause instanceof ReadTimeoutException){
            Channel channel = ctx.channel();
            logger.info("ReadTimeOut, begin close channel:" + channel.remoteAddress());
            channel.close().sync();
        }else if(cause instanceof IOException){
            logger.debug("IOException:" + cause);
        }
    }
}
