package com.idianyou.lifecircle.dto;

import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.ServiceStatusEnum;
import lombok.Data;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/28 18:23
 */
@Data
public class UpdateServiceBizParamDTO extends BaseDTO {

    /**
     * 数据类型
     */
    private DataTypeEnum dataTypeEnum;

    /**
     * 订单号
     */
    private String serviceDataId;

    /**
     * 服务状态
     */
    private ServiceStatusEnum serviceStatusEnum;

    /**
     * 全量的服务业务参数
     */
    private String allServiceBizParam;

    /**
     * 有变动的服务业务参数
     */
    private String changedBizParam;

}