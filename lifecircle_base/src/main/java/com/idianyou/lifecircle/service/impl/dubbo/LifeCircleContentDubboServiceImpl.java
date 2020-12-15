package com.idianyou.lifecircle.service.impl.dubbo;

import com.idianyou.lifecircle.entity.mongo.LifeCircleContentMongo;
import com.idianyou.lifecircle.entity.mongo.LifeCircleServiceBizParamMongo;
import com.idianyou.lifecircle.mongo.LifeCircleContentRepository;
import com.idianyou.lifecircle.mongo.LifeCircleServiceBizParamRepository;
import com.idianyou.lifecircle.service.LifeCircleContentDubboService;
import com.idianyou.lifecircle.service.local.RecommendedPersonalDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/8 14:19
 */
@Slf4j
@Service
public class LifeCircleContentDubboServiceImpl implements LifeCircleContentDubboService {

    @Autowired
    private LifeCircleContentRepository lifeCircleContentRepository;

    @Autowired
    private LifeCircleServiceBizParamRepository lifeCircleServiceBizParamRepository;

    @Autowired
    private RecommendedPersonalDataService recommendedPersonalDataService;

    @Override
    public LifeCircleContentMongo getById(long id) {
        return lifeCircleContentRepository.getEffectiveById(id);
    }

    @Override
    public LifeCircleServiceBizParamMongo getByContentId(long contentId) {
        LifeCircleContentMongo contentMongo = lifeCircleContentRepository.getEffectiveById(contentId);
        if (contentMongo != null && StringUtils.isNotBlank(contentMongo.getBizParamDataId())) {
            return lifeCircleServiceBizParamRepository.getById(contentMongo.getBizParamDataId());
        }
        return null;
    }

    @Override
    public void addPraiseCount(long id, int addCount) {
        if (addCount == 0) {
            return;
        }
        lifeCircleContentRepository.addPraiseCount(id, addCount);
    }

    @Override
    public void addCommentCount(long id, int addCount) {
        if (addCount == 0) {
            return;
        }
        lifeCircleContentRepository.addCommentCount(id, addCount);
    }

    @Override
    public List<Long> getRecommendedPersonalData(Long circleContentId, int dataNum) {
        return recommendedPersonalDataService.getRecommendedPersonalData(circleContentId, dataNum);
    }
}
