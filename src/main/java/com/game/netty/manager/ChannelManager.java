package com.game.netty.manager;

import com.game.account.domain.AccountInfo;
import com.game.chat.callback.OfflineCallBack;
import com.game.chat.message.S2CContent;
import com.game.chat.message.S2CMessage;
import com.game.netty.message.BaseMessage;
import com.game.netty.user.MessageWorker;
import io.netty.channel.Channel;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {

    private static final Logger logger = LoggerFactory.getLogger(ChannelManager.class);

    /** 所有channel列表 **/
    private static final Map<Channel, MessageWorker> workers = new ConcurrentHashMap<>();

    /** 在线用户列表 **/
    private static final Map<Integer, MessageWorker> onlines = new ConcurrentHashMap<>();

    private static ChannelManager instance = new ChannelManager();

    private ChannelManager(){}

    public static ChannelManager getInstance(){
        return instance;
    }

    public void addMessageWorker(MessageWorker worker){
        workers.put(worker.getChannel(), worker);
    }

    public void rmMessageWorker(MessageWorker worker){
        if(workers.containsKey(worker.getChannel())){
            workers.remove(worker.getChannel());
            AccountInfo info = worker.getAccount();
            if(info != null){
                rmAccountInfo(info);
            }
        }
    }

    public MessageWorker getMessageWorker(Channel channel){
        return workers.get(channel);
    }

    public void addAccountInfo(MessageWorker worker){
        if(worker.getAccount() == null){
            throw new NullPointerException("add null worker");
        }
        onlines.put(worker.getAccount().getId(), worker);
    }

    private void rmAccountInfo(AccountInfo info){
        if(info == null){
            return;
        }
        logger.info("用户下线:" + info.getId());
        onlines.remove(info.getId());
    }

    public boolean isOnline(int accountId){
        return onlines.containsKey(accountId);
    }

    public MessageWorker getMessageWorker(int accountId){
        return onlines.get(accountId);
    }

    public static boolean sendMessage(int targetAccountId, BaseMessage msg){
        MessageWorker worker = onlines.get(targetAccountId);
        if(worker == null){
            logger.info("向不在线的用户发送数据:" + targetAccountId);
            return false;
        }
        return worker.sendMessage(msg);
    }

    /***
     * 发送消息，如果目标用户不在线则回调
     * @param targetAccountId 目标用户
     * @param msg 消息
     * @param callBack 回调
     */
    public static void sendMsgOfflineCallBack(int targetAccountId, S2CMessage msg, OfflineCallBack callBack) throws Exception{
        MessageWorker worker = onlines.get(targetAccountId);
        if(worker == null){
            callBack.callback((S2CContent) msg.getResult().getData());
            return;
        }
        worker.sendMsgOfflineCallBack(msg, callBack);
    }

    public static boolean sendMessage(int targetAccountId, S2CMessage message){
        MessageWorker worker = onlines.get(targetAccountId);
        if(worker == null){
            logger.info("向不在线的用户发送数据:" + targetAccountId);
            return false;
        }
        return worker.sendMessage(message);
    }

    public static boolean sendMessageWithListener(int targetAccountId, S2CMessage message, GenericFutureListener listener){
        MessageWorker worker = onlines.get(targetAccountId);
        if(worker == null){
            logger.info("向不在线的用户发送数据:" + targetAccountId);
            return false;
        }
        return worker.sendMessageWithListener(message, listener);
    }
}
