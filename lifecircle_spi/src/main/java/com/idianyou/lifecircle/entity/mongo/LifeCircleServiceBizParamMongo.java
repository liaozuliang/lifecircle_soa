package com.idianyou.lifecircle.entity.mongo;

import com.alibaba.fastjson.JSONObject;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import lombok.Data;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/27 19:58
 */
@Data
public class LifeCircleServiceBizParamMongo extends BaseMongo<String> {

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
     * 服务特殊参数(如，可进入房间的人员)
     */
    private JSONObject serviceSpecialParam;

    /**
     * 服务业务参数
     */
    private String serviceBizParam;

    public static String getDataId(DataTypeEnum dataTypeEnum, String serviceDataId) {
        return dataTypeEnum.name().toLowerCase().concat("_").concat(serviceDataId);
    }

}