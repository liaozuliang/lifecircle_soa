package com.idianyou.lifecircle.service.impl.dubbo;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.idianyou.lifecircle.dto.InnerRpcResponse;
import com.idianyou.lifecircle.dto.LifeCircleChannelDTO;
import com.idianyou.lifecircle.entity.mongo.LifeCircleChannelMongo;
import com.idianyou.lifecircle.enums.ApiResponseCodeEnum;
import com.idianyou.lifecircle.mongo.LifeCircleChannelRepository;
import com.idianyou.lifecircle.service.LifeCircleChannelDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 生活圈-频道服务
 * @version: v1.0.0
 * @author: xxj
 * @date: 2020-04-27 17:04
 */
@Slf4j
@Service()
public class LifeCircleChannelDubboServiceImpl implements LifeCircleChannelDubboService {

    @Autowired
    private LifeCircleChannelRepository lifeCircleChannelRepository;

    /**
     * @desc 保存频道
     * @param dto：频道信息
     * @return InnerRpcResponse<LifeCircleChannelDTO>
     * @author xxj
     **/
    @Override
    public InnerRpcResponse<LifeCircleChannelDTO> save(LifeCircleChannelDTO dto) {
        log.info("LifeCircleChannelDubboServiceImpl->save->dto:{}", dto);
        InnerRpcResponse innerRpcResponse = new InnerRpcResponse();
        //检查参数是否为空
        if (dto == null || StringUtils.isBlank(dto.getChannelName())) {
            innerRpcResponse.setCode(ApiResponseCodeEnum.API_PARAM_NOT_NULL_ERROR.getCode());
            innerRpcResponse.setMessage(ApiResponseCodeEnum.API_PARAM_NOT_NULL_ERROR.getDesc());
            return innerRpcResponse;
        }
        //检查添加的频道名称是否已存在
        List<LifeCircleChannelMongo> mongos = lifeCircleChannelRepository.query(dto.getChannelName());
        if (CollectionUtils.isNotEmpty(mongos)) {
            innerRpcResponse.setCode(ApiResponseCodeEnum.API_DATA_ALREADY_ERROR.getCode());
            innerRpcResponse.setMessage(ApiResponseCodeEnum.API_DATA_ALREADY_ERROR.getDesc());
            return innerRpcResponse;
        }

        //dto 转换成 mongo 类
        LifeCircleChannelMongo mongo = new LifeCircleChannelMongo();
        BeanUtils.copyProperties(dto, mongo);

        //从mongo中获取最新的channelId +1
        Integer channelId = getTheLatestChannelId();
        mongo.set_id(channelId);
        lifeCircleChannelRepository.save(mongo);

        return innerRpcResponse;
    }
    /**
     * @desc 从mongo中获取最新的channelId +1
     * @param
     * @return channelId
     * @author xxj
     **/
    private Integer getTheLatestChannelId(){
        Integer channelId = 1;
        LifeCircleChannelMongo mongo = lifeCircleChannelRepository.getTheLatestChannelId();
        if (mongo != null){
            channelId = mongo.get_id() + 1;
        }

        return channelId;
    }

