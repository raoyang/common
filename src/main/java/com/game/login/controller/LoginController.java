package com.game.login.controller;

import com.game.chat.util.JsonUtils;
import com.game.common.vo.ResultVO;
import com.game.device.service.DeviceService;
import com.game.home.domain.CommParam;
import com.game.login.domain.Account;
import com.game.login.service.LoginService;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import com.game.util.SignUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
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
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    LoginService loginService;
    @Autowired
    ExecutorService threadHttp;
    @Autowired
    DeviceService deviceService;

    @PostMapping("/login")
    public String login( @RequestParam("p") String p,
                          @RequestParam("r") Long r,
                          @RequestParam("appId") String appId,
                          @RequestParam("t") String t,
                          @RequestParam("sign") String sign
    ) throws Exception {
        Map<String, String> params = Maps.newHashMap();
        params.put("t", t);
        params.put("appId", appId);
        params.put("r", String.valueOf(r));
        params.put("sign", sign);
        if (! SignUtil.validateSign(params)) {
            return CommonFunUtil.toRsp(ResultVO.error("sign check fail", Constant.RNT_CODE_SIGN_ERROR), r);
        }

        if (appId.equals("")) {
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        Account account = CommonFunUtil.toReq(p, r, Account.class);
        Gson gson = new Gson();
        logger.info("login param :" + gson.toJson(account));
        if (account == null) {
            return CommonFunUtil.toRsp(ResultVO.error("json exception", Constant.RNT_CODE_EXCEPTION), r);
        }

        //参数校验
        if(! CommonFunUtil.checkCommonParams(account) ) {
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        if (account.getUuid().equals("")) {
            logger.info("uuid is empty");
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        // async add device info
        threadHttp.execute(new Runnable() {
            @Override
            public void run() {
                deviceService.saveDeviceInfo(account);
            }
        });

        //Account account = new Gson().fromJson(p, Account.class);

        return CommonFunUtil.toRsp(loginService.login(account, appId), r);
    }

    @PostMapping("/login/visitor")
    public String visitor(@RequestParam("p") String p,
                            @RequestParam("r") Long r,
                            @RequestParam("appId") String appId,
                            @RequestParam("t") String t,
                            @RequestParam("sign") String sign
    ) throws Exception {
        Map<String, String> params = Maps.newHashMap();
        params.put("t", t);
        params.put("appId", appId);
        params.put("r", String.valueOf(r));
        params.put("sign", sign);
        if (! SignUtil.validateSign(params)) {
            return CommonFunUtil.toRsp(ResultVO.error("sign check fail", Constant.RNT_CODE_SIGN_ERROR), r);
        }

        Account account = CommonFunUtil.toReq(p, r, Account.class);
        if (account == null) {
            return CommonFunUtil.toRsp(ResultVO.error("json exception", Constant.RNT_CODE_EXCEPTION), r);
        }

        if(! CommonFunUtil.checkCommonParams(account) ) {
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }
        //CommParam commParam = JsonUtils.stringToObject(p, CommParam.class);

        // async add device info
        threadHttp.execute(()->{deviceService.saveDeviceInfo(account);});

        return CommonFunUtil.toRsp(loginService.visitorLogin(account), r);
    }
}
