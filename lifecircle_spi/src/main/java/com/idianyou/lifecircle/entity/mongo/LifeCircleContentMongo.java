package com.idianyou.lifecircle.entity.mongo;

import com.idianyou.lifecircle.dto.FromServiceInfo;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/27 19:58
 */
@Data
public class LifeCircleContentMongo extends BaseMongo<Long> {

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
    private String serviceDataId;

    /**
     * 业务系统中的数据ID
     */
    private String bizDataId;

    /**
     * 服务状态
     */
    private Integer serviceStatus;

    /**
     * 分享语
     */
    private String shareContent;

    /**
     * 分类(赶集商户、吃瓜商城)
     */
    private String categoryId;

    /**
     * 分类(赶集商户、吃瓜商城)
     */
    private String categoryName;

    /**
     * 来源服务信息
     */
    private FromServiceInfo fromServiceInfo;

    /**
     * 存储服务业务参数表数据ID
     */
    private String bizParamDataId;

    /**
     * 所属频道
     */
    private List<Integer> channelIdList;

    /**
     * 点赞数
     */
    private Integer praiseCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 点赞用户
     */
    private List<Long> praiseUserIds;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

}
