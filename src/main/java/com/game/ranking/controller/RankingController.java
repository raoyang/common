package com.game.ranking.controller;

import com.game.account.component.GToken;
import com.game.common.vo.ResultVO;
import com.game.ranking.domain.RankingRVO;
import com.game.ranking.service.RankingService;
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
public class RankingController {
    private static final Logger logger = LoggerFactory.getLogger(RankingController.class);
    @Autowired
    RankingService rankingService;
    @Autowired
    GToken gToken;

    @PostMapping("/ranking")
    public String ranking(@RequestParam("p") String p,
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

        RankingRVO rvo = CommonFunUtil.toReq(p, r, RankingRVO.class);

        /*Gson gson = new Gson();
        RankingRVO rvo = gson.fromJson(p, RankingRVO.class);*/

        ResultVO vo = gToken.checkGToken(rvo, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        return CommonFunUtil.toRsp(rankingService.ranking(rvo), r);
    }
}
