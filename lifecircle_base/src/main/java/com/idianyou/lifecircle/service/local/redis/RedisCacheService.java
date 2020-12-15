package com.idianyou.lifecircle.service.local.redis;

import com.google.common.base.Splitter;
import com.idianyou.lifecircle.constants.RedisKeys;
import com.idianyou.lifecircle.dto.redis.Category;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.ShowTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/28 11:41
 */
@Slf4j
@Service
public class RedisCacheService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 保存数据分类
     * @param dataTypeEnum
     * @param showTypeEnum
     * @param categoryName
     * @param categoryId
     */
    public void addDataCategory(DataTypeEnum dataTypeEnum, ShowTypeEnum showTypeEnum, String categoryName, String categoryId) {
        if (dataTypeEnum == null || showTypeEnum == null || StringUtils.isBlank(categoryName)) {
            return;
        }

        String key = RedisKeys.DATA_CATEGORY_ZSET.concat(dataTypeEnum.getType() + "_" + showTypeEnum.getType());

        String category = categoryName;
        if (StringUtils.isNotBlank(categoryId)) {
            category = categoryId + "_" + categoryName;
        }

        redisTemplate.boundZSetOps(key).add(category, System.currentTimeMillis());

        setCurrentCategorySortNo(System.currentTimeMillis() + "");
    }

    /**
     * 获取数据分类
     * @param dataTypeEnum
     * @param showTypeEnum
     * @return
     */
    public List<Category> getDataCategory(DataTypeEnum dataTypeEnum, ShowTypeEnum showTypeEnum) {
        if (dataTypeEnum == null || showTypeEnum == null) {
            return Collections.emptyList();
        }

        String key = RedisKeys.DATA_CATEGORY_ZSET.concat(dataTypeEnum.getType() + "_" + showTypeEnum.getType());
        Set<String> categoryStrSet = redisTemplate.boundZSetOps(key).reverseRange(0, -1);

        if (categoryStrSet == null) {
            return Collections.emptyList();
        }

        List<Category> categoryList = new ArrayList<>(categoryStrSet.size());
        List<String> strList = null;

        for (String str : categoryStrSet) {
            if (StringUtils.contains(str, "_")) {
                strList = Splitter.on("_").splitToList(str);
                categoryList.add(new Category(strList.get(0), strList.get(1)));
            } else {
                categoryList.add(new Category(null, str));
            }
        }

        return categoryList;
    }

    /**
     * 删除数据分类
     * @param dataTypeEnum
     * @param showTypeEnum
     * @param categoryList
     */
    public void delDataCategory(DataTypeEnum dataTypeEnum, ShowTypeEnum showTypeEnum, List<Category> categoryList) {
        if (dataTypeEnum == null
                || showTypeEnum == null
                || CollectionUtils.isEmpty(categoryList)) {
            return;
        }

        String key = RedisKeys.DATA_CATEGORY_ZSET.concat(dataTypeEnum.getType() + "_" + showTypeEnum.getType());

        List<String> categoryStrList = new ArrayList<>(categoryList.size());
        for (Category category : categoryList) {
            String categoryStr = category.getName();

            if (StringUtils.isNotBlank(category.getId())) {
                categoryStr = category.getId() + "_" + category.getName();
            }

            categoryStrList.add(categoryStr);
        }

        redisTemplate.boundZSetOps(key).remove(categoryStrList.toArray());
    }

    public void setCurrentCategorySortNo(String sortNo) {
        redisTemplate.boundValueOps(RedisKeys.CURRENT_CATEGORY_SORT_NO).set(sortNo);
    }

    public String getCurrentCategorySortNo() {
        String sortNo = (String) redisTemplate.boundValueOps(RedisKeys.CURRENT_CATEGORY_SORT_NO).get();
        if (StringUtils.isBlank(sortNo)) {
            sortNo = System.currentTimeMillis() + "";
            setCurrentCategorySortNo(sortNo);
        }
        return sortNo;
    }
}
