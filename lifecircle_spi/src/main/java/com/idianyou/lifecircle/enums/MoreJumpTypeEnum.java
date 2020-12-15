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
public enum MoreJumpTypeEnum {

    CHANNEL(1, "频道"),
    PROTOCOL(2, "协议"),
    ;

    private Integer type;
    private String desc;

}
