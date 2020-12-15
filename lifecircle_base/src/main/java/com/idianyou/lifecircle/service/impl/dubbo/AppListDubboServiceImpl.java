package com.idianyou.lifecircle.service.impl.dubbo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.idianyou.business.constant.UserIdentitiesEnum;
import com.idianyou.business.usercenter.dto.UserInfoDTO;
import com.idianyou.business.usercenter.service.UserCenterService;
import com.idianyou.circle.dto.LifeCircleServiceBizParamDTO;
import com.idianyou.lifecircle.constants.RedisConstants;
import com.idianyou.lifecircle.constants.RedisKeys;
import com.idianyou.lifecircle.dto.GetChannelDataContext;
import com.idianyou.lifecircle.dto.GetChannelDataDTO;
import com.idianyou.lifecircle.dto.GetUserDataDTO;
import com.idianyou.lifecircle.dto.SearchDTO;
import com.idianyou.lifecircle.dto.UserInfo;
import com.idianyou.lifecircle.dto.request.DelUserDataReq;
import com.idianyou.lifecircle.dto.request.GetChannelServicePortalReq;
import com.idianyou.lifecircle.dto.request.GetDetailsReq;
import com.idianyou.lifecircle.dto.request.GetUserNativeTypeReq;
import com.idianyou.lifecircle.dto.request.SetUserNativeTypeReq;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.dto.vo.ChannelDataVO;
import com.idianyou.lifecircle.dto.vo.ContentDetailsVO;
import com.idianyou.lifecircle.dto.vo.GetUserNativeTypeVO;
import com.idianyou.lifecircle.dto.vo.IconBtnRow;
import com.idianyou.lifecircle.dto.vo.SearchDataVO;
import com.idianyou.lifecircle.dto.vo.UnifiedSearchDataVO;
import com.idianyou.lifecircle.dto.vo.UserDataVO;
import com.idianyou.lifecircle.entity.mongo.LifeCircleChannelServicePortalMongo;
import com.idianyou.lifecircle.entity.mongo.LifeCircleContentMongo;
import com.idianyou.lifecircle.entity.mongo.LifeCircleServiceBizParamMongo;
import com.idianyou.lifecircle.enums.ContentTypeEnum;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.IdentityTypeEnum;
import com.idianyou.lifecircle.enums.JumpProtocolEnum;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.enums.MoreJumpTypeEnum;
import com.idianyou.lifecircle.enums.RoomPermissionTypeEnum;
import com.idianyou.lifecircle.enums.ShowTypeEnum;
import com.idianyou.lifecircle.enums.TimeDescEnum;
import com.idianyou.lifecircle.enums.UserNativeTypeEnum;
import com.idianyou.lifecircle.enums.YesNoEnum;
import com.idianyou.lifecircle.es.LifeCircleContentES;
import com.idianyou.lifecircle.es.LifeCircleContentESService;
import com.idianyou.lifecircle.exception.LifeCircleException;
import com.idianyou.lifecircle.mongo.LifeCircleChannelServicePortalRepository;
import com.idianyou.lifecircle.mongo.LifeCircleContentRepository;
import com.idianyou.lifecircle.mongo.LifeCircleServiceBizParamRepository;
import com.idianyou.lifecircle.service.AppListDubboService;
import com.idianyou.lifecircle.service.LifeCircleContentPublishDubboService;
import com.idianyou.lifecircle.service.local.FactoryService;
import com.idianyou.lifecircle.util.DateUtils;
import com.idianyou.spi.common.InnerRpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/6 17:02
 */
@Slf4j
@Service
public class AppListDubboServiceImpl implements AppListDubboService {

    @Autowired
    private LifeCircleContentRepository lifeCircleContentRepository;

    @Autowired
    private LifeCircleServiceBizParamRepository lifeCircleServiceBizParamRepository;

    @Autowired
    private LifeCircleChannelServicePortalRepository lifeCircleChannelServicePortalRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private LifeCircleContentPublishDubboService lifeCircleContentPublishDubboService;

    @Autowired
    private FactoryService factoryService;

    @Autowired
    private LifeCircleContentESService lifeCircleContentESService;

    @Value("${isTestEnv:false}")
    private boolean isTestEnv;

    @Override
    public List<ChannelDataGroupVO> getChannelData(GetChannelDataDTO dto) {
        GetChannelDataContext context = new GetChannelDataContext();
        context.setUserId(dto.getUserId());
        context.setDeviceId(dto.getDeviceId());
        context.setChannelEnum(dto.getChannelEnum());
        context.setReqNo(dto.getReqNo());

        if (isTestEnv) {
            context.setTimeDescEnum(TimeDescEnum.getTestTimeDescEnum());
        } else {
            context.setTimeDescEnum(TimeDescEnum.getCurrentTimeDescEnum());
        }

        UserNativeTypeEnum userNativeTypeEnum = UserNativeTypeEnum.NULL;
        try {
            GetUserNativeTypeReq req = new GetUserNativeTypeReq();
            req.setUserId(dto.getUserId());
            userNativeTypeEnum = UserNativeTypeEnum.codeOf(getUserNativeType(req).getUserNativeType());
        } catch (Exception e) {
            log.error("获取用户在本地的情况出错: dto={}", JSON.toJSONString(dto), e);
        }

        if (userNativeTypeEnum == null || UserNativeTypeEnum.NULL.equals(userNativeTypeEnum)) {
            userNativeTypeEnum = UserNativeTypeEnum.NATIVE_OUT;
        }

        context.setUserNativeTypeEnum(userNativeTypeEnum);

        if (LifeCircleChannelEnum.RECOMMEND.equals(dto.getChannelEnum())) {
            return factoryService.getServiceForRecommendChannel(context).getChannelData(context);
        } else if (LifeCircleChannelEnum.LIFE.equals(dto.getChannelEnum())) {
            return factoryService.getServiceForLifeChannel(context).getChannelData(context);
        } else if (LifeCircleChannelEnum.AMUSEMENT.equals(dto.getChannelEnum())) {
            return factoryService.getServiceForAmusementChannel(context).getChannelData(context);
        } else {
            return factoryService.getServiceForNormalChannel(context).getChannelData(context);
        }
    }

