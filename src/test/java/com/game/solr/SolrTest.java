package com.game.solr;


import com.game.account.domain.SearchOutput;
import com.game.login.service.LoginService;
import com.game.solr.domain.SolrAccount;
import com.game.solr.service.ESService;
import com.game.solr.service.SolrService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SolrTest {

    @Autowired
    private ESService esService;

    @Autowired
    private LoginService loginService;

    @Test
    public void test(){
        try {
            search();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*private void add() throws Exception{
            SolrAccount sa = new SolrAccount();
            int accountId = 10000017;
            sa.setId(accountId);
            sa.setAccountId(accountId);
            sa.setHeaderImg("http://localhost:8080/com");
            sa.setNickName(loginService.getAccountInfo(accountId).getNickName());
            solrService.addSolrAccount(null, sa);
    }

    private void update() throws Exception{
        SolrAccount sa = new SolrAccount();
        sa.setId(1);
        sa.setAccountId(1000006);
        sa.setHeaderImg("http://localhost:8080/cn");
        sa.setNickName("鱼缸里的金鱼");
        solrService.updateSolrAccount(null, sa);
    }*/

    private void search() throws Exception{
        SolrAccount account = new SolrAccount();
        account.setNickName("小强");
        SearchOutput output = esService.getAccountDetails(0, "小强");
        System.out.println(output);
    }
}
