package com.idianyou.lifecircle.local;

import com.alibaba.fastjson.JSON;
import com.idianyou.lifecircle.dto.GetChannelDataContext;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.enums.TimeDescEnum;
import com.idianyou.lifecircle.enums.UserNativeTypeEnum;
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
 * @date: 2020/6/10 9:41
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GetRecommendChannelDataServiceTests {

    @Autowired
    private FactoryService factoryService;

    @Test
    public void tt() {
        List<ChannelDataGroupVO> dataList = null;

        GetChannelDataContext context = new GetChannelDataContext();
        context.setUserId(15514633L);
        context.setChannelEnum(LifeCircleChannelEnum.RECOMMEND);

        // 本地人，在本地发展
        context.setUserNativeTypeEnum(UserNativeTypeEnum.NATIVE);
        context.setTimeDescEnum(TimeDescEnum.MORNING);
        context.setReqNo(System.currentTimeMillis()+"");

        dataList = factoryService.getServiceForRecommendChannel(context).getChannelData(context);
        log.info("===={}====={}======={}", context.getUserNativeTypeEnum().getDesc(), context.getTimeDescEnum().getDesc(), JSON.toJSONString(dataList));

        context.setUserNativeTypeEnum(UserNativeTypeEnum.NATIVE);
        context.setTimeDescEnum(TimeDescEnum.NOONING);
        context.setReqNo(System.currentTimeMillis()+"");

        dataList = factoryService.getServiceForRecommendChannel(context).getChannelData(context);
        log.info("===={}====={}======={}", context.getUserNativeTypeEnum().getDesc(), context.getTimeDescEnum().getDesc(), JSON.toJSONString(dataList));

        context.setUserNativeTypeEnum(UserNativeTypeEnum.NATIVE);
        context.setTimeDescEnum(TimeDescEnum.AFTERNOON);
        context.setReqNo(System.currentTimeMillis()+"");

        dataList = factoryService.getServiceForRecommendChannel(context).getChannelData(context);
        log.info("===={}====={}======={}", context.getUserNativeTypeEnum().getDesc(), context.getTimeDescEnum().getDesc(), JSON.toJSONString(dataList));

        // 本地人，在外地发展
        context.setUserNativeTypeEnum(UserNativeTypeEnum.NATIVE_OUT);
        context.setTimeDescEnum(TimeDescEnum.MORNING);
        context.setReqNo(System.currentTimeMillis()+"");

        dataList = factoryService.getServiceForRecommendChannel(context).getChannelData(context);
        log.info("===={}====={}======={}", context.getUserNativeTypeEnum().getDesc(), context.getTimeDescEnum().getDesc(), JSON.toJSONString(dataList));

        context.setUserNativeTypeEnum(UserNativeTypeEnum.NATIVE_OUT);
        context.setTimeDescEnum(TimeDescEnum.NOONING);
        context.setReqNo(System.currentTimeMillis()+"");

        dataList = factoryService.getServiceForRecommendChannel(context).getChannelData(context);
        log.info("===={}====={}======={}", context.getUserNativeTypeEnum().getDesc(), context.getTimeDescEnum().getDesc(), JSON.toJSONString(dataList));

        context.setUserNativeTypeEnum(UserNativeTypeEnum.NATIVE_OUT);
        context.setTimeDescEnum(TimeDescEnum.AFTERNOON);
        context.setReqNo(System.currentTimeMillis()+"");

        dataList = factoryService.getServiceForRecommendChannel(context).getChannelData(context);
        log.info("===={}====={}======={}", context.getUserNativeTypeEnum().getDesc(), context.getTimeDescEnum().getDesc(), JSON.toJSONString(dataList));

        // 朋友是本地人、其他、未设置
        context.setUserNativeTypeEnum(UserNativeTypeEnum.FRIEND_NATIVE);
        context.setTimeDescEnum(TimeDescEnum.MORNING);
        context.setReqNo(System.currentTimeMillis()+"");

        dataList = factoryService.getServiceForRecommendChannel(context).getChannelData(context);
        log.info("===={}====={}======={}", context.getUserNativeTypeEnum().getDesc(), context.getTimeDescEnum().getDesc(), JSON.toJSONString(dataList));

        context.setUserNativeTypeEnum(UserNativeTypeEnum.OTHER);
        context.setTimeDescEnum(TimeDescEnum.NOONING);
        context.setReqNo(System.currentTimeMillis()+"");

        dataList = factoryService.getServiceForRecommendChannel(context).getChannelData(context);
        log.info("===={}====={}======={}", context.getUserNativeTypeEnum().getDesc(), context.getTimeDescEnum().getDesc(), JSON.toJSONString(dataList));

        context.setUserNativeTypeEnum(UserNativeTypeEnum.NULL);
        context.setTimeDescEnum(TimeDescEnum.AFTERNOON);
        context.setReqNo(System.currentTimeMillis()+"");

        dataList = factoryService.getServiceForRecommendChannel(context).getChannelData(context);
        log.info("===={}====={}======={}", context.getUserNativeTypeEnum().getDesc(), context.getTimeDescEnum().getDesc(), JSON.toJSONString(dataList));

    }

}
