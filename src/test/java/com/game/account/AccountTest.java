package com.game.account;

import com.game.account.domain.AccountInfo;
import com.game.account.domain.AccountsInfo;
import com.game.account.service.AccountService;
import com.game.common.vo.ResultVO;
import com.game.login.domain.Account;
import com.game.login.domain.LoginVO;
import com.game.login.service.LoginService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AccountTest {

    @Autowired
    AccountService service;

    @Autowired
    LoginService loginService;

    @Autowired
    private AccountService accountService;

    @Test
    public void test(){
        Account account = new Account();
        account.setAccountId(10000005); //10000002
        account.setUuid("1");
        //accountService.updateLatAndLon(account);
    }

    private void req(){
        AccountsInfo req = new AccountsInfo();
        List<Integer> list = new ArrayList<>();
        list.add(10000001);
        req.setAccounts(list);
        ResultVO vo = service.detailAccount(req);
        System.out.println(vo);
    }

    private void update(){
        Account account = new Account();
        account.setAccountId(10000001);
        account.setOpenId("2255333284714328");
        account.setPlatform(1);
        account.setHeaderImg("http://www.headImage.com.cn");
        account.setNickName("张三丰");
        account.setAddress("这是详细地址");
        List<String> list = new ArrayList<>();
        list.add("http://www.photo1.com.cn");
        list.add("http://www.photo2.com.cn");
        account.setPhotoWall(list);
        service.update(account);
    }
}
