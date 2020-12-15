package com.idianyou.lifecircle.dto;

import lombok.Data;

/**
 * @Description: 区域
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/4 18:02
 */
@Data
public class AreaDTO extends BaseDTO {

    /**
     * 省份
     */
    private String provinceCode;

    /**
     * 市
     */
    private String cityCode;

    /**
     * 县
     */
    private String areaCode;

    /**
     * 乡镇
     */
    private String townshipCode;

    /**
     * 街道/村组
     */
    private String streetCode;
}
