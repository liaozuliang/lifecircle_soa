package com.idianyou.lifecircle.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/22 10:26
 */
@Data
public class BaseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String acceptId;
    private String androidId;
    private String appCoreVersion;
    private String appId;
    private String appVersionCode;
    private String appVersionId;
    private String appVersionName;
    private String crTime;
    private String deviceCode;
    private String deviceId; // 设备ID
    private String deviceType;
    private String hardware;
    private String mobileModel;
    private String phoneModel;
    private String sid;
    private String spUserId; // 推广商ID
    private String subPackageName;
    private String systemVersion;
    private String systemVersionRelease;
    private String userCertificate; // 登录身份标识
    private Long longitude; // 经度
    private Long latitude;  // 纬度

    private Integer v; // 接口版本号
}
