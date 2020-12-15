package com.idianyou.lifecircle.dto.lifeShopsGoods;

import com.idianyou.lifecircle.dto.AreaDTO;
import com.idianyou.lifecircle.dto.BaseDTO;
import com.idianyou.lifecircle.dto.Location;
import com.idianyou.lifecircle.enums.lifeShopsGoods.DataTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 生活商品、商铺搜索条件
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/4 16:40
 */
@Data
public class LifeShopsGoodsSearchDTO extends BaseDTO {

    /**
     * 搜索词
     */
    private String keywords;

    /**
     * 数据类型
     */
    private DataTypeEnum dataType;

    /**
     * 区域
     */
    private AreaDTO area;

    /**
     * 距离
     */
    private DistanceObj distanceObj;

    /**
     * 分页对象
     */
    private PageObj pageObj = new PageObj();

    /**
     * 本页最后一条数据的排序值，用于获取下一页时传入
     */
    private String lastSortValues;

    @Data
    public static class DistanceObj extends BaseDTO {
        /**
         * 距离，km
         */
        private Integer distance;

        /**
         * 中心坐标
         */
        private Location location;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PageObj extends BaseDTO {

        /**
         * 当前页(1为第一页)
         */
        private int currentPage = 1;

        /**
         * 分页条数
         */
        private int pageSize = 10;
    }
}
