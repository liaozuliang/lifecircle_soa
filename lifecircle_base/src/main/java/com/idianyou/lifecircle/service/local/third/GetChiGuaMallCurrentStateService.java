package com.idianyou.lifecircle.service.local.third;

import com.idianyou.lifecircle.dto.UpdateServiceBizParamDTO;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.platform.spi.service.PlatformService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 获取[吃瓜商城]当前状态数据
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/11 15:53
 */
@Slf4j
@Service
public class GetChiGuaMallCurrentStateService extends AbstractGetServiceCurrentStateService {

    @Autowired
    private PlatformService platformService;

    @Override
    public boolean supports(DataTypeEnum dataTypeEnum) {
        return DataTypeEnum.CHIGUA_MALL.equals(dataTypeEnum);
    }

    @Override
    public UpdateServiceBizParamDTO getServiceCurrentState(String serviceDataId) {
        return platformService.queryUpdateServiceBizParamDTO(serviceDataId);
    }
}