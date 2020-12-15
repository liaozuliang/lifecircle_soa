package com.idianyou.lifecircle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 走socket通道的业务类型
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/6 16:38
 */
@Getter
@AllArgsConstructor
public enum SocketBizTypeEnum {

    UPDATE_CHANGED_BIZ_PARAM(1, "频道列表数据刷新");

    private Integer type;
    private String desc;
}