    public List<ChannelDataGroupVO> getChannelData_old(GetChannelDataDTO dto) {
        // 推荐频道、生活频道、娱乐频道
        if (LifeCircleChannelEnum.RECOMMEND.equals(dto.getChannelEnum())
                || LifeCircleChannelEnum.LIFE.equals(dto.getChannelEnum())
                || LifeCircleChannelEnum.AMUSEMENT.equals(dto.getChannelEnum())) {
            GetChannelDataContext context = new GetChannelDataContext();
            context.setUserId(dto.getUserId());
            context.setDeviceId(dto.getDeviceId());
            context.setChannelEnum(dto.getChannelEnum());
            context.setReqNo(dto.getReqNo());

            if (isTestEnv) {
                context.setTimeDescEnum(TimeDescEnum.getTestTimeDescEnum());
            } else {
                context.setTimeDescEnum(TimeDescEnum.getCurrentTimeDescEnum());
            }

            UserNativeTypeEnum userNativeTypeEnum = UserNativeTypeEnum.NULL;
            try {
                GetUserNativeTypeReq req = new GetUserNativeTypeReq();
                req.setUserId(dto.getUserId());
                userNativeTypeEnum = UserNativeTypeEnum.codeOf(getUserNativeType(req).getUserNativeType());
            } catch (Exception e) {
                log.error("获取用户在本地的情况出错: dto={}", JSON.toJSONString(dto), e);
            }

            if (userNativeTypeEnum == null || UserNativeTypeEnum.NULL.equals(userNativeTypeEnum)) {
                userNativeTypeEnum = UserNativeTypeEnum.NATIVE_OUT;
            }

            context.setUserNativeTypeEnum(userNativeTypeEnum);

            if (LifeCircleChannelEnum.RECOMMEND.equals(dto.getChannelEnum())) {
                return factoryService.getServiceForRecommendChannel(context).getChannelData(context);
            } else if (LifeCircleChannelEnum.LIFE.equals(dto.getChannelEnum())) {
                return factoryService.getServiceForLifeChannel(context).getChannelData(context);
            } else if (LifeCircleChannelEnum.AMUSEMENT.equals(dto.getChannelEnum())) {
                return factoryService.getServiceForAmusementChannel(context).getChannelData(context);
            }
        }

        List<ChannelDataGroupVO> channelDataGroupVOList = new ArrayList<>();
        List<ChannelDataVO> channelDataVOList = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        Date maxCreateTime = new Date();
        int pageSize = 100;

        Integer channelId = null;
        if (!LifeCircleChannelEnum.RECOMMEND.equals(dto.getChannelEnum())) {
            channelId = dto.getChannelEnum().getChannelId();
        }

        Set<Long> includeIdSet = new HashSet<>(dto.getDataNum());

        List<LifeCircleContentMongo> contentMongoList = null;
        ChannelDataVO channelDataVO = null;
        Map<Long, JSONObject> contentId_serviceSpecialParam_map = new HashMap<>();

        boolean doQuery = false;
        int findCount = 0; // 已查询到数据条数
        int queryTimes = 0; // 查询次数

        String requestNo = dto.getUserId() + "_" + dto.getReqNo();
        String channelRequestNo = requestNo + "_" + dto.getChannelEnum().getChannelId();

        try {
            // 记录当前批次号最后活跃时间
            redisTemplate.boundValueOps(RedisKeys.REQUESTNO_RELATED_LAST_ACTIVE_TIME.concat(requestNo)).set(System.currentTimeMillis() + "", 60, TimeUnit.MINUTES);

            // 当前批次号频道已查询了哪些数据
            Set<String> reqestNoContentIdsSet = redisTemplate.boundSetOps(RedisKeys.REQUESTNO_RELATED_CONTENTIDS_SET.concat(channelRequestNo)).members();
            if (reqestNoContentIdsSet == null) {
                reqestNoContentIdsSet = new HashSet<>();
            }

            do {
                contentMongoList = lifeCircleContentRepository.getChannelData(maxCreateTime, pageSize, channelId);
                queryTimes++;

                if (!CollectionUtils.isEmpty(contentMongoList)) {
                    for (LifeCircleContentMongo contentMongo : contentMongoList) {
                        if (findCount >= dto.getDataNum()) {
                            break;
                        }

                        if (contentMongo.getCreateTime().before(maxCreateTime)) {
                            maxCreateTime = contentMongo.getCreateTime();
                        }

                        if (includeIdSet.contains(contentMongo.get_id())) {
                            continue;
                        }

                        if (reqestNoContentIdsSet.contains(contentMongo.get_id().toString())) {
                            continue;
                        }

                        ChannelDataVO dataVO = transferToChannelDataVO(contentMongo, dto, contentId_serviceSpecialParam_map);
                        if (dataVO == null) {
                            continue;
                        }

                        channelDataVOList.add(dataVO);
                        includeIdSet.add(contentMongo.get_id());

                        findCount++;
                    }
                }

                if (findCount < dto.getDataNum()
                        && !CollectionUtils.isEmpty(contentMongoList)
                        && contentMongoList.size() == pageSize) {
                    doQuery = true;
                } else {
                    doQuery = false;
                }
            } while (doQuery);

            // 分组
            setGroup(channelDataGroupVOList, channelDataVOList, dto, contentId_serviceSpecialParam_map);

            if (includeIdSet.size() > 0) {
                for (Long contentId : includeIdSet) {
                    List<String> requestNoList = null;

                    String requestNoListStr = (String) redisTemplate.boundValueOps(RedisKeys.CONTENTID_RELATED_REQUESTNOS.concat(contentId.toString())).get();
                    if (StringUtils.isNotBlank(requestNoListStr)) {
                        requestNoList = JSON.parseArray(requestNoListStr, String.class);
                    }

                    if (requestNoList == null) {
                        requestNoList = new ArrayList<>();
                    }

                    if (!requestNoList.contains(requestNo)) {
                        requestNoList.add(requestNo);

                        // 动态ID关联查询批次号
                        redisTemplate.boundValueOps(RedisKeys.CONTENTID_RELATED_REQUESTNOS.concat(contentId.toString())).set(JSON.toJSONString(requestNoList), RedisConstants.CACHE_DAYS, TimeUnit.DAYS);
                    }

                    // 保存到频道已查询
                    redisTemplate.boundSetOps(RedisKeys.REQUESTNO_RELATED_CONTENTIDS_SET.concat(channelRequestNo)).add(contentId.toString());
                    redisTemplate.boundSetOps(RedisKeys.REQUESTNO_RELATED_CONTENTIDS_SET.concat(channelRequestNo)).expire(RedisConstants.CACHE_DAYS, TimeUnit.DAYS);
                }

                // 记录当前用户的查询批次号
                redisTemplate.boundValueOps(RedisKeys.USERID_RELATED_REQUESTNO.concat(dto.getUserId().toString())).set(requestNo, RedisConstants.CACHE_DAYS, TimeUnit.DAYS);
            }

            long takeTime = System.currentTimeMillis() - startTime;
            log.info("查询频道数据成功，共{}条，查询了{}次，耗时{}ms, dto={}", findCount, queryTimes, takeTime, JSON.toJSONString(dto));
        } catch (LifeCircleException e) {
            log.error("查询频道数据出错：dto={}", JSON.toJSONString(dto), e);
            throw e;
        } catch (Exception e) {
            log.error("查询频道数据出错：dto={}", JSON.toJSONString(dto), e);
            throw new LifeCircleException("查询频道数据出错");
        }

        return channelDataGroupVOList;
    }

