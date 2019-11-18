package com.game.favogame.component;

import com.game.favogame.domain.AccountGameRecord;
import com.game.record.dao.AccountGameRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FavogamePersistent {

    private static final Logger logger = LoggerFactory.getLogger(FavogamePersistent.class);

    @Autowired
    private AccountGameRecordMapper recordMapper;

    private Map<Integer, Set<AccountGameRecord>> gameCountCache = new HashMap<>();

    /** 批处理的条数 **/
    private static final int BATCH_COUNT = 1000;

    /***
     * 增加一次游戏进入的次数
     * @param record 记录
     */
    public synchronized void notifyAccountGameRecord(AccountGameRecord record){
        if(record == null){
            return;
        }
        Set<AccountGameRecord> set = gameCountCache.get(record.getAccountId());
        if(set == null){
            set = new HashSet<>();
            gameCountCache.put(record.getAccountId(), set);
        }
        set.add(record);
        StringBuilder sb = new StringBuilder();
        sb.append("accountId:")
                .append(record.getAccountId())
                .append(",")
                .append("gameId:")
                .append(record.getGameId())
                .append(" current count:")
                .append(record.getCount());
        logger.info(sb.toString());

    }

    /*@Scheduled(cron = "0 0/1 * * * ?")
    private void persistentAccountGameCount(){
        List<AccountGameRecord> cache = new ArrayList<>();
        synchronized (this){
            Map<Integer, Set<AccountGameRecord>> data = gameCountCache;
            for(Map.Entry<Integer, Set<AccountGameRecord>> entry : data.entrySet()){
                cache.addAll(entry.getValue());
            }
            gameCountCache.clear();
        }

        if(cache.isEmpty()){
            return;
        }

        if(cache.size() <= BATCH_COUNT){
            recordMapper.updateAccountGameRecord(cache);
        }else{
            int times = cache.size() / BATCH_COUNT + 1;
            for(int i = 0 ; i < times ; i ++){
                List<AccountGameRecord> temp = null;
                if(i == (times - 1)){
                    temp = cache.subList(i * BATCH_COUNT, cache.size());
                }else{
                    temp = cache.subList(i * BATCH_COUNT, (i + 1) * BATCH_COUNT);
                }
                if(!temp.isEmpty()){
                    recordMapper.updateAccountGameRecord(cache);
                }
            }
        }
    }*/
}
