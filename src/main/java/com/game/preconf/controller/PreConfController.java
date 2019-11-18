package com.game.preconf.controller;

import com.game.common.vo.ResultVO;
import com.game.hotfix.Primain;
import com.game.preconf.service.PreConfService;
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
public class PreConfController {
    @Autowired
    PreConfService preConfService;

    @PostMapping("/gConf")
    public String gConf(@RequestParam("p") String p,
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

        if (appId.equals("")) {
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        return CommonFunUtil.toRsp(preConfService.queryConf(), r);
    }

    @PostMapping("/hotfix")
    public String hotfix(){
        try {
            Primain.process(this.getClass().getClassLoader());
            return "success";
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
