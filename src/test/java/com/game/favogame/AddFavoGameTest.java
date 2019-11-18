package com.game.favogame;

import com.game.home.domain.CommParam;
import com.game.record.domain.Record;
import com.game.util.HttpUtil;
import com.google.gson.Gson;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class AddFavoGameTest {

    public static void main(String args[]){
        testFavogame();
    }

    private static void testFavogame(){
        //byte[] msg = HttpUtil.getInstance().sendHttpPost("http://10.78.48.138:8002/api/hotfix");
        byte[] msg = HttpUtil.getInstance().sendHttpPost("http://10.186.130.47:8011/api/hotfix");
        System.out.println(new String(msg, Charset.forName("utf-8")));
    }

    private static void testPost(){

        Record record = new Record();
        record.setEvent(1);
        record.setGameId("993");
        record.setAccountId(1);
        Gson gson = new Gson();
        String p = gson.toJson(record);
        Map<String, String> map = new HashMap<>();
        map.put("p", p);
        map.put("r", "100");
        map.put("appId", "appId");
        map.put("t", "t");
        map.put("sign", "sign");
        HttpUtil.getInstance().sendHttpPost("http://localhost:8002/api/record", map);
    }
}
