package com.game.record;

import com.game.common.vo.ResultVO;
import com.game.record.service.RecordService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RecordTest {

    @Autowired
    private RecordService recordService;

    @Test
    public void test(){
        ResultVO vo = recordService.getRandomGameGod();
        System.out.println(vo);
    }
}
