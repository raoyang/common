package com.game.solr.util;

import com.game.solr.domain.SolrAccount;
import com.game.solr.domain.SolrBaseDomain;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolrUtil {

    private static final Logger logger = LoggerFactory.getLogger(SolrUtil.class);

    private static Map<String, Field> solrAccountFields = new HashMap<>();

    static {
        List<Field> allFields = new ArrayList<>();
        Class clazz = SolrAccount.class;
        while (clazz != null){
            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields){
                allFields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        for(Field field : allFields){
            field.setAccessible(true);
            solrAccountFields.put(field.getName(), field);
        }
    }

    /***
     * 根据查询到的数据，返回一个用户信息
     * @param doc
     * @return
     */
    public static SolrAccount getSolrDocument(SolrDocument doc) throws Exception{

        if(doc == null){
            throw new NullPointerException("doc can't be null.");
        }
        SolrAccount account = new SolrAccount();
        for(Map.Entry<String, Field> entry : solrAccountFields.entrySet()){
            String name = entry.getKey();
            Field field = entry.getValue();
            Object value = doc.get(name);
            if(value != null && value instanceof ArrayList && !(field.getType() == ArrayList.class)){
                ArrayList listValue = (ArrayList)value;
                Object realValue = listValue.get(0);
                if(realValue instanceof Long){
                    Long longValue = (Long)realValue;
                    if(field.getType() == int.class){
                        field.set(account, longValue.intValue());
                    }else if(field.getType() == short.class){
                        field.set(account, longValue.shortValue());
                    }else if(field.getType() == byte.class){
                        field.set(account, longValue.byteValue());
                    }else{
                        field.set(account, realValue);
                    }
                }else{
                    field.set(account, realValue);
                }
            }else{
                if(value != null){
                    if(value instanceof String){
                        String strValue = (String)value;
                        field.set(account, Integer.parseInt(strValue)); //只有id字段读回来是字符串
                    }else if(value instanceof Long){
                        field.set(account, value);
                    }
                }
            }
        }

        return account;
    }

    /***
     * 获取查询关键字
     * @param t
     * @param <T>
     * @return
     */
    public static <T extends SolrBaseDomain> String getKeywords(T t){
        return t.getKeywords();
    }
}
