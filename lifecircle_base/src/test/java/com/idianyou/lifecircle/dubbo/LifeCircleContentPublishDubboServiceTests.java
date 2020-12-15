package com.idianyou.lifecircle.dubbo;

import com.alibaba.fastjson.JSON;
import com.idianyou.business.constant.UserIdentitiesEnum;
import com.idianyou.business.usercenter.dto.UserInfoDTO;
import com.idianyou.business.usercenter.service.UserCenterService;
import com.idianyou.lifecircle.dto.DeleteDTO;
import com.idianyou.lifecircle.dto.InnerRpcResponse;
import com.idianyou.lifecircle.dto.LifeCircleContentPublishDTO;
import com.idianyou.lifecircle.dto.UpdateServiceBizParamDTO;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.ServiceStatusEnum;
import com.idianyou.lifecircle.enums.ShowTypeEnum;
import com.idianyou.lifecircle.service.LifeCircleContentPublishDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.eclipse.core.internal.dtree.DeletedNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Set;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/29 11:30
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LifeCircleContentPublishDubboServiceTests {

    @Autowired
    private LifeCircleContentPublishDubboService lifeCircleContentPublishDubboService;

    @Autowired
    private UserCenterService userCenterService;

    @Test
    public void testInitDataCategory() {
        lifeCircleContentPublishDubboService.initDataCategory();
    }

    @Test
    public void testInitESData() {
        lifeCircleContentPublishDubboService.initESData(0L);
    }

    @Test
    public void getUserInfo() {
        com.idianyou.spi.common.InnerRpcResponse<Set<Long>> response1 = userCenterService.getIdentityDestinedUsers(UserIdentitiesEnum.RUZI_CATTLE);
        log.info("===============getIdentityDestinedUsers=======RUZI_CATTLE========{}", JSON.toJSONString(response1));

        response1 = userCenterService.getIdentityDestinedUsers(UserIdentitiesEnum.PARTNER_SERVICE);
        log.info("===============getIdentityDestinedUsers=======PARTNER_SERVICE========{}", JSON.toJSONString(response1));

        com.idianyou.spi.common.InnerRpcResponse<UserInfoDTO> response = userCenterService.getUserInfo(15514179L);
        log.info("===============getUserInfo==============={}", JSON.toJSONString(response));
    }

    @Test
    public void testPublish() {
        LifeCircleContentPublishDTO publishDTO = new LifeCircleContentPublishDTO();
        publishDTO.setUserId(15514179L);
        publishDTO.setDataTypeEnum(DataTypeEnum.WONDERFUL_MOMENTS);
        publishDTO.setShowTypeEnum(ShowTypeEnum.WONDERFUL_MOMENTS_SERVICE_DYNAMIC);
        publishDTO.setServiceDataId("111");
        publishDTO.setServiceClientId("cg84e4465ee3d42c57");
        publishDTO.setServiceStatusEnum(ServiceStatusEnum.RUNNING);
        publishDTO.setServiceBizParam("{11111}");
        publishDTO.setCreateTime(new Date());

        InnerRpcResponse<Long> response = lifeCircleContentPublishDubboService.publish(publishDTO);
        log.info("======testPublish======{}", JSON.toJSONString(response));
    }

    @Test
    public void testPublish2() {
        LifeCircleContentPublishDTO publishDTO = new LifeCircleContentPublishDTO();
        publishDTO.setUserId(1550664L);
        publishDTO.setDataTypeEnum(DataTypeEnum.WONDERFUL_MOMENTS);
        publishDTO.setShowTypeEnum(ShowTypeEnum.WONDERFUL_MOMENTS_SHARE);
        publishDTO.setServiceDataId("222");
        publishDTO.setServiceClientId("cg84e4465ee3d42c57");
        publishDTO.setServiceStatusEnum(ServiceStatusEnum.RUNNING);
        publishDTO.setShareContent("好玩，这个群真热闹");
        publishDTO.setServiceBizParam("{22222}");
        publishDTO.setLongitude(45.5645);
        publishDTO.setLatitude(90.4323);
        publishDTO.setCreateTime(new Date());

        InnerRpcResponse<Long> response = lifeCircleContentPublishDubboService.publish(publishDTO);
        log.info("======testPublish======{}", JSON.toJSONString(response));
    }

    @Test
    public void testUpdateServiceBizParam() {
        UpdateServiceBizParamDTO bizParamDTO = new UpdateServiceBizParamDTO();
        bizParamDTO.setDataTypeEnum(DataTypeEnum.WONDERFUL_MOMENTS);
        bizParamDTO.setServiceDataId("111");
        bizParamDTO.setServiceStatusEnum(ServiceStatusEnum.RUNNING);
        bizParamDTO.setAllServiceBizParam("{1111111aaaaaa}");
        bizParamDTO.setChangedBizParam("{aaaaaa}");

        InnerRpcResponse response = lifeCircleContentPublishDubboService.updateServiceBizParam(bizParamDTO);
        log.info("======testUpdateServiceBizParam======{}", JSON.toJSONString(response));
    }

    @Test
    public void testDelete() {
        DeleteDTO dto = new DeleteDTO();
        dto.setDataTypeEnum(DataTypeEnum.WONDERFUL_MOMENTS);
        dto.setShowTypeEnum(ShowTypeEnum.WONDERFUL_MOMENTS_SERVICE_DYNAMIC);
        dto.setMaxCreateTime(DateUtils.addDays(new Date(), -15));
        lifeCircleContentPublishDubboService.delete(dto);
    }
}
