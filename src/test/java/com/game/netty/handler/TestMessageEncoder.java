package com.game.netty.handler;

import com.game.netty.message.BaseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMessageEncoder extends MessageToByteEncoder<BaseMessage> {

    private static final Logger logger = LoggerFactory.getLogger(TestMessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, BaseMessage msg, ByteBuf out) throws Exception {
        if(msg == null){
            return;
        }
        if(!msg.isValid()){
            logger.error("write error message：" + msg);
            return;
        }
        out.writeShort(msg.getModule());
        out.writeShort(msg.getCmd());
        out.writeInt(msg.getBody().length);
        out.writeBytes(msg.getBody());
    }
}
