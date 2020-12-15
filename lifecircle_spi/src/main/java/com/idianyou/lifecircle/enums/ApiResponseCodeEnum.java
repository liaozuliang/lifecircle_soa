package com.idianyou.lifecircle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 生活圈 api 接口返回码
 * @version: v1.0.0
 * @author: xxj
 * @date: 2020-04-27 17:04
 */
@Getter
@AllArgsConstructor
public enum ApiResponseCodeEnum {

    /* 生活圈 api 接口返回码 */
    API_PARAM_NOT_NULL_ERROR("9001", "必要参数不能为空！"),
    API_DATA_ALREADY_ERROR("9002", "所要添加的数据已存在！"),
    API_DATA_IS_NOT_FOUND_ERROR("9002", "所要添加的数据已存在！"),
    ;

    private String code;
    private String desc;
}
