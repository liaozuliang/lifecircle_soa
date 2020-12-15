package com.idianyou.lifecircle.controller.channel.out;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 生活圈-频道-封装数据实体类
 * @version: v1.0.0
 * @author: xxj
 * @date: 2020-04-27 21:04
 */
@Data
public class LifeCircleChannelOut implements Serializable {

    private static final long serialVersionUID = 4797636765179685030L;

    private Integer channelId;
    private String channelName;

}
