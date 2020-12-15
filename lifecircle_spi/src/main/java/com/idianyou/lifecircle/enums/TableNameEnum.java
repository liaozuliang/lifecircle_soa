package com.idianyou.lifecircle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/26 9:51
 */
@Getter
@AllArgsConstructor
public enum TableNameEnum {

    LIFE_CRICLE_CONTENT("生活圈动态", 1879048192), // 16进制70000000
    ;

    private String desc;
    private long baseNum;
}
