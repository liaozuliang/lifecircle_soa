package com.idianyou.lifecircle.service.local.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.idianyou.lifecircle.dto.GetChannelDataDTO;
import com.idianyou.lifecircle.dto.GetTypeDataContext;
import com.idianyou.lifecircle.dto.QueryTypeDataParam;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.dto.vo.ChannelDataVO;
import com.idianyou.lifecircle.entity.mongo.LifeCircleContentMongo;
import com.idianyou.lifecircle.entity.mongo.LifeCircleServiceBizParamMongo;
import com.idianyou.lifecircle.enums.DataQueryTypeEnum;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.MoreJumpTypeEnum;
import com.idianyou.lifecircle.enums.RoomPermissionTypeEnum;
import com.idianyou.lifecircle.enums.YesNoEnum;
import com.idianyou.lifecircle.es.LifeCircleContentESService;
import com.idianyou.lifecircle.mongo.LifeCircleContentRepository;
import com.idianyou.lifecircle.mongo.LifeCircleServiceBizParamRepository;
import com.idianyou.lifecircle.service.impl.dubbo.AppListDubboServiceImpl;
import com.idianyou.lifecircle.service.local.redis.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: 获取分类数据
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/27 15:56
 */
@Slf4j
public abstract class AbstractGetTypeDataService {

    @Autowired
    protected LifeCircleContentRepository lifeCircleContentRepository;

    @Autowired
    protected LifeCircleServiceBizParamRepository lifeCircleServiceBizParamRepository;

    @Autowired
    protected AppListDubboServiceImpl appListDubboServiceImpl;

    @Autowired
    protected RedisCacheService redisCacheService;

    @Autowired
    protected LifeCircleContentESService lifeCircleContentESService;

    public abstract boolean supports(GetTypeDataContext context);

    public abstract ChannelDataGroupVO getData(GetTypeDataContext context);

    public ChannelDataGroupVO getTypeData(GetTypeDataContext context) {
        if (context == null
                || context.getDataNum() <= 0
                || context.getUserId() == null
                || context.getDataTypeEnum() == null
                || context.getShowTypeEnum() == null) {
            log.error("参数错误，无法获取分类数据，context={}", JSON.toJSONString(context));
            return null;
        }

        ChannelDataGroupVO dataGroupVO = null;
        try {
            dataGroupVO = getData(context);
        } catch (Exception e) {
            log.error("{}-分类[{}({})]数据失败，context={}",
                    context.getBaseLog(),
                    context.getQueryTypeEnum().getDesc(),
                    context.getShowTypeEnum().getType(),
                    JSON.toJSONString(context),
                    e);
        }

        return dataGroupVO;
    }

