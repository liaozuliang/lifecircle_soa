package com.idianyou.lifecircle.mongo;

import com.google.common.collect.Lists;
import com.idianyou.lifecircle.dto.GetUserDataDTO;
import com.idianyou.lifecircle.dto.QueryTypeDataParam;
import com.idianyou.lifecircle.entity.mongo.LifeCircleContentMongo;
import com.idianyou.lifecircle.enums.ContentTypeEnum;
import com.idianyou.lifecircle.enums.ServiceStatusEnum;
import com.idianyou.lifecircle.enums.ShowTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/27 20:05
 */
@Repository
public class LifeCircleContentRepository extends BaseRepository<LifeCircleContentMongo> {

    public static final String TABLE_NAME = "life_circle_content";

    @Override
    public LifeCircleContentMongo save(LifeCircleContentMongo lifeCircleContentMongo) {
        return mongoTemplate.save(lifeCircleContentMongo, TABLE_NAME);
    }

    @Override
    public LifeCircleContentMongo getById(Object id) {
        return mongoTemplate.findById(id, LifeCircleContentMongo.class, TABLE_NAME);
    }

    public LifeCircleContentMongo getEffectiveById(Long id) {
        if (id == null) {
            return null;
        }

        Query query = Query.query(Criteria.where("_id").is(id));
        query.addCriteria(Criteria.where("serviceStatus").is(ServiceStatusEnum.RUNNING.getStatus()));

        return mongoTemplate.findOne(query, LifeCircleContentMongo.class, TABLE_NAME);
    }

    public List<LifeCircleContentMongo> getByServiceDataId(String serviceDataId) {
        return mongoTemplate.find(Query.query(Criteria.where("serviceDataId").is(serviceDataId)), LifeCircleContentMongo.class, TABLE_NAME);
    }

    public void updateById(LifeCircleContentMongo mongo) {
        if (mongo.get_id() == null) {
            return;
        }

        Query query = Query.query(Criteria.where("_id").is(mongo.get_id()));

        Update update = new Update();
        update.set("updateTime", new Date());

        if (mongo.getServiceStatus() != null) {
            update.set("serviceStatus", mongo.getServiceStatus());
        }

        if (StringUtils.isNotBlank(mongo.getCategoryName())) {
            update.set("categoryName", mongo.getCategoryName());
        }

        if (StringUtils.isNotBlank(mongo.getCategoryId())) {
            update.set("categoryId", mongo.getCategoryId());
        }

        mongoTemplate.updateFirst(query, update, TABLE_NAME);
    }

    public void updateByBizParamDataId(LifeCircleContentMongo mongo, String bizParamDataId) {
        if (StringUtils.isBlank(bizParamDataId) || mongo == null) {
            return;
        }

        Query query = Query.query(Criteria.where("bizParamDataId").is(bizParamDataId));

        Update update = new Update();
        update.set("updateTime", new Date());

        if (mongo.getServiceStatus() != null) {
            update.set("serviceStatus", mongo.getServiceStatus());
        }

        mongoTemplate.updateMulti(query, update, TABLE_NAME);
    }

    public List<LifeCircleContentMongo> getChannelData(Date maxCreateTime, int dataNum, Integer channelId) {
        Query query = Query.query(Criteria.where("createTime").lt(maxCreateTime));

        query.addCriteria(Criteria.where("serviceStatus").is(ServiceStatusEnum.RUNNING.getStatus()));

        if (channelId != null) {
            query.addCriteria(Criteria.where("channelIdList").is(channelId));
        }

        query.with(Sort.by(Sort.Direction.DESC, "createTime"));
        query.limit(dataNum);

        return mongoTemplate.find(query, LifeCircleContentMongo.class, TABLE_NAME);
    }

    public List<LifeCircleContentMongo> getUserHistoryData(GetUserDataDTO dto, List<Integer> serviceShowTypeList) {
        if (dto == null || dto.getUserId() == null) {
            return Collections.emptyList();
        }

        Query query = Query.query(Criteria.where("userId").is(dto.getUserId()));

        if (dto.getLastId() != null) {
            query.addCriteria(Criteria.where("_id").lt(dto.getLastId()));
        }

        query.addCriteria(Criteria.where("serviceStatus").is(ServiceStatusEnum.RUNNING.getStatus()));

        Criteria personalCriteria = Criteria.where("contentType").is(ContentTypeEnum.PERSONAL.getType());
        if (CollectionUtils.isEmpty(serviceShowTypeList)) {
            query.addCriteria(personalCriteria);
        } else {
            Criteria serviceCriteria = Criteria.where("contentType").is(ContentTypeEnum.SERVICE.getType())
                    .and("showType").in(serviceShowTypeList);
            query.addCriteria(new Criteria().orOperator(personalCriteria, serviceCriteria));
        }

        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        query.limit(dto.getPageSize());

        return mongoTemplate.find(query, LifeCircleContentMongo.class, TABLE_NAME);
    }

