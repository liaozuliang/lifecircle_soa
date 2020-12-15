package com.idianyou.lifecircle.local;

import com.alibaba.fastjson.JSON;
import com.idianyou.lifecircle.entity.mongo.LifeCircleContentMongo;
import com.idianyou.lifecircle.mongo.LifeCircleContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/7 19:53
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LifeCircleContentRepositoryTests {

    @Autowired
    private LifeCircleContentRepository lifeCircleContentRepository;

    @Test
    public void testGetChannelData() {
        List<LifeCircleContentMongo> contentMongoList = lifeCircleContentRepository.getChannelData(new Date(), 1000, null);
        log.info("=============testGetChannelData================{}", JSON.toJSONString(contentMongoList));
    }

}
