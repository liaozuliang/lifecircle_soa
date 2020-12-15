package com.idianyou.lifecircle.dto;

import lombok.Data;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/6 14:18
 */
@Data
public class ChangedDataDTO extends BaseDTO {

    /**
     * 动态ID
     */
    private Long id;

    /**
     * 数据类型
     */
    private Integer dataType;

    /**
     * 展示类型
     */
    private Integer showType;

    /**
     * 业务参数
     */
    private String bizParam;

}
