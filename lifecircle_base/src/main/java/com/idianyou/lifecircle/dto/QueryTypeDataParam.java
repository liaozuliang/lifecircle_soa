package com.idianyou.lifecircle.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/28 11:19
 */
@Data
public class QueryTypeDataParam {

    private Integer dataType;

    private Integer showType;

    private List<Long> idList;

    private String clientId;

    private String serviceName;

    private String notServiceName;

    private String categoryId;

    private List<String> categoryIdList;

    private String categoryName;

    private List<String> categoryNameList;

    private boolean ignoreServiceStatus;

    private List<Integer> channelIdList;

    private Long maxId;

    private int pageSize = 100;

    /**
     * 随机获取数据
     */
    private boolean randomGetData;

    /**
     * 最小发布时间
     */
    private Date minCreateTime;

    /**
     * 最大发布时间
     */
    private Date maxCreateTime;

    public boolean isCategoryQuery() {
        return !StringUtils.isAnyBlank(categoryId, categoryName);
    }

}
