package com.idianyou.lifecircle.service.local.third;

import com.idianyou.lifecircle.dto.UpdateServiceBizParamDTO;
import com.idianyou.lifecircle.enums.DataTypeEnum;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/29 20:34
 */
public abstract class AbstractGetServiceCurrentStateService {

    /**
     * 是否支持
     * @param dataTypeEnum
     * @return
     */
    public abstract boolean supports(DataTypeEnum dataTypeEnum);

    /**
     * 获取服务当前状态
     * @param serviceDataId
     * @return
     */
    public abstract UpdateServiceBizParamDTO getServiceCurrentState(String serviceDataId);
}
