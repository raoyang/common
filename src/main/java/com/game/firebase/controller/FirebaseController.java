package com.game.firebase.controller;

import com.game.common.vo.ResultVO;
import com.game.firebase.domain.FirebaseTokenVO;
import com.game.firebase.service.FirebaseService;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class FirebaseController {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseController.class);

    @Autowired
    FirebaseService firebaseService;

    @PostMapping("/firebase/update")
    public String updateFirebaseToken(@RequestParam("p") String p,
                                      @RequestParam("r") Long r,
                                      @RequestParam("appId") String appId,
                                      @RequestParam("t") String t,
                                      @RequestParam("sign") String sign) throws Exception {

        logger.debug("the update is called");

        FirebaseTokenVO firebaseTokenVO = CommonFunUtil.toReq(p, r, FirebaseTokenVO.class);
        if (firebaseTokenVO == null) {
            return CommonFunUtil.toRsp(ResultVO.error("parse error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        if (StringUtils.isEmpty(firebaseTokenVO.getFirebaseToken()) || StringUtils.isEmpty(firebaseTokenVO.getFirebaseId())) {
            logger.warn("the token is null or the user id is empty");
            return CommonFunUtil.toRsp(ResultVO.error("parse error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        int accountId = firebaseTokenVO.getAccountId();
        String firebaseToken = firebaseTokenVO.getFirebaseToken();
        String firebaseId    = firebaseTokenVO.getFirebaseId();


        firebaseService.insertOrUpdateFirebaseTokenAsync(accountId, firebaseId, firebaseToken);

        return CommonFunUtil.toRsp(ResultVO.success(), r);
    }
}
