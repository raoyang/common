package com.game.chat;

import com.game.chat.event.BaseEvent;
import com.game.chat.message.OriginChatMessage;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

/***
 * 消息分发接口
 * @author raoyang
 */
@Component
public class ChatDispatcher {

    private static Logger logger = LoggerFactory.getLogger(ChatDispatcher.class);

    private static EventBus eventBus = new EventBus("CHAT_CENTER");

    private static ChatDispatcher instance = null;

    @Autowired
    private ChatCenter center;

    @PostConstruct
    public void init(){
        instance = this;
        register(center);
    }

    private void register(Object obj){
        eventBus.register(obj);
    }

    public static ChatDispatcher getInstance(){
        return instance;
    }

    /***
     * 抛消息
     * @param msg
     */
    public void postMsg(OriginChatMessage msg){
        if(msg == null){
            return;
        }
        eventBus.post(msg);
    }

    public void postEvent(BaseEvent event){
        if(event != null){
            eventBus.post(event);
        }
    }
}
