package com.game.favogame.controller;

import com.game.account.component.GToken;
import com.game.common.vo.ResultVO;
import com.game.favogame.domain.FavoGameRVO;
import com.game.favogame.service.FavoGameService;
import com.game.home.domain.CommParam;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import com.game.util.SignUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class FavoGameController {
    @Autowired
    FavoGameService favoGameService;
    @Autowired
    GToken gToken;

    @PostMapping("/favoriteGame")
    public String favorite(@RequestParam("p") String p,
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

        /*Gson gson = new Gson();
        FavoGameRVO favoGameRVO = gson.fromJson(p, FavoGameRVO.class);*/

        FavoGameRVO favoGameRVO = CommonFunUtil.toReq(p, r, FavoGameRVO.class);
        if (favoGameRVO == null) {
            return CommonFunUtil.toRsp(ResultVO.error("parse param error", Constant.RNT_CODE_EXCEPTION), r);
        }

        int accountId = favoGameRVO.getAccountId();
        if (accountId == 0) {
            return CommonFunUtil.toRsp(ResultVO.error("accountId is empty", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        boolean isThird = false;
        int taId = favoGameRVO.getTaId();
        if (taId != 0) {
            accountId = taId;
            isThird = true;
        }

        ResultVO vo = gToken.checkGToken(favoGameRVO, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        return CommonFunUtil.toRsp(favoGameService.favorite(accountId, isThird, favoGameRVO), r);
    }
}
