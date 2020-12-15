package com.idianyou.lifecircle.service.local.third;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.idianyou.lifecircle.dto.UpdateServiceBizParamDTO;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.ServiceStatusEnum;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Description: 获取[K歌之王]当前状态数据
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/11 15:53
 */
@Slf4j
@Service
public class GetKSongCurrentStateService extends AbstractGetServiceCurrentStateService {

    @Value("${kSongRoomStatusUrl}")
    private String roomStatusUrl;

    @Override
    public boolean supports(DataTypeEnum dataTypeEnum) {
        return DataTypeEnum.K_SONG_KING.equals(dataTypeEnum);
    }

    @Override
    public UpdateServiceBizParamDTO getServiceCurrentState(String serviceDataId) {
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("serviceDataId", serviceDataId)
                .add("dataType", DataTypeEnum.K_SONG_KING.getType().toString())
                .build();

        Request request = new Request.Builder().url(roomStatusUrl).post(formBody).build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                log.error("获取[K歌之王]当前状态数据失败：serviceDataId={}, response={}", serviceDataId, JSON.toJSONString(response));
                return null;
            }

            JSONObject body = JSON.parseObject(response.body().string());

            if (!"200".equals(body.getString("resultCode"))) {
                log.error("获取[K歌之王]当前状态数据失败：serviceDataId={}, body={}", serviceDataId, JSON.toJSONString(body));
                return null;
            }

            UpdateServiceBizParamDTO bizParamDTO = new UpdateServiceBizParamDTO();

            bizParamDTO.setServiceDataId(body.getJSONObject("message").getString("serviceDataId"));
            bizParamDTO.setDataTypeEnum(DataTypeEnum.typeOf(body.getJSONObject("message").getInteger("dataType")));
            bizParamDTO.setServiceStatusEnum(ServiceStatusEnum.statusOf(body.getJSONObject("message").getInteger("serviceStatus")));
            bizParamDTO.setAllServiceBizParam(body.getJSONObject("message").getString("allServiceBizParam"));
            bizParamDTO.setChangedBizParam(body.getJSONObject("message").getString("changedBizParam"));

            return bizParamDTO;
        } catch (Exception e) {
            log.error("获取[K歌之王]当前状态数据出错：serviceDataId={}", serviceDataId, e);
        }

        return null;
    }
}