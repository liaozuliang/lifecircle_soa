package com.idianyou.lifecircle.dto;

import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.idianyou.lifecircle.enums.DataQueryTypeEnum;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.IdentityTypeEnum;
import com.idianyou.lifecircle.enums.ShowTypeEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/27 15:40
 */
@Data
public class GetTypeDataContext {

    private DataQueryTypeEnum queryTypeEnum;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 数量
     */
    private int dataNum;

    /**
     * 组数量
     */
    private int groupNum;

    /**
     * 身份类型
     */
    private IdentityTypeEnum identityTypeEnum;

    /**
     * 服务clientId
     */
    private String clientId;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 排除的服务名称
     */
    private String notServiceName;

    /**
     * 分类
     */
    private String categoryId;

    /**
     * 分类
     */
    private String categoryName;

    /**
     * 数据类型
     */
    private DataTypeEnum dataTypeEnum;

    /**
     * 展示类型
     */
    private ShowTypeEnum showTypeEnum;

    /**
     * 忽略数据状态
     */
    private boolean ignoreServiceStatus;

    /**
     * 频道
     */
    private List<Integer> channelIdList;

    /**
     * 需要排除的数据ID
     */
    private Set<String> excludeDataIdSet = new ConcurrentHashSet<>();

    /**
     * 已找到符合条件的数据ID
     */
    private Set<String> passedDataIdSet = new ConcurrentHashSet<>();

    /**
     * 已选中的数据BizDataId
     */
    private Set<String> passedBizDataIdSet = new ConcurrentHashSet<>();

    /**
     * 日志
     */
    private String baseLog;

    /**
     * 随机获取数据
     */
    private boolean randomGetData;

    /**
     * 最小发布时间
     */
    private Date minCreateTime;

}