    public List<LifeCircleContentMongo> getTypeData(GetTypeDataContext context, QueryTypeDataParam param) {
        List<LifeCircleContentMongo> contentMongoList = new ArrayList<>(context.getDataNum());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<LifeCircleContentMongo> pageDataList = null;
        boolean doQuery = true;
        int queryTimes = 0;

        if (param.getPageSize() <= 0) {
            param.setPageSize(100);
        }

        do {
            //pageDataList = lifeCircleContentRepository.getTypeData(param);
            pageDataList = lifeCircleContentESService.getTypeData(param);
            queryTimes++;

            if (CollectionUtils.isEmpty(pageDataList)) {
                doQuery = false;
                break;
            }

            param.setMaxId(pageDataList.get(pageDataList.size() - 1).get_id());

            // 随机打乱顺序
            if (param.isRandomGetData()) {
                Collections.shuffle(pageDataList);
            }

            for (LifeCircleContentMongo contentMongo : pageDataList) {
                if (contentMongoList.size() >= context.getDataNum()) {
                    doQuery = false;
                    break;
                }

                if (!param.isCategoryQuery()
                        && context.getExcludeDataIdSet().contains(contentMongo.get_id().toString())) {
                    continue;
                }

                if (context.getPassedDataIdSet().contains(contentMongo.get_id().toString())) {
                    continue;
                }

                if (StringUtils.isNotBlank(contentMongo.getBizDataId())
                        && context.getPassedBizDataIdSet().contains(contentMongo.getBizDataId())) {
                    continue;
                }

                // 检查房间权限(K歌之王、最佳辩手)
                if (Objects.equals(DataQueryTypeEnum.K_SONG, context.getQueryTypeEnum())
                        || Objects.equals(DataQueryTypeEnum.BEST_DEBATER, context.getQueryTypeEnum())) {
                    if (!checkRoomPermission(contentMongo, context.getUserId())) {
                        context.getPassedDataIdSet().add(contentMongo.get_id().toString());
                        continue;
                    }
                }

                contentMongoList.add(contentMongo);
                context.getPassedDataIdSet().add(contentMongo.get_id().toString());

                if (StringUtils.isNotBlank(contentMongo.getBizDataId())) {
                    context.getPassedBizDataIdSet().add(contentMongo.getBizDataId());
                }
            }
        } while (doQuery);

        stopWatch.stop();
        log.info("{}-分类[{}({})]数据完成，共{}条，查询了{}次，耗时{}ms",
                context.getBaseLog(),
                context.getQueryTypeEnum() != null ? context.getQueryTypeEnum().getDesc() : "",
                context.getShowTypeEnum() != null ? context.getShowTypeEnum().getType() : "",
                contentMongoList.size(),
                queryTimes,
                stopWatch.getTotalTimeMillis());

        return contentMongoList;
    }