    public List<LifeCircleContentMongo> getUserNewData(GetUserDataDTO dto, List<Integer> serviceShowTypeList) {
        if (dto == null || dto.getUserId() == null) {
            return Collections.emptyList();
        }

        Query query = Query.query(Criteria.where("userId").is(dto.getUserId()));

        if (dto.getFirstId() != null) {
            query.addCriteria(Criteria.where("_id").gt(dto.getFirstId()));
        }

        query.addCriteria(Criteria.where("serviceStatus").is(ServiceStatusEnum.RUNNING.getStatus()));

        Criteria personalCriteria = Criteria.where("contentType").is(ContentTypeEnum.PERSONAL.getType());
        if (CollectionUtils.isEmpty(serviceShowTypeList)) {
            query.addCriteria(personalCriteria);
        } else {
            Criteria serviceCriteria = Criteria.where("contentType").is(ContentTypeEnum.SERVICE.getType())
                    .and("showType").in(serviceShowTypeList);
            query.addCriteria(new Criteria().orOperator(personalCriteria, serviceCriteria));
        }

        query.with(Sort.by(Sort.Direction.ASC, "_id"));
        query.limit(dto.getPageSize());

        List<LifeCircleContentMongo> dataList = mongoTemplate.find(query, LifeCircleContentMongo.class, TABLE_NAME);
        if (CollectionUtils.isNotEmpty(dataList)) {
            LifeCircleContentMongo[] arr = dataList.toArray(new LifeCircleContentMongo[]{});
            CollectionUtils.reverseArray(arr);
            return Arrays.asList(arr);
        }

        return Collections.emptyList();
    }

    public LifeCircleContentMongo getUserLastOne(GetUserDataDTO dto, List<Integer> serviceShowTypeList) {
        if (dto == null || dto.getUserId() == null) {
            return null;
        }

        Query query = Query.query(Criteria.where("userId").is(dto.getUserId()));

        query.addCriteria(Criteria.where("serviceStatus").is(ServiceStatusEnum.RUNNING.getStatus()));

        Criteria personalCriteria = Criteria.where("contentType").is(ContentTypeEnum.PERSONAL.getType());
        if (CollectionUtils.isEmpty(serviceShowTypeList)) {
            query.addCriteria(personalCriteria);
        } else {
            Criteria serviceCriteria = Criteria.where("contentType").is(ContentTypeEnum.SERVICE.getType())
                    .and("showType").in(serviceShowTypeList);
            query.addCriteria(new Criteria().orOperator(personalCriteria, serviceCriteria));
        }

        query.with(Sort.by(Sort.Direction.ASC, "_id"));
        query.limit(1);

        return mongoTemplate.findOne(query, LifeCircleContentMongo.class, TABLE_NAME);
    }

    public List<LifeCircleContentMongo> getTypeData(QueryTypeDataParam param) {
        if (param == null) {
            return Collections.emptyList();
        }

        Query query = new Query();

        if (param.getDataType() != null) {
            query.addCriteria(Criteria.where("dataType").is(param.getDataType()));
        }

        if (param.getShowType() != null) {
            query.addCriteria(Criteria.where("showType").is(param.getShowType()));
        }

        if (StringUtils.isNotBlank(param.getClientId())) {
            query.addCriteria(Criteria.where("fromServiceInfo.clientId").is(param.getClientId()));
        }

        if (StringUtils.isNotBlank(param.getServiceName())) {
            query.addCriteria(Criteria.where("fromServiceInfo.name").is(param.getServiceName()));
        }

        if (StringUtils.isNotBlank(param.getNotServiceName())) {
            query.addCriteria(Criteria.where("fromServiceInfo.name").ne(param.getNotServiceName()));
        }

        if (StringUtils.isNotBlank(param.getCategoryId())) {
            query.addCriteria(Criteria.where("categoryId").is(param.getCategoryId()));
        }

        if (CollectionUtils.isNotEmpty(param.getCategoryIdList())) {
            query.addCriteria(Criteria.where("categoryId").in(param.getCategoryIdList()));
        }

        if (StringUtils.isNotBlank(param.getCategoryName())) {
            query.addCriteria(Criteria.where("categoryName").is(param.getCategoryName()));
        }

        if (CollectionUtils.isNotEmpty(param.getCategoryNameList())) {
            query.addCriteria(Criteria.where("categoryName").in(param.getCategoryNameList()));
        }

        if (!param.isIgnoreServiceStatus()) {
            query.addCriteria(Criteria.where("serviceStatus").is(ServiceStatusEnum.RUNNING.getStatus()));
        } else {
            query.addCriteria(Criteria.where("serviceStatus").ne(ServiceStatusEnum.DELETED.getStatus()));
        }

        if (CollectionUtils.isNotEmpty(param.getChannelIdList())) {
            query.addCriteria(Criteria.where("channelIdList").in(param.getChannelIdList()));
        }

        if (param.getMaxId() != null) {
            query.addCriteria(Criteria.where("_id").lt(param.getMaxId()));
        }

        if (CollectionUtils.isNotEmpty(param.getIdList())) {
            query.addCriteria(Criteria.where("_id").in(param.getIdList()));
        }

        if (param.getMinCreateTime() != null) {
            query.addCriteria(Criteria.where("createTime").gt(param.getMinCreateTime()));
        }

        if (param.getMaxCreateTime() != null) {
            query.addCriteria(Criteria.where("createTime").lt(param.getMaxCreateTime()));
        }

        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        query.limit(param.getPageSize());

        return mongoTemplate.find(query, LifeCircleContentMongo.class, TABLE_NAME);
    }

