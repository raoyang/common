package com.game;

import com.game.util.CommonFunUtil;

public class WeekTimeTest {
    public static void main(String args[]) {
        Long nextWeekLeftSecond = CommonFunUtil.netWeekLeftTimeSecond();

        System.out.println(nextWeekLeftSecond);
    }
}
