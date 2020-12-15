package com.idianyou.lifecircle.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/2 12:22
 */
@Repository
public interface LifeShopsGoodsESRepository extends ElasticsearchRepository<LifeShopsGoodsES, String> {

}