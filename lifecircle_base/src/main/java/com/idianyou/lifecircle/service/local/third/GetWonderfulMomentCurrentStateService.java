package com.idianyou.lifecircle.service.local.third;

import com.alibaba.fastjson.JSONObject;
import com.idianyou.circle.service.dubbo.GroupChatWonderfulMomentDubboService;
import com.idianyou.lifecircle.dto.UpdateServiceBizParamDTO;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.ServiceStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 获取[精彩瞬间]当前状态数据
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/11 15:53
 */
@Slf4j
@Service
public class GetWonderfulMomentCurrentStateService extends AbstractGetServiceCurrentStateService {

    @Autowired
    private GroupChatWonderfulMomentDubboService groupChatWonderfulMomentDubboService;

    @Override
    public boolean supports(DataTypeEnum dataTypeEnum) {
        return DataTypeEnum.WONDERFUL_MOMENTS.equals(dataTypeEnum);
    }

    @Override
    public UpdateServiceBizParamDTO getServiceCurrentState(String serviceDataId) {
        JSONObject jsonObject = groupChatWonderfulMomentDubboService.getServiceCurrentState(Long.valueOf(serviceDataId));

        UpdateServiceBizParamDTO bizParamDTO = new UpdateServiceBizParamDTO();
        bizParamDTO.setDataTypeEnum(DataTypeEnum.WONDERFUL_MOMENTS);
        bizParamDTO.setServiceDataId(serviceDataId);

        if (jsonObject != null) {
            bizParamDTO.setServiceStatusEnum(ServiceStatusEnum.statusOf(jsonObject.getInteger("serviceStatus")));
            bizParamDTO.setAllServiceBizParam(jsonObject.getString("serviceBizParam"));
        }

        if (bizParamDTO.getServiceStatusEnum() == null) {
            bizParamDTO.setServiceStatusEnum(ServiceStatusEnum.OVER);
        }

        if (StringUtils.isBlank(bizParamDTO.getAllServiceBizParam())) {
            bizParamDTO.setAllServiceBizParam("");
        }

        bizParamDTO.setChangedBizParam(bizParamDTO.getAllServiceBizParam());

        return bizParamDTO;
    }
}