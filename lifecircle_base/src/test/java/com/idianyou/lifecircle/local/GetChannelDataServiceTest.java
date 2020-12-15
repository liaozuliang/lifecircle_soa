package com.idianyou.lifecircle.local;

import com.alibaba.fastjson.JSON;
import com.idianyou.lifecircle.dto.GetChannelDataContext;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.service.local.FactoryService;
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
 * @date: 2020/5/29 20:08
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GetChannelDataServiceTest {

    @Autowired
    private FactoryService factoryService;

    @Test
    public void testGetChannelData() {
        GetChannelDataContext context = new GetChannelDataContext();
        context.setUserId(15514345L);
        context.setReqNo("aaaaaaaaaaaa");
        context.setChannelEnum(LifeCircleChannelEnum.RECOMMEND);

        List<ChannelDataGroupVO> dataList = factoryService.getService(context.getChannelEnum()).getChannelData(context);
        log.info("======testGetChannelData======={}", JSON.toJSONString(dataList));
    }

}
