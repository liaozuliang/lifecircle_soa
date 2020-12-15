package com.idianyou.lifecircle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 动态分类
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/24 17:25
 */
@Getter
@AllArgsConstructor
public enum ContentTypeEnum {

    PERSONAL(1, "个人动态"),
    SERVICE(2, "服务动态"),
    ;

    private Integer type;
    private String desc;

    public static ContentTypeEnum typeOf(Integer type) {
        for (ContentTypeEnum typeEnum : values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }
}
