package com.idianyou.lifecircle.controller.channel.controller;

import com.idianyou.lifecircle.controller.BaseController;
import com.idianyou.lifecircle.controller.channel.out.LifeCircleChannelOut;
import com.idianyou.lifecircle.dto.InnerRpcResponse;
import com.idianyou.lifecircle.dto.LifeCircleChannelDTO;
import com.idianyou.lifecircle.service.LifeCircleChannelDubboService;
import com.idianyou.lifecircle.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 生活圈-频道服务动态相关
 * @version: v1.0.0
 * @author: xxj
 * @date: 2020-04-27 19:04
 */
@Slf4j
@RequestMapping("/lifecircle/channel")
@RestController
public class LifeCircleChannelController extends BaseController {

    @Autowired
    private LifeCircleChannelDubboService lifeCircleChannelDubboService;


    @RequestMapping("getLifeCircleChannelList")
    public Result getLifeCircleChannelList() {

        //验证用户是否登录

        List<LifeCircleChannelOut> list = new ArrayList<>();
        //从 mongo 中获取频道列表
        try {
            InnerRpcResponse<List<LifeCircleChannelDTO>> response = lifeCircleChannelDubboService.queryAll();
            if (response != null && CollectionUtils.isNotEmpty(response.getData())){
                List<LifeCircleChannelDTO> channelDTOS = response.getData();
                for (LifeCircleChannelDTO dto : channelDTOS) {
                    LifeCircleChannelOut out = new LifeCircleChannelOut();
                    out.setChannelId(dto.get_id());
                    out.setChannelName(dto.getChannelName());
                    list.add(out);
                }
            }
        }catch (Exception e){
            log.error("LifeCircleChannelController->getCircleContentChannel->查询用户频道数据异常");
        }

        return Result.success(list);
    }
}
