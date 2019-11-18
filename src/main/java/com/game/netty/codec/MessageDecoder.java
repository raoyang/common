package com.game.netty.codec;

import com.game.account.domain.AccountInfo;
import com.game.logger.ExceptionLogger;
import com.game.netty.NettyServer;
import com.game.netty.manager.ChannelManager;
import com.game.netty.message.BaseMessage;
import com.game.netty.user.MessageWorker;
import com.game.util.AESUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

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
        if(length < 0){
            in.resetReaderIndex();
            return;
        }

        if(length > 1024 * 1024){
            ExceptionLogger.exceptionLogger("读到异常包, module:" + moduleId + ", cmd:" + cmd + ", length:" + length + "地址是:" + ctx.channel().remoteAddress());
            MessageWorker mw = ChannelManager.getInstance().getMessageWorker(ctx.channel());
            if(mw != null){
                AccountInfo info = mw.getAccount();
                if(info != null){
                    ExceptionLogger.exceptionLogger("用户ID是:" + info.getId());
                }
            }
            ctx.channel().close().sync();
        }

        if(in.readableBytes() < length){
            in.resetReaderIndex();
            return;
        }

        byte[] body = new byte[length];
        in.readBytes(body, 0, length);

        BaseMessage msg = BaseMessage.valueOf(moduleId, cmd);
        try {
            byte[] bytes = AESUtil.decrypt(body);
            if(bytes == null){
                msg.setBody(new byte[0]);
            }else{
                msg.setBody(bytes);
            }
            out.add(msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
