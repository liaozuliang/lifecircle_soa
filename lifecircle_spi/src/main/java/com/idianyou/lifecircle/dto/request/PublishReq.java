package com.idianyou.lifecircle.dto.request;

import lombok.Data;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/28 18:44
 */
@Data
public class PublishReq {

    /**
     * 发布人用户ID
     */
    private Long userId;

    /**
     * 数据类型
     */
    private Integer dataType;

    /**
     * 服务展示类型
     */
    private Integer showType;

    /**
     * 订单号
     */
    private String serviceDataId;

    /**
     * 对应服务clientId
     */
    private String serviceClientId;

    /**
     * 服务状态
     */
    private Integer serviceStatus;

    /**
     * 分享语
     */
    private String shareContent;

    /**
     * 服务特殊参数(如，可进入房间的人员)
     */
    private String serviceSpecialParam;

    /**
     * 服务业务参数
     */
    private String serviceBizParam;

    /**
     * 发布时间(毫秒时间戳)
     */
    private Long createTime;

}
