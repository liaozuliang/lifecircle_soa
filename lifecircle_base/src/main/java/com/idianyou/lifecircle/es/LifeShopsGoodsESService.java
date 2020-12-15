package com.idianyou.lifecircle.es;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idianyou.lifecircle.constants.AppConf;
import com.idianyou.lifecircle.dto.lifeShopsGoods.LifeShopsGoodsSearchDTO;
import com.idianyou.lifecircle.enums.lifeShopsGoods.DataTypeEnum;
import com.idianyou.lifecircle.enums.lifeShopsGoods.OnOffStatusEnum;
import com.idianyou.lifecircle.enums.lifeShopsGoods.StatusEnum;
import com.idianyou.lifecircle.exception.LifeCircleException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/4 16:05
 */
@Slf4j
@Service
public class LifeShopsGoodsESService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private LifeShopsGoodsESRepository lifeShopsGoodsESRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppConf appConf;

    public void save(LifeShopsGoodsES lifeShopsGoodsES) {
        lifeShopsGoodsESRepository.save(lifeShopsGoodsES);
    }

    public void delete(String id) {
        lifeShopsGoodsESRepository.deleteById(id);
    }

    public static String getESId(Integer dataId, DataTypeEnum dataType) {
        if (dataId == null || dataType == null) {
            log.error("dataId或dataType为空, 无法获取ES数据Id");
            throw new LifeCircleException("dataId或dataType为空");
        }
        return dataType.name().toLowerCase().concat(dataId.toString());
    }

    public long count(LifeShopsGoodsSearchDTO searchDTO) {
        long count = 0;

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<LifeShopsGoodsES> dataList = new ArrayList<>();

        if (searchDTO == null || StringUtils.isBlank(searchDTO.getKeywords())) {
            log.error("ES搜索[生活商品/商铺]总数量失败：关键字为空");
            return count;
        }

        BoolQueryBuilder boolQueryBuilder = getSearchCondition(searchDTO);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withIndices(LifeShopsGoodsES.INDEX_NAME)
                .withTypes(LifeShopsGoodsES.INDEX_TYPE)
                .build();

        count = elasticsearchTemplate.count(searchQuery);

        stopWatch.stop();
        log.info("ES搜索[生活商品/商铺]总数量结束, 共{}条, 耗时{}ms, searchDTO={}", count, stopWatch.getTotalTimeMillis(), JSON.toJSONString(searchDTO));

        return count;
    }

    /**
     * 使用searchAfter实现深度翻页
     * @param searchDTO
     * @return
     */
    public List<LifeShopsGoodsESExt> search2(LifeShopsGoodsSearchDTO searchDTO) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<LifeShopsGoodsESExt> dataList = new ArrayList<>();

        if (searchDTO == null || StringUtils.isBlank(searchDTO.getKeywords())) {
            log.error("ES搜索[生活商品/商铺]失败：关键字为空");
            return dataList;
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(getSearchCondition(searchDTO));

        // 根据排序值过滤数据
        if (StringUtils.isNotBlank(searchDTO.getLastSortValues())) {
            try {
                //Object[] searchAfterValues = ESSortValuesUtil.decodeSortValues(searchDTO.getLastSortValues());
                Object[] searchAfterValues = objectMapper.readValue(URLDecoder.decode(searchDTO.getLastSortValues(), "UTF-8"), Object[].class);
                searchSourceBuilder.searchAfter(searchAfterValues);
            } catch (Exception e) {
                log.error("解析searchAfterValues出错：", e);
            }
        }

        // 使用searchAfter 必须设置from为0或者-1
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(searchDTO.getPageObj().getPageSize());

        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(new HighlightBuilder.Field("name"));
        highlightBuilder.field(new HighlightBuilder.Field("name.ik"));
        searchSourceBuilder.highlighter(highlightBuilder);

        // 排序
        searchSourceBuilder.sort("_score", SortOrder.DESC);
        searchSourceBuilder.sort("monthSales", SortOrder.DESC);
        searchSourceBuilder.sort("weight", SortOrder.DESC);
        searchSourceBuilder.sort("createTime", SortOrder.DESC);
        searchSourceBuilder.sort("_id", SortOrder.DESC);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(LifeShopsGoodsES.INDEX_NAME).types(LifeShopsGoodsES.INDEX_TYPE);
        searchRequest.source(searchSourceBuilder);

        List<LifeShopsGoodsESExt> lifeShopsGoodsESExtList = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        elasticsearchTemplate.getClient().search(searchRequest, new ActionListener<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse response) {
                LifeShopsGoodsESExt shopsGoodsES = null;
                List<String> matchKeywordList = null;
                HighlightField highlightField = null;

                try {
                    for (SearchHit hit : response.getHits().getHits()) {
                        String source = hit.getSourceAsString();

                        shopsGoodsES = objectMapper.readValue(source, LifeShopsGoodsESExt.class);
                        shopsGoodsES.setScore(hit.getScore());
                        //shopsGoodsES.setSortValuesStr(ESSortValuesUtil.encodeSortValues(hit.getSortValues()));
                        shopsGoodsES.setSortValues(URLEncoder.encode(objectMapper.writeValueAsString(hit.getSortValues()), "UTF-8"));

                        matchKeywordList = new ArrayList<>();

                        highlightField = hit.getHighlightFields().get("name.ik");
                        getMatchedKeywords(highlightField, matchKeywordList);

                        highlightField = hit.getHighlightFields().get("name");
                        getMatchedKeywords(highlightField, matchKeywordList);

                        if (shopsGoodsES != null &&
                                shopsGoodsES.getId() != null
                                && CollectionUtils.isNotEmpty(matchKeywordList)) {
                            shopsGoodsES.setNameMatchedKeywords(matchKeywordList);
                            lifeShopsGoodsESExtList.add(shopsGoodsES);
                        }
                    }
                } catch (Exception e) {
                    log.error("解析结果出错：", e);
                }

                countDownLatch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                log.error("查询失败：", e);
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (Exception e) {
            log.error("等待查询完成出错：", e);
        }

        stopWatch.stop();
        log.info("ES搜索[生活商品/商铺]结束, 共{}条, 耗时{}ms, searchDTO={}", lifeShopsGoodsESExtList.size(), stopWatch.getTotalTimeMillis(), JSON.toJSONString(searchDTO));

        return lifeShopsGoodsESExtList;
    }

    public List<LifeShopsGoodsESExt> search(LifeShopsGoodsSearchDTO searchDTO) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<LifeShopsGoodsESExt> dataList = new ArrayList<>();

        if (searchDTO == null || StringUtils.isBlank(searchDTO.getKeywords())) {
            log.error("ES搜索[生活商品/商铺]失败：关键字为空");
            return dataList;
        }

        BoolQueryBuilder boolQueryBuilder = getSearchCondition(searchDTO);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(PageRequest.of(searchDTO.getPageObj().getCurrentPage() - 1, searchDTO.getPageObj().getPageSize()))
                //.withFields("id", "dataId", "dataType")
                .withHighlightFields(new HighlightBuilder.Field("name"), new HighlightBuilder.Field("name.ik"))
                .withIndices(LifeShopsGoodsES.INDEX_NAME)
                .withTypes(LifeShopsGoodsES.INDEX_TYPE)
                .build();

        searchQuery.addSort(Sort.by(
                Sort.Order.desc("_score"),
                Sort.Order.desc("monthSales"),
                Sort.Order.desc("weight"),
                Sort.Order.desc("createTime"),
                Sort.Order.desc("_id")
        ));

        AggregatedPage<LifeShopsGoodsESExt> page = elasticsearchTemplate.queryForPage(searchQuery, LifeShopsGoodsESExt.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                List<LifeShopsGoodsESExt> resultList = new ArrayList<>();

                LifeShopsGoodsESExt shopsGoodsES = null;
                List<String> matchKeywordList = null;
                HighlightField highlightField = null;

                for (SearchHit hit : response.getHits().getHits()) {
                    String source = hit.getSourceAsString();
                    try {
                        shopsGoodsES = objectMapper.readValue(source, LifeShopsGoodsESExt.class);
                        shopsGoodsES.setScore(hit.getScore());

                        matchKeywordList = new ArrayList<>();

                        highlightField = hit.getHighlightFields().get("name.ik");
                        getMatchedKeywords(highlightField, matchKeywordList);

                        highlightField = hit.getHighlightFields().get("name");
                        getMatchedKeywords(highlightField, matchKeywordList);
                    } catch (Exception e) {
                        log.error("解析搜索结果出错：", e);
                    }

                    if (shopsGoodsES != null &&
                            shopsGoodsES.getId() != null
                            && CollectionUtils.isNotEmpty(matchKeywordList)) {
                        shopsGoodsES.setNameMatchedKeywords(matchKeywordList);
                        resultList.add(shopsGoodsES);
                    }
                }

                return new AggregatedPageImpl(resultList);
            }
        });

        stopWatch.stop();
        log.info("ES搜索[生活商品/商铺]结束, 共{}条, 耗时{}ms, searchDTO={}", page.getContent().size(), stopWatch.getTotalTimeMillis(), JSON.toJSONString(searchDTO));

        return page.getContent();
    }

    private void getMatchedKeywords(HighlightField highlightField, List<String> matchKeywordList) {
        if (highlightField != null) {
            String highlightStr = highlightField.fragments()[0].toString();
            if (StringUtils.isBlank(highlightStr)) {
                return;
            }

            Pattern pattern = Pattern.compile("<em>(.*?)</em>");
            Matcher matcher = pattern.matcher(highlightStr);
            String matchKeyword = null;

            while (matcher.find()) {
                matchKeyword = matcher.group(1);

                if (!matchKeywordList.contains(matchKeyword)) {
                    matchKeywordList.add(matchKeyword);
                }
            }
        }
    }

    private BoolQueryBuilder getSearchCondition(LifeShopsGoodsSearchDTO searchDTO) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.termQuery("status", StatusEnum.VALID.getStatus()));
        boolQueryBuilder.must(QueryBuilders.termQuery("onOffStatus", OnOffStatusEnum.ON.getStatus()));

        // 完全匹配 + ik分词 + 单字分词
        MatchPhraseQueryBuilder matchPhrase = QueryBuilders.matchPhraseQuery("name", searchDTO.getKeywords()).boost(10);
        MatchQueryBuilder ikAnalyzer = QueryBuilders.matchQuery("name.ik", searchDTO.getKeywords()).analyzer("ik_smart").boost(5);
        MatchQueryBuilder standardAnalyzer = QueryBuilders.matchQuery("name", searchDTO.getKeywords()).boost(1);

        BoolQueryBuilder nameBoolQueryBuilder = QueryBuilders.boolQuery();
        nameBoolQueryBuilder.should(matchPhrase);
        nameBoolQueryBuilder.should(ikAnalyzer);
        nameBoolQueryBuilder.should(standardAnalyzer);

        boolQueryBuilder.must(nameBoolQueryBuilder);

        if (searchDTO.getDataType() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("dataType", searchDTO.getDataType().getType()));
        }

        if (searchDTO.getArea() != null) {
            // 省份
            if (StringUtils.isNotBlank(searchDTO.getArea().getProvinceCode())) {
                boolQueryBuilder.must(QueryBuilders.termQuery("area.provinceCode", searchDTO.getArea().getProvinceCode()));
            }

            // 市
            if (StringUtils.isNotBlank(searchDTO.getArea().getCityCode())) {
                boolQueryBuilder.must(QueryBuilders.termQuery("area.cityCode", searchDTO.getArea().getCityCode()));
            }

            // 县
            if (StringUtils.isNotBlank(searchDTO.getArea().getAreaCode())) {
                boolQueryBuilder.must(QueryBuilders.termQuery("area.areaCode", searchDTO.getArea().getAreaCode()));
            }

            // 乡镇
            if (StringUtils.isNotBlank(searchDTO.getArea().getTownshipCode())) {
                boolQueryBuilder.must(QueryBuilders.termQuery("area.townshipCode", searchDTO.getArea().getTownshipCode()));
            }

            // 街道/村组
            if (StringUtils.isNotBlank(searchDTO.getArea().getStreetCode())) {
                boolQueryBuilder.must(QueryBuilders.termQuery("area.streetCode", searchDTO.getArea().getStreetCode()));
            }
        }

        return boolQueryBuilder;
    }
}