    /**
     * 分组
     * @param channelDataGroupVOList
     * @param channelDataVOList
     * @param dto
     */
    public void setGroup(List<ChannelDataGroupVO> channelDataGroupVOList, List<ChannelDataVO> channelDataVOList, GetChannelDataDTO dto, Map<Long, JSONObject> contentId_serviceSpecialParam_map) {
        if (!CollectionUtils.isEmpty(channelDataVOList)) {
            boolean needGroup = false;
            if (LifeCircleChannelEnum.RECOMMEND.equals(dto.getChannelEnum())
                    || LifeCircleChannelEnum.LIFE.equals(dto.getChannelEnum())
                    || LifeCircleChannelEnum.AMUSEMENT.equals(dto.getChannelEnum())) {
                needGroup = true;
            }

            String groupName = null;
            String categoryDesc = null;
            String moreJumpProtocol = null;

            Map<String, ChannelDataGroupVO> dataGroupVOMap = new HashMap<>();
            for (ChannelDataVO dataVO : channelDataVOList) {
                ChannelDataGroupVO groupVO = null;

                // 吃瓜商城、赶集-商品
                if (ShowTypeEnum.CHIGUA_MALL_SERVICE_DYNAMIC.getType().equals(dataVO.getShowType())
                        || ShowTypeEnum.MARKET_PRODUCT.getType().equals(dataVO.getShowType())
                        || ShowTypeEnum.NOVEL_SERVICE_DYNAMIC.getType().equals(dataVO.getShowType())) {
                    JSONObject serviceSpecialParam = contentId_serviceSpecialParam_map.get(dataVO.getId());
                    if (serviceSpecialParam != null) {
                        groupName = serviceSpecialParam.getString("categoryName");
                        categoryDesc = serviceSpecialParam.getString("categoryDesc");
                        moreJumpProtocol = serviceSpecialParam.getString("categoryProtocol");
                    }
                    if (StringUtils.isBlank(groupName)) {
                        groupName = dataVO.getFromServiceInfo().getName();
                    }
                    if (StringUtils.isBlank(moreJumpProtocol)) {
                        moreJumpProtocol = dataVO.getFromServiceInfo().getJumpProtocol();
                    }

                    groupVO = dataGroupVOMap.get(groupName);
                }

                // 直播
                if (ShowTypeEnum.LIVE_SERVICE_DYNAMIC.getType().equals(dataVO.getShowType())) {
                    groupVO = dataGroupVOMap.get("全民直播");
                }

                // Boss开讲
                if (ShowTypeEnum.BOSS_LECTURE_SERVICE_DYNAMIC.getType().equals(dataVO.getShowType())) {
                    groupVO = dataGroupVOMap.get("Boss开讲");
                }

                // 赶集-商户
                if (ShowTypeEnum.MARKET_LIFE_SERVICE.getType().equals(dataVO.getShowType())) {
                    groupVO = dataGroupVOMap.get(dataVO.getFromServiceInfo().getName());
                }

                if (groupVO == null) {
                    groupVO = new ChannelDataGroupVO();
                    groupVO.setIsGroup(YesNoEnum.NO.getCode());
                    channelDataGroupVOList.add(groupVO);
                }

                groupVO.getDataList().add(dataVO);

                if (needGroup) {
                    groupVO.setHasMore(YesNoEnum.NO.getCode());

                    // 吃瓜商城、赶集-商品
                    if (ShowTypeEnum.CHIGUA_MALL_SERVICE_DYNAMIC.getType().equals(dataVO.getShowType())
                            || ShowTypeEnum.MARKET_PRODUCT.getType().equals(dataVO.getShowType())
                            || ShowTypeEnum.NOVEL_SERVICE_DYNAMIC.getType().equals(dataVO.getShowType())) {
                        groupVO.setIsGroup(YesNoEnum.YES.getCode());
                        groupVO.setGroupName(groupName);
                        groupVO.setGroupDesc(categoryDesc);
                        groupVO.setMoreJumpType(MoreJumpTypeEnum.PROTOCOL.getType());
                        groupVO.setMoreJumpProtocol(moreJumpProtocol);
                        dataGroupVOMap.put(groupVO.getGroupName(), groupVO);
                    }

                    // 直播
                    if (ShowTypeEnum.LIVE_SERVICE_DYNAMIC.getType().equals(dataVO.getShowType())) {
                        groupVO.setIsGroup(YesNoEnum.YES.getCode());
                        groupVO.setGroupName("全民直播");
                        groupVO.setMoreJumpType(MoreJumpTypeEnum.CHANNEL.getType());
                        groupVO.setChannelId(LifeCircleChannelEnum.LIVE.getChannelId());
                        dataGroupVOMap.put(groupVO.getGroupName(), groupVO);
                    }

                    // Boss开讲
                    if (ShowTypeEnum.BOSS_LECTURE_SERVICE_DYNAMIC.getType().equals(dataVO.getShowType())) {
                        groupVO.setIsGroup(YesNoEnum.YES.getCode());
                        groupVO.setGroupName("Boss开讲");
                        groupVO.setMoreJumpType(MoreJumpTypeEnum.CHANNEL.getType());
                        groupVO.setChannelId(LifeCircleChannelEnum.LIVE.getChannelId());
                        dataGroupVOMap.put(groupVO.getGroupName(), groupVO);
                    }

                    // 赶集-商户
                    if (ShowTypeEnum.MARKET_LIFE_SERVICE.getType().equals(dataVO.getShowType())) {
                        groupVO.setIsGroup(YesNoEnum.YES.getCode());
                        groupVO.setGroupName(dataVO.getFromServiceInfo().getName());
                        groupVO.setGroupDesc(categoryDesc);
                        groupVO.setMoreJumpType(MoreJumpTypeEnum.PROTOCOL.getType());
                        groupVO.setMoreJumpProtocol(dataVO.getFromServiceInfo().getJumpProtocol());
                        dataGroupVOMap.put(groupVO.getGroupName(), groupVO);
                    }

                    if (groupVO.getDataList().size() >= 2) {
                        groupVO.setHasMore(YesNoEnum.YES.getCode());
                    }
                }
            }
        }
    }

