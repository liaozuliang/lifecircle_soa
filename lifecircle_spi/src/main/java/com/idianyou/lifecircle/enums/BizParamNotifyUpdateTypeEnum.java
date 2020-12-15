package com.idianyou.lifecircle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 业务参数通知变更类型
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/24 17:25
 */
@Getter
@AllArgsConstructor
public enum BizParamNotifyUpdateTypeEnum {

    DIRECT(1, "直接更新"),
    API(2, "调用接口获取最新变动数据"),
    ;

    private Integer type;
    private String desc;

    public static BizParamNotifyUpdateTypeEnum typeOf(Integer type) {
        for (BizParamNotifyUpdateTypeEnum typeEnum : values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }
}
