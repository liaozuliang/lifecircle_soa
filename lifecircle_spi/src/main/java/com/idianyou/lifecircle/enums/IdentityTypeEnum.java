package com.idianyou.lifecircle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 用户身份类型
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/24 17:25
 */
@Getter
@AllArgsConstructor
public enum IdentityTypeEnum {

    OXEN(1, "孺子牛"),
    SERVICE_PARTNER(2, "服务合伙人"),
    MERCHANT_PARTNER(3, "商户合伙人"),
    MERCHANT(4, "商户"),
    FARMER(5, "农户"),
    CHIGUA_SUPPLIER(6, "吃瓜供货商"),
    AGENT(7, "代理商"),
    ;

    private Integer type;
    private String desc;

    public static IdentityTypeEnum typeOf(Integer type) {
        for (IdentityTypeEnum typeEnum : values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }
}