    /**
     * @desc 批量保存频道
     * @param dtos：频道信息
     * @return InnerRpcResponse<LifeCircleChannelDTO>
     * @author xxj
     **/
    @Override
    public InnerRpcResponse<Integer> bulkSave(List<LifeCircleChannelDTO> dtos) {
        log.info("LifeCircleChannelDubboServiceImpl->bulkSave->dtos:{}", dtos);
        InnerRpcResponse innerRpcResponse = new InnerRpcResponse();
        //检查参数是否为空
        if (CollectionUtils.isEmpty(dtos)) {
            innerRpcResponse.setCode(ApiResponseCodeEnum.API_PARAM_NOT_NULL_ERROR.getCode());
            innerRpcResponse.setMessage(ApiResponseCodeEnum.API_PARAM_NOT_NULL_ERROR.getDesc());
            return innerRpcResponse;
        }
        //检查添加的频道名称是否已存在
        List<String> channelNames = new ArrayList<>();
        for (LifeCircleChannelDTO dto : dtos) {
            if (dto != null) {
                channelNames.add(dto.getChannelName());
            }
        }
        List<LifeCircleChannelMongo> mongos = lifeCircleChannelRepository.queryByChannelNames(channelNames);
        if (CollectionUtils.isNotEmpty(mongos)) {
            innerRpcResponse.setCode(ApiResponseCodeEnum.API_DATA_ALREADY_ERROR.getCode());
            innerRpcResponse.setMessage(ApiResponseCodeEnum.API_DATA_ALREADY_ERROR.getDesc());
            return innerRpcResponse;
        }

        List<LifeCircleChannelMongo> mongoList = new ArrayList<>();

        //从mongo中获取最新的channelId +1
        Integer channelId = getTheLatestChannelId();
        for (int i = 0; i < dtos.size(); i++) {
            LifeCircleChannelDTO dto = dtos.get(i);
            if (dto != null) {
                LifeCircleChannelMongo mongo = new LifeCircleChannelMongo();
                //dto 转换成 mongo 类
                BeanUtils.copyProperties(dto, mongo);
                mongo.set_id(channelId + i);
                mongoList.add(mongo);
            }
        }

        lifeCircleChannelRepository.bulkSave(mongoList);

        return innerRpcResponse;
    }

    /**
     * @desc 根据 _id 查询频道
     * @param _id：频道 id，例："_id" : 1
     * @return InnerRpcResponse<LifeCircleChannelDTO>
     * @author xxj
     **/
    @Override
    public InnerRpcResponse<LifeCircleChannelDTO> queryById(Integer _id) {
        log.info("LifeCircleChannelDubboServiceImpl->queryById->_id:{}", _id);
        InnerRpcResponse innerRpcResponse = new InnerRpcResponse();
        //检查参数是否为空
        if (_id <= 0) {
            innerRpcResponse.setCode(ApiResponseCodeEnum.API_PARAM_NOT_NULL_ERROR.getCode());
            innerRpcResponse.setMessage(ApiResponseCodeEnum.API_PARAM_NOT_NULL_ERROR.getDesc());
            return innerRpcResponse;
        }

        LifeCircleChannelMongo mongo = lifeCircleChannelRepository.queryOne(_id);
        LifeCircleChannelDTO dto = new LifeCircleChannelDTO();
        if (mongo != null) {
            //mongo 转换成 dto 类
            BeanUtils.copyProperties(mongo, dto);
            dto.set_id(mongo.get_id());
        }

        innerRpcResponse.setData(dto);
        return innerRpcResponse;
    }

    /**
     * @desc 根据 _ids 查询频道
     * @param _ids：频道 _ids 集合，例："_ids" : 1
     * @return InnerRpcResponse<LifeCircleChannelDTO>
     * @author xxj
     **/
    @Override
    public InnerRpcResponse<List<LifeCircleChannelDTO>> queryByIds(List<Integer> _ids) {
        log.info("LifeCircleChannelDubboServiceImpl->queryByIds->_ids:{}", _ids);
        InnerRpcResponse innerRpcResponse = new InnerRpcResponse();
        //检查参数是否为空
        if (CollectionUtils.isEmpty(_ids)) {
            innerRpcResponse.setCode(ApiResponseCodeEnum.API_PARAM_NOT_NULL_ERROR.getCode());
            innerRpcResponse.setMessage(ApiResponseCodeEnum.API_PARAM_NOT_NULL_ERROR.getDesc());
            return innerRpcResponse;
        }

        List<LifeCircleChannelMongo> channelMongos = lifeCircleChannelRepository.query(_ids);
        //mongo 转换成 dto 类
        List<LifeCircleChannelDTO> dtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(channelMongos)) {
            for (LifeCircleChannelMongo mongo : channelMongos) {
                if (mongo != null) {
                    LifeCircleChannelDTO dto = new LifeCircleChannelDTO();
                    BeanUtils.copyProperties(mongo, dto);
                    dto.set_id(mongo.get_id());
                    dtos.add(dto);
                }
            }
        }

