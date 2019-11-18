package com.game.friend;

import com.game.friend.domain.FriendMessage;
import com.game.friend.service.FriendService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FriendTest {

    @Autowired
    private FriendService service;

    @Test
    public void test(){
        FriendMessage msg = new FriendMessage();
        msg.setAccountId(10000090);
        msg.setApplyId(10000093);
        service.friendApply(msg);
    }
}
