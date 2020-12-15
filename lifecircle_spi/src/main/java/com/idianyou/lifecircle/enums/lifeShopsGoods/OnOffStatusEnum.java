package com.idianyou.lifecircle.enums.lifeShopsGoods;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 上下架状态
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/4 16:44
 */
@Getter
@AllArgsConstructor
public enum OnOffStatusEnum {

    ON(1, "上架"),
    OFF(0, "下架");

    private Integer status;
    private String desc;

    public static OnOffStatusEnum typeOf(Integer status) {
        for (OnOffStatusEnum typeEnum : values()) {
            if (typeEnum.getStatus().equals(status)) {
                return typeEnum;
            }
        }
        return null;
    }
}
