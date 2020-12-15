package com.idianyou.lifecircle.controller;

import com.alibaba.fastjson.JSON;
import com.idianyou.lifecircle.dto.InnerRpcResponse;
import com.idianyou.lifecircle.dto.LifeCircleContentPublishDTO;
import com.idianyou.lifecircle.dto.UpdateServiceBizParamDTO;
import com.idianyou.lifecircle.dto.request.PublishReq;
import com.idianyou.lifecircle.dto.request.UpdateBizParamReq;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.ServiceStatusEnum;
import com.idianyou.lifecircle.enums.ShowTypeEnum;
import com.idianyou.lifecircle.exception.LifeCircleException;
import com.idianyou.lifecircle.service.LifeCircleContentPublishDubboService;
import com.idianyou.lifecircle.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Description: 服务动态发布相关
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/24 14:39
 */
@Slf4j
@RequestMapping("/lifecircle/content")
@RestController
public class LifeCircleContentPublishController extends BaseController {

    @Autowired
    private LifeCircleContentPublishDubboService lifeCircleContentPublishDubboService;

    /**
     * 发布动态
     * @return
     */
    @RequestMapping(value = "publish")
    public Result publish(PublishReq req) {
        try {
            checkPublishParam(req);

            LifeCircleContentPublishDTO publishDTO = new LifeCircleContentPublishDTO();
            publishDTO.setUserId(req.getUserId());
            publishDTO.setDataTypeEnum(DataTypeEnum.typeOf(req.getDataType()));
            publishDTO.setShowTypeEnum(ShowTypeEnum.typeOf(req.getShowType()));
            publishDTO.setServiceDataId(req.getServiceDataId());
            publishDTO.setServiceClientId(req.getServiceClientId());
            publishDTO.setServiceStatusEnum(ServiceStatusEnum.statusOf(req.getServiceStatus()));
            publishDTO.setShareContent(req.getShareContent());
            publishDTO.setServiceBizParam(req.getServiceBizParam());
            publishDTO.setCreateTime(new Date(req.getCreateTime()));

            if (StringUtils.isNotBlank(req.getServiceSpecialParam())) {
                publishDTO.setServiceSpecialParam(JSON.parseObject(req.getServiceSpecialParam()));
            }

            InnerRpcResponse<Long> response = lifeCircleContentPublishDubboService.publish(publishDTO);
            if (response.isSuccess()) {
                return Result.success(response.getData());
            } else {
                return Result.failed(response.getCode(), response.getMessage());
            }
        } catch (LifeCircleException e) {
            log.error("服务动态发布出错：req={}", JSON.toJSONString(req), e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("服务动态发布出错：req={}", JSON.toJSONString(req), e);
            return Result.systemError();
        }
    }

    private void checkPublishParam(PublishReq req) {
        try {
            Assert.notNull(req, "参数不能为空");
            //Assert.notNull(req.getUserId(), "参数[userId]错误");
            Assert.notNull(DataTypeEnum.typeOf(req.getDataType()), "参数[dataType]错误");
            Assert.notNull(ShowTypeEnum.typeOf(req.getShowType()), "参数[showType]错误");
            Assert.notNull(ServiceStatusEnum.statusOf(req.getServiceStatus()), "参数[serviceStatus]错误");
            Assert.notNull(req.getCreateTime(), "参数[createTime]错误");
            Assert.isTrue(StringUtils.isNotBlank(req.getServiceDataId()), "参数[serviceDataId]错误");
            Assert.isTrue(StringUtils.isNotBlank(req.getServiceClientId()), "参数[serviceClientId]错误");
            Assert.isTrue(StringUtils.isNotBlank(req.getServiceBizParam()), "参数[serviceBizParam]错误");

            if (StringUtils.isNotBlank(req.getServiceSpecialParam())) {
                try {
                    JSON.parseObject(req.getServiceSpecialParam());
                } catch (Exception e) {
                    throw new LifeCircleException("501", "参数[serviceSpecialParam]错误");
                }
            }
        } catch (Exception e) {
            throw new LifeCircleException("501", e.getMessage());
        }
    }

    /**
     * 更新服务业务参数
     * @return
     */
    @RequestMapping("updateBizParam")
    public Result updateBizParam(UpdateBizParamReq req) {
        try {
            checkUpdateBizParam(req);

            log.info("=======UpdateBizParamReq==={}", JSON.toJSONString(req));

            UpdateServiceBizParamDTO bizParamDTO = new UpdateServiceBizParamDTO();

            bizParamDTO.setDataTypeEnum(DataTypeEnum.typeOf(req.getDataType()));
            bizParamDTO.setServiceDataId(req.getServiceDataId());
            bizParamDTO.setServiceStatusEnum(ServiceStatusEnum.statusOf(req.getServiceStatus()));
            bizParamDTO.setAllServiceBizParam(req.getAllServiceBizParam());
            bizParamDTO.setChangedBizParam(req.getChangedBizParam());

            InnerRpcResponse response = lifeCircleContentPublishDubboService.updateServiceBizParam(bizParamDTO);
            if (response.isSuccess()) {
                return Result.success();
            } else {
                return Result.failed(response.getCode(), response.getMessage());
            }
        } catch (LifeCircleException e) {
            log.error("更新服务业务参数出错：req={}", JSON.toJSONString(req), e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("更新服务业务参数出错：req={}", JSON.toJSONString(req), e);
            return Result.systemError();
        }
    }

    private void checkUpdateBizParam(UpdateBizParamReq req) {
        try {
            Assert.notNull(req, "参数不能为空");
            Assert.notNull(DataTypeEnum.typeOf(req.getDataType()), "参数[dataType]错误");
            Assert.notNull(ServiceStatusEnum.statusOf(req.getServiceStatus()), "参数[serviceStatus]错误");
            Assert.isTrue(StringUtils.isNotBlank(req.getServiceDataId()), "参数[serviceDataId]错误");
            Assert.isTrue(StringUtils.isNotBlank(req.getAllServiceBizParam()), "参数[allServiceBizParam]错误");
            Assert.isTrue(StringUtils.isNotBlank(req.getChangedBizParam()), "参数[changedBizParam]错误");
        } catch (Exception e) {
            throw new LifeCircleException("501", e.getMessage());
        }
    }

    /**
     * 初始化数据分类
     * @return
     */
    @RequestMapping("initDataCategory")
    public Result initDataCategory() {
        Long userId = null;
        try {
            userId = getLoginUserId();
            if (userId == null) {
                return Result.notLogin();
            }

            lifeCircleContentPublishDubboService.initDataCategory();

            return Result.success();
        } catch (LifeCircleException e) {
            log.error("[初始化数据分类]出错：userId={}", userId, e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("[初始化数据分类]出错：userId={}", userId, e);
            return Result.systemError();
        }
    }

    /**
     * 初始化ES数据
     * @return
     */
    @RequestMapping("initESData")
    public Result initESData(Long startId) {
        Long userId = null;
        try {
            userId = getLoginUserId();
            if (userId == null) {
                return Result.notLogin();
            }

            lifeCircleContentPublishDubboService.initESData(startId);

            return Result.success();
        } catch (LifeCircleException e) {
            log.error("[初始化ES数据]出错：userId={}, startId={}", userId, startId, e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("[初始化ES数据]出错：userId={}, startId={}", userId, startId, e);
            return Result.systemError();
        }
    }

    /**
     * 初始化ES单条数据
     * @return
     */
    @RequestMapping("initESOneData")
    public Result initESOneData(Long contentId) {
        Long userId = null;
        try {
            if (contentId == null || contentId <= 0) {
                return Result.failed("501", "参数[contentId]错误");
            }

            userId = getLoginUserId();
            if (userId == null) {
                return Result.notLogin();
            }

            lifeCircleContentPublishDubboService.initESOneData(contentId);

            return Result.success();
        } catch (LifeCircleException e) {
            log.error("[初始化ES单条数据]出错：userId={}, contentId={}", userId, contentId, e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("[初始化ES单条数据]出错：userId={}, contentId={}", userId, contentId, e);
            return Result.systemError();
        }
    }

}