package com.game.tool.controller;

import com.game.common.vo.ResultVO;
import com.game.tool.service.ToolService;
import com.game.util.Constant;
import com.game.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tool")
public class ToolController {
    @Autowired
    ToolService toolService;

    @GetMapping("/genAIAccount")
    public ResultVO genAIAccount(HttpServletRequest request) {
        String ip = IpUtil.getIpAddr(request);
        if (! ip.equals("127.0.0.1")) {
            return ResultVO.error("forbidden", Constant.RNT_CODE_EXCEPTION);
        }

        return toolService.genAIAccount();
    }
}
