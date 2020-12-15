package com.idianyou.lifecircle.es;

import lombok.Data;

import java.util.List;

/**
 * @Description: 生活商铺、商品
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/4 15:09
 */
@Data
public class LifeShopsGoodsESExt extends LifeShopsGoodsES {

    /**
     * 名称匹配上的关键字
     */
    private List<String> nameMatchedKeywords;

    /**
     * 排序值
     */
    private String sortValues;

}