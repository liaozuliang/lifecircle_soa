package com.idianyou.lifecircle.controller;

import com.idianyou.lifecircle.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/8 10:58
 */
@Slf4j
@RequestMapping("/lifecircle/tool")
@RestController
public class ToolController extends BaseController {

    @RequestMapping("getCityName")
    public Result getCityName() {
        return Result.success();
    }

    @RequestMapping("getWeather")
    public Result getWeather() {
        String url = "https://www.tianqiapi.com/free/day?appid=22316522&appsecret=u66VAcFX&city=贵州省贵阳市云岩区";

        return Result.success();
    }
}
