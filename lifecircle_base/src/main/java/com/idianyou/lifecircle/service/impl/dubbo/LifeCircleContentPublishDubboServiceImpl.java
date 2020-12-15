package com.idianyou.lifecircle.service.impl.dubbo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.idianyou.business.constant.UserIdentitiesEnum;
import com.idianyou.business.usercenter.dto.UserInfoDTO;
import com.idianyou.business.usercenter.service.UserCenterService;
import com.idianyou.circle.dto.LifeCircleServiceBizParamDTO;
import com.idianyou.lifecircle.constants.RedisConstants;
import com.idianyou.lifecircle.constants.RedisKeys;
import com.idianyou.lifecircle.dto.ChangedDataDTO;
import com.idianyou.lifecircle.dto.DeleteDTO;
import com.idianyou.lifecircle.dto.FromServiceInfo;
import com.idianyou.lifecircle.dto.InnerRpcResponse;
import com.idianyou.lifecircle.dto.LifeCircleContentPublishDTO;
import com.idianyou.lifecircle.dto.QueryTypeDataParam;
import com.idianyou.lifecircle.dto.UpdateServiceBizParamDTO;
import com.idianyou.lifecircle.dto.redis.ServiceBizParamRedisDTO;
import com.idianyou.lifecircle.entity.mongo.LifeCircleContentMongo;
import com.idianyou.lifecircle.entity.mongo.LifeCircleServiceBizParamMongo;
import com.idianyou.lifecircle.enums.ContentTypeEnum;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.IdentityTypeEnum;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.enums.ServiceStatusEnum;
import com.idianyou.lifecircle.enums.ShowTypeEnum;
import com.idianyou.lifecircle.enums.TableNameEnum;
import com.idianyou.lifecircle.es.LifeCircleContentES;
import com.idianyou.lifecircle.es.LifeCircleContentESService;
import com.idianyou.lifecircle.exception.LifeCircleException;
import com.idianyou.lifecircle.mongo.LifeCircleContentRepository;
import com.idianyou.lifecircle.mongo.LifeCircleServiceBizParamRepository;
import com.idianyou.lifecircle.service.GenerateUniqueIdDubboService;
import com.idianyou.lifecircle.service.LifeCircleContentPublishDubboService;
import com.idianyou.lifecircle.service.local.FactoryService;
import com.idianyou.lifecircle.service.local.ServiceBizParamChangedNotifyAppService;
import com.idianyou.lifecircle.service.local.redis.RedisCacheService;
import com.idianyou.miniapps.spi.dto.request.PlatformFilterDto;
import com.idianyou.miniapps.spi.dto.response.MiniappInfoBriefDTO;
import com.idianyou.miniapps.spi.service.MiniappsInfoDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/24 15:34
 */
@Slf4j
@Service
public class LifeCircleContentPublishDubboServiceImpl implements LifeCircleContentPublishDubboService {

    @Autowired
    private LifeCircleContentRepository lifeCircleContentRepository;

    @Autowired
    private LifeCircleServiceBizParamRepository lifeCircleServiceBizParamRepository;

    @Autowired
    private GenerateUniqueIdDubboService generateUniqueIdDubboService;

    @Autowired
    private MiniappsInfoDubboService miniappsInfoDubboService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private FactoryService factoryService;

    @Autowired
    private ServiceBizParamChangedNotifyAppService serviceBizParamChangedNotifyAppService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private LifeCircleContentESService lifeCircleContentESService;

