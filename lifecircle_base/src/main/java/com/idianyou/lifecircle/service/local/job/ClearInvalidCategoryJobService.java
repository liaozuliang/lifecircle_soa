package com.idianyou.lifecircle.service.local.job;

import com.idianyou.lifecircle.dto.QueryTypeDataParam;
import com.idianyou.lifecircle.dto.redis.Category;
import com.idianyou.lifecircle.entity.mongo.LifeCircleContentMongo;
import com.idianyou.lifecircle.enums.DataQueryTypeEnum;
import com.idianyou.lifecircle.mongo.LifeCircleContentRepository;
import com.idianyou.lifecircle.service.local.redis.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 清除无效的主题
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/8/3 10:10
 */
@Slf4j
@Service
public class ClearInvalidCategoryJobService {

    @Autowired
    private LifeCircleContentRepository lifeCircleContentRepository;

    @Autowired
    private RedisCacheService redisCacheService;

    public void doClear() {
        QueryTypeDataParam param = null;
        List<Category> categoryList = null;
        List<LifeCircleContentMongo> contentMongoList = null;
        List<Category> delCategoryList = new ArrayList<>();

        for (DataQueryTypeEnum queryTypeEnum : DataQueryTypeEnum.values()) {
            if (!queryTypeEnum.isCategoryQuery()) {
                continue;
            }

            categoryList = redisCacheService.getDataCategory(queryTypeEnum.getDataTypeEnum(), queryTypeEnum.getShowTypeEnum());
            if (CollectionUtils.isEmpty(categoryList)) {
                continue;
            }

            param = new QueryTypeDataParam();
            param.setDataType(queryTypeEnum.getDataTypeEnum().getType());
            param.setShowType(queryTypeEnum.getShowTypeEnum().getType());
            param.setPageSize(1);

            delCategoryList.clear();
            for (Category category : categoryList) {
                param.setCategoryId(category.getId());
                param.setCategoryName(category.getName());

                contentMongoList = lifeCircleContentRepository.getTypeData(param);
                if (CollectionUtils.isEmpty(contentMongoList)) {
                    delCategoryList.add(category);
                }
            }

            redisCacheService.delDataCategory(queryTypeEnum.getDataTypeEnum(), queryTypeEnum.getShowTypeEnum(), delCategoryList);
        }
    }
}
