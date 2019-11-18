package com.game.home.controller;

import com.game.account.service.AccountService;
import com.game.common.vo.DataVO;
import com.game.home.domain.Home;
import com.game.home.domain.HomePageVO;
import com.game.home.service.HomeService;
import com.game.login.domain.Account;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import com.game.util.SignUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/api")
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    HomeService homeService;
    @Autowired
    ExecutorService threadHttp;
    @Autowired
    AccountService accountService;

    @PostMapping("/home")
    public String home(@RequestParam("p") String p,
                       @RequestParam("r") Long r,
                       @RequestParam("appId") String appId,
                       @RequestParam("t") String t,
                       @RequestParam("sign") String sign
    ) throws Exception{
        Map<String, String> params = Maps.newHashMap();
        params.put("t", t);
        params.put("appId", appId);
        params.put("r", String.valueOf(r));
        params.put("sign", sign);
        if (! SignUtil.validateSign(params)) {
            return CommonFunUtil.toRsp(DataVO.error("sign check fail", Constant.RNT_CODE_SIGN_ERROR), r);
        }

        HomePageVO pageVO = CommonFunUtil.toReq(p, r, HomePageVO.class);

        // 参数校验
        if (!CommonFunUtil.checkCommonParams(pageVO)) {
            logger.info("home params check fail");
            return CommonFunUtil.toRsp(DataVO.error("param check fail", Constant.RNT_CODE_PARAM_ERROR), r);
        }

       //HomePageVO pageVO = new Gson().fromJson(p, HomePageVO.class);

        threadHttp.execute(new Runnable() {
            @Override
            public void run() {
                Account account = new Account();
                account.setAccountId(pageVO.getAccountId());
                account.setAge(-1);
                account.setLongitude(pageVO.getLongitude());
                account.setLatitude(pageVO.getLatitude());
                account.setUuid(pageVO.getUuid());
                accountService.updateDeviceInfo(account);

                if (TextUtils.isEmpty(pageVO.getLatitude()) || TextUtils.isEmpty(pageVO.getLongitude())) {
                    return;
                }
                if (pageVO.getLatitude().equals("0") && pageVO.getLongitude().equals("0")) {
                    return;
                }

                accountService.updateLatAndLon(account);
            }
        });

        return CommonFunUtil.toRsp(homeService.home(pageVO), r);
    }
}
