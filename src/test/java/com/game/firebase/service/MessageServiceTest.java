package com.game.firebase.service;

import com.game.firebase.base.FirebaseResult;
import com.game.firebase.domain.MessageEntity;
import com.game.firebase.domain.MultiFirebaseMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MessageServiceTest {

    @Autowired
    MessageService messageService;

    @Test
    public void toMultiRecipient() {

        MultiFirebaseMessage.Builder builder = new MultiFirebaseMessage.Builder();
        builder.isNotification(false);
        builder.recipients(10000072, 10000162);
        builder.timeToLive(3600);
        MessageEntity entity = new MessageEntity();
        entity.setNotificationType(3);
        entity.setCmd(1);
        entity.setVer(1);
        entity.setData(new MockMessageEntity());
        builder.entity(entity);
        //List<FirebaseResult> resultList = messageService.toMultiRecipient(builder.build());
    }
}