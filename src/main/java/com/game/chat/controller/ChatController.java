package com.game.chat.controller;

import com.game.account.component.GToken;
import com.game.chat.service.ChatService;
import com.game.common.vo.ResultVO;
import com.game.login.domain.Account;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import com.game.util.SignUtil;
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
@RequestMapping("/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService service;
    @Autowired
    GToken gToken;

    @PostMapping("/offlineChat")
    public String requestOfflineChatMessage(@RequestParam("p") String p,
                                             @RequestParam("r") Long r,
                                             @RequestParam("appId") String appId,
                                             @RequestParam("t") String t,
                                             @RequestParam("sign") String sign
    )throws Exception{
        logger.debug("param p:" + p + ", r:" + r + ", t:" + t);
        Map<String, String> params = Maps.newHashMap();
        params.put("t", t);
        params.put("appId", appId);
        params.put("r", String.valueOf(r));
        params.put("sign", sign);
        if (! SignUtil.validateSign(params)) {
            return CommonFunUtil.toRsp(ResultVO.error("sign check fail", Constant.RNT_CODE_SIGN_ERROR), r);
        }
        Account account = CommonFunUtil.toReq(p, r,  Account.class);

        ResultVO vo = gToken.checkGToken(account, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        //将离线消息，返回给客户端
        return CommonFunUtil.toRsp(service.getOfflineChatMsg(account.getAccountId()), r);
    }
}
