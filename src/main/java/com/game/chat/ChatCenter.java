package com.game.chat;

import com.game.account.dao.AccountMapper;
import com.game.chat.annotation.Protocol;
import com.game.chat.event.BaseEvent;
import com.game.chat.event.impl.LoginEvent;
import com.game.chat.message.OriginChatMessage;
import com.game.chat.service.ChatService;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import sun.rmi.runtime.Log;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/***
 * 消息处理中心
 * @author raoyang
 */
@Component
public class ChatCenter {

    private static final Logger logger = LoggerFactory.getLogger(ChatCenter.class);

    @Autowired
    private ChatService chatService;

    private static final Map<Integer, Method> methods = new HashMap<>();

    @PostConstruct
    private void init(){
        Class clazz = chatService.getClass();
        Method[] mds = clazz.getDeclaredMethods();
        for(Method method : mds){
            if(method.isAnnotationPresent(Protocol.class)){
                method.setAccessible(true);
                Protocol pro = method.getDeclaredAnnotation(Protocol.class);
                methods.put(pro.cmd(), method);
            }
        }
    }

    @Subscribe
    public void onChatMessage(OriginChatMessage chat){
        Method method = methods.get(chat.getCmd());
        if(method != null){
            try{
                method.invoke(chatService, chat);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            logger.error("client send not exist command, module:" + chat.getModuleId() + ", cmd:" + chat.getCmd());
        }
    }

    @Subscribe
    public void onEvent(BaseEvent event){
        if(event instanceof LoginEvent){
            LoginEvent loginEvent = (LoginEvent)event;
            chatService.onPlayerLogin(loginEvent);
        }
    }
}
