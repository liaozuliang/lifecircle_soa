package com.idianyou.lifecircle.local;

import com.alibaba.fastjson.JSON;
import com.idianyou.lifecircle.dto.InnerRpcResponse;
import com.idianyou.lifecircle.dto.UpdateServiceBizParamDTO;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.ServiceStatusEnum;
import com.idianyou.lifecircle.service.local.ServiceBizParamChangedNotifyAppService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/6 15:49
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceBizParamChangedNotifyAppServiceTests {

    @Autowired
    private ServiceBizParamChangedNotifyAppService serviceBizParamChangedNotifyAppService;

    @Test
    public void testDoNotify() {
        UpdateServiceBizParamDTO bizParamDTO = new UpdateServiceBizParamDTO();
        bizParamDTO.setDataTypeEnum(DataTypeEnum.WONDERFUL_MOMENTS);
        bizParamDTO.setServiceDataId("82111349");
        bizParamDTO.setServiceStatusEnum(ServiceStatusEnum.RUNNING);
        bizParamDTO.setChangedBizParam("{\"commentNumber\":0,\"groupName\":\"A3置顶群\",\"groupProtocol\":\"dypush://defaultpackage/gamecenter?eyJ0eXBlIjozMTIsInZhbHVlIjoie1wiZmxhZ1wiOjAsXCJyb3V0ZXJwYXJhbXNcIjpcIntcXFwi\\rZ3JvdXBJZFxcXCI6XFxcIjExNjMzXFxcIn1cIixcInJvdXRlcnBhdGhcIjpcIi9pbS90b0ludml0\\rZUdyb3VwV2loU2NhblwifSJ9\\r\",\"imageUrl\":\"http://alfs.chigua.cn/dianyou/data/im/default/20200319/JeXzi8Yr84.png\",\"detailProtocol\":\"dypush://defaultpackage/gamecenter?eyJ0eXBlIjozMTIsInZhbHVlIjp7InJvdXRlcnBhdGgiOiIvbGlmZS90b01vbWVudERldGFpbFBhdGgiLCJyb3V0ZXJwYXJhbXMiOiJ7XCJtb21lbnREZXRhaWxJZFwiOlwiODIxMTEzNDlcIn0ifX0=\",\"praiseNumber\":0,\"contentTitle\":\"腹股沟管道路线\",\"giftNumber\":0}");

        serviceBizParamChangedNotifyAppService.doNotify(1550798L, 2L, bizParamDTO);
    }

}
