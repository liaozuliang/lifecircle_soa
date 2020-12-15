package com.idianyou.lifecircle.dto;

import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.enums.TimeDescEnum;
import com.idianyou.lifecircle.enums.UserNativeTypeEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/27 17:14
 */
@Data
public class GetChannelDataContext {

    /**
     * 用户
     */
    private Long userId;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 频道
     */
    private LifeCircleChannelEnum channelEnum;

    /**
     * 用户在本地的情况
     */
    private UserNativeTypeEnum userNativeTypeEnum;

    /**
     * 时间段
     */
    private TimeDescEnum timeDescEnum;

    /**
     * 请求批次号
     */
    private String reqNo;

    /**
     * 日志
     */
    private String baseLog;

    /**
     * 需要清除历史已查询主题的前缀
     */
    private List<String> needDelQueryedCategoryList = new ArrayList<>();

    /**
     * 需要排除的数据ID
     */
    private Set<String> excludeDataIdSet = new ConcurrentHashSet<>();

    /**
     * 已选中的数据BizDataId
     */
    private Set<String> passedBizDataIdSet = new ConcurrentHashSet<>();

    /**
     * 已选中的数据Id
     */
    private Set<String> passedDataIdSet = new ConcurrentHashSet<>();

    /**
     * 需要排除的主题
     */
    private Set<String> excludeCategorySet = new ConcurrentHashSet<>();

}
