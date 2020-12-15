package com.idianyou.lifecircle.controller;

import com.alibaba.fastjson.JSON;
import com.idianyou.lifecircle.dto.ChangedDataDTO;
import com.idianyou.lifecircle.dto.GetChannelDataDTO;
import com.idianyou.lifecircle.dto.GetUserDataDTO;
import com.idianyou.lifecircle.dto.request.DelUserDataReq;
import com.idianyou.lifecircle.dto.request.GetChannelDataReq;
import com.idianyou.lifecircle.dto.request.GetChannelServicePortalReq;
import com.idianyou.lifecircle.dto.request.GetDetailsReq;
import com.idianyou.lifecircle.dto.request.GetLastBizParamReq;
import com.idianyou.lifecircle.dto.request.GetUserDataReq;
import com.idianyou.lifecircle.dto.request.GetUserNativeTypeReq;
import com.idianyou.lifecircle.dto.request.SetUserNativeTypeReq;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.dto.vo.ContentDetailsVO;
import com.idianyou.lifecircle.dto.vo.GetUserNativeTypeVO;
import com.idianyou.lifecircle.dto.vo.IconBtnRow;
import com.idianyou.lifecircle.dto.vo.UserDataVO;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.enums.UserNativeTypeEnum;
import com.idianyou.lifecircle.exception.LifeCircleException;
import com.idianyou.lifecircle.service.AppListDubboService;
import com.idianyou.lifecircle.service.FilterUnknownShowTypeService;
import com.idianyou.lifecircle.service.LifeCircleContentPublishDubboService;
import com.idianyou.lifecircle.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/6 15:38
 */
@Slf4j
@RequestMapping("/lifecircle/appList")
@RestController
public class AppListController extends BaseController {

    @Autowired
    private LifeCircleContentPublishDubboService lifeCircleContentPublishDubboService;

    @Autowired
    private AppListDubboService appListDubboService;

    @Autowired
    private FilterUnknownShowTypeService filterUnknownShowTypeService;

