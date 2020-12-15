package com.idianyou.lifecircle.controller;

import com.alibaba.fastjson.JSONObject;
import com.idianyou.lifecircle.dto.BaseParam;
import com.idianyou.lifecircle.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/21 16:48
 */
@Slf4j
@RequestMapping("/test")
@RestController
public class TTController extends BaseController {

    @RequestMapping("tt")
    public Result tt(@RequestParam Map<String, Object> paramMap) {

        Long s = getLoginUserId();

        JSONObject obj = new JSONObject();
        obj.put("abc", "中国");

        return Result.success(paramMap);
    }

    @RequestMapping("tt2")
    public Result tt2(BaseParam baseParam) {

        JSONObject obj = new JSONObject();
        obj.put("abc", "中国");

        return Result.success(baseParam);
    }
}