    public ChannelDataVO transferToChannelDataVO(LifeCircleContentMongo contentMongo, GetChannelDataDTO dto, Map<Long, JSONObject> contentId_serviceSpecialParam_map) {
        ChannelDataVO channelDataVO = new ChannelDataVO();

        channelDataVO.setId(contentMongo.get_id());
        channelDataVO.setShowType(contentMongo.getShowType());
        channelDataVO.setShareContent(contentMongo.getShareContent());
        channelDataVO.setFromServiceInfo(contentMongo.getFromServiceInfo());
        channelDataVO.setChannelIdList(contentMongo.getChannelIdList());

        LifeCircleServiceBizParamMongo bizParamMongo = lifeCircleServiceBizParamRepository.getById(contentMongo.getBizParamDataId());

        // 处理视频Id错误问题
        if (DataTypeEnum.FRIEND_STYLE_DATA.getType().equals(contentMongo.getDataType())) {
            LifeCircleServiceBizParamDTO bizParamDTO = JSON.parseObject(bizParamMongo.getServiceBizParam()).toJavaObject(LifeCircleServiceBizParamDTO.class);
            if (bizParamDTO.getContent() != null
                    && bizParamDTO.getContent().getVideoInfo() != null) {
                String videoId = bizParamDTO.getContent().getVideoInfo().getVideoId();
                if (StringUtils.isNotBlank(videoId) && videoId.contains("_")) {
                    bizParamDTO.getContent().getVideoInfo().setVideoId(contentMongo.get_id().toString());
                    bizParamMongo.setServiceBizParam(JSON.toJSONString(bizParamDTO));

                    LifeCircleServiceBizParamMongo updateMongo = new LifeCircleServiceBizParamMongo();
                    updateMongo.set_id(bizParamMongo.get_id());
                    updateMongo.setServiceBizParam(bizParamMongo.getServiceBizParam());

                    lifeCircleServiceBizParamRepository.updateById(updateMongo);
                }
            }
        }

        channelDataVO.setServiceBizParam(bizParamMongo.getServiceBizParam());

        // K歌之王、最佳辩手房间权限
        if (DataTypeEnum.K_SONG_KING.getType().equals(contentMongo.getDataType())
                || DataTypeEnum.BEST_DEBATER.getType().equals(contentMongo.getDataType())) {
            Integer permission = bizParamMongo.getServiceSpecialParam().getInteger("permission");
            List<String> permissionUserIds = (List<String>) bizParamMongo.getServiceSpecialParam().get("permissionUserIds");
            if (permission != null) {
                if (RoomPermissionTypeEnum.SECRET.getType().equals(permission)) {
                    log.error("没有房间权限[私密], showType={}, contentId={}", contentMongo.getShowType(), contentMongo.get_id());
                    return null;
                }

                if (RoomPermissionTypeEnum.JOIN.getType().equals(permission)
                        && !CollectionUtils.isEmpty(permissionUserIds)
                        && !permissionUserIds.contains(dto.getUserId().toString())) {
                    log.error("没有房间权限[部分参与], showType={}, contentId={}", contentMongo.getShowType(), contentMongo.get_id());
                    return null;
                }

                if (RoomPermissionTypeEnum.NOT_JOIN.getType().equals(permission)
                        && !CollectionUtils.isEmpty(permissionUserIds)
                        && permissionUserIds.contains(dto.getUserId().toString())) {
                    log.error("没有房间权限[不让谁参与], showType={}, contentId={}", contentMongo.getShowType(), contentMongo.get_id());
                    return null;
                }
            }
        }

        // 用户信息
        if (contentMongo.getUserId() != null) {
            InnerRpcResponse<UserInfoDTO> response = userCenterService.getUserInfo(contentMongo.getUserId());
            if (response != null && response.getData() != null) {
                UserInfo userInfo = new UserInfo();

                userInfo.setUserId(contentMongo.getUserId());
                userInfo.setNickName(response.getData().getUserName());
                userInfo.setHeadImg(response.getData().getUserImages());
                userInfo.setIdiograph(response.getData().getIdiograph());
                userInfo.setIdentityTypeList(new ArrayList<>());

                if (!CollectionUtils.isEmpty(response.getData().getUserIdentities())) {
                    for (Integer userIdentity : response.getData().getUserIdentities()) {
                        if (UserIdentitiesEnum.RUZI_CATTLE.getIdentitiesId().equals(userIdentity)) {
                            userInfo.getIdentityTypeList().add(IdentityTypeEnum.OXEN.getType());
                        } else if (UserIdentitiesEnum.PARTNER_SERVICE.getIdentitiesId().equals(userIdentity)) {
                            userInfo.getIdentityTypeList().add(IdentityTypeEnum.SERVICE_PARTNER.getType());
                        }
                    }
                }

                channelDataVO.setUserInfo(userInfo);
            } else {
                log.error("查询用户信息出错：userId={}, response={}", contentMongo.getUserId(), JSON.toJSONString(response));
            }
        }

        channelDataVO.setPraiseCount(0);
        if (contentMongo.getPraiseCount() != null) {
            channelDataVO.setPraiseCount(contentMongo.getPraiseCount());
        }

        channelDataVO.setCommentCount(0);
        if (contentMongo.getCommentCount() != null) {
            channelDataVO.setCommentCount(contentMongo.getCommentCount());
        }

        // 是否为热点
        channelDataVO.setIsHot(YesNoEnum.NO.getCode());

        // 本人是否点赞
        channelDataVO.setIsSelfPraised(YesNoEnum.NO.getCode());
        if (dto.getUserId() != null
                && ContentTypeEnum.PERSONAL.getType().equals(contentMongo.getContentType())
                && !CollectionUtils.isEmpty(contentMongo.getPraiseUserIds())) {
            if (contentMongo.getPraiseUserIds().contains(dto.getUserId().longValue())) {
                channelDataVO.setIsSelfPraised(YesNoEnum.YES.getCode());
            }
        }

        // 发布时间
        channelDataVO.setCreateTimeDesc(DateUtils.getBetweenTime(contentMongo.getCreateTime(), new Date()));

        if (bizParamMongo.getServiceSpecialParam() != null) {
            contentId_serviceSpecialParam_map.put(channelDataVO.getId(), bizParamMongo.getServiceSpecialParam());
        }

        return channelDataVO;
    }

