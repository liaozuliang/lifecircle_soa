package com.idianyou.lifecircle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/13 9:12
 */
@Getter
@AllArgsConstructor
public enum RoomPermissionTypeEnum {

    OPEN(1, "公开"),
    SECRET(2, "私密"),
    JOIN(3, "部分参与"),
    NOT_JOIN(4, "不让谁参与"),
    ;

    private Integer type;
    private String desc;
}
