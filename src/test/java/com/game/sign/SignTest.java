package com.game.sign;

import com.game.util.Constant;
import com.game.util.SignUtil;
import com.game.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignTest {
    public static void main(String args[]) {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJnSWQiOiIxMDExNTUzMTUzMTg0MDAwMTAxMCIsInVJZCI6MTAwMDAwMTcsImN0IjoxNTU4NjgzMjM5NTEzfQ.SMTUSbRFeVqSY7GjD_JWPUeThRRgSmw2bW7GrLviqF0";
        String uId = "10000017";
        String gameId = "10115531531840001010";

        try {
            Map<String, String> params = Maps.newHashMap();
            params.put("token", token);
            params.put("gameId", gameId);
            params.put("uId", uId);

            String sign = SignUtil.buildSign(params, "70ffc50ec2588e8dcec04d4ee5eb894c");
            System.out.println("sign :" + sign);
        } catch (Exception e) {

        }


    }
}