    @Override
    public ContentDetailsVO getContentDetails(GetDetailsReq req) {
        LifeCircleContentMongo contentMongo = lifeCircleContentRepository.getById(req.getContentId());
        if (contentMongo == null) {
            throw new LifeCircleException("数据不存在或已被删除");
        }

        ContentDetailsVO detailsVO = new ContentDetailsVO();

        detailsVO.setId(contentMongo.get_id());
        detailsVO.setShowType(contentMongo.getShowType());
        detailsVO.setShareContent(contentMongo.getShareContent());
        detailsVO.setFromServiceInfo(contentMongo.getFromServiceInfo());

        LifeCircleServiceBizParamMongo bizParamMongo = lifeCircleServiceBizParamRepository.getById(contentMongo.getBizParamDataId());
        detailsVO.setServiceBizParam(bizParamMongo.getServiceBizParam());

        // 用户信息
        if (contentMongo.getUserId() != null) {
            InnerRpcResponse<UserInfoDTO> response = userCenterService.getUserInfo(contentMongo.getUserId());
            if (response != null && response.getData() != null) {
                UserInfo userInfo = new UserInfo();

                userInfo.setUserId(contentMongo.getUserId());
                userInfo.setNickName(response.getData().getUserName());
                userInfo.setHeadImg(response.getData().getUserImages());
                userInfo.setIdiograph(response.getData().getIdiograph());
                userInfo.setIdentityTypeList(new ArrayList<>());

                if (!CollectionUtils.isEmpty(response.getData().getUserIdentities())) {
                    for (Integer userIdentity : response.getData().getUserIdentities()) {
                        if (UserIdentitiesEnum.RUZI_CATTLE.getIdentitiesId().equals(userIdentity)) {
                            userInfo.getIdentityTypeList().add(IdentityTypeEnum.OXEN.getType());
                        } else if (UserIdentitiesEnum.PARTNER_SERVICE.getIdentitiesId().equals(userIdentity)) {
                            userInfo.getIdentityTypeList().add(IdentityTypeEnum.SERVICE_PARTNER.getType());
                        }
                    }
                }

                detailsVO.setUserInfo(userInfo);
            } else {
                log.error("查询用户信息出错：userId={}, response={}", contentMongo.getUserId(), JSON.toJSONString(response));
            }
        }

        detailsVO.setPraiseCount(0);
        if (contentMongo.getPraiseCount() != null) {
            detailsVO.setPraiseCount(contentMongo.getPraiseCount());
        }

        detailsVO.setCommentCount(0);
        if (contentMongo.getCommentCount() != null) {
            detailsVO.setCommentCount(contentMongo.getCommentCount());
        }

        // 本人是否点赞
        detailsVO.setIsSelfPraised(YesNoEnum.NO.getCode());
        if (req.getUserId() != null) {
            if (ContentTypeEnum.PERSONAL.getType().equals(contentMongo.getContentType())
                    && !CollectionUtils.isEmpty(contentMongo.getPraiseUserIds())) {
                if (contentMongo.getPraiseUserIds().contains(req.getUserId().longValue())) {
                    detailsVO.setIsSelfPraised(YesNoEnum.YES.getCode());
                }
            }
        }

        // 点赞用户
        if (!CollectionUtils.isEmpty(contentMongo.getPraiseUserIds())) {
            List<UserInfoDTO> userInfoDTOList = userCenterService.getUserInfos(contentMongo.getPraiseUserIds());
            if (!CollectionUtils.isEmpty(userInfoDTOList)) {
                List<UserInfo> praiseUserList = new ArrayList<>(userInfoDTOList.size());

                for (UserInfoDTO userInfoDTO : userInfoDTOList) {
                    UserInfo userInfo = new UserInfo();

                    userInfo.setUserId(userInfoDTO.getUserId());
                    userInfo.setNickName(userInfoDTO.getUserName());
                    userInfo.setHeadImg(userInfoDTO.getUserImages());
                }

                detailsVO.setPraiseUserList(praiseUserList);
            }
        }

        // 发布时间
        detailsVO.setCreateTimeDesc(DateUtils.getBetweenTime(contentMongo.getCreateTime(), new Date()));

        return detailsVO;
    }

