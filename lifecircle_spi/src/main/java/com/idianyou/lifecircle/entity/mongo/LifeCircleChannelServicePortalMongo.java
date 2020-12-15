package com.idianyou.lifecircle.entity.mongo;

import lombok.Data;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/19 15:46
 */
@Data
public class LifeCircleChannelServicePortalMongo extends BaseMongo<Integer> {

    /**
     * 频道
     */
    private Integer channelId;

    /**
     * 行序号
     */
    private Integer rowNo;

    /**
     * 列序号
     */
    private Integer columnNo;

    /**
     * 名称
     */
    private String name;

    /**
     * 图标
     */
    private String icon;

    /**
     * 跳转协议
     */
    private String protocol;

    /**
     * 是否删除
     */
    private Integer isDelete;

}