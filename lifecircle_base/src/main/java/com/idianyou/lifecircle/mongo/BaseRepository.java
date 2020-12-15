package com.idianyou.lifecircle.mongo;

import com.idianyou.lifecircle.entity.mongo.BaseMongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/27 20:05
 */
public abstract class BaseRepository<T extends BaseMongo> {

    @Autowired
    protected MongoTemplate mongoTemplate;

    public abstract T save(T t);

    public abstract T getById(Object id);
}
