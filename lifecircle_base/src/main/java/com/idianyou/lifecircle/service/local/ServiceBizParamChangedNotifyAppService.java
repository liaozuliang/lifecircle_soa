package com.idianyou.lifecircle.service.local;

import com.alibaba.fastjson.JSON;
import com.idianyou.im.limit.service.ImLimitService;
import com.idianyou.lifecircle.constants.RedisConstants;
import com.idianyou.lifecircle.constants.RedisKeys;
import com.idianyou.lifecircle.dto.BizParamChangedSocketNotifyDTO;
import com.idianyou.lifecircle.dto.ChangedDataDTO;
import com.idianyou.lifecircle.dto.UpdateServiceBizParamDTO;
import com.idianyou.lifecircle.entity.mongo.LifeCircleContentMongo;
import com.idianyou.lifecircle.entity.mongo.LifeCircleServiceBizParamMongo;
import com.idianyou.lifecircle.enums.BizParamNotifyUpdateTypeEnum;
import com.idianyou.lifecircle.enums.SocketBizTypeEnum;
import com.idianyou.lifecircle.mongo.LifeCircleContentRepository;
import com.idianyou.lifecircle.service.LifeCircleContentPublishDubboService;
import com.idianyou.lifecircle.util.DisconfPropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 服务业务数据有变动时通知前端
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/30 11:38
 */
@Slf4j
@Service
public class ServiceBizParamChangedNotifyAppService {

    @Autowired
    private ImLimitService imLimitService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LifeCircleContentPublishDubboService lifeCircleContentPublishDubboService;

    @Autowired
    private LifeCircleContentRepository lifeCircleContentRepository;

    @Value("${notifySendUserId}")
    private Long notifySendUserId;

    @Value("${bizParamChangedNotifyUpdateType}")
    private Integer bizParamChangedNotifyUpdateType;

    /**
     * 通知前端数据有变更
     * @param dto
     */
    public void notifyApp(UpdateServiceBizParamDTO dto) {
        try {
            String bizParamDataId = LifeCircleServiceBizParamMongo.getDataId(dto.getDataTypeEnum(), dto.getServiceDataId());

            List<Long> contentIdList = lifeCircleContentPublishDubboService.getContentIdsByBizParamDataId(bizParamDataId);
            if (CollectionUtils.isEmpty(contentIdList)) {
                return;
            }

            for (Long contentId : contentIdList) {
                String requestNoListStr = (String) redisTemplate.boundValueOps(RedisKeys.CONTENTID_RELATED_REQUESTNOS.concat(contentId.toString())).get();
                if (StringUtils.isNotBlank(requestNoListStr)) {
                    List<String> requestNoList = JSON.parseArray(requestNoListStr, String.class);
                    List<String> needRemoveList = new ArrayList<>(requestNoList.size());
                    if (!CollectionUtils.isEmpty(requestNoList)) {
                        for (String requestNo : requestNoList) {
                            // 检查最后活跃时间超过半小时就不再通知
                            String lastActiveTimeStr = (String) redisTemplate.boundValueOps(RedisKeys.REQUESTNO_RELATED_LAST_ACTIVE_TIME.concat(requestNo)).get();
                            if (StringUtils.isBlank(lastActiveTimeStr)) {
                                needRemoveList.add(requestNo);
                                log.info("最后活跃时间为空不通知前端刷新数据, requestNo={}", requestNo);
                                continue;
                            }

                            long time = System.currentTimeMillis() - NumberUtils.toLong(lastActiveTimeStr, 0);
                            if (time > 1 * 60 * 60 * 1000) {
                                needRemoveList.add(requestNo);
                                log.info("最后活跃时间超过1小时不通知前端刷新数据, requestNo={}", requestNo);
                                continue;
                            }

                            String[] arr = requestNo.split("_");
                            String userId = arr[0];
                            String userCurrRequestNo = (String) redisTemplate.boundValueOps(RedisKeys.USERID_RELATED_REQUESTNO.concat(userId)).get();
                            if (requestNo.equals(userCurrRequestNo)) {
                                doNotify(Long.valueOf(userId), contentId, dto);
                            } else {
                                needRemoveList.add(requestNo);
                            }
                        }
                    }

                    if (needRemoveList.size() > 0) {
                        List<String> updateRequestNoList = new ArrayList<>(requestNoList.size());
                        updateRequestNoList.addAll(requestNoList);
                        updateRequestNoList.removeAll(needRemoveList);
                        redisTemplate.boundValueOps(RedisKeys.CONTENTID_RELATED_REQUESTNOS.concat(contentId.toString())).set(JSON.toJSONString(updateRequestNoList), RedisConstants.CACHE_DAYS, TimeUnit.DAYS);
                    }
                }
            }
        } catch (Exception e) {
            log.error("通知前端数据有变更出错：dto={}", JSON.toJSONString(dto), e);
        }
    }

    public void doNotify(Long toUserId, Long contentId, UpdateServiceBizParamDTO dto) {
        try {
            BizParamNotifyUpdateTypeEnum bizParamNotifyUpdateTypeEnum = BizParamNotifyUpdateTypeEnum.typeOf(bizParamChangedNotifyUpdateType);
            if (bizParamNotifyUpdateTypeEnum == null) {
                bizParamNotifyUpdateTypeEnum = BizParamNotifyUpdateTypeEnum.API;
            }

            List<ChangedDataDTO> changedDataList = new ArrayList<>();

            ChangedDataDTO changedDataDTO = new ChangedDataDTO();
            changedDataDTO.setId(contentId);
            changedDataList.add(changedDataDTO);

            if (dto != null && BizParamNotifyUpdateTypeEnum.DIRECT.equals(bizParamNotifyUpdateTypeEnum)) {
                LifeCircleContentMongo contentMongo = lifeCircleContentRepository.getById(contentId);

                changedDataDTO.setDataType(dto.getDataTypeEnum().getType());
                changedDataDTO.setShowType(contentMongo.getShowType());
                changedDataDTO.setBizParam(dto.getChangedBizParam());
            }

            BizParamChangedSocketNotifyDTO notifyDTO = new BizParamChangedSocketNotifyDTO();
            notifyDTO.setBizType(SocketBizTypeEnum.UPDATE_CHANGED_BIZ_PARAM.getType());
            notifyDTO.setUpdateType(bizParamNotifyUpdateTypeEnum.getType());
            notifyDTO.setChangedDataDTOList(changedDataList);

            imLimitService.sendPrivateFreshMsg(notifySendUserId, toUserId, 9, JSON.toJSONString(notifyDTO));
        } catch (Exception e) {
            log.error("调用IM通道接口通知前端数据有变更出错：toUserId={}, contentId={}, dto={}", toUserId, contentId, JSON.toJSONString(dto), e);
        }
    }
}