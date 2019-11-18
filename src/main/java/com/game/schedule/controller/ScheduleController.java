package com.game.schedule.controller;

import com.game.schedule.service.AroundPlayerService;
import com.game.schedule.service.QuartzService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    QuartzService quartzService;
    @Autowired
    AroundPlayerService playerService;

    @GetMapping("/match")
    public void match() throws IOException {
        quartzService.match();
    }

    @GetMapping("/around")
    public void around(@RequestParam("accountId") int accountId) {
        playerService.dealAroundPlayer(accountId, null);
    }
}
