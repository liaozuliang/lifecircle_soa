package com.idianyou.lifecircle.service.local.third;

import com.idianyou.lifecircle.dto.UpdateServiceBizParamDTO;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description: 获取[猜猜我是谁]当前状态数据
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/11 15:53
 */
@Slf4j
@Service
public class GetGuessCurrentStateService extends AbstractGetServiceCurrentStateService {

    @Override
    public boolean supports(DataTypeEnum dataTypeEnum) {
        return DataTypeEnum.GUESS_WHO_I_AM.equals(dataTypeEnum);
    }

    @Override
    public UpdateServiceBizParamDTO getServiceCurrentState(String serviceDataId) {
        return null;
    }
}