    @Override
    public InnerRpcResponse<Long> publish(LifeCircleContentPublishDTO dto) {
        Long dataId = null;

        try {
            checkPublishParam(dto);

            String bizParamDataId = LifeCircleServiceBizParamMongo.getDataId(dto.getDataTypeEnum(), dto.getServiceDataId());
            if (StringUtils.isBlank(bizParamDataId)) {
                log.error("发布生活圈动态失败：数据类型或对应服务数据ID错误, dataType={}, serviceDataId={}", dto.getDataTypeEnum().getType(), dto.getServiceDataId());
                return new InnerRpcResponse<>("501", "数据类型或对应服务数据ID错误");
            }

            LifeCircleServiceBizParamMongo existsBizParamMongo = lifeCircleServiceBizParamRepository.getById(bizParamDataId);
            if (existsBizParamMongo != null) {
                log.error("发布生活圈动态失败：对应服务数据ID已存在, bizParamDataId={}", bizParamDataId);
                return new InnerRpcResponse<>("503", "对应服务数据ID已存在，请走更新接口");
            }

            dataId = generateUniqueIdDubboService.generateAutoIncId(TableNameEnum.LIFE_CRICLE_CONTENT);

            Date now = dto.getCreateTime();

            // 保存动态数据
            LifeCircleContentMongo contentMongo = new LifeCircleContentMongo();

            contentMongo.set_id(dataId);
            contentMongo.setUserId(dto.getUserId());
            contentMongo.setDataType(dto.getDataTypeEnum().getType());
            contentMongo.setShowType(dto.getShowTypeEnum().getType());
            contentMongo.setContentType(dto.getShowTypeEnum().getContentTypeEnum().getType());
            contentMongo.setServiceStatus(ServiceStatusEnum.RUNNING.getStatus());
            contentMongo.setServiceDataId(dto.getServiceDataId());
            contentMongo.setShareContent(dto.getShareContent());
            contentMongo.setCreateTime(now);
            contentMongo.setUpdateTime(now);
            contentMongo.setLongitude(dto.getLongitude());
            contentMongo.setLatitude(dto.getLatitude());
            contentMongo.setBizParamDataId(bizParamDataId);

            contentMongo.setChannelIdList(getChannelIdList(dto));
            if (contentMongo.getChannelIdList().size() == 0) {
                contentMongo.setChannelIdList(null);
            }

            if (StringUtils.isNotBlank(dto.getServiceClientId())) {
                contentMongo.setFromServiceInfo(getFromServiceInfo(dto.getServiceClientId(), dto));
            }

            if (dto.getServiceSpecialParam() != null) {
                String categoryName = dto.getServiceSpecialParam().getString("categoryName");
                if (StringUtils.isNotBlank(categoryName)) {
                    contentMongo.setCategoryName(categoryName);

                    String categoryId = dto.getServiceSpecialParam().getString("categoryId");
                    if (StringUtils.isNotBlank(categoryId)) {
                        contentMongo.setCategoryId(categoryId);
                    }

                    // 主题简介
                    String categoryDesc = dto.getServiceSpecialParam().getString("categoryDesc");

                    redisCacheService.addDataCategory(dto.getDataTypeEnum(), dto.getShowTypeEnum(), contentMongo.getCategoryName(), contentMongo.getCategoryId());
                }

                String bizDataId = dto.getServiceSpecialParam().getString("bizDataId");
                if (StringUtils.isNotBlank(bizDataId)) {
                    bizDataId = dto.getDataTypeEnum().name().toLowerCase().concat("_").concat(bizDataId);
                    contentMongo.setBizDataId(bizDataId);
                }
            }

            lifeCircleContentRepository.save(contentMongo);

            // 保存服务业务参数
            LifeCircleServiceBizParamMongo bizParamMongo = new LifeCircleServiceBizParamMongo();
            bizParamMongo.set_id(contentMongo.getBizParamDataId());
            bizParamMongo.setDataType(contentMongo.getDataType());
            bizParamMongo.setServiceDataId(contentMongo.getServiceDataId());
            bizParamMongo.setServiceStatus(contentMongo.getServiceStatus());
            bizParamMongo.setServiceSpecialParam(dto.getServiceSpecialParam());

            replaceVideoId(dto, dataId, bizParamMongo.get_id());
            bizParamMongo.setServiceBizParam(dto.getServiceBizParam());

            bizParamMongo.setCreateTime(now);
            bizParamMongo.setUpdateTime(now);

            lifeCircleServiceBizParamRepository.save(bizParamMongo);

            // 更新ES数据
            lifeCircleContentESService.add(trans2LifeCircleContentES(contentMongo, bizParamMongo));

            // 更新缓存中的服务业务参数快照数据
            ServiceBizParamRedisDTO redisDTO = new ServiceBizParamRedisDTO();
            redisDTO.setBizParamDataId(contentMongo.getBizParamDataId());
            redisDTO.setServiceDataId(contentMongo.getServiceDataId());
            redisDTO.setDataType(contentMongo.getDataType());
            redisDTO.setServiceStatus(contentMongo.getServiceStatus());
            redisDTO.setServiceBizParam(dto.getServiceBizParam());
            redisDTO.setServiceChangedBizParam(null);
            redisDTO.setUpdateTime(new Date());

            redisTemplate.boundValueOps(RedisKeys.BIZ_PARAM_DATAID_RELATED_SERVICE_DATA.concat(contentMongo.getBizParamDataId())).set(JSON.toJSONString(redisDTO), RedisConstants.CACHE_DAYS, TimeUnit.DAYS);

            // 保存服务数据ID与动态ID的映射
            List<Long> contentIdList = null;
            String contentIdListStr = (String) redisTemplate.boundValueOps(RedisKeys.BIZ_PARAM_DATAID_RELATED_CONTENTIDS.concat(contentMongo.getBizParamDataId())).get();
            if (StringUtils.isNotBlank(contentIdListStr)) {
                contentIdList = JSON.parseArray(contentIdListStr, Long.class);
            }

            if (contentIdList == null) {
                contentIdList = new ArrayList<>();
            }

            if (!contentIdList.contains(dataId)) {
                contentIdList.add(dataId);
            }

            redisTemplate.boundValueOps(RedisKeys.BIZ_PARAM_DATAID_RELATED_CONTENTIDS.concat(contentMongo.getBizParamDataId())).set(JSON.toJSONString(contentIdList), RedisConstants.CACHE_DAYS, TimeUnit.DAYS);

            log.info("发布生活圈动态成功, contentId={}, bizParamDataId={}, dataType={}, showType={}, userId={}", dataId, contentMongo.getBizParamDataId(), contentMongo.getDataType(), contentMongo.getShowType(), contentMongo.getUserId());
        } catch (LifeCircleException e) {
            log.error("发布生活圈动态出错: dto={}", JSON.toJSONString(dto), e);
            return new InnerRpcResponse<>(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("发布生活圈动态出错: dto={}", JSON.toJSONString(dto), e);
            return new InnerRpcResponse<>("501", "发布生活圈动态出错");
        }

        return new InnerRpcResponse<>(dataId);
    }

    public LifeCircleContentES trans2LifeCircleContentES(LifeCircleContentMongo contentMongo, LifeCircleServiceBizParamMongo bizParamMongo) {
        LifeCircleContentES contentES = new LifeCircleContentES();

        contentES.setId(contentMongo.get_id());
        contentES.setUserId(contentMongo.getUserId());
        contentES.setDataType(contentMongo.getDataType());
        contentES.setContentType(contentMongo.getContentType());
        contentES.setShowType(contentMongo.getShowType());
        contentES.setServiceDataId(contentMongo.getServiceDataId());
        contentES.setBizDataId(contentMongo.getBizDataId());
        contentES.setServiceStatus(contentMongo.getServiceStatus());
        contentES.setCategoryId(contentMongo.getCategoryId());
        contentES.setCategoryName(contentMongo.getCategoryName());
        contentES.setBizParamDataId(contentMongo.getBizParamDataId());
        contentES.setChannelIdList(contentMongo.getChannelIdList());
        contentES.setLongitude(contentMongo.getLongitude());
        contentES.setLatitude(contentMongo.getLatitude());
        contentES.setCreateTime(contentMongo.getCreateTime());
        contentES.setUpdateTime(contentMongo.getUpdateTime());

        if (contentMongo.getFromServiceInfo() != null) {
            contentES.setFromServiceClientId(contentMongo.getFromServiceInfo().getClientId());
            contentES.setFromServiceName(contentMongo.getFromServiceInfo().getName());
        }

        if (bizParamMongo.getServiceSpecialParam() != null) {
            Integer permissionType = bizParamMongo.getServiceSpecialParam().getInteger("permission");
            List<String> permissionUserIds = (List<String>) bizParamMongo.getServiceSpecialParam().get("permissionUserIds");
            if (permissionType != null || !CollectionUtils.isEmpty(permissionUserIds)) {
                contentES.setPermissionType(permissionType);
                contentES.setPermissionUserIds(permissionUserIds);
            }
        }

        // 提取搜索文本
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotBlank(contentMongo.getShareContent())) {
            sb.append(contentMongo.getShareContent());
        }

        if (StringUtils.isNotBlank(bizParamMongo.getServiceBizParam())) {
            JSONObject bizParam = JSON.parseObject(bizParamMongo.getServiceBizParam());

            JSONArray imageList = null;
            JSONArray btnList = null;
            String text = null;
            for (Map.Entry<String, Object> entry : bizParam.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }

                if (entry.getValue() instanceof String) {
                    if (StringUtils.isBlank(entry.getValue().toString())) {
                        continue;
                    }

                    if (Objects.equals(entry.getValue().toString(), contentMongo.getShareContent())) {
                        continue;
                    }
                }

                if (DataTypeEnum.K_SONG_KING.getType().equals(contentMongo.getDataType())) {
                    if ("title".equals(entry.getKey())) {
                        continue;
                    }
                }

                if (DataTypeEnum.FRIEND_STYLE_DATA.getType().equals(contentMongo.getDataType())) {
                    continue;
                }

                if ("imageList".equals(entry.getKey())) {
                    if (!ShowTypeEnum.MARKET_COMMODITY.getType().equals(contentMongo.getShowType())) {
                        continue;
                    }

                    imageList = bizParam.getJSONArray("imageList");
                    if (imageList != null && imageList.size() > 0) {
                        for (int i = 0; i < imageList.size(); i++) {
                            text = imageList.getJSONObject(i).getString("goodsName");
                            if (StringUtils.isNotBlank(text)) {
                                sb.append(text).append(";");
                            }
                        }
                    }
                    continue;
                }

                if (entry.getKey().toLowerCase().contains("protocol")) {
                    continue;
                }

                if (entry.getValue().toString().contains("http://")
                        || entry.getValue().toString().contains("dypush://")
                        || entry.getValue().toString().contains("chigua://")) {
                    continue;
                }

                if (NumberUtils.isNumber(entry.getValue().toString())) {
                    continue;
                }

                if ("btnList".equals(entry.getKey())) {
                    btnList = bizParam.getJSONArray("btnList");
                    if (btnList != null && btnList.size() > 0) {
                        for (int i = 0; i < btnList.size(); i++) {
                            text = btnList.getJSONObject(i).getString("text");
                            if (StringUtils.isNotBlank(text)) {
                                sb.append(text).append(";");
                            }
                        }
                    }
                    continue;
                }

                sb.append(entry.getValue()).append(";");
            }
        }

