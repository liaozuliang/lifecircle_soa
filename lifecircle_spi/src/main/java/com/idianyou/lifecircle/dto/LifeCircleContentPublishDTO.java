package com.idianyou.lifecircle.dto;

import com.alibaba.fastjson.JSONObject;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.ServiceStatusEnum;
import com.idianyou.lifecircle.enums.ShowTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/24 15:24
 */
@Data
public class LifeCircleContentPublishDTO extends BaseDTO {

    /**
     * 发布人用户ID
     */
    private Long userId;

    /**
     * 数据类型
     */
    private DataTypeEnum dataTypeEnum;

    /**
     * 服务展示类型
     */
    private ShowTypeEnum showTypeEnum;

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
    private ServiceStatusEnum serviceStatusEnum;

    /**
     * 分享语
     */
    private String shareContent;

    /**
     * 服务特殊参数(如，可进入房间的人员)
     */
    private JSONObject serviceSpecialParam;

    /**
     * 服务业务参数
     */
    private String serviceBizParam;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 发布时间
     */
    private Date createTime;

}