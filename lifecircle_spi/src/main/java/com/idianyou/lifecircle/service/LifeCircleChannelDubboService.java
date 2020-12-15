package com.idianyou.lifecircle.service;

import com.idianyou.lifecircle.dto.InnerRpcResponse;
import com.idianyou.lifecircle.dto.LifeCircleChannelDTO;

import java.util.List;

/**
 * @Description: 生活圈-频道服务
 * @version: v1.0.0
 * @author: xxj
 * @date: 2020-04-27 17:04
 */
public interface LifeCircleChannelDubboService {

    /**
     * @desc 保存频道
     * @param dto：频道信息
     * @return InnerRpcResponse<LifeCircleChannelDTO>
     * @author xxj
     **/
    InnerRpcResponse<LifeCircleChannelDTO> save(LifeCircleChannelDTO dto);

    /**
     * @desc 批量保存频道
     * @param dtos：频道信息
     * @return InnerRpcResponse<LifeCircleChannelDTO>
     * @author xxj
     **/
    InnerRpcResponse<Integer> bulkSave(List<LifeCircleChannelDTO> dtos);

    /**
     * @desc 根据 _id 查询频道
     * @param _id：频道 id，例："_id" : 1
     * @return InnerRpcResponse<LifeCircleChannelDTO>
     * @author xxj
     **/
    InnerRpcResponse<LifeCircleChannelDTO> queryById(Integer _id);

    /**
     * @desc 根据 _ids 查询频道
     * @param _ids：频道 _ids 集合，例："_ids" : 1
     * @return InnerRpcResponse<LifeCircleChannelDTO>
     * @author xxj
     **/
    InnerRpcResponse<List<LifeCircleChannelDTO>> queryByIds(List<Integer> _ids);

    /**
     * @desc 查询全部频道
     * @param
     * @return InnerRpcResponse<LifeCircleChannelDTO>
     * @author xxj
     **/
    InnerRpcResponse<List<LifeCircleChannelDTO>> queryAll();

    /**
     * @desc 根据 _ids 查询频道
     * @param _id：频道 id，例："_id" : 1
     * @return InnerRpcResponse<LifeCircleChannelDTO>
     * @author xxj
     **/
    InnerRpcResponse<Boolean> detele(Integer _id);

    /**
     * @desc 根据 _ids 查询频道
     * @param dto：频道信息集合
     * @return InnerRpcResponse<Boolean> 是否
     * @author xxj
     **/
    InnerRpcResponse<Boolean> update(LifeCircleChannelDTO dto);
}
