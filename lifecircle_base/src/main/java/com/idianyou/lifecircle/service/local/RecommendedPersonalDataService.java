package com.idianyou.lifecircle.service.local;

import com.alibaba.fastjson.JSON;
import com.idianyou.circle.dto.LifeCircleServiceBizParamDTO;
import com.idianyou.lifecircle.constants.RedisKeys;
import com.idianyou.lifecircle.dto.QueryTypeDataParam;
import com.idianyou.lifecircle.entity.mongo.LifeCircleContentMongo;
import com.idianyou.lifecircle.entity.mongo.LifeCircleServiceBizParamMongo;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.es.LifeCircleContentESService;
import com.idianyou.lifecircle.mongo.LifeCircleContentRepository;
import com.idianyou.lifecircle.mongo.LifeCircleServiceBizParamRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 个人动态推荐数据
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/11/25 19:22
 */
@Slf4j
@Service
public class RecommendedPersonalDataService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LifeCircleContentRepository lifeCircleContentRepository;

    @Autowired
    private LifeCircleContentESService lifeCircleContentESService;

    @Autowired
    private LifeCircleServiceBizParamRepository lifeCircleServiceBizParamRepository;

    public List<Long> getRecommendedPersonalData(Long circleContentId, int dataNum) {
        List<Long> circleContentIdList = new ArrayList<>();

        if (circleContentId == null || dataNum <= 0) {
            return circleContentIdList;
        }

        List<String> recommendedIdList = null;
        try {
            recommendedIdList = redisTemplate.boundListOps(RedisKeys.PERSONAL_DATA_RECOMMENDED_LIST).range(0, -1);
        } catch (Exception e) {
            log.error("从redis获取个人动态推荐数据出错：", e);
            return circleContentIdList;
        }

        if (CollectionUtils.isEmpty(recommendedIdList)) {
            recommendedIdList = loadDataToRedis();
        }

        if (CollectionUtils.isEmpty(recommendedIdList)) {
            return circleContentIdList;
        }

        // 随机打乱
        Collections.shuffle(recommendedIdList);

        for (String id : recommendedIdList) {
            if (circleContentIdList.size() < dataNum
                    && !Objects.equals(id, circleContentId.toString())
                    && !circleContentIdList.contains(id)) {
                circleContentIdList.add(Long.valueOf(id));
            }
        }

        return circleContentIdList;
    }

    private List<String> loadDataToRedis() {
        List<String> recommendedIdList = new ArrayList<>();

        QueryTypeDataParam param = new QueryTypeDataParam();
        param.setDataType(DataTypeEnum.FRIEND_STYLE_DATA.getType());
        param.setPageSize(1000);
        param.setIgnoreServiceStatus(false);

        List<Long> circleContentIdList = null;
        LifeCircleContentMongo contentMongo = null;
        LifeCircleServiceBizParamMongo bizParamMongo = null;
        LifeCircleServiceBizParamDTO bizParamDTO = null;

        int maxSize = 1000;
        boolean doQuery = true;

        do {
            circleContentIdList = lifeCircleContentESService.getTypeDataIds(param);
            if (CollectionUtils.isEmpty(circleContentIdList)) {
                doQuery = false;
                break;
            }

            for (Long circleContentId : circleContentIdList) {
                param.setMaxId(circleContentId);

                if (recommendedIdList.size() >= maxSize) {
                    doQuery = false;
                    break;
                }

                contentMongo = lifeCircleContentRepository.getById(circleContentId);
                bizParamMongo = lifeCircleServiceBizParamRepository.getById(contentMongo.getBizParamDataId());
                bizParamDTO = JSON.parseObject(bizParamMongo.getServiceBizParam()).toJavaObject(LifeCircleServiceBizParamDTO.class);

                // 朋友圈图文
                if (bizParamDTO.getSubShowType() == 501 && !recommendedIdList.contains(circleContentId.toString())) {
                    recommendedIdList.add(circleContentId.toString());
                }
            }
        } while (doQuery);

        try {
            redisTemplate.delete(RedisKeys.PERSONAL_DATA_RECOMMENDED_LIST);
            redisTemplate.boundListOps(RedisKeys.PERSONAL_DATA_RECOMMENDED_LIST).leftPushAll(recommendedIdList.toArray(new String[]{}));
            redisTemplate.expire(RedisKeys.PERSONAL_DATA_RECOMMENDED_LIST, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("个人动态推荐数据写入redis出错：", e);
        }

        return recommendedIdList;
    }
}
