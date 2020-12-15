package com.idianyou.lifecircle.es;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/10 9:53
 */
@Data
public class AreaES {

    /**
     * 省份
     */
    @Field(type = FieldType.Keyword)
    private String provinceCode;

    /**
     * 市
     */
    @Field(type = FieldType.Keyword)
    private String cityCode;

    /**
     * 县
     */
    @Field(type = FieldType.Keyword)
    private String areaCode;

    /**
     * 乡镇
     */
    @Field(type = FieldType.Keyword)
    private String townshipCode;

    /**
     * 街道/村组
     */
    @Field(type = FieldType.Keyword)
    private String streetCode;
}
