package com.idianyou.lifecircle.es;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idianyou.lifecircle.dto.QueryTypeDataParam;
import com.idianyou.lifecircle.dto.SearchDTO;
import com.idianyou.lifecircle.entity.mongo.LifeCircleContentMongo;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.enums.ServiceStatusEnum;
import com.idianyou.lifecircle.enums.ShowTypeEnum;
import com.idianyou.lifecircle.mongo.LifeCircleContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/2 14:42
 */
@Slf4j
@Service
public class LifeCircleContentESService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private LifeCircleContentESRepository lifeCircleContentESRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LifeCircleContentRepository lifeCircleContentRepository;

    public void add(LifeCircleContentES contentES) {
        try {
            lifeCircleContentESRepository.save(contentES);
        } catch (Exception e) {
            log.error("新增ES数据出错: contentId={}", contentES.getId(), e);
        }
    }

    public void update(LifeCircleContentES contentES) {
        if (contentES == null || contentES.getId() == null) {
            return;
        }

        UpdateRequest updateRequest = new UpdateRequest();
        boolean updField = false;
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder().startObject();

            if (contentES.getServiceStatus() != null) {
                builder.field("serviceStatus", contentES.getServiceStatus());
                updField = true;
            }

            if (contentES.getCreateTime() != null) {
                builder.field("createTime", contentES.getCreateTime());
                updField = true;
            }

            if (updField) {
                builder.field("updateTime", new Date());
            } else {
                return;
            }

            builder.endObject();

            updateRequest.doc(builder);

            // 解决并发更新失败问题
            updateRequest.retryOnConflict(5);

            UpdateQuery updateQuery = new UpdateQuery();
            updateQuery.setId(contentES.getId().toString());
            updateQuery.setClazz(LifeCircleContentES.class);
            updateQuery.setUpdateRequest(updateRequest);

            elasticsearchTemplate.update(updateQuery);
        } catch (Exception e) {
            log.error("更新ES数据出错: contentId={}", contentES.getId(), e);
        }
    }

    public void updateServiceStatusByBizParamDataId(String bizParamDataId, ServiceStatusEnum serviceStatusEnum) {
        if (serviceStatusEnum == null || StringUtils.isBlank(bizParamDataId)) {
            return;
        }

        List<Long> idList = getIdByBizParamDataId(bizParamDataId);

        LifeCircleContentES contentES = null;
        for (Long id : idList) {
            contentES = new LifeCircleContentES();
            contentES.setId(id);
            contentES.setServiceStatus(serviceStatusEnum.getStatus());
            update(contentES);
        }
    }

    public LifeCircleContentES getById(Long id) {
        return lifeCircleContentESRepository.findById(id).get();
    }

    public List<Long> getIdByBizParamDataId(String bizParamDataId) {
        List<Long> idList = new ArrayList<>();

        if (StringUtils.isBlank(bizParamDataId)) {
            return idList;
        }

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("bizParamDataId", bizParamDataId))
                .withIndices(LifeCircleContentES.INDEX_NAME)
                .withTypes(LifeCircleContentES.INDEX_TYPE)
                .build();

        List<String> strIdList = elasticsearchTemplate.queryForIds(searchQuery);
        if (CollectionUtils.isNotEmpty(strIdList)) {
            for (String strId : strIdList) {
                idList.add(Long.valueOf(strId));
            }
        }

        return idList;
    }

    public List<LifeCircleContentES> search(SearchDTO searchDTO) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<LifeCircleContentES> dataList = new ArrayList<>();

        if (searchDTO == null || StringUtils.isBlank(searchDTO.getKeyword())) {
            return dataList;
        }

        if (searchDTO.getPageSize() <= 0) {
            searchDTO.setPageSize(15);
        }

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("serviceStatus", ServiceStatusEnum.RUNNING.getStatus()));
        boolQueryBuilder.must(QueryBuilders.matchQuery("searchText", searchDTO.getKeyword()));

        if (searchDTO.getLastId() != null) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("id").lt(searchDTO.getLastId()));
        }

        if (searchDTO.getChannelEnum() != null && !LifeCircleChannelEnum.RECOMMEND.equals(searchDTO.getChannelEnum())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("channelIdList", searchDTO.getChannelEnum().getChannelId()));
        }

        List<Integer> filterShowTypeList = getFilterShowType(searchDTO);
        if (CollectionUtils.isNotEmpty(filterShowTypeList)) {
            boolQueryBuilder.mustNot(QueryBuilders.termsQuery("showType", filterShowTypeList));
        }

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(PageRequest.of(0, searchDTO.getPageSize()))
                //.withFields("id")
                .withHighlightFields(new HighlightBuilder.Field("searchText"))
                .withIndices(LifeCircleContentES.INDEX_NAME)
                .withTypes(LifeCircleContentES.INDEX_TYPE)
                .build();

        searchQuery.addSort(Sort.by(Sort.Order.desc("createTime"), Sort.Order.desc("id")));

        AggregatedPage<LifeCircleContentES> page = elasticsearchTemplate.queryForPage(searchQuery, LifeCircleContentES.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                SearchHit[] hits = response.getHits().getHits();
                List<LifeCircleContentES> resultList = new ArrayList<>();

                LifeCircleContentES contentES = null;
                List<String> matchKeywordList = null;
                String searchText = null;
                String matchKeyword = null;
                HighlightField searchTextField = null;

                Pattern pattern = Pattern.compile("<em>(.*?)</em>");
                Matcher matcher = null;

                for (SearchHit hit : response.getHits().getHits()) {
                    String source = hit.getSourceAsString();
                    try {
                        contentES = objectMapper.readValue(source, LifeCircleContentES.class);

                        searchTextField = hit.getHighlightFields().get("searchText");
                        if (searchTextField != null) {
                            searchText = searchTextField.fragments()[0].toString();
                            if (StringUtils.isBlank(searchText)) {
                                continue;
                            }

                            matcher = pattern.matcher(searchText);
                            matchKeywordList = new ArrayList<>();

                            while (matcher.find()) {
                                matchKeyword = matcher.group(1);

                                if (!matchKeywordList.contains(matchKeyword)) {
                                    matchKeywordList.add(matchKeyword);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("解析搜索结果出错：", e);
                    }

                    if (contentES != null &&
                            contentES.getId() != null
                            && CollectionUtils.isNotEmpty(matchKeywordList)) {
                        contentES.setPermissionUserIds(matchKeywordList);
                        resultList.add(contentES);
                    }
                }

                return new AggregatedPageImpl(resultList);
            }
        });

        stopWatch.stop();
        log.info("ES搜索关键词结束, 共{}条, 耗时{}ms, searchDTO={}", page.getContent().size(), stopWatch.getTotalTimeMillis(), JSON.toJSONString(searchDTO));

        return page.getContent();
    }

    private List<Integer> getFilterShowType(SearchDTO searchDTO) {
        List<Integer> showTypeList = new ArrayList<>();

        // 老板本过滤小说
        if (searchDTO.getV() == null || searchDTO.getV() < 3) {
            showTypeList.add(ShowTypeEnum.NOVEL_SERVICE_DYNAMIC.getType());
        }

        return showTypeList;
    }

    public String searchLastId(SearchDTO searchDTO) {
        List<LifeCircleContentES> dataList = new ArrayList<>();

        if (searchDTO == null || StringUtils.isBlank(searchDTO.getKeyword())) {
            return null;
        }

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("serviceStatus", ServiceStatusEnum.RUNNING.getStatus()));
        boolQueryBuilder.must(QueryBuilders.matchQuery("searchText", searchDTO.getKeyword()));

        if (searchDTO.getLastId() != null) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("id").lt(searchDTO.getLastId()));
        }

        if (searchDTO.getChannelEnum() != null && !LifeCircleChannelEnum.RECOMMEND.equals(searchDTO.getChannelEnum())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("channelIdList", searchDTO.getChannelEnum().getChannelId()));
        }

        List<Integer> filterShowTypeList = getFilterShowType(searchDTO);
        if (CollectionUtils.isNotEmpty(filterShowTypeList)) {
            boolQueryBuilder.mustNot(QueryBuilders.termsQuery("showType", filterShowTypeList));
        }

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(PageRequest.of(0, 1))
                .withFields("id")
                .withIndices(LifeCircleContentES.INDEX_NAME)
                .withTypes(LifeCircleContentES.INDEX_TYPE)
                .build();

        searchQuery.addSort(Sort.by(Sort.Order.asc("createTime"), Sort.Order.asc("id")));

        List<String> idList = elasticsearchTemplate.queryForIds(searchQuery);
        if (CollectionUtils.isNotEmpty(idList)) {
            return idList.get(0);
        }

        return null;
    }

    public List<Long> getTypeDataIds(QueryTypeDataParam param) {
        List<Long> idsList = new ArrayList<>();

        if (param == null) {
            return idsList;
        }

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (param.getDataType() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("dataType", param.getDataType()));
        }

        if (param.getShowType() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("showType", param.getShowType()));
        }

        if (StringUtils.isNotBlank(param.getClientId())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("fromServiceClientId", param.getClientId()));
        }

        if (StringUtils.isNotBlank(param.getServiceName())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("fromServiceName", param.getServiceName()));
        }

        if (StringUtils.isNotBlank(param.getNotServiceName())) {
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("fromServiceName", param.getNotServiceName()));
        }

        if (StringUtils.isNotBlank(param.getCategoryId())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("categoryId", param.getCategoryId()));
        }

        if (CollectionUtils.isNotEmpty(param.getCategoryIdList())) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("categoryId", param.getCategoryIdList()));
        }

        if (StringUtils.isNotBlank(param.getCategoryName())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("categoryName", param.getCategoryName()));
        }

        if (CollectionUtils.isNotEmpty(param.getCategoryNameList())) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("categoryName", param.getCategoryNameList()));
        }

        if (!param.isIgnoreServiceStatus()) {
            boolQueryBuilder.must(QueryBuilders.termQuery("serviceStatus", ServiceStatusEnum.RUNNING.getStatus()));
        } else {
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("serviceStatus", ServiceStatusEnum.DELETED.getStatus()));
        }

        if (CollectionUtils.isNotEmpty(param.getChannelIdList())) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("channelIdList", param.getChannelIdList()));
        }

        if (param.getMaxId() != null) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("id").lt(param.getMaxId()));
        }

        if (CollectionUtils.isNotEmpty(param.getIdList())) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("id", param.getIdList()));
        }

        if (param.getMinCreateTime() != null) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("createTime").gt(param.getMinCreateTime().getTime()));
        }

        if (param.getMaxCreateTime() != null) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("createTime").lt(param.getMaxCreateTime().getTime()));
        }

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(PageRequest.of(0, param.getPageSize()))
                .withFields("id")
                .withIndices(LifeCircleContentES.INDEX_NAME)
                .withTypes(LifeCircleContentES.INDEX_TYPE)
                .build();

        searchQuery.addSort(Sort.by(Sort.Order.desc("id")));

        List<String> idList = elasticsearchTemplate.queryForIds(searchQuery);
        if (CollectionUtils.isEmpty(idList)) {
            return idsList;
        }

        for (String strId : idList) {
            idsList.add(Long.valueOf(strId));
        }

        return idsList;
    }

    public List<LifeCircleContentMongo> getTypeData(QueryTypeDataParam param) {
        List<LifeCircleContentMongo> contentMongoList = new ArrayList<>();

        List<Long> idsList = getTypeDataIds(param);
        if (CollectionUtils.isEmpty(idsList)) {
            return contentMongoList;
        }

        contentMongoList = lifeCircleContentRepository.getByIdsWithSort(idsList, param);

        return contentMongoList;
    }
}
