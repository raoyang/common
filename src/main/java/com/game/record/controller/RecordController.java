package com.game.record.controller;

import com.game.common.vo.ResultVO;
import com.game.record.domain.AppRecord;
import com.game.record.domain.Record;
import com.game.record.service.RecordService;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import com.game.util.IpUtil;
import com.game.util.SignUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/api")
public class RecordController {
    @Autowired
    RecordService recordService;
    @Autowired
    ExecutorService threadHttp;

    @PostMapping("/record")
    public String record(@RequestParam("p") String p,
                           @RequestParam("r") Long r,
                           @RequestParam("appId") String appId,
                           @RequestParam("t") String t,
                           @RequestParam("sign") String sign,
                         HttpServletRequest request
    ) throws Exception{
        Map<String, String> params = Maps.newHashMap();
        params.put("t", t);
        params.put("appId", appId);
        params.put("r", String.valueOf(r));
        params.put("sign", sign);
        if (! SignUtil.validateSign(params)) {
            return CommonFunUtil.toRsp(ResultVO.error("sign check fail", Constant.RNT_CODE_SIGN_ERROR), r);
        }

        Record record = CommonFunUtil.toReq(p, r, Record.class);

        /*Gson gson = new Gson();
        Record record = gson.fromJson(p, Record.class);*/

        threadHttp.execute(()->{
            recordService.record(record, IpUtil.getIpAddr(request));
        });

        return CommonFunUtil.toRsp(ResultVO.success(), r);
    }

    @PostMapping("/ad/record")
    public String recordAD(@RequestParam("p") String p,
                         @RequestParam("r") Long r,
                         @RequestParam("appId") String appId,
                         @RequestParam("t") String t,
                         @RequestParam("sign") String sign,
                           HttpServletRequest request
    ) throws Exception{
        Map<String, String> params = Maps.newHashMap();
        params.put("t", t);
        params.put("appId", appId);
        params.put("r", String.valueOf(r));
        params.put("sign", sign);
        if (! SignUtil.validateSign(params)) {
            return CommonFunUtil.toRsp(ResultVO.error("sign check fail", Constant.RNT_CODE_SIGN_ERROR), r);
        }

        Record record = CommonFunUtil.toReq(p, r, Record.class);

        /*Gson gson = new Gson();
        Record record = gson.fromJson(p, Record.class);*/

        threadHttp.execute(()->{
            recordService.recordADLog(record, IpUtil.getIpAddr(request));
        });

        return CommonFunUtil.toRsp(ResultVO.success(), r);
    }

    @PostMapping("/app/record")
    public String recordApp(@RequestParam("p") String p,
                           @RequestParam("r") Long r,
                           @RequestParam("appId") String appId,
                           @RequestParam("t") String t,
                           @RequestParam("sign") String sign,
                            HttpServletRequest request
    ) throws Exception{
        Map<String, String> params = Maps.newHashMap();
        params.put("t", t);
        params.put("appId", appId);
        params.put("r", String.valueOf(r));
        params.put("sign", sign);
        if (! SignUtil.validateSign(params)) {
            return CommonFunUtil.toRsp(ResultVO.error("sign check fail", Constant.RNT_CODE_SIGN_ERROR), r);
        }

        AppRecord record = CommonFunUtil.toReq(p, r, AppRecord.class);

        /*Gson gson = new Gson();
        AppRecord record = gson.fromJson(p, AppRecord.class);*/

        threadHttp.execute(()->{
            recordService.recordAppLog(record, IpUtil.getIpAddr(request));
        });

        return CommonFunUtil.toRsp(ResultVO.success(), r);
    }

    @PostMapping("/gameGreater")
    public String gameGreater(@RequestParam("p") String p,
                              @RequestParam("r") Long r,
                              @RequestParam("appId") String appId,
                              @RequestParam("t") String t,
                              @RequestParam("sign") String sign) throws Exception{

        Map<String, String> params = Maps.newHashMap();
        params.put("t", t);
        params.put("appId", appId);
        params.put("r", String.valueOf(r));
        params.put("sign", sign);
        if (! SignUtil.validateSign(params)) {
            return CommonFunUtil.toRsp(ResultVO.error("sign check fail", Constant.RNT_CODE_SIGN_ERROR), r);
        }

        return CommonFunUtil.toRsp(recordService.getRandomGameGod(), r);
    }
}
