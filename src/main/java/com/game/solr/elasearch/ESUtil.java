package com.game.solr.elasearch;

import com.game.chat.util.JsonUtils;
import com.game.solr.domain.SolrAccount;
import com.game.solr.elasearch.domain.ESAccount;
import com.game.solr.elasearch.domain.ESUpdateVO;
import com.game.solr.elasearch.domain.Search;
import com.game.util.StringUtil;

public class ESUtil {

    /***
     * 构建url
     * @param opreation
     * @param url
     * @param type
     * @param id
     * @return
     */
    public static String buildUrl(Opreation opreation, String url, String index, String type, String id){
        StringBuilder sb = new StringBuilder();
        if(StringUtil.isBlank(url)){
            throw new NullPointerException("url为空.");
        }
        switch (opreation){
            case CREATE_INDEX:
                if(StringUtil.isBlank(index)){
                    throw new NullPointerException("创建索引，但是索引为空.");
                }
                sb.append(url)
                        .append(index);
                break;
            case DEL_INDEX:
                if(StringUtil.isBlank(index)){
                    throw new NullPointerException("删除索引，但是索引为空.");
                }
                sb.append(url)
                        .append(index);
                break;
            case CREATE_MAPPING:
                sb.append(url)
                        .append(index)
                        .append("/")
                        .append(type)
                        .append("/_mapping");
                break;
            case PUT_DOC:
                if(StringUtil.isBlank(index) || StringUtil.isBlank(type) || StringUtil.isBlank(id)){
                    throw new NullPointerException("添加数据到es，但是参数为空");
                }
                sb.append(url)
                        .append(index)
                        .append("/")
                        .append(type)
                        .append("/")
                        .append(id);
                break;
            case UPDATE_DOC:
                if(StringUtil.isBlank(index) || StringUtil.isBlank(type) || StringUtil.isBlank(id)){
                    throw new NullPointerException("更新数据到es，但是参数为空");
                }
                sb.append(url)
                        .append(index)
                        .append("/")
                        .append(type)
                        .append("/")
                        .append(id)
                        .append("/_update");
                break;
            case SEARCH:
                sb.append(url)
                        .append(index)
                        .append("/")
                        .append(type)
                        .append("/_search");
                break;
            case DEL_DOC:
                sb.append(url)
                        .append(index)
                        .append("/")
                        .append(type)
                        .append("/")
                        .append(id);
                break;
            default:
                break;
        }

        return sb.toString();
    }

    public static String buildCreateIndexBody(String type){
        if(type == null){
            throw new NullPointerException("创建索引，但是类型为空.");
        }
        return "{\n" +
                "    \"mappings\": {\n" +
                "        \""+type+"\": {\"accountId\":\"string\", \"nickName\":\"string\", \"headerImg\":\"string\"},\n" +
                "    }\n" +
                "}";
    }

    public static String buildPutDocBody(ESAccount account){
        if(account == null){
            throw new NullPointerException("account 为空.");
        }

        return JsonUtils.objectToString(account);
    }

    public static String buildUpdateDocBody(ESAccount account) {
        if(account == null){
            throw new NullPointerException("account 为空.");
        }

        ESUpdateVO updateVO = new ESUpdateVO();
        updateVO.setNickName(account.getNickName());

        return JsonUtils.objectToString(updateVO);
    }

    public static String buildSearchBody(Search search){
        if(search == null){
            throw new NullPointerException("搜寻body为空.");
        }
        return JsonUtils.objectToString(search);
    }

    public static String buildDelDocBody(){
        return "";
    }

}
