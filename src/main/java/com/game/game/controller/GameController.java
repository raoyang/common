package com.game.game.controller;

import com.game.account.component.GToken;
import com.game.common.vo.ResultVO;
import com.game.game.domain.TokenRVO;
import com.game.game.service.GameService;
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

@RestController
@RequestMapping("/api")
public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    GameService gameService;
    @Autowired
    GToken gToken;

    @PostMapping("/game/token")
    public String token(@RequestParam("p") String p,
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

        TokenRVO tokenRVO = CommonFunUtil.toReq(p, r, TokenRVO.class);
        //TokenRVO tokenRVO = new Gson().fromJson(p, TokenRVO.class);
        if (tokenRVO == null) {
            return CommonFunUtil.toRsp(ResultVO.error("parse error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        if (TextUtils.isEmpty(tokenRVO.getGameId()) || tokenRVO.getAccountId() == 0) {
            logger.debug("gameId or accountId empty");
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        ResultVO vo = gToken.checkGToken(tokenRVO, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        return CommonFunUtil.toRsp(gameService.genToken(tokenRVO), r);
    }
}
