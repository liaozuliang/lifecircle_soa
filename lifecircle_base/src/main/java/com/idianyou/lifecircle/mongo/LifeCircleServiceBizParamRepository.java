package com.idianyou.lifecircle.mongo;

import com.idianyou.lifecircle.entity.mongo.LifeCircleServiceBizParamMongo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/27 20:05
 */
@Repository
public class LifeCircleServiceBizParamRepository extends BaseRepository<LifeCircleServiceBizParamMongo> {

    public static final String TABLE_NAME = "life_circle_service_biz_param";

    @Override
    public LifeCircleServiceBizParamMongo save(LifeCircleServiceBizParamMongo mongo) {
        return mongoTemplate.save(mongo, TABLE_NAME);
    }

    @Override
    public LifeCircleServiceBizParamMongo getById(Object id) {
        return mongoTemplate.findById(id, LifeCircleServiceBizParamMongo.class, TABLE_NAME);
    }

    public void updateById(LifeCircleServiceBizParamMongo mongo) {
        if (StringUtils.isBlank(mongo.get_id())) {
            return;
        }

        Query query = Query.query(Criteria.where("_id").is(mongo.get_id()));

        Update update = new Update();
        update.set("updateTime", new Date());

        if (mongo.getServiceStatus() != null) {
            update.set("serviceStatus", mongo.getServiceStatus());
        }

        if (StringUtils.isNotBlank(mongo.getServiceBizParam())) {
            update.set("serviceBizParam", mongo.getServiceBizParam());
        }

        mongoTemplate.updateFirst(query, update, TABLE_NAME);
    }

}