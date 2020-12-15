package com.idianyou.lifecircle.enums.lifeShopsGoods;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/4 16:36
 */
@Getter
@AllArgsConstructor
public enum DataTypeEnum {

    SHOPS(1, "商铺"),
    GOODS(2, "商品");

    private Integer type;
    private String desc;

    public static DataTypeEnum typeOf(Integer type) {
        for (DataTypeEnum typeEnum : values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }
}
