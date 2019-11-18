package com.game.solr.service;

import com.game.account.domain.AccountInfo;
import com.game.account.domain.AccountPanelInfo;
import com.game.account.domain.SearchOutput;
import com.game.chat.util.JsonUtils;
import com.game.logger.ExceptionLogger;
import com.game.login.service.LoginService;
import com.game.solr.constent.SolrConstent;
import com.game.solr.dao.ESMapper;
import com.game.solr.elasearch.ESUtil;
import com.game.solr.elasearch.Opreation;
import com.game.solr.elasearch.domain.*;
import com.game.solr.elasearch.http.HttpClientUtils;
import com.game.solr.elasearch.listener.ApplicationStartedEventListener;
import com.game.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
public class ESService {

    private static final Logger logger = LoggerFactory.getLogger(ESService.class);

    @Autowired
    private LoginService loginService;

    @Autowired
    private ExecutorService threadHttp;

    @Autowired
    private ESMapper esMapper;

    @Value("${spring.data.es.host}")
    private String host;

    @Value("${spring.data.es.load}")
    private boolean load;

    @Value("${spring.data.es.rebuild}")
    private boolean rebuild;

    private static final String index = "user";
    private static final String type = "user_info"; //es类型

    public void loadUserAndPut2ES(){
        if(!load){
            return;
        }
        if (rebuild) {
            rebuildIndex();
        }

        int start = 0;
        int limit = 500;
        List<ESAccount> list = esMapper.getSolrAccounts(start, limit);
        while(list.size() > 0){
            for(ESAccount account : list){
                insertUser2Es(account);
            }
            start += limit;
            list = esMapper.getSolrAccounts(start, limit);
        }
    }

    private SearchOutput elasticSearch(String input, int count){
        if(StringUtil.isBlank(input)){
            return null;
        }
        Search search = new Search();
        search.setInput(input);
        search.setFrom(count * SolrConstent.INDEX_COUNT);
        search.setSize(SolrConstent.INDEX_COUNT);
        String url = ESUtil.buildUrl(Opreation.SEARCH, host, index, type, null);
        String body = ESUtil.buildSearchBody(search);
        String msg = HttpClientUtils.doPost(url, body);
        logger.info("查询结果:" + msg);
        if(msg == null){
            return null;
        }
        ESResult result = JsonUtils.stringToObject(msg, ESResult.class);
        logger.info("result:" + result);
        SearchOutput output =  buildSearchResult(result, count);
        logger.info("结果:" + output);
        return output;
    }

    public void saveAccount2Solr(AccountInfo accountInfo){
        threadHttp.execute(()->{
            try{
                ESAccount sa = new ESAccount();
                sa.setAccountId(accountInfo.getId());
                sa.setNickName(accountInfo.getNickName());
                insertUser2Es(sa);
            }catch (Exception e){
                e.printStackTrace();
                ExceptionLogger.exceptionLogger("saveAccount2Solr error, exception is:" + e.getMessage() + ", accounInfo is:" + accountInfo);
            }
        });
    }

    public void updateAccount2Es(AccountInfo accountInfo) {
        threadHttp.execute(()->{
            try{
                ESAccount esAccount = new ESAccount();
                esAccount.setAccountId(accountInfo.getId());
                esAccount.setNickName(accountInfo.getNickName());
                updateUser2Es(esAccount);
            }catch (Exception e){
                e.printStackTrace();
                ExceptionLogger.exceptionLogger("updateAccount2Es error, exception is:" + e.getMessage() + ", accounInfo is:" + accountInfo);
            }
        });
    }

    private void insertUser2Es(ESAccount account){
        if(account == null){
            return;
        }
        try {
            account.cal();
            String body = ESUtil.buildPutDocBody(account);
            String url = ESUtil.buildUrl(Opreation.PUT_DOC, host, index, type, String.valueOf(account.getAccountId()));
            String msg = HttpClientUtils.doPut(url, body);
            logger.info("插入用户:" + account.getAccountId() + "返回:" + msg);
        }catch (Exception e){
            logger.error("插入异常:" + account.getAccountId());
        }
    }

    private void updateUser2Es(ESAccount account) {
        if (account == null) {
            return;
        }

        try {
            String body = ESUtil.buildUpdateDocBody(account);
            String url = ESUtil.buildUrl(Opreation.UPDATE_DOC, host, index, type, String.valueOf(account.getAccountId()));
            String msg = HttpClientUtils.doPost(url, body);
            logger.info("更新用户:" + account.getAccountId() + "返回:" + msg);
        } catch (Exception e) {
            logger.error("updateUser2Es exception:" + account.getAccountId());
        }
    }

    /***
     * 根据输入返回数据
     * @param index
     * @param input
     * @return
     */

    public SearchOutput getAccountDetails(int index, String input) throws Exception{
        try{
            int account = Integer.parseInt(input);
            SearchOutput searchOutput = new SearchOutput();
            searchOutput.setIndex(0);
            searchOutput.setLength(1);
            List<AccountPanelInfo> list = new ArrayList<>();
            list.add(AccountPanelInfo.valueOfSearch(loginService.getAccountInfo(account)));
            searchOutput.setInfos(list);
            return searchOutput;
        }catch (Exception e){
            try{
                return elasticSearch(input, index);
            }catch (Exception e1){
                e1.printStackTrace();
                throw e1;
            }
        }
    }

    private SearchOutput buildSearchResult(ESResult result, int index){
        if(result.getHits() == null){
            return null;
        }
        if(result.getHits().getHits() == null || result.getHits().getHits().size() == 0){
            return null;
        }
        int count = result.getHits().getTotal();
        SearchOutput searchOutput = new SearchOutput();
        searchOutput.setIndex(index);
        searchOutput.setLength((count / SolrConstent.INDEX_COUNT) + 1);
        List<AccountPanelInfo> list = new ArrayList<>();
        for(ESResult.Hits2 hits2 : result.getHits().getHits()){
            ESResult.Source source = hits2.get_source();
            AccountInfo accountInfo = loginService.getAccountInfo(source.getAccountId());
            AccountPanelInfo panelInfo = AccountPanelInfo.valueOf(accountInfo);
            list.add(panelInfo);
        }
        searchOutput.setInfos(list);
        return searchOutput;
    }

    private void rebuildIndex() {
        //删除旧索引
        deleteEsIndex();
        //创建索引
        createEsIndex();
        //创建新索引mapping
        createEsMapping();
    }

    private void deleteEsIndex() {
        try {
            String url = ESUtil.buildUrl(Opreation.DEL_INDEX, host, index, type, null);
            String msg = HttpClientUtils.doDelete(url, null);
            logger.info("del index:" + index + ", return:" + msg);
        }catch (Exception e){
            logger.error("del index exception:" + e.getMessage());
        }
    }

    private void createEsIndex() {
        try {
            String url = ESUtil.buildUrl(Opreation.CREATE_INDEX, host, index, type, null);
            String msg = HttpClientUtils.doPut(url, null);
            logger.info("create index:" + index + ", return:" + msg);
        }catch (Exception e){
            logger.error("create index exception:" + e.getMessage());
        }
    }

    private void createEsMapping() {
        try {
            AccountMapping accountMapping = new AccountMapping();
            String mapping = JsonUtils.objectToString(accountMapping.build());

            String url = ESUtil.buildUrl(Opreation.CREATE_MAPPING, host, index, type, null);
            String msg = HttpClientUtils.doPost(url, mapping);
            logger.info("create index mapping:" + mapping + ", return:" + msg);
        }catch (Exception e){
            logger.error("create index mapping exception:" + e.getMessage());
        }
    }
}
