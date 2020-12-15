package com.idianyou.lifecircle.mongo;

import com.idianyou.lifecircle.entity.mongo.LifeCircleChannelServicePortalMongo;
import com.idianyou.lifecircle.enums.YesNoEnum;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/27 20:05
 */
@Repository
public class LifeCircleChannelServicePortalRepository extends BaseRepository<LifeCircleChannelServicePortalMongo> {

    public static final String TABLE_NAME = "life_circle_channel_service_portal";

    @Override
    public LifeCircleChannelServicePortalMongo save(LifeCircleChannelServicePortalMongo mongo) {
        return mongoTemplate.save(mongo, TABLE_NAME);
    }

    @Override
    public LifeCircleChannelServicePortalMongo getById(Object id) {
        return mongoTemplate.findById(id, LifeCircleChannelServicePortalMongo.class, TABLE_NAME);
    }

    public List<LifeCircleChannelServicePortalMongo> getByChannelId(int channelId) {
        Query query = new Query(Criteria.where("isDelete").is(YesNoEnum.NO.getCode()));
        query.addCriteria(Criteria.where("channelId").is(channelId));
        query.with(Sort.by(Sort.Order.asc("rowNo"), Sort.Order.asc("columnNo")));
        return mongoTemplate.find(query, LifeCircleChannelServicePortalMongo.class, TABLE_NAME);
    }
}