    public List<LifeCircleContentMongo> getRunningServiceData(long beginId, int pageSize, Date maxUpdateTime) {
        Query query = Query.query(Criteria.where("_id").gt(beginId));
        query.addCriteria(Criteria.where("updateTime").lt(maxUpdateTime));

        query.addCriteria(Criteria.where("contentType").is(ContentTypeEnum.SERVICE.getType()));
        query.addCriteria(Criteria.where("serviceStatus").is(ServiceStatusEnum.RUNNING.getStatus()));

        List<Integer> excludeShowTypeList = new ArrayList<>();
        //excludeShowTypeList.add(ShowTypeEnum.WONDERFUL_MOMENTS_SERVICE_DYNAMIC.getType());
        excludeShowTypeList.add(ShowTypeEnum.MARKET_COMMODITY.getType());
        excludeShowTypeList.add(ShowTypeEnum.CHIGUA_MALL_SERVICE_DYNAMIC.getType());
        query.addCriteria(Criteria.where("showType").nin(excludeShowTypeList));

        query.with(Sort.by(Sort.Direction.ASC, "_id"));
        query.limit(pageSize);

        return mongoTemplate.find(query, LifeCircleContentMongo.class, TABLE_NAME);
    }

    public void addPraiseCount(long id, int addCount) {
        Query query = Query.query(Criteria.where("_id").is(id));

        Update update = new Update();
        update.inc("praiseCount", addCount);

        mongoTemplate.updateFirst(query, update, TABLE_NAME);
    }

    public void addCommentCount(long id, int addCount) {
        Query query = Query.query(Criteria.where("_id").is(id));

        Update update = new Update();
        update.inc("commentCount", addCount);

        mongoTemplate.updateFirst(query, update, TABLE_NAME);
    }

    public List<LifeCircleContentMongo> getPageData(Long minId, int pageSize) {
        Query query = Query.query(Criteria.where("_id").gt(minId));

        query.with(Sort.by(Sort.Direction.ASC, "_id"));
        query.limit(pageSize);

        return mongoTemplate.find(query, LifeCircleContentMongo.class, TABLE_NAME);
    }

    public List<LifeCircleContentMongo> getByIds(List<Long> idsList) {
        if (CollectionUtils.isEmpty(idsList)) {
            return Lists.newArrayList();
        }
        Query query = Query.query(Criteria.where("_id").in(idsList));
        return mongoTemplate.find(query, LifeCircleContentMongo.class, TABLE_NAME);
    }

    public List<LifeCircleContentMongo> getByIdsWithSort(List<Long> idsList, QueryTypeDataParam param) {
        List<LifeCircleContentMongo> contentMongoList = getByIds(idsList);
        if (CollectionUtils.isEmpty(contentMongoList)) {
            return Lists.newArrayList();
        }

        List<LifeCircleContentMongo> resultList = new ArrayList<>(idsList.size());
        Map<Long, LifeCircleContentMongo> id_contentMongo_map = new HashMap<>(contentMongoList.size());

        for (LifeCircleContentMongo contentMongo : contentMongoList) {
            if (param != null) {
                if (!param.isIgnoreServiceStatus()) {
                    if (!Objects.equals(ServiceStatusEnum.RUNNING.getStatus(), contentMongo.getServiceStatus())) {
                        continue;
                    }
                } else {
                    if (Objects.equals(ServiceStatusEnum.DELETED.getStatus(), contentMongo.getServiceStatus())) {
                        continue;
                    }
                }
            }

            id_contentMongo_map.put(contentMongo.get_id(), contentMongo);
        }

        for (Long id : idsList) {
            if (id_contentMongo_map.containsKey(id)) {
                resultList.add(id_contentMongo_map.get(id));
            }
        }

        return resultList;
    }
}