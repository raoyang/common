package com.game.visohelper.controller;

import com.game.account.component.GToken;
import com.game.account.domain.SearchInput;
import com.game.common.vo.ResultVO;
import com.game.home.domain.CommParam;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import com.game.util.SignUtil;
import com.game.visohelper.domain.VisoInputVO;
import com.game.visohelper.service.VisoService;
import com.google.common.collect.Maps;
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
public class VisoController {

    private static final Logger logger = LoggerFactory.getLogger(VisoController.class);

    @Autowired
    private GToken gToken;

    @Autowired
    private VisoService service;

    @PostMapping("/visoChat")
    public String visoChat(@RequestParam("p") String p,
                                   @RequestParam("r") Long r,
                                   @RequestParam("appId") String appId,
                                   @RequestParam("t") String t,
                                   @RequestParam("sign") String sign
    ) throws Exception{
        logger.debug("param p:" + p + ", r:" + r + ", t:" + t);
        Map<String, String> params = Maps.newHashMap();
        params.put("t", t);
        params.put("appId", appId);
        params.put("r", String.valueOf(r));
        params.put("sign", sign);
        if (! SignUtil.validateSign(params)) {
            return CommonFunUtil.toRsp(ResultVO.error("sign check fail", Constant.RNT_CODE_SIGN_ERROR), r);
        }

        VisoInputVO inputVO = CommonFunUtil.toReq(p, r, VisoInputVO.class);
        if(inputVO == null){
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        ResultVO vo = gToken.checkGToken(inputVO, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        return CommonFunUtil.toRsp(service.leaveMessage(inputVO), r);
    }
}
