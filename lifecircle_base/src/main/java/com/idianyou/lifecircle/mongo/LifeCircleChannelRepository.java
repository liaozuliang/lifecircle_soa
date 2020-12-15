package com.idianyou.lifecircle.mongo;

import com.idianyou.lifecircle.entity.mongo.LifeCircleChannelMongo;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Description: 生活圈-频道
 * @version: v1.0.0
 * @author: xxj
 * @date: 2020-04-27 16:34
 */
@Repository
public class LifeCircleChannelRepository extends BaseRepository<LifeCircleChannelMongo> {

    private static final Logger logger = LoggerFactory.getLogger(LifeCircleChannelRepository.class);

    public static final String REPOSITORY_NAME = "life_circle_channel";

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @param mongo
     * @desc 保存频道
     * @author xxj
     **/
    @Override
    public LifeCircleChannelMongo save(LifeCircleChannelMongo mongo) {
        return mongoTemplate.save(mongo,REPOSITORY_NAME);
    }

    @Override
    public LifeCircleChannelMongo getById(Object _id) {
        Query query = new Query(Criteria.where("_id").is(_id));
        query.addCriteria(Criteria.where("currType").is(1));
        return mongoTemplate.findOne(query, LifeCircleChannelMongo.class, REPOSITORY_NAME);
    }

    /**
     * @param mongos
     * @desc 批量保存频道
     * @author xxj
     **/
    public BulkWriteResult bulkSave(List mongos) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,REPOSITORY_NAME);
        bulkOperations.insert(mongos);
        return bulkOperations.execute();
    }

    /**
     * @param
     * @desc 查询全部频道
     * @author xxj
     **/
    public List<LifeCircleChannelMongo> query() {
        Query query = new Query(Criteria.where("currType").is(1));
        query.with(new Sort(Sort.Direction.ASC, "sortNum"));
        return mongoTemplate.find(query, LifeCircleChannelMongo.class, REPOSITORY_NAME);
    }

    /**
     * @param _ids
     * @desc 根据 _ids 查询频道
     * @author xxj
     **/
    public List<LifeCircleChannelMongo> query(List<Integer> _ids) {
        Query query = new Query(Criteria.where("_id").in(_ids));
        query.addCriteria(Criteria.where("currType").is(1));
        query.with(new Sort(Sort.Direction.ASC, "sortNum"));
        return mongoTemplate.find(query, LifeCircleChannelMongo.class, REPOSITORY_NAME);
    }

    /**
     * @param _id
     * @desc 根据 _id 查询频道
     * @author xxj
     **/
    public LifeCircleChannelMongo queryOne(Integer _id) {
        Query query = new Query(Criteria.where("_id").is(_id));
        query.addCriteria(Criteria.where("currType").is(1));
        return mongoTemplate.findOne(query, LifeCircleChannelMongo.class, REPOSITORY_NAME);
    }

    /**
     * @param channelName
     * @desc 根据 channelName 查询频道
     * @author xxj
     **/
    public List<LifeCircleChannelMongo> query(String channelName) {
        Query query = new Query(Criteria.where("channelName").is(channelName));
        query.addCriteria(Criteria.where("currType").is(1));
        return mongoTemplate.find(query,LifeCircleChannelMongo.class,REPOSITORY_NAME);
    }

    /**
     * @param channelNames
     * @desc 根据 channelNames 查询频道
     * @author xxj
     **/
    public List<LifeCircleChannelMongo> queryByChannelNames(List<String> channelNames) {
        Query query = new Query(Criteria.where("channelName").in(channelNames));
        query.addCriteria(Criteria.where("currType").is(1));
        query.with(new Sort(Sort.Direction.ASC, "sortNum"));
        return mongoTemplate.find(query, LifeCircleChannelMongo.class, REPOSITORY_NAME);
    }

    /**
     * @param
     * @desc 获取最新添加的频道信息
     * @author xxj
     **/
    public LifeCircleChannelMongo getTheLatestChannelId() {
        Query query = new Query(Criteria.where("currType").is(1));
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        return mongoTemplate.findOne(query, LifeCircleChannelMongo.class, REPOSITORY_NAME);
    }

    /**
     * @param _id
     * @desc 根据 _id 删除频道
     * @author xxj
     **/
    public UpdateResult delete(Integer _id){

        Update update = new Update();
        update.set("currType", 1);
        update.set("updateTime",new Date());
        Query query = new Query(Criteria.where("_id").is(_id));

        return  mongoTemplate.updateFirst(query,update,REPOSITORY_NAME);
    }

    /**
     * @param mongo
     * @desc 更新频道
     * @author xxj
     **/
    public UpdateResult update(LifeCircleChannelMongo mongo){
        Update update = new Update();

        if(StringUtils.isNotBlank(mongo.getChannelName())){
            update.set("channelName", mongo.getChannelName());
        }
        if(mongo.getShowType() > 0){
            update.set("showType", mongo.getShowType());
        }
        if(mongo.getSortNum() > 0){
            update.set("sortNum", mongo.getSortNum());
        }
        if(StringUtils.isNotBlank(mongo.getRemark())){
            update.set("remark", mongo.getRemark());
        }

        update.set("updateTime", new Date());
        Query query = new Query(Criteria.where("_id").is(mongo.get_id()));
        update.set("operUserId", mongo.getOperUserId());
        update.set("operUserName", mongo.getOperUserName());

        return  mongoTemplate.updateFirst(query,update,REPOSITORY_NAME);
    }
}
