package com.idianyou.lifecircle.dubbo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.idianyou.lifecircle.service.LifeCircleContentDubboService;
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
 * @date: 2020/5/8 14:28
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LifeCircleContentDubboServiceTests {

    @Autowired
    private LifeCircleContentDubboService lifeCircleContentDubboService;

    @Test
    public void testGetRecommendedPersonalData() {
        for (int i = 0; i < 10; i++) {
            List<Long> idList = lifeCircleContentDubboService.getRecommendedPersonalData(1879141150L, 2);
            log.info("====testGetRecommendedPersonalData===={}", idList);
        }
    }

    @Test
    public void testAddPraiseCount() {
        lifeCircleContentDubboService.addPraiseCount(1, -1);
    }

    @Test
    public void testAddCommentCount() {
        lifeCircleContentDubboService.addCommentCount(1, -1);
    }
}