    @Override
    public UserDataVO getUserData(GetUserDataDTO dto) {
        UserDataVO userDataVO = new UserDataVO();
        userDataVO.setHasMore(YesNoEnum.NO.getCode());
        userDataVO.setDataList(Collections.EMPTY_LIST);

        List<Integer> serviceShowTypeList = new ArrayList<>();
        serviceShowTypeList.add(ShowTypeEnum.MARKET_LIVE.getType());

        LifeCircleContentMongo lastOne = lifeCircleContentRepository.getUserLastOne(dto, serviceShowTypeList);
        if (lastOne == null) {
            return userDataVO;
        }

        List<LifeCircleContentMongo> contentMongoList = null;

        if (dto.getFirstId() != null && dto.getFirstId() > 0) {
            contentMongoList = lifeCircleContentRepository.getUserNewData(dto, serviceShowTypeList);
        } else {
            contentMongoList = lifeCircleContentRepository.getUserHistoryData(dto, serviceShowTypeList);
        }

        if (CollectionUtils.isEmpty(contentMongoList)) {
            return userDataVO;
        }

        Map<Long, JSONObject> contentId_serviceSpecialParam_map = new HashMap<>();
        List<ChannelDataVO> channelDataVOList = new ArrayList<>();
        List<ChannelDataGroupVO> channelDataGroupVOList = new ArrayList<>(contentMongoList.size());

        GetChannelDataDTO getChannelDataDTO = new GetChannelDataDTO();
        getChannelDataDTO.setUserId(dto.getLoginUserid());
        getChannelDataDTO.setChannelEnum(LifeCircleChannelEnum.RECOMMEND);

        for (LifeCircleContentMongo contentMongo : contentMongoList) {
            ChannelDataVO dataVO = transferToChannelDataVO(contentMongo, getChannelDataDTO, contentId_serviceSpecialParam_map);
            if (dataVO == null) {
                continue;
            }

            channelDataVOList.add(dataVO);
        }

        // 分组
        setGroup(channelDataGroupVOList, channelDataVOList, getChannelDataDTO, contentId_serviceSpecialParam_map);
        userDataVO.setDataList(channelDataGroupVOList);

        // 是否是最后一条数据
        if (!lastOne.get_id().equals(contentMongoList.get(contentMongoList.size() - 1).get_id())) {
            userDataVO.setHasMore(YesNoEnum.YES.getCode());
        }

        return userDataVO;
    }

    @Override
    public void delUserData(DelUserDataReq req) {
        lifeCircleContentPublishDubboService.deletePersonal(req.getContentId(), req.getUserId());
    }

    @Override
    public GetUserNativeTypeVO getUserNativeType(GetUserNativeTypeReq req) {
        String type = (String) redisTemplate.boundHashOps(RedisKeys.USER_NATIVE_TYPE_MAP).get(req.getUserId().toString());

        GetUserNativeTypeVO vo = new GetUserNativeTypeVO();
        vo.setUserNativeType(UserNativeTypeEnum.codeOf(NumberUtils.toInt(type, 0)).getCode());

        return vo;
    }

    @Override
    public void setUserNativeType(SetUserNativeTypeReq req) {
        redisTemplate.boundHashOps(RedisKeys.USER_NATIVE_TYPE_MAP).put(req.getUserId().toString(), req.getUserNativeType().toString());
    }

    @Override
    public UserDataVO search(SearchDTO searchDTO) {
        UserDataVO userDataVO = new UserDataVO();

        List<ChannelDataGroupVO> channelDataGroupVOList = new ArrayList<>();
        userDataVO.setHasMore(YesNoEnum.NO.getCode());
        userDataVO.setDataList(channelDataGroupVOList);

        String lastId = lifeCircleContentESService.searchLastId(searchDTO);
        if (lastId == null) {
            return userDataVO;
        }

        List<LifeCircleContentES> contentESList = lifeCircleContentESService.search(searchDTO);
        if (CollectionUtils.isEmpty(contentESList)) {
            return userDataVO;
        }

        if (!lastId.equals(contentESList.get(contentESList.size() - 1).getId().toString())) {
            userDataVO.setHasMore(YesNoEnum.YES.getCode());
        }

        GetChannelDataDTO dto = new GetChannelDataDTO();
        dto.setUserId(searchDTO.getUserId());
        dto.setChannelEnum(LifeCircleChannelEnum.LOCAL); // 不分组

        Map<Long, JSONObject> contentId_serviceSpecialParam_map = new HashMap<>();
        List<ChannelDataVO> channelDataVOList = new ArrayList<>(contentESList.size());

        LifeCircleContentMongo contentMongo = null;
        Map<Long, List<String>> contentId_matchKeyword = new HashMap<>(contentESList.size());

        for (LifeCircleContentES contentES : contentESList) {
            contentMongo = lifeCircleContentRepository.getById(contentES.getId());
            if (contentMongo == null) {
                continue;
            }

            ChannelDataVO dataVO = transferToChannelDataVO(contentMongo, dto, contentId_serviceSpecialParam_map);
            if (dataVO == null) {
                continue;
            }

            channelDataVOList.add(dataVO);
            contentId_matchKeyword.put(contentES.getId(), contentES.getPermissionUserIds());
        }

        // 分组
        setGroup(channelDataGroupVOList, channelDataVOList, dto, contentId_serviceSpecialParam_map);

        // 匹配到的关键词
        for (ChannelDataGroupVO dataGroupVO : channelDataGroupVOList) {
            for (ChannelDataVO channelDataVO : dataGroupVO.getDataList()) {
                dataGroupVO.setMatchKeywordList(contentId_matchKeyword.get(channelDataVO.getId()));
            }
        }

        return userDataVO;
    }

