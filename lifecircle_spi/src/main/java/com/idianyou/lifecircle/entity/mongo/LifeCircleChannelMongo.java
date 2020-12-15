package com.idianyou.lifecircle.entity.mongo;

import lombok.Data;

/**
 * @Description: 生活圈-频道
 * @version: v1.0.0
 * @author: xxj
 * @date: 2020-04-27 16:34
 */
@Data
public class LifeCircleChannelMongo extends BaseMongo<Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * 频道名称
     */
    private String channelName;

    /**
     * 排序
     */
    private int sortNum;

    /**
     * 是否隐藏:1-是 2-否
     */
    private int showType;

    /**
     * 来源，默认：lifeCircle
     */
    private String fromCode;

    /**
     * 描述
     */
    private String remark;

    /**
     * 0:失效 1:有效
     */
    private int currType;

    /**
     * 操作人ID
     */
    private int operUserId;

    /**
     * 操作人名称
     */
    private String operUserName;

}
