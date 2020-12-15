package com.idianyou.lifecircle.service;

import com.idianyou.lifecircle.dto.InnerRpcResponse;
import com.idianyou.lifecircle.dto.PageObject;
import com.idianyou.lifecircle.dto.lifeShopsGoods.DataObj;
import com.idianyou.lifecircle.dto.lifeShopsGoods.LifeShopsGoodsDTO;
import com.idianyou.lifecircle.dto.lifeShopsGoods.LifeShopsGoodsSearchDTO;
import com.idianyou.lifecircle.enums.lifeShopsGoods.DataTypeEnum;

/**
 * @Description: 生活商品、商铺ES相关操作
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/4 16:32
 */
public interface LifeShopsGoodsESDubboService {

    /**
     * 新增或修改
     * @param dto
     * @return 返回ES中的Id
     */
    InnerRpcResponse<String> save(LifeShopsGoodsDTO dto);

    /**
     * 删除
     * @param dataId   数据ID
     * @param dataType 数据类型
     */
    InnerRpcResponse<Boolean> delete(Integer dataId, DataTypeEnum dataType);

    /**
     * 搜索
     * @param searchDTO
     * @return
     */
    InnerRpcResponse<PageObject<DataObj>> search(LifeShopsGoodsSearchDTO searchDTO);

    /**
     * 搜索数量
     * @param searchDTO
     * @return
     */
    InnerRpcResponse<Long> searchCount(LifeShopsGoodsSearchDTO searchDTO);
}
