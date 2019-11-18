package com.game.solr.service;

import com.game.account.domain.AccountInfo;
import com.game.account.domain.AccountPanelInfo;
import com.game.account.domain.SearchOutput;
import com.game.logger.ExceptionLogger;
import com.game.login.service.LoginService;
import com.game.solr.constent.SolrConstent;
import com.game.solr.domain.SolrAccount;
import com.game.solr.util.SolrUtil;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
public class SolrService {

    private static final Logger logger = LoggerFactory.getLogger(SolrService.class);

    @Autowired
    private SolrClient solrClient;

    @Autowired
    private LoginService loginService;

    @Autowired
    private ExecutorService threadHttp;

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
            SolrAccount account = new SolrAccount();
            account.setNickName(input);
            try{
                return searchSolrAccount(null, account, index);
            }catch (Exception e1){
                e1.printStackTrace();
                throw e1;
            }
        }
    }

    /***
     * 添加一个索引记录
     * @param collection
     * @param account
     * @return
     * @throws Exception
     */
    public boolean addSolrAccount(String collection, SolrAccount account) throws Exception {
        try {
            String collect = collection == null ? SolrConstent.DEFAULT_COLLECTION : collection;
            solrClient.addBean(collect, account);
            solrClient.commit(collect);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    /***
     * 更新solr账户索引数据
     * @param collection
     * @param account
     * @return
     */
    public boolean updateSolrAccount(String collection, SolrAccount account) throws Exception{
        try{
            String collect = collection == null ? SolrConstent.DEFAULT_COLLECTION : collection;
            solrClient.addBean(collect, account);
            solrClient.commit(collect);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }


    /***
     * 查找solr仓库
     * @param collection
     * @param input
     * @param index
     * @return
     * @throws Exception
     */
    public SearchOutput searchSolrAccount(String collection, SolrAccount input, int index) throws Exception{
        try{
            String collect = collection == null ? SolrConstent.DEFAULT_COLLECTION : collection;
            SolrQuery params = new SolrQuery();
            params.set("q", SolrUtil.getKeywords(input));
            params.setStart(index * SolrConstent.INDEX_COUNT);
            params.setRows(SolrConstent.MAX_VIEW_COUNT); //最多查询30条
            QueryResponse queryResponse = solrClient.query(collect, params);
            SolrDocumentList results = queryResponse.getResults();
            int count = (int)results.getNumFound();
            if(count == 0){
                return null; //没有检索到数据
            }
            SearchOutput output = new SearchOutput();
            output.setIndex(index);
            output.setLength((count / SolrConstent.INDEX_COUNT) + 1);

            List<AccountPanelInfo> list = new ArrayList<>();
            for(SolrDocument doc : results){
                AccountPanelInfo panelInfo = AccountPanelInfo.valueOfSolrAccount(SolrUtil.getSolrDocument(doc));
                AccountInfo accountInfo = loginService.getAccountInfo(panelInfo.getId());
                panelInfo.setHeaderImg(accountInfo.getHeaderImg());
                list.add(panelInfo);
            }
            output.setInfos(list);
            return output;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    private void createCollection(){
    }


    /***
     * 仅供测试使用接口
     * @param input
     * @return
     */
    public boolean delete(String input){
        try{
            solrClient.deleteByQuery("user","*:*");
            solrClient.commit("user");
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public void saveAccount2Solr(AccountInfo accountInfo){
        threadHttp.execute(()->{
            try{
                SolrAccount sa = new SolrAccount();
                sa.setId(accountInfo.getId());
                sa.setAccountId(accountInfo.getId());
                sa.setHeaderImg(accountInfo.getHeaderImg() == null ? "" : accountInfo.getHeaderImg());
                sa.setNickName(accountInfo.getNickName());
                addSolrAccount(null, sa);
            }catch (Exception e){
                e.printStackTrace();
                ExceptionLogger.exceptionLogger("saveAccount2Solr error, exception is:" + e.getMessage() + ", accounInfo is:" + accountInfo);
            }
        });
    }

    public void updateAccount2Solr(AccountInfo accountInfo){
        threadHttp.execute(()->{
            try{
                SolrAccount sa = new SolrAccount();
                sa.setId(accountInfo.getId());
                sa.setAccountId(accountInfo.getId());
                sa.setHeaderImg(accountInfo.getHeaderImg() == null ? "" : accountInfo.getHeaderImg());
                sa.setNickName(accountInfo.getNickName());
                updateSolrAccount(null, sa);
            }catch (Exception e){
                e.printStackTrace();
                ExceptionLogger.exceptionLogger("updateAccount2Solr error, exception is:" + e.getMessage() + ", accounInfo is:" + accountInfo);
            }
        });
    }
}
