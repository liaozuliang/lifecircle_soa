package com.idianyou.lifecircle.config;

import com.idianyou.lifecircle.es.LifeShopsGoodsES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import javax.annotation.PostConstruct;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/3 10:01
 */
@Configuration
public class ElasticsearchConfig {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @PostConstruct
    public void init() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");

        if (!elasticsearchTemplate.indexExists(LifeShopsGoodsES.INDEX_NAME)) {
            elasticsearchTemplate.createIndex(LifeShopsGoodsES.class);
            elasticsearchTemplate.putMapping(LifeShopsGoodsES.class);
        }
    }
}
