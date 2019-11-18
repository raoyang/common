package com.game.open.controller;

import com.game.chat.util.JsonUtils;
import com.game.common.vo.ResultVO;
import com.game.login.domain.Account;
import com.game.open.domain.GameDataRVO;
import com.game.open.service.OpenService;
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
@RequestMapping("/oapi")
public class OpenController {
    private static final Logger logger = LoggerFactory.getLogger(OpenController.class);
    @Autowired
    OpenService openService;

    @PostMapping("/login")
    public String login(@RequestParam("p") String p,
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

        if (appId.equals("")) {
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }
        Account account = CommonFunUtil.toReq(p, r, Account.class);
        //Account account = new Gson().fromJson(p, Account.class);
        if (account == null) {
            return CommonFunUtil.toRsp(ResultVO.error("json exception", Constant.RNT_CODE_EXCEPTION), r);
        }

        //参数校验
        if(! CommonFunUtil.checkCommonParams(account) ) {
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        return CommonFunUtil.toRsp(openService.login(account), r);

    }

    @PostMapping("/auth/token")
    public ResultVO authToken(@RequestParam("gameId") String gameId,
                        @RequestParam("uId") int uId,
                        @RequestParam("token") String token,
                        @RequestParam("sign") String sign
    ) {

        return openService.authToken(gameId, uId, token, sign);
    }

    @PostMapping("/game/data")
    public String gameData(@RequestParam("p") String p,
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

        GameDataRVO gameDataRVO = CommonFunUtil.toReq(p, r, GameDataRVO.class);
        if (gameDataRVO == null) {
            return CommonFunUtil.toRsp(ResultVO.error("json exception", Constant.RNT_CODE_PARAM_ERROR), r);
        }
        logger.debug("game data:" + JsonUtils.objectToString(gameDataRVO));
        //GameDataRVO gameDataRVO = JsonUtils.stringToObject(p, GameDataRVO.class);

        int type = gameDataRVO.getGd().getType();
        if (type == 0) {
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        switch (type) {
            case Constant.GAME_DATA_TYPE_FACE:
                return CommonFunUtil.toRsp(openService.gameFace(), r);
            case Constant.GAME_DATA_TYPE_CONFIG:
                return CommonFunUtil.toRsp(openService.gameDataCfg(gameDataRVO), r);
            case Constant.GAME_DATA_TYPE_RANKING:
                return CommonFunUtil.toRsp(openService.gameRank(gameDataRVO), r);
            default:
                break;
        }

        return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
    }
}
