package com.game.tool.service;

import com.game.account.domain.AccountInfo;
import com.game.common.vo.ResultVO;
import com.game.tool.dao.AIAccountMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ToolService {
    private static final Logger logger = LoggerFactory.getLogger(ToolService.class);
    private static Random random = new Random();
    private static String[] lowercase = {
            "a","b","c","d","e","f","g","h","i","j","k",
            "l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
    private static List<Integer> ages = Lists.newArrayList(18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35);
    private static List<String> addressx = Lists.newArrayList("Indonesia", "India", "Philippines");
    private static String defaultAvatar = "http://p0.qhimg.com/t01d4869dd2d184465c.jpg";

    @Value("${ai.random.num}")
    private int AINum;

    @Autowired
    AIAccountMapper mapper;

    public ResultVO genAIAccount() {
        List<AccountInfo> accountInfos = new ArrayList<>();

        int totalNum = AINum;

        while (totalNum > 0) {
            int saveNum = 100;
            int oneLoop = saveNum;
            if (totalNum < saveNum) {
                oneLoop = totalNum;
            }

            for (int num = 0; num < oneLoop; num ++) {
                AccountInfo accountInfo = new AccountInfo();
                accountInfo.setHeaderImg(defaultAvatar);
                accountInfo.setNickName(genNickName(6));
                accountInfo.setAge(genAge());
                accountInfo.setConstellation(genConstellation());
                accountInfo.setSex(genSex());
                accountInfo.setAddress(genAddress());
                accountInfo.setBirthday("");
                accountInfos.add(accountInfo);
            }

            mapper.BatchSaveAIAccount(accountInfos);
            totalNum -= oneLoop;
        }


        return ResultVO.success();
    }


    private String genNickName(int length) {
        StringBuilder sb = new StringBuilder();
        int max = lowercase.length;
        for (int index = 0; index < length; index ++) {
            sb.append(lowercase[random.nextInt(max)]);
        }

        return sb.toString();
    }

    private int genAge() {
        return ages.get(random.nextInt(ages.size()));
    }

    private int genConstellation() {
        return random.nextInt(12) + 1;
    }

    private int genSex() {
        return random.nextInt(2) + 1;
    }

    private String genAddress() {
        return addressx.get(random.nextInt(addressx.size()));
    }
}
