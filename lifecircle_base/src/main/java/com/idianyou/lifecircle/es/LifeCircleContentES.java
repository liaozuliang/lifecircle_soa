package com.idianyou.lifecircle.es;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Score;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/2 14:39
 */
@Data
@Document(indexName = LifeCircleContentES.INDEX_NAME, type = LifeCircleContentES.INDEX_TYPE)
public class LifeCircleContentES {

    public static final String INDEX_NAME = "lifecircle";
    public static final String INDEX_TYPE = "content";

    @Id
    private Long id;

    @Score
    private Float score;

    /**
     * 发布人用户ID
     */
    private Long userId;

    /**
     * 数据类型
     */
    private Integer dataType;

    /**
     * 动态类型
     */
    private Integer contentType;

    /**
     * 服务展示类型
     */
    private Integer showType;

    /**
     * 订单号
     */
    @Field(type = FieldType.Keyword)
    private String serviceDataId;

    /**
     * 业务系统中的数据ID
     */
    @Field(type = FieldType.Keyword)
    private String bizDataId;

    /**
     * 服务状态
     */
    private Integer serviceStatus;

    /**
     * 分类(赶集商户、吃瓜商城)
     */
    @Field(type = FieldType.Keyword)
    private String categoryId;

    /**
     * 分类(赶集商户、吃瓜商城)
     */
    @Field(type = FieldType.Keyword)
    private String categoryName;

    /**
     * 来源服务clientId
     */
    @Field(type = FieldType.Keyword)
    private String fromServiceClientId;

    /**
     * 来源服务名称
     */
    @Field(type = FieldType.Keyword)
    private String fromServiceName;

    /**
     * 权限类型
     */
    @Field(type = FieldType.Keyword)
    private Integer permissionType;

    /**
     * 权限用户
     */
    @Field(type = FieldType.Keyword)
    private List<String> permissionUserIds;

    /**
     * 存储服务业务参数表数据ID
     */
    @Field(type = FieldType.Keyword)
    private String bizParamDataId;

    /**
     * 所属频道
     */
    @Field(type = FieldType.Keyword)
    private List<Integer> channelIdList;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 搜索文本
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String searchText;

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
