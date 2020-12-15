package com.idianyou.lifecircle.dto.redis;

import com.idianyou.lifecircle.dto.BaseDTO;
import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/28 21:03
 */
@Data
public class ServiceBizParamRedisDTO extends BaseDTO {

    /**
     * 存储服务业务参数表数据ID
     */
    private String bizParamDataId;

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
     * 服务业务参数
     */
    private String serviceBizParam;

    /**
     * 变动的服务业务参数
     */
    private String serviceChangedBizParam;

    /**
     * 更新时间
     */
    private Date updateTime;

}