    @Override
    public SearchDataVO searchV2(SearchDTO searchDTO) {
        SearchDataVO searchDataVO = new SearchDataVO();

        List<SearchDataVO.ChannelGroup> channelGroupList = new ArrayList<>();
        searchDataVO.setHasMore(YesNoEnum.NO.getCode());
        searchDataVO.setChannelGroupList(channelGroupList);

        String lastId = lifeCircleContentESService.searchLastId(searchDTO);
        if (lastId == null) {
            return searchDataVO;
        }

        List<LifeCircleContentES> contentESList = lifeCircleContentESService.search(searchDTO);
        if (CollectionUtils.isEmpty(contentESList)) {
            return searchDataVO;
        }

        if (!lastId.equals(contentESList.get(contentESList.size() - 1).getId().toString())) {
            searchDataVO.setHasMore(YesNoEnum.YES.getCode());
        }

        searchDataVO.setLastId(contentESList.get(contentESList.size() - 1).getId());

        GetChannelDataDTO dto = new GetChannelDataDTO();
        dto.setUserId(searchDTO.getUserId());
        dto.setChannelEnum(searchDTO.getChannelEnum());

        Map<Long, JSONObject> contentId_serviceSpecialParam_map = new HashMap<>();
        List<ChannelDataVO> channelDataVOList = new ArrayList<>(contentESList.size());

        LifeCircleContentMongo contentMongo = null;
        Map<Long, List<String>> contentId_matchKeyword = new HashMap<>(contentESList.size());

        // 去重过滤
        Set<String> existsDataSet = new HashSet<>(contentESList.size());
        String bizDataId = null;

        for (LifeCircleContentES contentES : contentESList) {
            if (StringUtils.isNotBlank(contentES.getBizDataId())) {
                bizDataId = contentES.getShowType() + "_" + contentES.getBizDataId();
                if (existsDataSet.contains(bizDataId)) {
                    continue;
                } else {
                    existsDataSet.add(bizDataId);
                }
            }

            contentMongo = lifeCircleContentRepository.getById(contentES.getId());
            if (contentMongo == null) {
                continue;
            }

            ChannelDataVO dataVO = transferToChannelDataVO(contentMongo, dto, contentId_serviceSpecialParam_map);
            if (dataVO == null) {
                continue;
            }

            channelDataVOList.add(dataVO);
            contentId_matchKeyword.put(contentES.getId(), contentES.getPermissionUserIds());
        }

        // 分组
        List<ChannelDataGroupVO> channelDataGroupVOList = new ArrayList<>();
        setGroup(channelDataGroupVOList, channelDataVOList, dto, contentId_serviceSpecialParam_map);

        // 匹配到的关键词、频道分组
        Map<Integer, SearchDataVO.ChannelGroup> channelId_channelGroup_map = new HashMap<>();
        List<SearchDataVO.ChannelGroup> noChannelGroupList = new ArrayList<>();

        SearchDataVO.ChannelGroup channelGroup = null;
        boolean needChannelGroup = LifeCircleChannelEnum.RECOMMEND.equals(searchDTO.getChannelEnum());

        List<String> matchKeywordList = null;
        for (ChannelDataGroupVO dataGroupVO : channelDataGroupVOList) {
            if (CollectionUtils.isEmpty(dataGroupVO.getDataList())) {
                continue;
            }

            // 匹配到的关键词
            matchKeywordList = new ArrayList<>();
            for (ChannelDataVO channelDataVO : dataGroupVO.getDataList()) {
                for (String keyword : contentId_matchKeyword.get(channelDataVO.getId())) {
                    if (!matchKeywordList.contains(keyword)) {
                        matchKeywordList.add(keyword);
                    }
                }
            }
            dataGroupVO.setMatchKeywordList(matchKeywordList);

            // 取第一条数据
            ChannelDataVO channelDataVO = dataGroupVO.getDataList().get(0);

            // 不需要频道分组、已经是分组的、没有频道
            if (!needChannelGroup
                    || YesNoEnum.YES.getCode().equals(dataGroupVO.getIsGroup())
                    || CollectionUtils.isEmpty(channelDataVO.getChannelIdList())) {
                channelGroup = new SearchDataVO.ChannelGroup();

                channelGroup.setChannelId(null);
                channelGroup.setChannelName(null);
                channelGroup.setDataList(new ArrayList<>(1));
                channelGroup.setIsGroup(YesNoEnum.NO.getCode());
                channelGroup.getDataList().add(dataGroupVO);

                noChannelGroupList.add(channelGroup);
                continue;
            }

            // 取第一个频道
            Integer channelId = channelDataVO.getChannelIdList().get(0);

            channelGroup = channelId_channelGroup_map.get(channelId);
            if (channelGroup == null) {
                channelGroup = new SearchDataVO.ChannelGroup();

                channelGroup.setChannelId(channelId);
                channelGroup.setChannelName(LifeCircleChannelEnum.channelIdOf(channelId).getChannelName());
                channelGroup.setDataList(new ArrayList<>());
                channelGroup.setIsGroup(YesNoEnum.YES.getCode());

                channelId_channelGroup_map.put(channelId, channelGroup);
                channelGroupList.add(channelGroup);
            }

            // 组里面已经有2条数据了
            if (channelGroup.getDataList().size() >= 2) {
                channelGroup = new SearchDataVO.ChannelGroup();

                channelGroup.setChannelId(null);
                channelGroup.setChannelName(null);
                channelGroup.setDataList(new ArrayList<>(1));
                channelGroup.setIsGroup(YesNoEnum.NO.getCode());
                channelGroup.getDataList().add(dataGroupVO);

                noChannelGroupList.add(channelGroup);
                continue;
            }

            channelGroup.getDataList().add(dataGroupVO);
        }

        if (!CollectionUtils.isEmpty(noChannelGroupList)) {
            channelGroupList.addAll(noChannelGroupList);
        }

        return searchDataVO;
    }

    @Override
    public List<IconBtnRow> getChannelServicePortal(GetChannelServicePortalReq req) {
        List<IconBtnRow> rowList = new ArrayList<>();
        if (req == null || req.getChannelId() == null) {
            return rowList;
        }

        List<LifeCircleChannelServicePortalMongo> servicePortalMongoList = lifeCircleChannelServicePortalRepository.getByChannelId(req.getChannelId());
        if (CollectionUtils.isEmpty(servicePortalMongoList)) {
            return rowList;
        }

        Map<Integer, IconBtnRow> rowNo_iconBtnRow_map = new HashMap<>();
        IconBtnRow iconBtnRow = null;
        IconBtnRow.IconBtn iconBtn = null;

        for (LifeCircleChannelServicePortalMongo portalMongo : servicePortalMongoList) {
            if (portalMongo.getRowNo() == null) {
                continue;
            }

            iconBtnRow = rowNo_iconBtnRow_map.get(portalMongo.getRowNo());
            if (iconBtnRow == null) {
                iconBtnRow = new IconBtnRow();
                rowNo_iconBtnRow_map.put(portalMongo.getRowNo(), iconBtnRow);
                rowList.add(iconBtnRow);
            }

            iconBtn = new IconBtnRow.IconBtn();
            iconBtn.setName(portalMongo.getName());
            iconBtn.setIcon(portalMongo.getIcon());
            iconBtn.setProtocol(portalMongo.getProtocol());

            iconBtnRow.getIconBtnList().add(iconBtn);
        }

        return rowList;
    }

