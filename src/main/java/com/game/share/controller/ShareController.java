package com.game.share.controller;

import com.game.chat.util.JsonUtils;
import com.game.common.vo.ResultVO;
import com.game.share.domain.ShareRVO;
import com.game.share.service.ShareService;
import com.game.util.CommonFunUtil;
import com.game.util.Constant;
import com.game.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;

@RestController
@RequestMapping("/api")
public class ShareController {
    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);
    private static final Logger shareLog = LoggerFactory.getLogger("share");

    @Autowired
    ShareService shareService;
    @Value("${share.home.url}")
    String shareHomeUrl;
    @Value("${share.score.url}")
    String shareScoreUrl;
    @Value("${share.score.prefect.url}")
    String sharePrefectUrl;

    @GetMapping("/share/homePage")
    public void homePage(@RequestParam("p") String p,
                         @RequestParam("r") Long r,
                         @RequestParam("appId") String appId,
                         HttpServletResponse response
    ) throws Exception{
        logger.debug("home page param: p=" + p);
        String pE = URLEncoder.encode(p, "UTF-8");
        ShareRVO rvo = CommonFunUtil.toReq(pE, r, ShareRVO.class);
        if (rvo == null) {
            logger.warn("share:homepage, parse param p error");
            return;
        }

        if (! appId.equals(Constant.APP_TYPE_GAME_PLATFORM)) {
            logger.warn("share:homepage, appId error");
            return;
        }

        logger.info("to share home page, data:" + JsonUtils.objectToString(rvo));

        String redirectUrl = shareHomeUrl + "?p=" + p + "&r=" + r;
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/share/score")
    public void scorePage(@RequestParam("p") String p,
                         @RequestParam("r") Long r,
                         HttpServletResponse response
    ) throws Exception{
        logger.debug("share score param: p=" + p);
        String pE = URLEncoder.encode(p, "UTF-8");
        ShareRVO rvo = CommonFunUtil.toReq(pE, r, ShareRVO.class);
        if (rvo == null) {
            logger.warn("share:scorepage, parse param p error");
            return;
        }

        String redirectUrl = "";
        if (rvo.getType() == 0) {
            redirectUrl = shareScoreUrl;
        } else {
            redirectUrl = sharePrefectUrl;
        }
        if (redirectUrl.isEmpty()) {
            logger.error("share:get score data, parse param type error");
            return;
        }

        redirectUrl += "?p=" + p + "&r=" + r;
        response.sendRedirect(redirectUrl);
    }

    @PostMapping("/share/profile")
    public ResultVO homePageData(@RequestParam("p") String p,
                                 @RequestParam("r") Long r,
                                 HttpServletRequest request
    ) throws Exception{
        String pE = p.replace(' ', '+');
        pE = URLEncoder.encode(pE, "UTF-8");
        ShareRVO rvo = CommonFunUtil.toReq(pE, r, ShareRVO.class);
        if (rvo == null) {
            logger.warn("share:get home page data, parse param p error");
            return ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR);
        }
        //ShareRVO rvo = JsonUtils.stringToObject(p, ShareRVO.class);
        //查看个人主页打点
        String ip = IpUtil.getIpAddr(request);
        StringBuilder sb = new StringBuilder();
        sb.append(ip).append("|")
                .append("profile|")
                .append(JsonUtils.objectToString(rvo));
        String msg = sb.toString();
        //shareLog.info(msg);

        return shareService.homePageData(rvo);
    }


    @PostMapping("/share/sd")
    public ResultVO scoreData(@RequestParam("p") String p,
                                 @RequestParam("r") Long r,
                              HttpServletRequest request
    ) throws Exception{
        String pE = p.replace(' ', '+');
        pE = URLEncoder.encode(pE, "UTF-8");
        ShareRVO rvo = CommonFunUtil.toReq(pE, r, ShareRVO.class);
        if (rvo == null) {
            logger.warn("share: get score data, parse param p error");
            return ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR);
        }

        //查看玩家战绩打点
        String ip = IpUtil.getIpAddr(request);
        StringBuilder sb = new StringBuilder();
        sb.append(ip).append("|")
                .append("score|")
                .append(JsonUtils.objectToString(rvo));
        String msg = sb.toString();
        //shareLog.info(msg);

        return shareService.scoreData(rvo);
    }

    @PostMapping("/share/record")
    public ResultVO record(@RequestParam("p") String p,
                              @RequestParam("r") Long r,
                              @RequestParam("type") int type,
                              @RequestParam("uri") String uri,
                              HttpServletRequest request
    ) throws Exception{
        String pE = p.replace(' ', '+');
        pE = URLEncoder.encode(pE, "UTF-8");
        ShareRVO rvo = CommonFunUtil.toReq(pE, r, ShareRVO.class);
        if (rvo == null) {
            logger.error("parse param p error");
            return ResultVO.error("param error", Constant.RNT_CODE_PARAM_ERROR);
        }

        //点击页面打点
        String ip = IpUtil.getIpAddr(request);
        StringBuilder sb = new StringBuilder();
        sb.append(ip).append("|")
                .append(uri).append("|")
                .append(type).append("|")
                .append(JsonUtils.objectToString(rvo));
        String msg = sb.toString();
        //shareLog.info(msg);

        return ResultVO.success();
    }
}
