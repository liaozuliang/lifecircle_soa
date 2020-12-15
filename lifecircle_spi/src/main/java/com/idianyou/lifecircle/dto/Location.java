package com.idianyou.lifecircle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 经纬度
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/4 15:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location extends BaseDTO {

    /**
     * 经度
     */
    private Double lon;

    /**
     * 纬度
     */
    private Double lat;
}
