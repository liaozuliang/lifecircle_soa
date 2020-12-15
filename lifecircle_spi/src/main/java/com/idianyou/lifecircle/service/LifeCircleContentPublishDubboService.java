package com.idianyou.lifecircle.service;

import com.idianyou.lifecircle.dto.ChangedDataDTO;
import com.idianyou.lifecircle.dto.DeleteDTO;
import com.idianyou.lifecircle.dto.InnerRpcResponse;
import com.idianyou.lifecircle.dto.LifeCircleContentPublishDTO;
import com.idianyou.lifecircle.dto.UpdateServiceBizParamDTO;
import com.idianyou.lifecircle.enums.IdentityTypeEnum;

import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/24 15:25
 */
public interface LifeCircleContentPublishDubboService {

    /**
     * 发布服务动态
     * @param dto
     * @return
     */
    InnerRpcResponse<Long> publish(LifeCircleContentPublishDTO dto);

    /**
     * 分享方式发布动态
     * @param dto
     * @return
     */
    InnerRpcResponse<Long> sharePublish(LifeCircleContentPublishDTO dto);

    /**
     * 更新服务业务参数
     * @param dto
     */
    InnerRpcResponse updateServiceBizParam(UpdateServiceBizParamDTO dto);

    /**
     * 删除数据
     * @param contentId
     */
    void delete(long contentId);

    /**
     * 删除数据
     * @param dto
     */
    void delete(DeleteDTO dto);

    /**
     * 删除个人动态数据
     * @param contentId
     * @param userId
     */
    void deletePersonal(long contentId, Long userId);

    /**
     * 通过业务参数ID获取动态ID
     * @param bizParamDataId
     * @return
     */
    List<Long> getContentIdsByBizParamDataId(String bizParamDataId);

    /**
     * 获取最新的业务参数
     * @param contentIdList
     * @return
     */
    List<ChangedDataDTO> getLastBizParam(List<Long> contentIdList);

    /**
     * 通知前端业务数据有变更
     * @param toUserId
     * @param contentId
     */
    void socketNotify(Long toUserId, Long contentId);

    /**
     * 同步服务当前状态
     * @param contentId
     */
    void syncServiceCurrentState(Long contentId);

    /**
     * 获取用户身份
     * @param userId
     * @return
     */
    List<IdentityTypeEnum> getUserIdentity(Long userId);

    /**
     * 初始化数据分类
     */
    void initDataCategory();

    /**
     * 初始化ES数据
     */
    void initESData(Long startId);

    /**
     * 初始化ES单条数据
     * @param contentId
     */
    void initESOneData(Long contentId);
}
