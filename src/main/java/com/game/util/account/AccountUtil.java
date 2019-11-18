package com.game.util.account;

import com.game.util.pic.MD5Util;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class AccountUtil {
    private static Random random = new Random();
    private static List<Integer> ages = Lists.newArrayList(18, 19, 20, 21, 22, 23, 24, 25);
    private static List<Integer> sexs = Lists.newArrayList(1, 1, 1, 2, 1, 1);

    public static String genNickName() {
        int fixNum = random.nextInt(9000) + 1000;
        Long t = new Date().getTime();
        String rMd5 = MD5Util.getMD5String("game:" + fixNum + t);

        return "guest" + rMd5.substring(16, 24);
    }

    public static int genSex() {
        return sexs.get(random.nextInt(sexs.size()));
    }

    public static int genAge() {
        return ages.get(random.nextInt(ages.size()));
    }

    public static int genConstellation() {
        return random.nextInt(12) + 1;
    }

}
