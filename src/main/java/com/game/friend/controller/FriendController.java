package com.game.friend.controller;

import com.game.account.component.GToken;
import com.game.chat.util.JsonUtils;
import com.game.common.vo.DataVO;
import com.game.common.vo.ResultVO;
import com.game.friend.domain.AroundPlayerRVO;
import com.game.friend.domain.FriendMessage;
import com.game.friend.service.FriendService;
import com.game.login.domain.Account;
import com.game.schedule.service.AroundPlayerService;
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
import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/api")
public class FriendController {

    private static final Logger logger = LoggerFactory.getLogger(FriendController.class);

    @Autowired
    FriendService friendService;
    @Autowired
    GToken gToken;
    @Autowired
    ExecutorService threadHttp;
    @Autowired
    AroundPlayerService aroundPlayerService;

    @PostMapping("/friendRecommend")
    public String friendRecommend(
            @RequestParam("p") String p,
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
        /*Gson gson = new Gson();
        Account account = gson.fromJson(p, Account.class);*/
        if (account == null) {
            return CommonFunUtil.toRsp(ResultVO.error("json exception", Constant.RNT_CODE_EXCEPTION), r);
        }

        //参数校验
        if(! CommonFunUtil.checkCommonParams(account) ) {
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        if (TextUtils.isEmpty(account.getLatitude()) || TextUtils.isEmpty(account.getLongitude())) {
            return CommonFunUtil.toRsp(ResultVO.error("no data", Constant.RNT_CODE_NO_DATA), r);
        }

        ResultVO vo = gToken.checkGToken(account, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        //即时刷新附近的人
        if (account.getFlush() == 1) {
            threadHttp.execute(new Runnable() {
                @Override
                public void run() {
                    if (account.getAccountId() != 0) {
                        aroundPlayerService.dealAroundPlayer(account.getAccountId(), account);
                    }
                }
            });
        }

        return CommonFunUtil.toRsp(friendService.friendRecommend(account), r);
    }

    @PostMapping("/aroundPlayer")
    public String aroundPlayer(
            @RequestParam("p") String p,
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

        AroundPlayerRVO playerRVO = CommonFunUtil.toReq(p, r, AroundPlayerRVO.class);
        /*Gson gson = new Gson();
        AroundPlayerRVO playerRVO = gson.fromJson(p, AroundPlayerRVO.class);*/
        if (playerRVO == null) {
            return CommonFunUtil.toRsp(ResultVO.error("json exception", Constant.RNT_CODE_EXCEPTION), r);
        }

        //参数校验
        if(! CommonFunUtil.checkCommonParams(playerRVO) ) {
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        if (TextUtils.isEmpty(playerRVO.getLatitude()) || TextUtils.isEmpty(playerRVO.getLongitude())) {
            return CommonFunUtil.toRsp(ResultVO.error("no data", Constant.RNT_CODE_NO_DATA), r);
        }

        ResultVO vo = gToken.checkGToken(playerRVO, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        //即时刷新附近的人
        if (playerRVO.getFlush() == 1) {
            threadHttp.execute(new Runnable() {
                @Override
                public void run() {
                    if (playerRVO.getAccountId() != 0) {
                        aroundPlayerService.dealAroundPlayer(playerRVO.getAccountId(), playerRVO);
                    }
                }
            });
        }

        return CommonFunUtil.toRsp(friendService.aroundPlayer(playerRVO), r);
    }

    @PostMapping("/friendFB")
    public String friendFacebook(
            @RequestParam("p") String p,
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

        Account account = CommonFunUtil.toReq(p, r, Account.class);
        /*Gson gson = new Gson();
        Account account = gson.fromJson(p, Account.class);*/
        if (account == null) {
            return CommonFunUtil.toRsp(ResultVO.error("json exception", Constant.RNT_CODE_EXCEPTION), r);
        }

        //参数校验
        if(! CommonFunUtil.checkCommonParams(account) ) {
            return CommonFunUtil.toRsp(ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR), r);
        }

        ResultVO vo = gToken.checkGToken(account, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        return CommonFunUtil.toRsp(friendService.friendFacebook(account), r);
    }

    @PostMapping("/addFriend")
    public String addFriend(
            @RequestParam("p") String p,
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
        FriendMessage msg = CommonFunUtil.toReq(p, r, FriendMessage.class);

        ResultVO vo = gToken.checkGToken(msg, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        return CommonFunUtil.toRsp(friendService.friendApply(msg), r);
    }

    @PostMapping("/consentFriendApply")
    public String agreeFriendApply(
            @RequestParam("p") String p,
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
        FriendMessage msg = CommonFunUtil.toReq(p, r, FriendMessage.class);

        ResultVO vo = gToken.checkGToken(msg, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        return CommonFunUtil.toRsp(friendService.agreeFriendApply(msg), r);
    }

    @PostMapping("/removeFriend")
    public String rmFriend(
            @RequestParam("p") String p,
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
        FriendMessage msg = CommonFunUtil.toReq(p, r, FriendMessage.class);

        ResultVO vo = gToken.checkGToken(msg, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        return CommonFunUtil.toRsp(friendService.rmFriend(msg), r);
    }

    @PostMapping("/blacked")
    public String blackedPlayer(
            @RequestParam("p") String p,
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
        FriendMessage msg = CommonFunUtil.toReq(p, r, FriendMessage.class);

        ResultVO vo = gToken.checkGToken(msg, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }
        //FriendMessage msg = JsonUtils.stringToObject(p, FriendMessage.class);

        return CommonFunUtil.toRsp(friendService.addBlackList(msg), r);
    }

    @PostMapping("/friends")
    public String friendList(
            @RequestParam("p") String p,
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
        FriendMessage msg = CommonFunUtil.toReq(p, r, FriendMessage.class);

        ResultVO vo = gToken.checkGToken(msg, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        return CommonFunUtil.toRsp(ResultVO.success(friendService.getFriendInfos(friendService.getFriendList(msg.getAccountId()))), r);
    }

    @PostMapping("/blacks")
    public String blackList(
            @RequestParam("p") String p,
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
        FriendMessage msg = CommonFunUtil.toReq(p, r, FriendMessage.class);

        ResultVO vo = gToken.checkGToken(msg, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }
        //FriendMessage msg = JsonUtils.stringToObject(p, FriendMessage.class);

        return CommonFunUtil.toRsp(ResultVO.success(friendService.getBlackInfos(friendService.getBlackList(msg.getAccountId()))), r);
    }

    @PostMapping("/applyList")
    public String applyList(
            @RequestParam("p") String p,
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
        FriendMessage msg = CommonFunUtil.toReq(p, r, FriendMessage.class);

        ResultVO vo = gToken.checkGToken(msg, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        return CommonFunUtil.toRsp(ResultVO.success(friendService.getApplyList(msg.getAccountId())), r);
    }

    @PostMapping("/removeBlack")
    public String deleteBlack(
            @RequestParam("p") String p,
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
        FriendMessage msg = CommonFunUtil.toReq(p, r, FriendMessage.class);

        ResultVO vo = gToken.checkGToken(msg, appId);
        if (vo.getCode() != Constant.RNT_CODE_SUCCESS) {
            return CommonFunUtil.toRsp(vo, r);
        }

        return CommonFunUtil.toRsp(friendService.deleteBlack(msg), r);
    }
}
