package com.game.netty.user;

import com.game.account.domain.AccountInfo;
import com.game.chat.ChatDispatcher;
import com.game.chat.callback.OfflineCallBack;
import com.game.chat.event.impl.LoginEvent;
import com.game.chat.message.OriginChatMessage;
import com.game.chat.message.S2CContent;
import com.game.chat.message.S2CMessage;
import com.game.chat.util.JsonUtils;
import com.game.logger.ExceptionLogger;
import com.game.login.service.LoginService;
import com.game.match.MatchDispatcher;
import com.game.netty.constant.LoginCmd;
import com.game.netty.constant.Module;
import com.game.netty.manager.ChannelManager;
import com.game.netty.message.BaseMessage;
import com.game.netty.message.LoginMessage;
import com.game.util.Constant;
import io.netty.channel.Channel;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.Charset;

public class MessageWorker {

    private static final Logger logger = LoggerFactory.getLogger(MessageWorker.class);

    private Channel channel;

    private AccountInfo account;

    public static MessageWorker valueOf(Channel channel){
        MessageWorker worker = new MessageWorker();
        worker.channel = channel;
        return worker;
    }

    /***
     * 消息入口
     * @param msg
     */
    public void onMessage(BaseMessage msg){
        try {
            switch (msg.getModule()){
                case Module.LOGIN:
                    LoginService.getInstance().onPlayerLogin(msg, this);
                    break;
                case Module.CHAT:
                    if(account == null){
                        logger.debug("account not login.");
                        return;
                    }
                    ChatDispatcher.getInstance().postMsg(OriginChatMessage.valueOf(msg, account.getId()));
                    break;
                case Module.MATCH:
                    if(account == null){
                        logger.debug("account not login.");
                        return;
                    }
                    MatchDispatcher.getInstance().onMatch(msg, account.getId());
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            ExceptionLogger.exceptionLogger("执行消息,module:" + msg.getModule() + ",cmd:" + msg.getCmd() + "异常");
            e.printStackTrace();
        }
    }

    public Channel getChannel(){
        return channel;
    }

    public AccountInfo getAccount(){
        return account;
    }

    public boolean sendMessage(BaseMessage msg){
        if(channel.isActive()){
            int accountId = account == null ? 0 : account.getId();
            logger.info("向accountId:" + accountId + "发送消息， module:" + msg.getModule() + ", cmd:" + msg.getCmd());
            channel.writeAndFlush(msg);
            return true;
        }
        int accountId = account == null ? 0 : account.getId();
        logger.info("通道不活跃，发送数据失败:" + accountId);
        return false;
    }

    public boolean sendMessage(S2CMessage message){
        if(channel.isActive()){
            int accountId = account == null ? 0 : account.getId();
            logger.info("向accountId:" + accountId + "发送消息， module:" + message.getModule() + ", cmd:" + message.getCmd());
            channel.writeAndFlush(message.serialize());
            return true;
        }
        int accountId = account == null ? 0 : account.getId();
        logger.info("通道不活跃，发送数据失败:" + accountId);
        return false;
    }

    public void sendMsgOfflineCallBack(S2CMessage msg, OfflineCallBack callBack) throws Exception{
        if(channel.isActive()){
            //消息转换，发送
            int accountId = account == null ? 0 : account.getId();
            logger.info("向accountId:" + accountId + "发送消息， module:" + msg.getModule() + ", cmd:" + msg.getCmd());
            channel.writeAndFlush(msg.serialize());
            return;
        }
        callBack.callback((S2CContent) msg.getResult().getData());
    }

    public boolean sendMessageWithListener(S2CMessage message, GenericFutureListener listener){
        if(channel.isActive()){
            int accountId = account == null ? 0 : account.getId();
            logger.info("向accountId:" + accountId + "发送消息， module:" + message.getModule() + ", cmd:" + message.getCmd());
            channel.writeAndFlush(message.serialize()).addListener(listener);
            return true;
        }
        int accountId = account == null ? 0 : account.getId();
        logger.info("通道不活跃，发送数据失败:" + accountId);
        return false;
    }

    /***
     * 执行用户task
     * 注意：task不能包含任何io阻塞的任务，比如访问数据库、Redis、文件系统等待
     * @param task
     */
    public void excute(Runnable task){
        if(task != null){
            channel.eventLoop().execute(task);
        }
    }

    public void setAccount(AccountInfo account) {
        this.account = account;
    }
}
