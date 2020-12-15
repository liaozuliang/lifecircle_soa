package com.idianyou.lifecircle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/24 17:25
 */
@Getter
@AllArgsConstructor
public enum YesNoEnum {

    NO(0, "否"),
    YES(1, "是"),
    ;

    private Integer code;
    private String desc;

}
