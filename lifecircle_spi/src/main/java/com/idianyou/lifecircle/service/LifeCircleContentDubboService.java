package com.idianyou.lifecircle.service;

import com.idianyou.lifecircle.entity.mongo.LifeCircleContentMongo;
import com.idianyou.lifecircle.entity.mongo.LifeCircleServiceBizParamMongo;

import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/8 14:13
 */
public interface LifeCircleContentDubboService {

    /**
     * 通过ID获取动态内容
     * @param id
     * @return
     */
    LifeCircleContentMongo getById(long id);

    /**
     * 通过动态ID获取业务参数
     * @param contentId
     * @return
     */
    LifeCircleServiceBizParamMongo getByContentId(long contentId);

    /**
     * 追加更新点赞数
     * @param id
     * @param addCount
     */
    void addPraiseCount(long id, int addCount);

    /**
     * 追加更新评论数
     * @param id
     * @param addCount
     */
    void addCommentCount(long id, int addCount);

    /**
     * 获取个人动态推荐数据
     * @param circleContentId
     * @param dataNum
     * @return
     */
    List<Long> getRecommendedPersonalData(Long circleContentId, int dataNum);
}
