package com.idianyou.lifecircle.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/6 11:33
 */
@Data
public class BizParamChangedSocketNotifyDTO extends BaseDTO {

    /**
     * 业务类型
     */
    private Integer bizType;

    /**
     * 前端数据更新方式(1:直接更新，2:调用接口获取最新变动数据)
     */
    private Integer updateType;

    /**
     * 变动的数据
     */
    private List<ChangedDataDTO> changedDataDTOList;
}