        innerRpcResponse.setData(dtos);
        return innerRpcResponse;
    }

    /**
     * @desc 查询全部频道
     * @param
     * @return InnerRpcResponse<LifeCircleChannelDTO>
     * @author xxj
     **/
    @Override
    public InnerRpcResponse<List<LifeCircleChannelDTO>> queryAll() {
        log.info("LifeCircleChannelDubboServiceImpl->queryAll");
        InnerRpcResponse innerRpcResponse = new InnerRpcResponse();

        List<LifeCircleChannelMongo> channelMongos = lifeCircleChannelRepository.query();
        //mongo 转换成 dto 类
        List<LifeCircleChannelDTO> dtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(channelMongos)) {
            for (LifeCircleChannelMongo mongo : channelMongos) {
                if (mongo != null) {
                    LifeCircleChannelDTO dto = new LifeCircleChannelDTO();
                    BeanUtils.copyProperties(mongo, dto);
                    dto.set_id(mongo.get_id());
                    dtos.add(dto);
                }
            }
        }

        innerRpcResponse.setData(dtos);
        return innerRpcResponse;
    }

    /**
     * @desc 根据 _ids 查询频道
     * @param _id：频道 id，例："_id" : 1
     * @return InnerRpcResponse<LifeCircleChannelDTO>
     * @author xxj
     **/
    @Override
    public InnerRpcResponse<Boolean> detele(Integer _id) {
        log.info("LifeCircleChannelDubboServiceImpl->detele->_id:{}", _id);
        InnerRpcResponse innerRpcResponse = new InnerRpcResponse();

        //检查参数是否为空
        if (_id <= 0) {
            innerRpcResponse.setCode(ApiResponseCodeEnum.API_PARAM_NOT_NULL_ERROR.getCode());
            innerRpcResponse.setMessage(ApiResponseCodeEnum.API_PARAM_NOT_NULL_ERROR.getDesc());
            return innerRpcResponse;
        }

        LifeCircleChannelMongo mongo = lifeCircleChannelRepository.queryOne(_id);
        if (mongo == null) {
            innerRpcResponse.setCode(ApiResponseCodeEnum.API_DATA_IS_NOT_FOUND_ERROR.getCode());
            innerRpcResponse.setMessage(ApiResponseCodeEnum.API_DATA_IS_NOT_FOUND_ERROR.getDesc());
            return innerRpcResponse;
        }
        lifeCircleChannelRepository.delete(_id);

        return innerRpcResponse;
    }

    /**
     * @desc 根据 _ids 查询频道
     * @param dto：频道信息集合
     * @return InnerRpcResponse<Boolean> 是否
     * @author xxj
     **/
    @Override
    public InnerRpcResponse<Boolean> update(LifeCircleChannelDTO dto) {
        log.info("LifeCircleChannelDubboServiceImpl->update->dto:{}", dto);
        InnerRpcResponse innerRpcResponse = new InnerRpcResponse();
        //检查参数是否为空
        if (dto == null || dto.get_id() <= 0) {
            innerRpcResponse.setCode(ApiResponseCodeEnum.API_PARAM_NOT_NULL_ERROR.getCode());
            innerRpcResponse.setMessage(ApiResponseCodeEnum.API_PARAM_NOT_NULL_ERROR.getDesc());
            return innerRpcResponse;
        }

        LifeCircleChannelMongo mongo = lifeCircleChannelRepository.queryOne(dto.get_id());
        if (mongo == null) {
            innerRpcResponse.setCode(ApiResponseCodeEnum.API_DATA_IS_NOT_FOUND_ERROR.getCode());
            innerRpcResponse.setMessage(ApiResponseCodeEnum.API_DATA_IS_NOT_FOUND_ERROR.getDesc());
            return innerRpcResponse;
        }
        //dto 转换成 mongo 类
        LifeCircleChannelMongo channelMongo = new LifeCircleChannelMongo();
        BeanUtils.copyProperties(dto, channelMongo);
        channelMongo.set_id(dto.get_id());
        lifeCircleChannelRepository.update(channelMongo);

        innerRpcResponse.setData(dto);
        return innerRpcResponse;
    }
}
