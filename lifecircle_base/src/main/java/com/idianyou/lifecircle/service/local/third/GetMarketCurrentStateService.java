package com.idianyou.lifecircle.service.local.third;

import com.idianyou.lifecircle.dto.UpdateServiceBizParamDTO;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.market.spi.service.FarmerDubboService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 获取[赶集]当前状态数据
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/11 15:53
 */
@Slf4j
@Service
public class GetMarketCurrentStateService extends AbstractGetServiceCurrentStateService {

    @Autowired
    private FarmerDubboService farmerDubboService;

    @Override
    public boolean supports(DataTypeEnum dataTypeEnum) {
        return DataTypeEnum.MARKET.equals(dataTypeEnum);
    }

    @Override
    public UpdateServiceBizParamDTO getServiceCurrentState(String serviceDataId) {
        return farmerDubboService.getFarmerByDataId(serviceDataId);
    }
}