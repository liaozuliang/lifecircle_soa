package com.idianyou.lifecircle.dto;

import com.alibaba.fastjson.JSONObject;
import com.idianyou.lifecircle.enums.ServiceStatusEnum;
import lombok.Data;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/29 20:36
 */
@Data
public class ServiceCurrentStateDTO extends BaseDTO {

    /**
     * 服务状态
     */
    private ServiceStatusEnum serviceStatusEnum;

    /**
     * 服务特殊参数(如，可进入房间的人员)
     */
    private JSONObject serviceSpecialParam;

    /**
     * 服务业务参数
     */
    private String serviceBizParam;

    /**
     * 订单号
     */
    private String serviceDataId;

    /**
     * 对应服务clientId
     */
    private String serviceClientId;
}
