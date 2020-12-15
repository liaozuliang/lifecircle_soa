package com.idianyou.lifecircle.dto.request;

import lombok.Data;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/28 19:32
 */
@Data
public class UpdateBizParamReq {

    /**
     * 数据类型
     */
    private Integer dataType;

    /**
     * 订单号
     */
    private String serviceDataId;

    /**
     * 服务状态
     */
    private Integer serviceStatus;

    /**
     * 全量的服务业务参数
     */
    private String allServiceBizParam;

    /**
     * 有变动的服务业务参数
     */
    private String changedBizParam;
}
