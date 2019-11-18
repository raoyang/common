package com.game.firebase.service;


import com.game.firebase.base.FirebaseHttpSender;
import com.game.firebase.base.FirebaseResult;
import com.game.firebase.base.Message;
import com.game.firebase.domain.FirebaseMessage;
import com.game.firebase.domain.MultiFirebaseMessage;
import com.game.util.CollectionUtil;
import com.game.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;


@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    FirebaseHttpSender firebaseHttpSender;

    @Autowired
    FirebaseService firebaseService;


    @Resource(name = "threadHttp")
    ExecutorService executorService;

    public void toSingleUserAsync(FirebaseMessage firebaseMessage) {
        executorService.execute(()->toSingleUser(firebaseMessage));
    }

    public int toSingleUser(FirebaseMessage fireBaseMessage) {

        String firebaseToken = queryFirebaseTokenWithId(fireBaseMessage.getTargetUserId());
        if (!StringUtils.isEmpty(firebaseToken)) {
            Message message = new Message();
            message.to = firebaseToken;
            message.timeToLive = fireBaseMessage.getTimeToLive();
            message.data = fireBaseMessage.getMessageEntity();

            FirebaseResult firebaseResult = firebaseHttpSender.send(message);
            if (!CollectionUtil.isEmpty(firebaseResult.getErrorMessage())) {
                logger.warn("there is something wrong from result");
                firebaseResult.getErrorMessage().forEach((s, result)-> logger.warn(result.toString()));
            }

            return 1;
        } else {
            logger.warn("the firebase token is not found for user id " + fireBaseMessage.getTargetUserId());
            return 0;
        }
    }


    public List<FirebaseResult> toMultiRecipient(MultiFirebaseMessage fireBaseMessage) {
        List<String> recipients = fireBaseMessage.getRecipientIds();
        if (CollectionUtil.isEmpty(recipients)) {
            logger.error("the recipients is empty, please check the input param");
            return null;
        }

        List<FirebaseResult> firebaseResultList = new ArrayList<>();
        FirebaseResult result = null;

        Message message = new Message();
        message.timeToLive = fireBaseMessage.getTimeToLive();
        message.data = fireBaseMessage.getMessageEntity();

        int size = recipients.size();
        int beginIdx = 0;
        while ((beginIdx + 1000) < size) {
            message.recipients = queryFirebaseTokens(recipients.subList(beginIdx, beginIdx + 1000));
            beginIdx += 1000;

            result = firebaseHttpSender.send(message);
            if (result != null) {
                firebaseResultList.add(result);
            }
        }

        if (beginIdx < size){
            message.recipients = queryFirebaseTokens(recipients.subList(beginIdx, size));
            result = firebaseHttpSender.send(message);
            if (result != null) {
                firebaseResultList.add(result);
            }
        }

        return firebaseResultList;
    }


    /**
     * TODO
     * @param recipientIds
     * @return
     */
    List<String> queryFirebaseTokens(List<String> recipientIds) {
        if(!CollectionUtil.isEmpty(recipientIds)) {
            return firebaseService.findTokens(recipientIds);
        }
        return new LinkedList<>();
    }

    String queryFirebaseTokenWithId(String userId) {
        if (StringUtil.isInteger(userId)) {
            return firebaseService.findToken(Integer.parseInt(userId));
        } else {
            return "";
        }
    }
}
