package com.game.album.controller;

import com.game.album.domain.AlbumDetailRVO;
import com.game.album.service.AlbumService;
import com.game.common.vo.DataVO;
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
public class AlbumController {

    @Autowired
    AlbumService albumService;

    @PostMapping("/album/detail")
    public String details(@RequestParam("p") String p,
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
            return CommonFunUtil.toRsp(DataVO.error("sign check fail", Constant.RNT_CODE_SIGN_ERROR), r);
        }

        AlbumDetailRVO detailRVO = CommonFunUtil.toReq(p, r, AlbumDetailRVO.class);
        //AlbumDetailRVO detailRVO = new Gson().fromJson(p, AlbumDetailRVO.class);
        if (detailRVO == null) {
            return CommonFunUtil.toRsp(DataVO.error("parse param error", Constant.RNT_CODE_EXCEPTION), r);
        }

        return CommonFunUtil.toRsp(albumService.details(detailRVO), r);
    }
}
