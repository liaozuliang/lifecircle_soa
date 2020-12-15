package com.idianyou.lifecircle.service.local.learn;

import com.alibaba.fastjson.JSON;
import com.idianyou.lifecircle.dto.redis.Category;
import com.idianyou.lifecircle.pool.operate.bean.CategoryGoodsBean;
import com.idianyou.lifecircle.pool.operate.bean.CategoryShopsBean;
import com.idianyou.lifecircle.pool.operate.service.UserPoolService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 获取机器学习推荐数据
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/7/31 14:34
 */
@Slf4j
@Service
public class GetLearnDataService {

    @Autowired
    private UserPoolService userPoolService;

    /**
     * 获取商户推荐主题
     * @param userId
     * @param dataNum
     * @param baseLog
     * @return
     */
    public List<Category> getShopsCategory(long userId, int dataNum, String baseLog) {
        List<Category> categoryList = new ArrayList<>();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<CategoryShopsBean> shopsBeanList = null;

        try {
            shopsBeanList = userPoolService.getShopsCategoryList(userId, dataNum);
        } catch (Exception e) {
            log.error("{} 获取机器学习赶集商户推荐主题出错：", baseLog, e);
        }

        stopWatch.stop();
        log.info("{} 获取机器学习赶集商户推荐主题：{}, 耗时{}ms", baseLog, JSON.toJSONString(shopsBeanList), stopWatch.getTotalTimeMillis());

        if (CollectionUtils.isEmpty(shopsBeanList)) {
            return categoryList;
        }

        for (CategoryShopsBean bean : shopsBeanList) {
            if (StringUtils.isAnyBlank(bean.getCategoryId(), bean.getCategoryName())) {
                continue;
            }

            categoryList.add(new Category(bean.getCategoryId(), bean.getCategoryName()));
        }

        return categoryList;
    }

    /**
     * 获取商品推荐主题
     * @param userId
     * @param dataNum
     * @param baseLog
     * @return
     */
    public List<Category> getGoodsCategory(long userId, int dataNum, String baseLog) {
        List<Category> categoryList = new ArrayList<>();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<CategoryGoodsBean> goodsBeanList = null;
        try {
            goodsBeanList = userPoolService.getGoodsCategoryList(userId, dataNum);
        } catch (Exception e) {
            log.error("{} 获取机器学习赶集商品推荐主题出错：", baseLog, e);
        }

        stopWatch.stop();
        log.info("{} 获取机器学习赶集商品推荐主题：{}, 耗时{}ms", baseLog, JSON.toJSONString(goodsBeanList), stopWatch.getTotalTimeMillis());

        if (CollectionUtils.isEmpty(goodsBeanList)) {
            return categoryList;
        }

        for (CategoryGoodsBean bean : goodsBeanList) {
            if (StringUtils.isAnyBlank(bean.getCategoryId(), bean.getCategoryName())) {
                continue;
            }

            categoryList.add(new Category(bean.getCategoryId(), bean.getCategoryName()));
        }

        return categoryList;
    }

    /**
     * 获取农户推荐数据
     * @param userId
     * @param dataNum
     * @param baseLog
     * @return
     */
    public List<Long> getFarmerContentId(long userId, int dataNum, String baseLog) {
        List<Long> contentIdList = new ArrayList<>();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            List<String> contentIdStrList = userPoolService.getStallCategoryList(userId, dataNum);
            if (CollectionUtils.isNotEmpty(contentIdStrList)) {
                for (String strId : contentIdStrList) {
                    contentIdList.add(Long.valueOf(strId));
                }
            }
        } catch (Exception e) {
            log.error("{} 获取机器学习赶集农户推荐数据出错：", baseLog, e);
        }

        stopWatch.stop();
        log.info("{} 获取机器学习赶集农户推荐数据：{}, 耗时{}ms", baseLog, JSON.toJSONString(contentIdList), stopWatch.getTotalTimeMillis());

        return contentIdList;
    }
}
