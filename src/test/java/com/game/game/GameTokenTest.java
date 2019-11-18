package com.game.game;

import com.game.util.CommonFunUtil;
import com.game.util.JwtUtil;
import com.game.util.StringUtil;

public class GameTokenTest {
    public static void main(String args[]) {
        String tokenJWT = "eyJhbGciOiJIUzI1NiJ9.eyJnSWQiOiIxMDExNTUzMTUzMTg0MDAwMTAxMCIsInVJZCI6ImFiZjE2YTQyOWJkZWUzNjYiLCJjdCI6MTU1ODU4NDA2MTc4Mn0.vDs5t7GqM_sS4Ds_rjx0FD7OHDx2mUbdcfo3Z2ahAg0";
        String secret = "1008d049b710daf614a4f0765fcc8ed5";
        Object gameId = JwtUtil.getKey(tokenJWT, "gId", StringUtil.String2HexString(secret));
        if (gameId == null) {
            System.out.println("gameId null");
        } else {
            System.out.println("gameId:" + (String)gameId);
        }

        Object uId = JwtUtil.getKey(tokenJWT, "uId", StringUtil.String2HexString(secret));
        if (uId == null) {
            System.out.println("uId null");
        } else {
            System.out.println("uId：" + (String)uId);
        }

        Object cTime = JwtUtil.getKey(tokenJWT, "ct", StringUtil.String2HexString(secret));
        if (cTime == null) {
            System.out.println("createTime null");
        } else {
            String createTime = CommonFunUtil.timeStamp2Date((Long)cTime);
            System.out.println("createTime：" + createTime);
        }

    }
}
