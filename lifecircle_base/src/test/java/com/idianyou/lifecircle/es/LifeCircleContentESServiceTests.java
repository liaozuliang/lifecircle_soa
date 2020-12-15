package com.idianyou.lifecircle.es;

import com.alibaba.fastjson.JSON;
import com.idianyou.lifecircle.entity.mongo.LifeCircleContentMongo;
import com.idianyou.lifecircle.entity.mongo.LifeCircleServiceBizParamMongo;
import com.idianyou.lifecircle.enums.ServiceStatusEnum;
import com.idianyou.lifecircle.mongo.LifeCircleContentRepository;
import com.idianyou.lifecircle.mongo.LifeCircleServiceBizParamRepository;
import com.idianyou.lifecircle.service.impl.dubbo.LifeCircleContentPublishDubboServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/2 20:47
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LifeCircleContentESServiceTests {

    @Autowired
    private LifeCircleContentESService lifeCircleContentESService;

    @Autowired
    private LifeCircleContentRepository lifeCircleContentRepository;

    @Autowired
    private LifeCircleServiceBizParamRepository lifeCircleServiceBizParamRepository;

    @Autowired
    private LifeCircleContentPublishDubboServiceImpl lifeCircleContentPublishDubboServiceImpl;

    @Test
    public void testAdd() {
        Long contentId = 1879063369L;

        LifeCircleContentMongo contentMongo = lifeCircleContentRepository.getById(contentId);
        LifeCircleServiceBizParamMongo bizParamMongo = lifeCircleServiceBizParamRepository.getById(contentMongo.getBizParamDataId());

        LifeCircleContentES contentES = lifeCircleContentPublishDubboServiceImpl.trans2LifeCircleContentES(contentMongo, bizParamMongo);
        lifeCircleContentESService.add(contentES);
    }

    @Test
    public void testUpdate() {
        Long contentId = 1879063369L;

        LifeCircleContentES contentES = new LifeCircleContentES();
        contentES.setId(contentId);
        contentES.setServiceStatus(ServiceStatusEnum.OVER.getStatus());

        lifeCircleContentESService.update(contentES);
    }

    @Test
    public void testQuery() {
        Long contentId = 1879063369L;
        LifeCircleContentES contentES = lifeCircleContentESService.getById(contentId);
        log.info("=========getById========={}", JSON.toJSONString(contentES));

        List<Long> idList = lifeCircleContentESService.getIdByBizParamDataId("market_6-8a7efd937271af30017271b37f1201da");
        log.info("=========getIdByBizParamDataId========={}", JSON.toJSONString(idList));
    }

}
