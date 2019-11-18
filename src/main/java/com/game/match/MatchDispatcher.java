package com.game.match;

import com.game.chat.annotation.Protocol;
import com.game.chat.util.JsonUtils;
import com.game.match.domain.CommonMessage;
import com.game.match.domain.MatchMessage;
import com.game.match.service.MatchService;
import com.game.netty.constant.MatchCmd;
import com.game.netty.message.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Component
public class MatchDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(MatchDispatcher.class);

    private static MatchDispatcher instance = null;

    private static Map<Integer, Method> methods = new HashMap<>();

    @Autowired
    private MatchService matchService;

    @PostConstruct
    private void init(){
        methods.clear();
        Class clazz = matchService.getClass();
        Method[] mds = clazz.getDeclaredMethods();
        for(Method method : mds){
            if(method.isAnnotationPresent(Protocol.class)){
                method.setAccessible(true);
                Protocol pro = method.getDeclaredAnnotation(Protocol.class);
                methods.put(pro.cmd(), method);
            }
        }

        instance = this;
    }

    public static MatchDispatcher getInstance(){
        return instance;
    }

    public void onMatch(BaseMessage message, int accountId) throws Exception{

        String content = new String(message.getBody(), Charset.forName("utf-8"));
        if(message.getCmd() == MatchCmd.COMMON_TRANSFER_PIPLINE_C2S){
            CommonMessage msg = JsonUtils.stringToObject(content, CommonMessage.class);
            Method method = methods.get((int)message.getCmd());
            method.invoke(matchService, msg, accountId);
        }else{
            MatchMessage matchMessage = JsonUtils.stringToObject(content, MatchMessage.class);
            matchMessage.setAccountId(accountId);
            Method method = methods.get((int)message.getCmd());
            method.invoke(matchService, matchMessage);
        }
    }
}
