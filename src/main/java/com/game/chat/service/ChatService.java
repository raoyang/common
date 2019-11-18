package com.game.chat.service;

import com.game.account.dao.AccountMapper;
import com.game.account.domain.AccountInfo;
import com.game.chat.annotation.Protocol;
import com.game.chat.constant.ChatCmd;
import com.game.chat.constant.ChatRedisKey;
import com.game.chat.constant.ChatType;
import com.game.chat.constant.RefuseType;
import com.game.chat.event.impl.LoginEvent;
import com.game.chat.message.S2CContent;
import com.game.chat.message.c2s.C2SChatMessage;
import com.game.chat.message.OriginChatMessage;
import com.game.chat.message.s2c.S2CChatMessage;
import com.game.chat.message.S2CMessage;
import com.game.chat.message.s2c.S2CRefuseMessage;
import com.game.chat.util.JsonUtils;
import com.game.common.vo.ResultVO;
import com.game.friend.service.FriendService;
import com.game.netty.constant.Module;
import com.game.netty.manager.ChannelManager;
import com.game.util.Constant;
import com.game.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private AccountMapper mapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private FriendService friendService;

    @Protocol(module = Module.CHAT, cmd = ChatCmd.ACCOUNT_SENT_CHAT_MSG_C2S)
    private void accountSendChatMessage(OriginChatMessage chatMessage) throws Exception{
        C2SChatMessage message = C2SChatMessage.valueOf(chatMessage);
        switch (message.getFrom()){
            case ChatType.PERSONAL_CHAT:
                personalChat(message);
                break;
            case ChatType.GROUP_CHAT:
                groupChat(message);
                break;
                default:
                    break;
        }
    }

    public void onPlayerLogin(LoginEvent event){
        int accountId = event.getId();
        ListOperations listOperations = redisTemplate.opsForList();
        String key = ChatRedisKey.ACCOUNT_CHAT + accountId;
        List<String> list = listOperations.range(key, 0 , -1);
        if(list != null && !list.isEmpty()){
            List<S2CChatMessage> result = new ArrayList<>();
            for(String s : list){
                result.add(JsonUtils.stringToObject(s, S2CChatMessage.class));
            }
            for(int i = 0 ; i < result.size() ; i ++){
                S2CMessage s2cMessage = S2CMessage.valueOf(Module.CHAT, ChatCmd.BROADCAST_CHAT_MSG_S2C);
                s2cMessage.setResult(ResultVO.success(result.get(i)));
                if(i == (result.size() - 1)){ //最后一次再加监听器
                   ChannelManager.sendMessageWithListener(accountId, s2cMessage, (future) -> {
                       if(future.isSuccess()){
                           listOperations.trim(key, 1, 0); //清空列表
                       }
                    });
                }else{
                    ChannelManager.sendMessage(accountId, s2cMessage);
                }
            }
        }
    }

    private void personalChat(C2SChatMessage message) throws Exception{
        logger.info("用户:" + message.getFrom() + "给用户:" + message.getSender() + "发送消息:" + message);
        int refuse = isBlack(message);
        if(refuse > 0){
            S2CMessage blackMsg = S2CMessage.valueOf(Module.CHAT, ChatCmd.CHAT_MSG_REFUSE_S2C);
            S2CRefuseMessage refuseContent = new S2CRefuseMessage();
            refuseContent.setMsgId(message.getMsgId());
            refuseContent.setReason(refuse); //默认给0，被拉黑
            blackMsg.setResult(ResultVO.success(refuseContent));
            ChannelManager.sendMessage(message.getSender(), blackMsg);
            logger.info("用户:" + message.getFrom() + "给用户:" + message.getSender() + "发送消息被拒绝，因为黑名单.");
            return;
        }
        //将消息直接转发给接收者
        S2CChatMessage.Builder build = S2CChatMessage.newBuilder();
        S2CChatMessage s2cChatMessage = build.setSuffix(message.getSuffix())
                .setFrom(message.getFrom())
                .setType(message.getType())
                .setSender(message.getSender())
                .setOnline(true) //默认在线，如果不在线，回调处再修改
                .setContent(message.getContent()).build();

        S2CMessage s2cMessage = S2CMessage.valueOf(Module.CHAT, ChatCmd.BROADCAST_CHAT_MSG_S2C);
        s2cMessage.setResult(ResultVO.success(s2cChatMessage));

        /*
        ChannelManager.sendMsgOfflineCallBack(message.getTo(), s2cMessage, (msg) -> {
            sendMessage2RecieverFail(message, msg);
        });
        */
        final S2CChatMessage msg = s2cChatMessage;
        boolean result = ChannelManager.sendMessageWithListener(message.getTo(), s2cMessage, (future)->{
            if(!future.isSuccess()){
                //保存消息到缓存
                logger.info("msg send fail.");
                sendMessage2RecieverFail(message, msg);
            }
        });
        if(!result){
            //保存消息到缓存
            sendMessage2RecieverFail(message, msg);
        }
        chatMsgResponse(message);
    }

    private void groupChat(C2SChatMessage message){

    }

    private boolean isAccountExist(int accountId){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String src = (String) valueOperations.get(Constant.REDIS_KEY_ACCOUNT + accountId);
        if(!StringUtil.isBlank(src)){
            return true;
        }
        AccountInfo info = mapper.queryById(accountId);
        if(info != null){
            valueOperations.set(Constant.REDIS_KEY_ACCOUNT + info.getId(), JsonUtils.objectToString(info));
            redisTemplate.expire(Constant.REDIS_KEY_ACCOUNT + info.getId(), Constant.REDIS_KEY_ACCOUNT_EXPIRE, TimeUnit.HOURS);
            return true;
        }
        return false;
    }

    public ResultVO getOfflineChatMsg(int accountId){
        ListOperations listOperations = redisTemplate.opsForList();
        String key = ChatRedisKey.ACCOUNT_CHAT + accountId;
        List<String> list = listOperations.range(key, 0 , -1);
        if(list != null && !list.isEmpty()){
            List<S2CChatMessage> result = new ArrayList<>();
            for(String s : list){
                result.add(JsonUtils.stringToObject(s, S2CChatMessage.class));
            }
            listOperations.trim(key, 1, 0); //清空列表
            return ResultVO.success(result);
        }
        return ResultVO.error("no offline chat message.", Constant.RNT_CODE_NO_DATA);
    }

    private int isBlack(C2SChatMessage message){
        if(message.getFrom() == ChatType.PERSONAL_CHAT){
            int account = message.getTo();
            if(friendService.isBlack(message.getSender(), account)){
                return RefuseType.BLACKED_BY_OTHER;
            }else if(friendService.isBlack(account, message.getSender())){
                return RefuseType.BLACK_OTHER;
            }else{
                return 0;
            }
        }
        return 0;
    }

    /***
     * 消息回执
     * @param message
     */
    private void chatMsgResponse(C2SChatMessage message){
        S2CMessage response = S2CMessage.valueOf(Module.CHAT, ChatCmd.CHAT_MSG_SEND_RESULT);
        S2CRefuseMessage content = new S2CRefuseMessage();
        content.setMsgId(message.getMsgId());
        response.setResult(ResultVO.success(content));
        ChannelManager.sendMessage(message.getSender(), response);
    }

    private void sendMessage2RecieverFail(C2SChatMessage message, S2CContent msg){
        //判断receive是否是合法用户
        if(!isAccountExist(message.getTo())){
            logger.info("account:" + message.getSender() + "send message, but receive:" + message.getTo() + "is not exist.");
            return;
        }
        logger.info("用户:" + message.getFrom() + "给用户:" + message.getSender() + "发送离线消息");
        S2CChatMessage chatMessage = (S2CChatMessage)msg;
        chatMessage.setOnline(false);

        //将消息缓存在redis，设置过期时间
        ListOperations listOperations = redisTemplate.opsForList();
        String key = ChatRedisKey.ACCOUNT_CHAT + message.getTo();
        listOperations.leftPush(key, JsonUtils.objectToString(chatMessage));
        redisTemplate.expire(key, ChatRedisKey.ACCOUNT_CHAT_EXPIRE, TimeUnit.DAYS);
    }
}
