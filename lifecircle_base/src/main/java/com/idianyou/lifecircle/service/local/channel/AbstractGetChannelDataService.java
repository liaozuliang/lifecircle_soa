package com.idianyou.lifecircle.service.local.channel;

import com.alibaba.fastjson.JSON;
import com.idianyou.lifecircle.constants.RedisConstants;
import com.idianyou.lifecircle.constants.RedisKeys;
import com.idianyou.lifecircle.dto.GetChannelDataContext;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.dto.vo.ChannelDataVO;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.service.local.FactoryService;
import com.idianyou.lifecircle.service.local.redis.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 获取频道数据
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/27 15:56
 */
@Slf4j
public abstract class AbstractGetChannelDataService {

    @Autowired
    protected RedisTemplate redisTemplate;

    @Autowired
    protected FactoryService factoryService;

    @Autowired
    protected RedisCacheService redisCacheService;

    public abstract boolean supports(LifeCircleChannelEnum channelEnum);

    public abstract List<ChannelDataGroupVO> getData(GetChannelDataContext context);

    public List<ChannelDataGroupVO> getChannelData(GetChannelDataContext context) {
        if (context == null
                || context.getUserId() == null
                || context.getReqNo() == null
                || context.getChannelEnum() == null) {
            log.error("参数错误，无法获取频道数据，context={}", JSON.toJSONString(context));
            return Collections.emptyList();
        }

        String taskNo = UUID.randomUUID().toString();
        String baseLog = "获取频道[" + context.getChannelEnum().getChannelName() + "]数据(userId=" + context.getUserId() + ", reqNo=" + context.getReqNo() + ", taskNo=" + taskNo + ")";
        context.setBaseLog(baseLog);

        log.info("{}开始", context.getBaseLog());

        StopWatch stopWatch = new StopWatch();

        List<ChannelDataGroupVO> dataGroupVOList = null;
        Set<String> passedIdSet = context.getPassedDataIdSet();

        try {
            stopWatch.start("beforeQuery");

            String requestNo = context.getUserId() + "_" + context.getReqNo();
            String channelRequestNo = requestNo + "_" + context.getChannelEnum().getChannelId();

            // 记录当前批次号最后活跃时间
            redisTemplate.boundValueOps(RedisKeys.REQUESTNO_RELATED_LAST_ACTIVE_TIME.concat(requestNo)).set(System.currentTimeMillis() + "", 60, TimeUnit.MINUTES);

            // 当前批次号频道已查询了哪些数据
            Set<String> reqestNoContentIdsSet = redisTemplate.boundSetOps(RedisKeys.REQUESTNO_RELATED_CONTENTIDS_SET.concat(channelRequestNo)).members();
            if (reqestNoContentIdsSet == null) {
                reqestNoContentIdsSet = new HashSet<>();
            }

            if (CollectionUtils.isNotEmpty(reqestNoContentIdsSet)) {
                context.getExcludeDataIdSet().addAll(reqestNoContentIdsSet);
            }

            // 当前批次号频道已查询了哪些主题
            String categorySortNo = redisCacheService.getCurrentCategorySortNo();
            String reqestNoCategorySetKey = RedisKeys.REQUESTNO_RELATED_CATEGORY_SET.concat(channelRequestNo).concat("_").concat(categorySortNo);

            Set<String> reqestNoCategorySet = redisTemplate.boundSetOps(reqestNoCategorySetKey).members();
            if (reqestNoCategorySet == null) {
                reqestNoCategorySet = new HashSet<>();
            }

            if (CollectionUtils.isNotEmpty(reqestNoCategorySet)) {
                context.getExcludeCategorySet().addAll(reqestNoCategorySet);
            }

            stopWatch.stop();
            log.info("{} 查询数据前操作, 耗时{}ms", context.getBaseLog(), stopWatch.getLastTaskInfo().getTimeMillis());
            stopWatch.start("query");

            dataGroupVOList = getData(context);

            stopWatch.stop();
            log.info("{} 查询数据操作, 耗时{}ms", context.getBaseLog(), stopWatch.getLastTaskInfo().getTimeMillis());
            stopWatch.start("afterQuery");

            // 记录已查询过的数据
            if (CollectionUtils.isNotEmpty(dataGroupVOList)) {
                for (ChannelDataGroupVO groupVO : dataGroupVOList) {
                    if (CollectionUtils.isEmpty(groupVO.getDataList())) {
                        continue;
                    }

                    for (ChannelDataVO channelDataVO : groupVO.getDataList()) {
                        passedIdSet.add(channelDataVO.getId().toString());
                    }
                }
            }

            if (passedIdSet.size() > 0) {
                for (String contentId : passedIdSet) {
                    List<String> requestNoList = null;

                    String requestNoListStr = (String) redisTemplate.boundValueOps(RedisKeys.CONTENTID_RELATED_REQUESTNOS.concat(contentId.toString())).get();
                    if (StringUtils.isNotBlank(requestNoListStr)) {
                        requestNoList = JSON.parseArray(requestNoListStr, String.class);
                    }

                    if (requestNoList == null) {
                        requestNoList = new ArrayList<>();
                    }

                    if (!requestNoList.contains(requestNo)) {
                        requestNoList.add(requestNo);

                        // 动态ID关联查询批次号
                        redisTemplate.boundValueOps(RedisKeys.CONTENTID_RELATED_REQUESTNOS.concat(contentId.toString())).set(JSON.toJSONString(requestNoList), RedisConstants.CACHE_DAYS, TimeUnit.DAYS);
                    }

                    // 保存到频道已查询
                    redisTemplate.boundSetOps(RedisKeys.REQUESTNO_RELATED_CONTENTIDS_SET.concat(channelRequestNo)).add(contentId.toString());
                    redisTemplate.boundSetOps(RedisKeys.REQUESTNO_RELATED_CONTENTIDS_SET.concat(channelRequestNo)).expire(RedisConstants.CACHE_DAYS, TimeUnit.DAYS);
                }

                // 记录当前用户的查询批次号
                redisTemplate.boundValueOps(RedisKeys.USERID_RELATED_REQUESTNO.concat(context.getUserId().toString())).set(requestNo, RedisConstants.CACHE_DAYS, TimeUnit.DAYS);
            }

            // 记录已查询过的主题
            if (context.getExcludeCategorySet().size() > 0) {
                if (CollectionUtils.isNotEmpty(reqestNoCategorySet)) {
                    context.getExcludeCategorySet().removeAll(reqestNoCategorySet);
                }

                for (String categoryNo : context.getExcludeCategorySet()) {
                    redisTemplate.boundSetOps(reqestNoCategorySetKey).add(categoryNo);
                }
                redisTemplate.boundSetOps(reqestNoCategorySetKey).expire(1, TimeUnit.HOURS);
            }

            // 全部主题已查完，删除以便重头开始
            if (CollectionUtils.isNotEmpty(context.getNeedDelQueryedCategoryList())) {
                redisTemplate.boundSetOps(reqestNoCategorySetKey).remove(context.getNeedDelQueryedCategoryList().toArray());
            }

            stopWatch.stop();
            log.info("{} 查询数据后操作, 耗时{}ms", context.getBaseLog(), stopWatch.getLastTaskInfo().getTimeMillis());
        } catch (Exception e) {
            log.error("{}失败，context={}", context.getBaseLog(), JSON.toJSONString(context), e);
        }

        if (dataGroupVOList == null) {
            dataGroupVOList = Collections.emptyList();
        }

        if (stopWatch.currentTaskName() != null) {
            stopWatch.stop();
        }

        log.info("{}结束，共{}条，耗时{}ms",
                context.getBaseLog(),
                dataGroupVOList.size(),
                stopWatch.getTotalTimeMillis());

        return dataGroupVOList;
    }
}