        String searchText = sb.toString();
        if (StringUtils.isNotBlank(searchText)) {
            contentES.setSearchText(searchText);
        }

        return contentES;
    }


    /**
     * 替换VideoId
     * @param dto
     * @param contentId
     * @param bizParamDataId
     */
    private void replaceVideoId(LifeCircleContentPublishDTO dto, Long contentId, String bizParamDataId) {
        if (DataTypeEnum.FRIEND_STYLE_DATA.equals(dto.getDataTypeEnum())) {
            LifeCircleServiceBizParamDTO bizParamDTO = JSON.parseObject(dto.getServiceBizParam()).toJavaObject(LifeCircleServiceBizParamDTO.class);
            if (bizParamDTO.getContent() != null
                    && bizParamDTO.getContent().getVideoInfo() != null) {
                String videoId = bizParamDTO.getContent().getVideoInfo().getVideoId();
                if (StringUtils.isNotBlank(videoId) && videoId.contains("_")) {
                    bizParamDTO.getContent().getVideoInfo().setVideoId(contentId.toString());
                    dto.setServiceBizParam(JSON.toJSONString(bizParamDTO));
                }
            }
        }
    }

    private List<Integer> getChannelIdList(LifeCircleContentPublishDTO dto) {
        Set<Integer> channelIdSet = new HashSet<>();

        List<IdentityTypeEnum> userIdentityTypeList = new ArrayList<>();
        if (dto.getUserId() != null) {
            userIdentityTypeList = getUserIdentity(dto.getUserId());
        }

        // 孺子牛
        boolean isOxen = userIdentityTypeList.contains(IdentityTypeEnum.OXEN);

        // 个人动态
        if (ContentTypeEnum.PERSONAL.equals(dto.getShowTypeEnum().getContentTypeEnum())) {
            // 动态频道(农户、商户合伙人动态、服务合伙人动态)
            boolean isFarmer = userIdentityTypeList.contains(IdentityTypeEnum.FARMER);
            boolean isMerchantPartner = userIdentityTypeList.contains(IdentityTypeEnum.MERCHANT_PARTNER);
            boolean isServicePartner = userIdentityTypeList.contains(IdentityTypeEnum.SERVICE_PARTNER);
            if (isFarmer || isMerchantPartner || isServicePartner) {
                channelIdSet.add(LifeCircleChannelEnum.DYNAMIC.getChannelId());
            }

            // 热点频道(孺子牛、代理商发布的生活圈动态)
            boolean isAgent = userIdentityTypeList.contains(IdentityTypeEnum.AGENT);
            if (isOxen || isAgent) {
                channelIdSet.add(LifeCircleChannelEnum.LOCAL.getChannelId());
            }
        }

        // 服务动态
        if (ContentTypeEnum.SERVICE.equals(dto.getShowTypeEnum().getContentTypeEnum())) {
            // 生活频道(赶集、商户列表、吃瓜商城、找工作、开公司、商户动态、定制服务)
            if (DataTypeEnum.MARKET.equals(dto.getDataTypeEnum())
                    || DataTypeEnum.MERCHANT.equals(dto.getDataTypeEnum())
                    || DataTypeEnum.APPLY_JOB.equals(dto.getDataTypeEnum())
                    || DataTypeEnum.FORM_COMPANY.equals(dto.getDataTypeEnum())
                    || DataTypeEnum.CHIGUA_MALL.equals(dto.getDataTypeEnum())) {
                // 孺子牛直播
                if (ShowTypeEnum.MARKET_LIVE.equals(dto.getShowTypeEnum()) && isOxen) {
                    channelIdSet.add(LifeCircleChannelEnum.LOCAL.getChannelId());
                } else {
                    channelIdSet.add(LifeCircleChannelEnum.LIFE.getChannelId());
                }
            }

            // 直播频道(全民直播、Boss开讲、拍屏直播)
            if (DataTypeEnum.LIVE_FOR_ALL.equals(dto.getDataTypeEnum())
                    || DataTypeEnum.BOSS_LECTURE.equals(dto.getDataTypeEnum())) {
                channelIdSet.add(LifeCircleChannelEnum.LIVE.getChannelId());
            }

            // 娱乐频道(K歌之王、最佳辩手、猜猜我是谁、游戏、暗号红包、精彩瞬间、小说)
            if (DataTypeEnum.K_SONG_KING.equals(dto.getDataTypeEnum())
                    || DataTypeEnum.BEST_DEBATER.equals(dto.getDataTypeEnum())
                    || DataTypeEnum.GUESS_WHO_I_AM.equals(dto.getDataTypeEnum())
                    || DataTypeEnum.LANDLORDS.equals(dto.getDataTypeEnum())
                    || DataTypeEnum.CIPHER_REDPACKET.equals(dto.getDataTypeEnum())
                    || DataTypeEnum.NOVEL.equals(dto.getDataTypeEnum())
                    || DataTypeEnum.WONDERFUL_MOMENTS.equals(dto.getDataTypeEnum())) {
                channelIdSet.add(LifeCircleChannelEnum.AMUSEMENT.getChannelId());
            }
        }

        List<Integer> channelIdList = new ArrayList<>(channelIdSet.size());
        channelIdList.addAll(channelIdSet);

        return channelIdList;
    }

    /**
     * 获取用户身份
     * @param userId
     * @return
     */
    public List<IdentityTypeEnum> getUserIdentity(Long userId) {
        List<IdentityTypeEnum> identityTypeEnumList = new ArrayList<>();

        try {
            com.idianyou.spi.common.InnerRpcResponse<UserInfoDTO> response = userCenterService.getUserInfo(userId);
            if (response != null && response.getData() != null) {
                if (!CollectionUtils.isEmpty(response.getData().getUserIdentities())) {
                    for (Integer userIdentity : response.getData().getUserIdentities()) {
                        if (UserIdentitiesEnum.RUZI_CATTLE.getIdentitiesId().equals(userIdentity)) {
                            identityTypeEnumList.add(IdentityTypeEnum.OXEN);
                        } else if (UserIdentitiesEnum.PARTNER_SERVICE.getIdentitiesId().equals(userIdentity)) {
                            identityTypeEnumList.add(IdentityTypeEnum.SERVICE_PARTNER);
                        } else if (UserIdentitiesEnum.AGENT.getIdentitiesId().equals(userIdentity)) {
                            identityTypeEnumList.add(IdentityTypeEnum.AGENT);
                        }
                    }
                }
            } else {
                log.error("查询用户信息出错：userId={}, response={}", userId, JSON.toJSONString(response));
            }
        } catch (Exception e) {
            log.error("查询用户信息出错：userId={}", userId, e);
        }

        return identityTypeEnumList;
    }

    private void checkPublishParam(LifeCircleContentPublishDTO dto) {
        try {
            Assert.notNull(dto, "参数不能为空");
            //Assert.notNull(dto.getUserId(), "参数[userId]错误");
            Assert.notNull(dto.getDataTypeEnum(), "参数[dataTypeEnum]错误");
            Assert.notNull(dto.getShowTypeEnum(), "参数[showTypeEnum]错误");
            Assert.notNull(dto.getServiceStatusEnum(), "参数[serviceStatusEnum]错误");
            Assert.notNull(dto.getCreateTime(), "参数[createTime]错误");
            Assert.isTrue(StringUtils.isNotBlank(dto.getServiceDataId()), "参数[serviceDataId]错误");
            Assert.isTrue(StringUtils.isNotBlank(dto.getServiceBizParam()), "参数[serviceBizParam]错误");

            if (ContentTypeEnum.SERVICE.equals(dto.getShowTypeEnum().getContentTypeEnum())
                    && StringUtils.isNotBlank(dto.getDataTypeEnum().getClientId())) {
                Assert.isTrue(StringUtils.isNotBlank(dto.getServiceClientId()), "参数[serviceClientId]错误");
            }

            Assert.notNull(dto.getServiceSpecialParam(), "参数[serviceSpecialParam]错误");

            String bizDataId = dto.getServiceSpecialParam().getString("bizDataId");
            Assert.isTrue(StringUtils.isNotBlank(bizDataId), "参数[serviceSpecialParam.bizDataId]错误");

            // 赶集、吃瓜商城
            if ((DataTypeEnum.MARKET.equals(dto.getDataTypeEnum()) || DataTypeEnum.CHIGUA_MALL.equals(dto.getDataTypeEnum()))
                    && ContentTypeEnum.SERVICE.equals(dto.getShowTypeEnum().getContentTypeEnum())) {
                Assert.isTrue(StringUtils.isNotBlank(dto.getServiceSpecialParam().getString("categoryName")), "参数[serviceSpecialParam.categoryName]错误");
            }
        } catch (Exception e) {
            throw new LifeCircleException("501", e.getMessage());
        }
    }

    @Override
    public InnerRpcResponse<Long> sharePublish(LifeCircleContentPublishDTO dto) {
        try {
            checkSharePublishParam(dto);
            return publish(dto);
        } catch (LifeCircleException e) {
            log.error("分享方式发布动态出错: dto={}", JSON.toJSONString(dto), e);
            return new InnerRpcResponse<>(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("分享方式发布动态出错: dto={}", JSON.toJSONString(dto), e);
            return new InnerRpcResponse<>("501", "分享方式发布动态出错");
        }
    }

    private void checkSharePublishParam(LifeCircleContentPublishDTO dto) {
        try {
            Assert.notNull(dto, "参数不能为空");
            Assert.notNull(dto.getUserId(), "参数[userId]错误");
            Assert.notNull(dto.getDataTypeEnum(), "参数[dataTypeEnum]错误");
            Assert.notNull(dto.getShowTypeEnum(), "参数[showTypeEnum]错误");
            Assert.notNull(dto.getCreateTime(), "参数[createTime]错误");
            Assert.isTrue(StringUtils.isNotBlank(dto.getServiceDataId()), "参数[serviceDataId]错误");
        } catch (Exception e) {
            throw new LifeCircleException("501", e.getMessage());
        }
    }

    /**
     * 获取服务信息
     * @param clientId
     * @param dto
     * @return
     */
    private FromServiceInfo getFromServiceInfo(String clientId, LifeCircleContentPublishDTO dto) {
        FromServiceInfo fromServiceInfo = null;

        try {
            MiniappInfoBriefDTO briefDTO = miniappsInfoDubboService.getMiniappInfoByClientId(clientId, new PlatformFilterDto());
            if (briefDTO != null) {
                fromServiceInfo = new FromServiceInfo();

                fromServiceInfo.setClientId(clientId);
                fromServiceInfo.setName(briefDTO.getName());
                fromServiceInfo.setIcon(briefDTO.getIcon());
                fromServiceInfo.setJumpProtocol(briefDTO.getStartupParam());

                // 赶集
                if (DataTypeEnum.MARKET.equals(dto.getDataTypeEnum())
                        && ContentTypeEnum.SERVICE.equals(dto.getShowTypeEnum().getContentTypeEnum())) {
                    String categoryName = dto.getServiceSpecialParam().getString("categoryName");
                    if (StringUtils.isNotBlank(categoryName)) {
                        fromServiceInfo.setName(categoryName);
                    }

                    String categoryIcon = dto.getServiceSpecialParam().getString("categoryIcon");
                    if (StringUtils.isNotBlank(categoryIcon)) {
                        fromServiceInfo.setIcon(categoryIcon);
                    }

                    String categoryProtocol = dto.getServiceSpecialParam().getString("categoryProtocol");
                    if (StringUtils.isNotBlank(categoryProtocol)) {
                        fromServiceInfo.setJumpProtocol(categoryProtocol);
                    }
                }

                // 吃瓜商城
                if (DataTypeEnum.CHIGUA_MALL.equals(dto.getDataTypeEnum())
                        && ContentTypeEnum.SERVICE.equals(dto.getShowTypeEnum().getContentTypeEnum())) {
                    fromServiceInfo.setName("吃瓜商城");
                }
            }
        } catch (Exception e) {
            log.error("获取来源服务信息失败: clientId={}", clientId, e);
            throw new LifeCircleException("获取来源服务信息失败");
        }

        return fromServiceInfo;
    }

    @Override
    public InnerRpcResponse updateServiceBizParam(UpdateServiceBizParamDTO dto) {
        try {
            checkUpdateServiceBizParam(dto);

            String bizParamDataId = LifeCircleServiceBizParamMongo.getDataId(dto.getDataTypeEnum(), dto.getServiceDataId());

            LifeCircleServiceBizParamMongo serviceBizParamMongo = lifeCircleServiceBizParamRepository.getById(bizParamDataId);
            if (serviceBizParamMongo == null) {
                log.error("更新服务业务参数出错:未找到对应数据, bizParamDataId={}", bizParamDataId);
                return new InnerRpcResponse<>("504", "未找到对应数据");
            }

            // 更新动态数据
            LifeCircleContentMongo contentMongo = new LifeCircleContentMongo();
            contentMongo.setServiceStatus(dto.getServiceStatusEnum().getStatus());

            lifeCircleContentRepository.updateByBizParamDataId(contentMongo, bizParamDataId);

            // 更新服务业务参数数据
            LifeCircleServiceBizParamMongo bizParamMongo = new LifeCircleServiceBizParamMongo();
            bizParamMongo.set_id(bizParamDataId);
            bizParamMongo.setServiceStatus(contentMongo.getServiceStatus());
            bizParamMongo.setServiceBizParam(dto.getAllServiceBizParam());

            lifeCircleServiceBizParamRepository.updateById(bizParamMongo);

            // 更新ES数据
            lifeCircleContentESService.updateServiceStatusByBizParamDataId(bizParamDataId, dto.getServiceStatusEnum());

            // 更新缓存中的服务业务参数快照数据
            ServiceBizParamRedisDTO redisDTO = new ServiceBizParamRedisDTO();
            redisDTO.setBizParamDataId(bizParamDataId);
            redisDTO.setServiceDataId(dto.getServiceDataId());
            redisDTO.setDataType(dto.getDataTypeEnum().getType());
            redisDTO.setServiceStatus(dto.getServiceStatusEnum().getStatus());
            redisDTO.setServiceBizParam(dto.getAllServiceBizParam());
            redisDTO.setServiceChangedBizParam(dto.getChangedBizParam());
            redisDTO.setUpdateTime(new Date());

            redisTemplate.boundValueOps(RedisKeys.BIZ_PARAM_DATAID_RELATED_SERVICE_DATA.concat(bizParamMongo.get_id())).set(JSON.toJSONString(redisDTO), RedisConstants.CACHE_DAYS, TimeUnit.DAYS);

            // 通知前端数据有变更
            CompletableFuture.runAsync(() -> {
                serviceBizParamChangedNotifyAppService.notifyApp(dto);
            });

            log.info("更新服务业务参数成功, dataType={}, bizParamDataId={}", dto.getDataTypeEnum().getType(), bizParamDataId);
        } catch (LifeCircleException e) {
            log.error("更新服务业务参数出错: dto={}", JSON.toJSONString(dto), e);
            return new InnerRpcResponse<>(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("更新服务业务参数出错: dto={}", JSON.toJSONString(dto), e);
            return new InnerRpcResponse<>("501", "更新服务业务参数出错");
        }

        return new InnerRpcResponse<>();
    }

    private void checkUpdateServiceBizParam(UpdateServiceBizParamDTO dto) {
        try {
            Assert.notNull(dto, "参数不能为空");
            Assert.notNull(dto.getDataTypeEnum(), "参数[dataTypeEnum]错误");
            Assert.notNull(dto.getServiceStatusEnum(), "参数[serviceStatusEnum]错误");
            Assert.isTrue(StringUtils.isNotBlank(dto.getServiceDataId()), "参数[serviceDataId]错误");
            Assert.isTrue(StringUtils.isNotBlank(dto.getAllServiceBizParam()), "参数[allServiceBizParam]错误");
            Assert.isTrue(StringUtils.isNotBlank(dto.getChangedBizParam()), "参数[changedBizParam]错误");
        } catch (Exception e) {
            throw new LifeCircleException("501", e.getMessage());
        }
    }

    public List<Long> getContentIdsByBizParamDataId(String bizParamDataId) {
        String contentIdListStr = (String) redisTemplate.boundValueOps(RedisKeys.BIZ_PARAM_DATAID_RELATED_CONTENTIDS.concat(bizParamDataId)).get();
        if (StringUtils.isNotBlank(contentIdListStr)) {
            List<Long> contentIdList = JSON.parseArray(contentIdListStr, Long.class);
            if (!CollectionUtils.isEmpty(contentIdList)) {
                return contentIdList;
            }
        }

        List<LifeCircleContentMongo> contentMongoList = lifeCircleContentRepository.getByServiceDataId(bizParamDataId);
        if (!CollectionUtils.isEmpty(contentMongoList)) {
            List<Long> contentIdList = new ArrayList<>(contentMongoList.size());
            for (LifeCircleContentMongo contentMongo : contentMongoList) {
                if (!contentIdList.contains(contentMongo.get_id())) {
                    contentIdList.add(contentMongo.get_id());
                }
            }

            if (!CollectionUtils.isEmpty(contentIdList)) {
                redisTemplate.boundValueOps(RedisKeys.BIZ_PARAM_DATAID_RELATED_CONTENTIDS.concat(bizParamDataId)).set(JSON.toJSONString(contentIdList), RedisConstants.CACHE_DAYS, TimeUnit.DAYS);
            }

            return contentIdList;
        }

        return Collections.emptyList();
    }

    @Override
    public List<ChangedDataDTO> getLastBizParam(List<Long> contentIdList) {
        if (CollectionUtils.isEmpty(contentIdList)) {
            return Collections.emptyList();
        }

        List<ChangedDataDTO> changedDataDTOList = new ArrayList<>();

        for (Long contentId : contentIdList) {
            LifeCircleContentMongo contentMongo = lifeCircleContentRepository.getById(contentId);
            if (contentMongo != null && StringUtils.isNotBlank(contentMongo.getBizParamDataId())) {
                ChangedDataDTO changedDataDTO = new ChangedDataDTO();
                changedDataDTO.setId(contentId);
                changedDataDTO.setDataType(contentMongo.getDataType());
                changedDataDTO.setShowType(contentMongo.getShowType());

                String serviceBizParamRedisDTOStr = (String) redisTemplate.boundValueOps(RedisKeys.BIZ_PARAM_DATAID_RELATED_SERVICE_DATA.concat(contentMongo.getBizParamDataId())).get();
                if (StringUtils.isNotBlank(serviceBizParamRedisDTOStr)) {
                    ServiceBizParamRedisDTO redisDTO = JSON.parseObject(serviceBizParamRedisDTOStr, ServiceBizParamRedisDTO.class);
                    if (redisDTO != null && StringUtils.isNotBlank(redisDTO.getServiceBizParam())) {
                        changedDataDTO.setBizParam(redisDTO.getServiceBizParam());
                    }
                }

                if (StringUtils.isBlank(changedDataDTO.getBizParam())) {
                    LifeCircleServiceBizParamMongo bizParamMongo = lifeCircleServiceBizParamRepository.getById(contentMongo.getBizParamDataId());
                    if (bizParamMongo != null) {
                        changedDataDTO.setBizParam(bizParamMongo.getServiceBizParam());
                    }
                }

                if (StringUtils.isBlank(changedDataDTO.getBizParam())) {
                    log.error("未找到最新业务参数数据，contentId={}", contentId);
                    continue;
                }

                changedDataDTOList.add(changedDataDTO);
            }
        }

        return changedDataDTOList;
    }

    @Override
    public void socketNotify(Long toUserId, Long contentId) {
        serviceBizParamChangedNotifyAppService.doNotify(toUserId, contentId, null);
    }

    @Override
    public void syncServiceCurrentState(Long contentId) {
        LifeCircleContentMongo contentMongo = lifeCircleContentRepository.getById(contentId);
        if (contentMongo == null) {
            throw new LifeCircleException("未找到对应数据");
        }

        if (!ContentTypeEnum.SERVICE.getType().equals(contentMongo.getContentType())) {
            throw new LifeCircleException("不是服务动态");
        }

        if (ServiceStatusEnum.OVER.getStatus().equals(contentMongo.getServiceStatus())) {
            throw new LifeCircleException("该服务已结束");
        }

        List<Integer> excludeShowTypeList = new ArrayList<>();
        excludeShowTypeList.add(ShowTypeEnum.WONDERFUL_MOMENTS_SERVICE_DYNAMIC.getType());
        excludeShowTypeList.add(ShowTypeEnum.MARKET_COMMODITY.getType());
        excludeShowTypeList.add(ShowTypeEnum.CHIGUA_MALL_SERVICE_DYNAMIC.getType());
        if (excludeShowTypeList.contains(contentMongo.getShowType())) {
            throw new LifeCircleException("不支持该类型数据同步，showType=" + contentMongo.getShowType());
        }

        UpdateServiceBizParamDTO bizParamDTO = factoryService.getService(DataTypeEnum.typeOf(contentMongo.getDataType())).getServiceCurrentState(contentMongo.getServiceDataId());
        if (bizParamDTO != null) {
            InnerRpcResponse response = updateServiceBizParam(bizParamDTO);
            if (!response.isSuccess()) {
                throw new LifeCircleException(response.getCode(), response.getMessage());
            }
        } else {
            throw new LifeCircleException("服务当前状态数据为空");
        }
    }

    @Override
    public void delete(DeleteDTO dto) {
        if (dto == null || dto.getShowTypeEnum() == null || dto.getDataTypeEnum() == null) {
            log.error("参数错误，无法删除，dto={}", JSON.toJSONString(dto));
            return;
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        QueryTypeDataParam param = new QueryTypeDataParam();

        param.setPageSize(100);
        param.setDataType(dto.getDataTypeEnum().getType());
        param.setShowType(dto.getShowTypeEnum().getType());
        param.setCategoryId(dto.getCategoryId());
        param.setCategoryName(dto.getCategoryName());
        param.setMaxCreateTime(dto.getMaxCreateTime());
        param.setIgnoreServiceStatus(false);
        param.setMaxId(null);

        List<LifeCircleContentMongo> pageDataList = null;
        boolean doQuery = true;
        int successCount = 0;

        do {
            pageDataList = lifeCircleContentRepository.getTypeData(param);

            if (CollectionUtils.isEmpty(pageDataList)) {
                doQuery = false;
                break;
            }

            param.setMaxId(pageDataList.get(pageDataList.size() - 1).get_id());

            for (LifeCircleContentMongo contentMongo : pageDataList) {
                delete(contentMongo.get_id());
                successCount++;
            }
        } while (doQuery);

        stopWatch.stop();
        log.info("删除动态数据成功，共{}条，耗时{}ms，dto={}", successCount, stopWatch.getTotalTimeMillis(), JSON.toJSONString(dto));
    }

    @Override
    public void delete(long contentId) {
        deleteData(contentId, false, null);
    }

    @Override
    public void deletePersonal(long contentId, Long userId) {
        deleteData(contentId, true, userId);
    }

    private void deleteData(long contentId, boolean onlyDelPersonal, Long userId) {
        LifeCircleContentMongo contentMongo = lifeCircleContentRepository.getById(contentId);
        if (contentMongo == null) {
            return;
        }

        if (onlyDelPersonal) {
            if (ContentTypeEnum.SERVICE.getType().equals(contentMongo.getContentType())) {
                throw new LifeCircleException("不能删除服务动态数据");
            }

            if (userId == null || !userId.equals(contentMongo.getUserId())) {
                throw new LifeCircleException("无权删除个人动态数据");
            }
        }

        LifeCircleContentMongo updateContentMongo = new LifeCircleContentMongo();
        updateContentMongo.set_id(contentId);
        updateContentMongo.setServiceStatus(ServiceStatusEnum.OVER.getStatus());

        lifeCircleContentRepository.updateById(updateContentMongo);

        LifeCircleServiceBizParamMongo updateBizParamMongo = new LifeCircleServiceBizParamMongo();
        updateBizParamMongo.set_id(contentMongo.getBizParamDataId());
        updateBizParamMongo.setServiceStatus(ServiceStatusEnum.OVER.getStatus());

        lifeCircleServiceBizParamRepository.updateById(updateBizParamMongo);

        LifeCircleContentES contentES = new LifeCircleContentES();
        contentES.setId(contentId);
        contentES.setServiceStatus(ServiceStatusEnum.OVER.getStatus());

        lifeCircleContentESService.update(contentES);
    }

    @Override
    public void initDataCategory() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        log.info("开始初始化数据分类");

        boolean doQuery = false;
        Long minId = 0L;
        int pageSize = 100;

        List<LifeCircleContentMongo> contentMongoList = null;
        LifeCircleContentMongo updContentMongo = null;
        LifeCircleServiceBizParamMongo bizParamMongo = null;
        int successCount = 0;

        do {
            contentMongoList = lifeCircleContentRepository.getPageData(minId, pageSize);
            if (CollectionUtils.isEmpty(contentMongoList)) {
                doQuery = false;
                break;
            }

            for (LifeCircleContentMongo contentMongo : contentMongoList) {
                if (contentMongo.get_id() > minId) {
                    minId = contentMongo.get_id();
                }

                if (StringUtils.isNotBlank(contentMongo.getCategoryName())) {
                    continue;
                }

                bizParamMongo = lifeCircleServiceBizParamRepository.getById(contentMongo.getBizParamDataId());
                if (bizParamMongo.getServiceSpecialParam() == null) {
                    continue;
                }

                String categoryName = bizParamMongo.getServiceSpecialParam().getString("categoryName");
                if (StringUtils.isBlank(categoryName)) {
                    continue;
                }

                updContentMongo = new LifeCircleContentMongo();
                updContentMongo.set_id(contentMongo.get_id());
                updContentMongo.setCategoryName(categoryName);

                String categoryId = bizParamMongo.getServiceSpecialParam().getString("categoryId");
                if (StringUtils.isBlank(categoryId)) {
                    categoryId = null;
                }
                updContentMongo.setCategoryId(categoryId);

                lifeCircleContentRepository.updateById(updContentMongo);

                redisCacheService.addDataCategory(DataTypeEnum.typeOf(contentMongo.getDataType()), ShowTypeEnum.typeOf(contentMongo.getShowType()), categoryName, categoryId);

                successCount++;
            }

            doQuery = true;
        } while (doQuery);

        stopWatch.stop();
        log.info("初始化数据分类完成, 成功{}条，耗时{}ms", successCount, stopWatch.getTotalTimeMillis());
    }


    @Override
    public void initESData(Long startId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        log.info("开始初始化ES数据");

        boolean doQuery = false;
        Long minId = 0L;
        int pageSize = 100;

        if (startId != null && startId > 0) {
            minId = startId;
        }

        List<LifeCircleContentMongo> contentMongoList = null;
        LifeCircleContentMongo updContentMongo = null;
        LifeCircleServiceBizParamMongo bizParamMongo = null;
        int successCount = 0;

        do {
            contentMongoList = lifeCircleContentRepository.getPageData(minId, pageSize);
            if (CollectionUtils.isEmpty(contentMongoList)) {
                doQuery = false;
                break;
            }

            for (LifeCircleContentMongo contentMongo : contentMongoList) {
                if (contentMongo.get_id() > minId) {
                    minId = contentMongo.get_id();
                }

                bizParamMongo = lifeCircleServiceBizParamRepository.getById(contentMongo.getBizParamDataId());

                lifeCircleContentESService.add(trans2LifeCircleContentES(contentMongo, bizParamMongo));

                successCount++;
            }

            doQuery = true;
        } while (doQuery);

        stopWatch.stop();
        log.info("初始化ES数据完成, 成功{}条，耗时{}ms", successCount, stopWatch.getTotalTimeMillis());
    }

    @Override
    public void initESOneData(Long contentId) {
        LifeCircleContentMongo contentMongo = lifeCircleContentRepository.getById(contentId);
        if (contentMongo == null) {
            throw new LifeCircleException("数据不存在");
        }

        LifeCircleServiceBizParamMongo bizParamMongo = lifeCircleServiceBizParamRepository.getById(contentMongo.getBizParamDataId());
        if (bizParamMongo == null) {
            throw new LifeCircleException("业务参数数据不存在");
        }

        lifeCircleContentESService.add(trans2LifeCircleContentES(contentMongo, bizParamMongo));
    }
}
