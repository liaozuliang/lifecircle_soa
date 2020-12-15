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
public enum ServiceStatusEnum {

    RUNNING(1, "进行中"),
    OVER(0, "已结束"),
    DELETED(2, "已删除");

    private Integer status;
    private String desc;

    public static ServiceStatusEnum statusOf(Integer status) {
        for (ServiceStatusEnum statusEnum : values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }
}
