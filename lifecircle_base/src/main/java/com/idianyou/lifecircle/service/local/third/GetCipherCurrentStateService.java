package com.idianyou.lifecircle.service.local.third;

import com.idianyou.cipher.spi.service.RedPackLifecircleService;
import com.idianyou.lifecircle.dto.UpdateServiceBizParamDTO;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 获取[暗号红包]当前状态数据
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/11 15:53
 */
@Slf4j
@Service
public class GetCipherCurrentStateService extends AbstractGetServiceCurrentStateService {

    @Autowired
    private RedPackLifecircleService redPackLifecircleService;

    @Override
    public boolean supports(DataTypeEnum dataTypeEnum) {
        return DataTypeEnum.CIPHER_REDPACKET.equals(dataTypeEnum);
    }

    @Override
    public UpdateServiceBizParamDTO getServiceCurrentState(String serviceDataId) {
        return redPackLifecircleService.getRedPackLifecircle(serviceDataId);
    }
}