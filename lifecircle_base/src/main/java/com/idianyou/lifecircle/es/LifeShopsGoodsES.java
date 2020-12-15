package com.idianyou.lifecircle.es;

import com.idianyou.lifecircle.dto.Location;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Score;

import java.util.Date;

/**
 * @Description: 生活商铺、商品
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/4 15:09
 */
@Data
@Document(indexName = LifeShopsGoodsES.INDEX_NAME, type = LifeShopsGoodsES.INDEX_TYPE)
public class LifeShopsGoodsES {

    public static final String INDEX_NAME = "life_shops_goods";
    public static final String INDEX_TYPE = "content";

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Score
    private Float score;

    /**
     * 数据ID(商品ID、商铺ID)
     */
    @Field(type = FieldType.Integer)
    private Integer dataId;

    /**
     * 数据类型
     */
    @Field(type = FieldType.Integer)
    private Integer dataType;

    /**
     * 商品或商铺名称
     */
    @MultiField(
            mainField = @Field(type = FieldType.Text),
            otherFields = {
                    @InnerField(suffix = "ik", type = FieldType.Text, analyzer = "ik_max_word")
            }
    )
    private String name;

    /**
     * 月销量
     */
    @Field(type = FieldType.Integer)
    private Integer monthSales;

    /**
     * 权重
     */
    @Field(type = FieldType.Double)
    private Double weight;

    /**
     * 状态
     */
    @Field(type = FieldType.Integer)
    private Integer status;

    /**
     * 上下架状态
     */
    @Field(type = FieldType.Integer)
    private Integer onOffStatus;

    /**
     * 经纬度
     */
    @GeoPointField
    private Location location;

    /**
     * 区域
     */
    @Field(type = FieldType.Nested)
    private AreaES area;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(type = FieldType.Date)
    private Date updateTime;
}