    /**
     * 获取频道数据
     * @param req
     * @return
     */
    @RequestMapping("getChannelData")
    public Result getChannelData(GetChannelDataReq req) {
        try {
            checkGetChannelDataReq(req);

            // 校验版本号

            Long userId = getLoginUserId();
            if (userId == null) {
                return Result.notLogin();
            }

            GetChannelDataDTO dto = new GetChannelDataDTO();
            dto.setUserId(userId);
            dto.setDeviceId(req.getDeviceId());
            dto.setReqNo(req.getReqNo());
            dto.setChannelEnum(LifeCircleChannelEnum.channelIdOf(req.getChannelId()));

            List<ChannelDataGroupVO> dataList = appListDubboService.getChannelData(dto);

            if (req.getV() == null || req.getV() < 1) {
                filterUnknownShowTypeService.filterNovel(dataList);
            }

            return Result.success(dataList);
        } catch (LifeCircleException e) {
            log.error("获取频道数据出错：req={}", JSON.toJSONString(req), e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("获取频道数据出错：req={}", JSON.toJSONString(req), e);
            return Result.systemError();
        }
    }

    private void checkGetChannelDataReq(GetChannelDataReq req) {
        try {
            Assert.notNull(req, "参数不能为空");
            Assert.notNull(req.getChannelId(), "参数[channelId]错误");
            Assert.notNull(LifeCircleChannelEnum.channelIdOf(req.getChannelId()), "参数[channelId]错误");
            Assert.isTrue(StringUtils.isNotBlank(req.getReqNo()), "参数[reqNo]错误");
            Assert.isTrue(StringUtils.isNotBlank(req.getAppVersionCode()), "参数[appVersionCode]错误");
        } catch (Exception e) {
            throw new LifeCircleException("501", e.getMessage());
        }
    }

    /**
     * 获取最新业务参数
     * @param req
     * @return
     */
    @RequestMapping("getLastBizParam")
    public Result getLastBizParam(GetLastBizParamReq req) {
        try {
            checkGetLastBizParamReq(req);

            Long userId = getLoginUserId();
            if (userId == null) {
                return Result.notLogin();
            }

            List<Long> contentIdList = new ArrayList<>();
            for (String contentIdStr : req.getContentIds().split(",")) {
                contentIdList.add(Long.valueOf(contentIdStr));
            }

            List<ChangedDataDTO> changedDataDTOList = lifeCircleContentPublishDubboService.getLastBizParam(contentIdList);

            return Result.success(changedDataDTOList);
        } catch (LifeCircleException e) {
            log.error("获取最新的业务参数出错：req={}", JSON.toJSONString(req), e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("获取最新的业务参数出错：req={}", JSON.toJSONString(req), e);
            return Result.systemError();
        }
    }

    private void checkGetLastBizParamReq(GetLastBizParamReq req) {
        try {
            Assert.notNull(req, "参数不能为空");
            Assert.isTrue(StringUtils.isNotBlank(req.getContentIds()), "参数[contentIds]错误");
            Assert.isTrue(StringUtils.isNotBlank(req.getAppVersionCode()), "参数[appVersionCode]错误");
        } catch (Exception e) {
            throw new LifeCircleException("501", e.getMessage());
        }
    }

    /**
     * 获取动态详情
     * @param req
     * @return
     */
    @RequestMapping("details")
    public Result details(GetDetailsReq req) {
        try {
            checkGetDetailsReq(req);

            Long userId = getLoginUserId();
            req.setUserId(userId);

            ContentDetailsVO detailsVO = appListDubboService.getContentDetails(req);

            return Result.success(detailsVO);
        } catch (LifeCircleException e) {
            log.error("获取最新的业务参数出错：req={}", JSON.toJSONString(req), e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("获取最新的业务参数出错：req={}", JSON.toJSONString(req), e);
            return Result.systemError();
        }
    }

    private void checkGetDetailsReq(GetDetailsReq req) {
        try {
            Assert.notNull(req, "参数不能为空");
            Assert.notNull(req.getContentId(), "参数[contentId]错误");
            Assert.isTrue(StringUtils.isNotBlank(req.getAppVersionCode()), "参数[appVersionCode]错误");
        } catch (Exception e) {
            throw new LifeCircleException("501", e.getMessage());
        }
    }

    /**
     * 通知前端业务数据有变更
     * @param toUserId
     * @param contentId
     * @return
     */
    @RequestMapping("socketNotify")
    public Result socketNotify(Long toUserId, Long contentId) {
        try {
            Long userId = getLoginUserId();
            if (userId == null) {
                return Result.notLogin();
            }

            lifeCircleContentPublishDubboService.socketNotify(toUserId, contentId);

            return Result.success();
        } catch (LifeCircleException e) {
            log.error("通知前端业务数据有变更出错：toUserId={}, contentId={}", toUserId, contentId, e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("通知前端业务数据有变更出错：toUserId={}, contentId={}", toUserId, contentId, e);
            return Result.systemError();
        }
    }

    /**
     * 同步服务当前状态
     * @param contentId
     * @return
     */
    @RequestMapping("syncServiceCurrentState")
    public Result syncServiceCurrentState(Long contentId) {
        try {
            if (contentId == null || contentId <= 0) {
                return Result.failed("501", "参数[contentId]错误");
            }

            Long userId = getLoginUserId();
            if (userId == null) {
                return Result.notLogin();
            }

            lifeCircleContentPublishDubboService.syncServiceCurrentState(contentId);

            return Result.success();
        } catch (LifeCircleException e) {
            log.error("同步服务当前状态出错：contentId={}", contentId, e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("同步服务当前状态出错：contentId={}", contentId, e);
            return Result.systemError();
        }
    }


    /**
     * 我的动态-生活动态
     * @param req
     * @return
     */
    @RequestMapping("getUserData")
    public Result getUserData(GetUserDataReq req) {
        try {
            checkGetUserDataReq(req);

            Long userId = getLoginUserId();
            if (userId == null) {
                return Result.notLogin();
            }

            GetUserDataDTO dto = new GetUserDataDTO();
            dto.setLoginUserid(userId);
            dto.setUserId(req.getUserId());
            dto.setFirstId(req.getFirstId());
            dto.setLastId(req.getLastId());

            UserDataVO userDataVO = appListDubboService.getUserData(dto);

            return Result.success(userDataVO);
        } catch (LifeCircleException e) {
            log.error("获取[我的动态-生活动态]出错：req={}", JSON.toJSONString(req), e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("获取[我的动态-生活动态]出错：req={}", JSON.toJSONString(req), e);
            return Result.systemError();
        }
    }

    private void checkGetUserDataReq(GetUserDataReq req) {
        try {
            Assert.notNull(req, "参数不能为空");
            Assert.notNull(req.getUserId(), "参数[userId]错误");
            Assert.isTrue(StringUtils.isNotBlank(req.getAppVersionCode()), "参数[appVersionCode]错误");

            if (req.getFirstId() != null && req.getFirstId() > 0
                    && req.getLastId() != null && req.getLastId() > 0) {
                Assert.notNull(null, "参数[firstId、lastId]不能同时有值");
            }

            if (req.getFirstId() != null && req.getFirstId() <= 0) {
                req.setFirstId(null);
            }

            if (req.getLastId() != null && req.getLastId() <= 0) {
                req.setLastId(null);
            }
        } catch (Exception e) {
            throw new LifeCircleException("501", e.getMessage());
        }
    }

    /**
     * 我的动态-生活动态-删除
     * @param req
     * @return
     */
    @RequestMapping("delUserData")
    public Result delUserData(DelUserDataReq req) {
        try {
            checkDelUserDataReq(req);

            Long userId = getLoginUserId();
            if (userId == null) {
                return Result.notLogin();
            }

            req.setUserId(userId);

            appListDubboService.delUserData(req);

            return Result.success();
        } catch (LifeCircleException e) {
            log.error("[我的动态-生活动态-删除]出错：req={}", JSON.toJSONString(req), e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("[我的动态-生活动态-删除]出错：req={}", JSON.toJSONString(req), e);
            return Result.systemError();
        }
    }

    private void checkDelUserDataReq(DelUserDataReq req) {
        try {
            Assert.notNull(req, "参数不能为空");
            Assert.notNull(req.getContentId(), "参数[contentId]错误");
            Assert.isTrue(StringUtils.isNotBlank(req.getAppVersionCode()), "参数[appVersionCode]错误");
        } catch (Exception e) {
            throw new LifeCircleException("501", e.getMessage());
        }
    }

    /**
     * 获取用户在本地的情况
     * @return
     */
    @RequestMapping("getUserNativeType")
    public Result getUserNativeType(GetUserNativeTypeReq req) {
        Long userId = null;
        try {
            checkGetUserNativeTypeReq(req);

            userId = getLoginUserId();
            if (userId == null) {
                return Result.notLogin();
            }

            req.setUserId(userId);

            GetUserNativeTypeVO userNativeTypeVO = appListDubboService.getUserNativeType(req);

            return Result.success(userNativeTypeVO);
        } catch (LifeCircleException e) {
            log.error("[获取用户在本地的情况]出错：userId={}", userId, e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("[获取用户在本地的情况]出错：userId={}", userId, e);
            return Result.systemError();
        }
    }

    private void checkGetUserNativeTypeReq(GetUserNativeTypeReq req) {
        try {
            Assert.notNull(req, "参数不能为空");
            Assert.isTrue(StringUtils.isNotBlank(req.getAppVersionCode()), "参数[appVersionCode]错误");
        } catch (Exception e) {
            throw new LifeCircleException("501", e.getMessage());
        }
    }

    /**
     * 设置用户在本地的情况
     * @return
     */
    @RequestMapping("setUserNativeType")
    public Result setUserNativeType(SetUserNativeTypeReq req) {
        Long userId = null;
        try {
            checkSetUserNativeTypeReq(req);

            userId = getLoginUserId();
            if (userId == null) {
                return Result.notLogin();
            }

            req.setUserId(userId);

            appListDubboService.setUserNativeType(req);

            return Result.success();
        } catch (LifeCircleException e) {
            log.error("[设置用户在本地的情况]出错：userId={}, req={}", userId, JSON.toJSONString(req), e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("[设置用户在本地的情况]出错：userId={}, req={}", userId, JSON.toJSONString(req), e);
            return Result.systemError();
        }
    }

    private void checkSetUserNativeTypeReq(SetUserNativeTypeReq req) {
        try {
            Assert.notNull(req, "参数不能为空");
            Assert.notNull(UserNativeTypeEnum.codeOf(req.getUserNativeType()), "参数[userNativeType]错误");
            Assert.isTrue(StringUtils.isNotBlank(req.getAppVersionCode()), "参数[appVersionCode]错误");
        } catch (Exception e) {
            throw new LifeCircleException("501", e.getMessage());
        }
    }

    /**
     * 获取频道服务入口
     * @return
     */
    @RequestMapping("getChannelServicePortal")
    public Result getChannelServicePortal(GetChannelServicePortalReq req) {
        Long userId = null;
        try {
            checkGetChannelServicePortalReq(req);

            userId = getLoginUserId();
            if (userId == null) {
                return Result.notLogin();
            }

            List<IconBtnRow> dataList = appListDubboService.getChannelServicePortal(req);

            return Result.success(dataList);
        } catch (LifeCircleException e) {
            log.error("[获取频道服务入口]出错：userId={}", userId, e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("[获取频道服务入口]出错：userId={}", userId, e);
            return Result.systemError();
        }
    }

    private void checkGetChannelServicePortalReq(GetChannelServicePortalReq req) {
        try {
            Assert.notNull(req, "参数不能为空");
            Assert.notNull(LifeCircleChannelEnum.channelIdOf(req.getChannelId()), "参数[channelId]错误");
        } catch (Exception e) {
            throw new LifeCircleException("501", e.getMessage());
        }
    }


    /**
     * 获取问答红包广告位数据
     * @return
     */
    @RequestMapping("getQAAdData")
    public Result getQAAdData() {
        Long userId = null;
        try {
            userId = getLoginUserId();
            if (userId == null) {
                return Result.notLogin();
            }

            GetChannelDataDTO dto = new GetChannelDataDTO();
            dto.setUserId(userId);
            dto.setReqNo(System.currentTimeMillis() + "");

            List<ChannelDataGroupVO> dataList = appListDubboService.getQAAdData(dto);

            return Result.success(dataList);
        } catch (LifeCircleException e) {
            log.error("获取[问答红包-广告位]数据出错：userId={}", userId, e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("获取[问答红包-广告位]数据出错：userId={}", userId, e);
            return Result.systemError();
        }
    }

}