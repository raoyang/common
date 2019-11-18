package com.game.photowall;

import com.game.account.domain.PhotoWallVO;
import com.game.account.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PhotoWall {

    @Autowired
    private AccountService accountService;

    @Test
    public void test(){
        update();
    }

    private void upload(){
        PhotoWallVO vo = new PhotoWallVO();
        vo.setAccountId(10000010);
        List<String> list = new ArrayList<>();
        for(int i = 0 ; i < 4 ; i ++){
            list.add("http://www.12315.com" + i);
        }
        vo.setUrl(list);
        accountService.uploadPhotoWallUrl(vo);
    }

    private void get(){
        PhotoWallVO vo = new PhotoWallVO();
        vo.setAccountId(10000010);
        accountService.getPhotoWallInfos(10000010);
    }

    private void update(){
        PhotoWallVO vo = new PhotoWallVO();
        vo.setAccountId(10000010);
        List<String> list = new ArrayList<>();
        for(int i = 0 ; i < 20 ; i ++){
            list.add("http://www.12315.com" + i);
        }
        vo.setUrl(list);
        accountService.updatePhotoWallInfo(vo);
    }
}
