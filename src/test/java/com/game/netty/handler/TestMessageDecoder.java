package com.game.netty.handler;

import com.game.netty.message.BaseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TestMessageDecoder extends ByteToMessageDecoder {

    private static final int PACKAGE_HEAD_LENGTH = 8; //模块号占2个，协议号占2个，包长度占4个

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if(in.readableBytes() < PACKAGE_HEAD_LENGTH){
            return;
        }

        in.markReaderIndex();

        //尝试读取模块号
        short moduleId = in.readShort();
        if(moduleId <= 0){
            in.resetReaderIndex();
            return;
        }

        //尝试读取协议号
        short cmd = in.readShort();
        if(cmd <= 0){
            in.resetReaderIndex();
            return;
        }

        int length = in.readInt();
        if(length <= 0){
            in.resetReaderIndex();
            return;
        }

        if(in.readableBytes() < length){
            return;
        }

        byte[] body = new byte[length];
        in.readBytes(body, 0, length);

        BaseMessage msg = BaseMessage.valueOf(moduleId, cmd);
        msg.setBody(body);
        out.add(msg);
    }
}
