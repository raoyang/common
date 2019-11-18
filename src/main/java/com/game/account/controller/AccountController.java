package com.game.account.controller;

import com.game.account.component.GToken;
import com.game.account.domain.*;
import com.game.account.service.AccountService;
import com.game.chat.util.JsonUtils;
import com.game.common.vo.ResultVO;
import com.game.home.domain.CommParam;
import com.game.login.domain.Account;
import com.game.login.service.LoginService;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import com.game.util.MD5;
import com.game.util.SignUtil;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    @Autowired
    AccountService accountService;
    @Autowired
    GToken gToken;

    /*
    @PostMapping("/accountAvatar")
    public String avatar(@RequestParam("p") String p,
                           @RequestParam("r") Long r,
                           @RequestParam("appId") String appId,
                           @RequestParam("t") String t,
                           @RequestParam("sign") String sign,
                         @RequestParam("file") MultipartFile file
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

        //Gson gson = new Gson();
        //Upload upload = gson.fromJson(p, Upload.class);
        Upload upload = CommonFunUtil.toReq(p, r, Upload.class);
        if (upload == null) {
            return CommonFunUtil.toRsp(ResultVO.error("AES fail", Constant.RNT_CODE_EXCEPTION), r);
        }

        if (upload.getAccountId() == 0) {
            return CommonFunUtil.toRsp(ResultVO.error("param fail", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        if (file.isEmpty()) {
            return CommonFunUtil.toRsp(ResultVO.error("file empty", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        //String fileMd5 = upload.getMd5();
        //if (fileMd5.equals("")) {
        //    logger.info("md5 empty");
        //    return CommonFunUtil.toRsp(ResultVO.error("file md5 check fail", Constant.RNT_CODE_PARAM_ERROR), r);
        //}

        //if (! fileMd5.equals(MD5.md5File(file))) {
        //    logger.info("md5 check fail, file md5:" + fileMd5);
        //   return CommonFunUtil.toRsp(ResultVO.error("file md5 check fail", Constant.RNT_CODE_PARAM_ERROR), r);
        //}

        ResultVO vo = gToken.checkGToken(upload, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        return CommonFunUtil.toRsp(accountService.avatar(file, upload.getAccountId()), r);
    }
    */

    @PostMapping("/accountUpdate")
    public String accountUpdate(@RequestParam("p") String p,
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

        /*Gson gson = new Gson();
        Account account = gson.fromJson(p, Account.class);*/
        Account account = CommonFunUtil.toReq(p, r, Account.class);
        if (account == null) {
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        if (account.getAccountId() == 0) {
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        ResultVO vo = gToken.checkGToken(account, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        return CommonFunUtil.toRsp(accountService.update(account), r);
    }

    @PostMapping("/accountInfo")
    public String getAccountInfo(@RequestParam("p") String p,
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
        AccountsInfo accounts = CommonFunUtil.toReq(p, r, AccountsInfo.class);
        if(accounts == null){
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }
        if(accounts.getAccounts() == null || accounts.getAccounts().size() == 0){
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        ResultVO vo = gToken.checkGToken(accounts, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }
        //AccountsInfo accounts = JsonUtils.stringToObject(p, AccountsInfo.class);

        return CommonFunUtil.toRsp(accountService.detailAccount(accounts), r);
    }

    /*
    //上传照片墙
    @PostMapping("/photoWall")
    public String uploadPhotoWallUrl(@RequestParam("p") String p,
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

        PhotoWallVO vo = CommonFunUtil.toReq(p, r, PhotoWallVO.class);
        if(vo == null){
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }
        //业务类更新PhotoWall
        return CommonFunUtil.toRsp(accountService.uploadPhotoWallUrl(vo), r);
    }

    @PostMapping("/reqPhotoWall")
    public String reqPhotoWallUrl(@RequestParam("p") String p,
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
        PhotoWallVO vo = CommonFunUtil.toReq(p, r, PhotoWallVO.class);
        if(vo == null){
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        return CommonFunUtil.toRsp(accountService.getPhotoWallInfos(vo.getAccountId()), r);
    }

    @PostMapping("/updatePhotoWall")
    public String updatePhotoWall(@RequestParam("p") String p,
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
        PhotoWallVO vo = CommonFunUtil.toReq(p, r, PhotoWallVO.class);
        if(vo == null){
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        return CommonFunUtil.toRsp(accountService.updatePhotoWallInfo(vo), r);
    }
    */

    @PostMapping("/account/like")
    public String like(@RequestParam("p") String p,
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

        AccountRVO rvo = CommonFunUtil.toReq(p, r, AccountRVO.class);
        if(rvo == null){
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        ResultVO vo = gToken.checkGToken(rvo, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        //AccountRVO rvo = JsonUtils.stringToObject(p, AccountRVO.class);

        return CommonFunUtil.toRsp(accountService.likeAccount(rvo), r);
    }

    @PostMapping("/account/complain")
    public String complain(@RequestParam("p") String p,
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

        AccountRVO rvo = CommonFunUtil.toReq(p, r, AccountRVO.class);
        if(rvo == null){
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        ResultVO vo = gToken.checkGToken(rvo, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        return CommonFunUtil.toRsp(accountService.complainAccount(rvo), r);
    }


    @PostMapping("/avatar/list")
    public String avatarList(@RequestParam("p") String p,
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

        AccountRVO rvo = CommonFunUtil.toReq(p, r, AccountRVO.class);
        if(rvo == null){
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        ResultVO vo = gToken.checkGToken(rvo, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        //AccountRVO rvo = JsonUtils.stringToObject(p, AccountRVO.class);

        return CommonFunUtil.toRsp(accountService.listAvatar(rvo), r);
    }
}
