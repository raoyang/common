package com.game.netty.codec;

import com.game.netty.NettyServer;
import com.game.netty.message.BaseMessage;
import com.game.util.AESUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageEncoder extends MessageToByteEncoder<BaseMessage> {

    private static final Logger logger = LoggerFactory.getLogger(MessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, BaseMessage msg, ByteBuf out) throws Exception {
        if(msg == null){
            return;
        }
        if(!msg.isValid()){
            logger.error("write error message：" + msg);
            return;
        }
        /*
        int length = 0;
        if(msg.getBody() != null){
            length = msg.getBody().length;
        }*/
        out.writeShort(msg.getModule());
        out.writeShort(msg.getCmd());
        if(msg.getBody() != null){
            byte[] en = AESUtil.encrypt(msg.getBody());
            if(en == null){
                out.writeInt(0);
            }else{
                out.writeInt(en.length);
                out.writeBytes(en);
            }
        }else{
            out.writeInt(0);
        }
    }

    public static void main(String args[]){
        try {
            byte[] en = AESUtil.encrypt(new byte[0]);
            System.out.println(en);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
