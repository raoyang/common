package com.game.viso;

import com.game.visohelper.domain.VisoInputVO;
import com.game.visohelper.service.VisoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class VisoTest {

    @Autowired
    private VisoService visoService;

    @Test
    public void test(){
        VisoInputVO vo = new VisoInputVO();
        vo.setAccountId(10001);
        vo.setMsg("噶发达发达沙发沙发嘎嘎噶士大夫嘎嘎");
        //visoService.leaveMessage(vo);
    }
}
