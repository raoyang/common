package com.game.banner.controller;

import com.game.banner.service.BannerService;
import com.game.common.vo.ResultVO;
import com.game.home.domain.CommParam;
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

@RestController
@RequestMapping("/api")
public class BannerController {
    private static final Logger logger = LoggerFactory.getLogger(BannerController.class);
    @Autowired
    BannerService bannerService;

    @PostMapping("/banner")
    public String banner(@RequestParam("p") String p,
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
            return CommonFunUtil.toRsp(ResultVO.error("sign check fail", Constant.RNT_CODE_SIGN_ERROR), r);
        }

        CommParam commParam = CommonFunUtil.toReq(p, r, CommParam.class);
        //CommParam commParam = new Gson().fromJson(p, CommParam.class);
        if (commParam == null) {
            logger.debug("parse param error");
            return CommonFunUtil.toRsp(ResultVO.error("parse param error", Constant.RNT_CODE_EXCEPTION), r);
        }

        return CommonFunUtil.toRsp(bannerService.queryBanner(commParam), r);
    }
}