    /**
     * 检查房间权限(K歌之王、最佳辩手)
     * @param contentMongo
     * @param userId
     * @return
     */
    public boolean checkRoomPermission(LifeCircleContentMongo contentMongo, Long userId) {
        if (contentMongo == null || userId == null) {
            return false;
        }

        if (DataTypeEnum.K_SONG_KING.getType().equals(contentMongo.getDataType())
                || DataTypeEnum.BEST_DEBATER.getType().equals(contentMongo.getDataType())) {

            LifeCircleServiceBizParamMongo bizParamMongo = lifeCircleServiceBizParamRepository.getById(contentMongo.getBizParamDataId());
            Integer permission = bizParamMongo.getServiceSpecialParam().getInteger("permission");
            List<String> permissionUserIds = (List<String>) bizParamMongo.getServiceSpecialParam().get("permissionUserIds");

            if (permission != null) {
                if (RoomPermissionTypeEnum.SECRET.getType().equals(permission)) {
                    log.error("没有房间权限[私密], showType={}, contentId={}", contentMongo.getShowType(), contentMongo.get_id());
                    return false;
                }

                if (RoomPermissionTypeEnum.JOIN.getType().equals(permission)
                        && !org.springframework.util.CollectionUtils.isEmpty(permissionUserIds)
                        && !permissionUserIds.contains(userId.toString())) {
                    log.error("没有房间权限[部分参与], showType={}, contentId={}", contentMongo.getShowType(), contentMongo.get_id());
                    return false;
                }

                if (RoomPermissionTypeEnum.NOT_JOIN.getType().equals(permission)
                        && !org.springframework.util.CollectionUtils.isEmpty(permissionUserIds)
                        && permissionUserIds.contains(userId.toString())) {
                    log.error("没有房间权限[不让谁参与], showType={}, contentId={}", contentMongo.getShowType(), contentMongo.get_id());
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public ChannelDataGroupVO getTypeData(GetTypeDataContext context, QueryTypeDataParam param, ChannelDataGroupVO dataGroupVO) {
        // 查询分类数据
        List<LifeCircleContentMongo> contentMongoList = getTypeData(context, param);
        if (CollectionUtils.isEmpty(contentMongoList)) {
            return null;
        }

        GetChannelDataDTO dto = new GetChannelDataDTO();
        dto.setUserId(context.getUserId());

        Map<Long, JSONObject> contentId_serviceSpecialParam_map = new HashMap<>(1);
        ChannelDataVO channelDataVO = null;

        // 组装分组
        for (LifeCircleContentMongo contentMongo : contentMongoList) {
            contentId_serviceSpecialParam_map.clear();

            channelDataVO = appListDubboServiceImpl.transferToChannelDataVO(contentMongo, dto, contentId_serviceSpecialParam_map);
            if (channelDataVO == null) {
                continue;
            }

            dataGroupVO.getDataList().add(channelDataVO);

            // 不分组
            if (YesNoEnum.NO.getCode().equals(dataGroupVO.getIsGroup())) {
                continue;
            }

            // 设置“更多”的跳转协议
            if (MoreJumpTypeEnum.PROTOCOL.getType().equals(dataGroupVO.getMoreJumpType())
                    && StringUtils.isBlank(dataGroupVO.getMoreJumpProtocol())) {
                // 从传参获取跳转协议
                if (contentId_serviceSpecialParam_map.size() > 0) {
                    for (JSONObject jsonObject : contentId_serviceSpecialParam_map.values()) {
                        String categoryProtocol = jsonObject.getString("categoryProtocol");
                        if (StringUtils.isNotBlank(categoryProtocol)) {
                            dataGroupVO.setMoreJumpProtocol(categoryProtocol);
                            break;
                        }
                    }
                }

                // 从来源服务信息中获取跳转协议
                if (StringUtils.isBlank(dataGroupVO.getMoreJumpProtocol())
                        && contentMongo.getFromServiceInfo() != null
                        && StringUtils.isNotBlank(contentMongo.getFromServiceInfo().getJumpProtocol())) {
                    dataGroupVO.setMoreJumpProtocol(contentMongo.getFromServiceInfo().getJumpProtocol());
                }
            }

            // 设置主题简介
            if (StringUtils.isBlank(dataGroupVO.getGroupDesc())) {
                // 从传参获取
                if (contentId_serviceSpecialParam_map.size() > 0) {
                    for (JSONObject jsonObject : contentId_serviceSpecialParam_map.values()) {
                        String categoryDesc = jsonObject.getString("categoryDesc");
                        if (StringUtils.isNotBlank(categoryDesc)) {
                            dataGroupVO.setGroupDesc(categoryDesc);
                            break;
                        }
                    }
                }
            }
        }

        if (dataGroupVO.getDataList().size() == 0) {
            return null;
        }

        if (YesNoEnum.YES.getCode().equals(dataGroupVO.getIsGroup())) {
            if (dataGroupVO.getDataList().size() >= 2) {
                dataGroupVO.setHasMore(YesNoEnum.YES.getCode());
            } else {
                dataGroupVO.setHasMore(YesNoEnum.NO.getCode());
            }
        }

        return dataGroupVO;
    }

    public static QueryTypeDataParam getQueryTypeDataParam(GetTypeDataContext context) {
        QueryTypeDataParam param = new QueryTypeDataParam();

        if (context.getDataTypeEnum() != null) {
            param.setDataType(context.getDataTypeEnum().getType());
        }

        if (context.getShowTypeEnum() != null) {
            param.setShowType(context.getShowTypeEnum().getType());
        }

        param.setClientId(context.getClientId());
        param.setServiceName(context.getServiceName());
        param.setNotServiceName(context.getNotServiceName());
        param.setCategoryId(context.getCategoryId());
        param.setCategoryName(context.getCategoryName());
        param.setIgnoreServiceStatus(context.isIgnoreServiceStatus());
        param.setChannelIdList(context.getChannelIdList());
        param.setMaxId(null);
        param.setRandomGetData(context.isRandomGetData());
        param.setMinCreateTime(context.getMinCreateTime());

        return param;
    }
}