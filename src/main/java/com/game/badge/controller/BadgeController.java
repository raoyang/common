package com.game.badge.controller;

import com.game.account.component.GToken;
import com.game.badge.service.BadgeService;
import com.game.common.vo.ResultVO;
import com.game.home.domain.CommParam;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import com.game.util.SignUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class BadgeController {
    @Autowired
    BadgeService badgeService;
    @Autowired
    GToken gToken;

    @PostMapping("/account/badge")
    public String complain(@RequestParam("p") String p,
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

        CommParam commParam = CommonFunUtil.toReq(p, r, CommParam.class);
        if(commParam == null){
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        ResultVO vo = gToken.checkGToken(commParam, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        return CommonFunUtil.toRsp(badgeService.accountBadge(commParam), r);
    }
}
