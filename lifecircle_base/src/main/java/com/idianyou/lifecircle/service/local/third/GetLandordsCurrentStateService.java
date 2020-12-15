package com.idianyou.lifecircle.service.local.third;

import com.idianyou.lifecircle.dto.UpdateServiceBizParamDTO;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description: 获取[斗地主]当前状态数据
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/11 15:53
 */
@Slf4j
@Service
public class GetLandordsCurrentStateService extends AbstractGetServiceCurrentStateService {

    @Override
    public boolean supports(DataTypeEnum dataTypeEnum) {
        return DataTypeEnum.LANDLORDS.equals(dataTypeEnum);
    }

    @Override
    public UpdateServiceBizParamDTO getServiceCurrentState(String serviceDataId) {
        return null;
    }
}