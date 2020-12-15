package com.idianyou.lifecircle.dto.lifeShopsGoods;

import com.idianyou.lifecircle.dto.AreaDTO;
import com.idianyou.lifecircle.dto.BaseDTO;
import com.idianyou.lifecircle.dto.Location;
import com.idianyou.lifecircle.enums.lifeShopsGoods.DataTypeEnum;
import com.idianyou.lifecircle.enums.lifeShopsGoods.OnOffStatusEnum;
import com.idianyou.lifecircle.enums.lifeShopsGoods.StatusEnum;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 生活商品、商铺信息
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/4 16:39
 */
@Data
public class LifeShopsGoodsDTO extends BaseDTO {

    /**
     * 数据ID(商品ID、商铺ID)
     */
    private Integer dataId;

    /**
     * 数据类型
     */
    private DataTypeEnum dataType;

    /**
     * 商品或商铺名称
     */
    private String name;

    /**
     * 月销量
     */
    private Integer monthSales;

    /**
     * 权重
     */
    private Double weight;

    /**
     * 状态
     */
    private StatusEnum status;

    /**
     * 上架状态
     */
    private OnOffStatusEnum onOffStatus;

    /**
     * 经纬度
     */
    private Location location;

    /**
     * 区域
     */
    private AreaDTO area;

    /**
     * 创建时间
     */
    private Date createTime;

}
