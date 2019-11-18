package com.game.solr.controller;

import com.game.account.component.GToken;
import com.game.account.domain.SearchInput;
import com.game.common.vo.ResultVO;
import com.game.solr.service.ESService;
import com.game.solr.service.SolrService;
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
@RequestMapping("/api")
public class SolrController {

    private static final Logger logger = LoggerFactory.getLogger(SolrController.class);

    @Autowired
    private ESService service;
    @Autowired
    GToken gToken;

    @PostMapping("/accountDetail")
    public String getAccountDetail(@RequestParam("p") String p,
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
        SearchInput searchInput = CommonFunUtil.toReq(p, r, SearchInput.class);
        if(searchInput == null){
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        ResultVO vo = gToken.checkGToken(searchInput, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        return CommonFunUtil.toRsp(ResultVO.success(service.getAccountDetails(searchInput.getIndex(), searchInput.getInput())), r);
    }
}
