package com.idianyou.lifecircle.dto;

import lombok.Data;

/**
 * @Description: 来源服务信息
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/24 18:02
 */
@Data
public class FromServiceInfo extends BaseDTO {

    private String clientId;        // 服务clientId
    private String name;            // 服务名称
    private String icon;            // 服务图标
    private String jumpProtocol;    // 跳转协议
}
