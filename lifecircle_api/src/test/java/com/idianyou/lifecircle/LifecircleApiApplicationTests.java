package com.idianyou.lifecircle;

import com.alibaba.fastjson.JSON;
import com.idianyou.lifecircle.dto.FromServiceInfo;
import com.idianyou.lifecircle.dto.UserInfo;
import com.idianyou.lifecircle.dto.vo.ChannelDataVO;
import com.idianyou.lifecircle.enums.IdentityTypeEnum;
import com.idianyou.lifecircle.enums.ShowTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LifecircleApiApplicationTests {

    @Test
    public void contextLoads() {
        log.info("===========系统启动成功============");
    }

    public static void main(String[] args) {
        ChannelDataVO contentVO = new ChannelDataVO();

        contentVO.setId(100000L);
        contentVO.setShowType(ShowTypeEnum.WONDERFUL_MOMENTS_SHARE.getType());
        contentVO.setShareContent("这群可真热闹啊");
        contentVO.setPraiseCount(20);
        contentVO.setCommentCount(2);
        contentVO.setCreateTimeDesc("03-28");
        contentVO.setServiceBizParam("");

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(1822132L);
        userInfo.setNickName("葫芦娃");
        userInfo.setHeadImg("");
        userInfo.setIdentityTypeList(Arrays.asList(IdentityTypeEnum.OXEN.getType()));
        userInfo.setIdiograph("精彩生活，美满人生");

        contentVO.setUserInfo(userInfo);

        FromServiceInfo fromServiceInfo = new FromServiceInfo();

        fromServiceInfo.setName("主群");
        fromServiceInfo.setIcon("");
        fromServiceInfo.setJumpProtocol("");

        contentVO.setFromServiceInfo(fromServiceInfo);


        log.info("============={}", JSON.toJSONString(contentVO));
    }

}
