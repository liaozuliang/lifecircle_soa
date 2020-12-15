package com.idianyou.lifecircle.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 生活圈-频道
 * @version: v1.0.0
 * @author: xxj
 * @date: 2020-04-27 16:34
 */
@Data
public class LifeCircleChannelDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Integer _id;

    /**
     * 频道名称
     */
    private String channelName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

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
