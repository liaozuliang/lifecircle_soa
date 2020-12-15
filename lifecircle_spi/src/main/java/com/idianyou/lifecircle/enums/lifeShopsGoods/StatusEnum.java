package com.idianyou.lifecircle.enums.lifeShopsGoods;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 状态
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/4 16:44
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {

    VALID(1, "有效"),
    INVALID(0, "无效/已删除");

    private Integer status;
    private String desc;

    public static StatusEnum typeOf(Integer status) {
        for (StatusEnum typeEnum : values()) {
            if (typeEnum.getStatus().equals(status)) {
                return typeEnum;
            }
        }
        return null;
    }
}
