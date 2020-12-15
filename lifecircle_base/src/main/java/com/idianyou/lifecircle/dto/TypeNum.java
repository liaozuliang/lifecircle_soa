package com.idianyou.lifecircle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/9 15:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeNum {

    /**
     * 条数
     */
    private int dataNum;

    /**
     * 组数
     */
    private int groupNum;
}