    @Override
    public UnifiedSearchDataVO unifiedSearch(SearchDTO searchDTO) {
        UnifiedSearchDataVO unifiedSearchDataVO = new UnifiedSearchDataVO();

        List<ChannelDataGroupVO> channelDataGroupVOList = new ArrayList<>();
        unifiedSearchDataVO.setHasMore(YesNoEnum.NO.getCode());
        unifiedSearchDataVO.setDataList(channelDataGroupVOList);

        String lastId = lifeCircleContentESService.searchLastId(searchDTO);
        if (lastId == null) {
            return unifiedSearchDataVO;
        }

        List<LifeCircleContentES> contentESList = lifeCircleContentESService.search(searchDTO);
        if (CollectionUtils.isEmpty(contentESList)) {
            return unifiedSearchDataVO;
        }

        if (!lastId.equals(contentESList.get(contentESList.size() - 1).getId().toString())) {
            unifiedSearchDataVO.setHasMore(YesNoEnum.YES.getCode());
        }

        unifiedSearchDataVO.setLastId(contentESList.get(contentESList.size() - 1).getId());

        GetChannelDataDTO dto = new GetChannelDataDTO();
        dto.setUserId(searchDTO.getUserId());
        dto.setChannelEnum(searchDTO.getChannelEnum());

        Map<Long, JSONObject> contentId_serviceSpecialParam_map = new HashMap<>();
        List<ChannelDataVO> channelDataVOList = new ArrayList<>(contentESList.size());

        LifeCircleContentMongo contentMongo = null;
        Map<Long, List<String>> contentId_matchKeyword = new HashMap<>(contentESList.size());

        // 去重过滤
        Set<String> existsDataSet = new HashSet<>(contentESList.size());
        String bizDataId = null;

        for (LifeCircleContentES contentES : contentESList) {
            if (StringUtils.isNotBlank(contentES.getBizDataId())) {
                bizDataId = contentES.getShowType() + "_" + contentES.getBizDataId();
                if (existsDataSet.contains(bizDataId)) {
                    continue;
                } else {
                    existsDataSet.add(bizDataId);
                }
            }

            contentMongo = lifeCircleContentRepository.getById(contentES.getId());
            if (contentMongo == null) {
                continue;
            }

            ChannelDataVO dataVO = transferToChannelDataVO(contentMongo, dto, contentId_serviceSpecialParam_map);
            if (dataVO == null) {
                continue;
            }

            channelDataVOList.add(dataVO);
            contentId_matchKeyword.put(contentES.getId(), contentES.getPermissionUserIds());
        }

        // 分组
        setGroup(channelDataGroupVOList, channelDataVOList, dto, contentId_serviceSpecialParam_map);

        // 匹配到的关键词
        List<String> matchKeywordList = null;
        for (ChannelDataGroupVO dataGroupVO : channelDataGroupVOList) {
            if (CollectionUtils.isEmpty(dataGroupVO.getDataList())) {
                continue;
            }

            // 更多跳转
            if (YesNoEnum.YES.getCode().equals(dataGroupVO.getIsGroup())
                    && YesNoEnum.YES.getCode().equals(dataGroupVO.getHasMore())) {
                if ("全民直播".equals(dataGroupVO.getGroupName())) {
                    dataGroupVO.setMoreJumpType(MoreJumpTypeEnum.PROTOCOL.getType());
                    dataGroupVO.setMoreJumpProtocol(JumpProtocolEnum.LIVE_INDEX.getProtocol());
                } else if ("Boss开讲".equals(dataGroupVO.getGroupName())) {
                    dataGroupVO.setMoreJumpType(MoreJumpTypeEnum.PROTOCOL.getType());
                    dataGroupVO.setMoreJumpProtocol(JumpProtocolEnum.BOSS_LECTURE_INDEX.getProtocol());
                }
            }

            matchKeywordList = new ArrayList<>();
            for (ChannelDataVO channelDataVO : dataGroupVO.getDataList()) {
                for (String keyword : contentId_matchKeyword.get(channelDataVO.getId())) {
                    if (!matchKeywordList.contains(keyword)) {
                        matchKeywordList.add(keyword);
                    }
                }
            }
            dataGroupVO.setMatchKeywordList(matchKeywordList);
        }

        return unifiedSearchDataVO;
    }

    @Override
    public List<ChannelDataGroupVO> getQAAdData(GetChannelDataDTO dto) {
        GetChannelDataContext context = new GetChannelDataContext();
        context.setUserId(dto.getUserId());
        context.setChannelEnum(LifeCircleChannelEnum.RECOMMEND);
        context.setReqNo(dto.getReqNo());
        context.setTimeDescEnum(TimeDescEnum.getCurrentTimeDescEnum());

        UserNativeTypeEnum userNativeTypeEnum = UserNativeTypeEnum.NULL;
        try {
            GetUserNativeTypeReq req = new GetUserNativeTypeReq();
            req.setUserId(dto.getUserId());
            userNativeTypeEnum = UserNativeTypeEnum.codeOf(getUserNativeType(req).getUserNativeType());
        } catch (Exception e) {
            log.error("获取用户在本地的情况出错: dto={}", JSON.toJSONString(dto), e);
        }

        if (userNativeTypeEnum == null || UserNativeTypeEnum.NULL.equals(userNativeTypeEnum)) {
            userNativeTypeEnum = UserNativeTypeEnum.NATIVE_OUT;
        }

        context.setUserNativeTypeEnum(userNativeTypeEnum);

        return factoryService.getServiceForQAAd(context).getChannelData(context);
    }
}