package com.idianyou.lifecircle.service.impl.dubbo;

import com.alibaba.fastjson.JSON;
import com.idianyou.lifecircle.dto.InnerRpcResponse;
import com.idianyou.lifecircle.dto.PageObject;
import com.idianyou.lifecircle.dto.lifeShopsGoods.DataObj;
import com.idianyou.lifecircle.dto.lifeShopsGoods.LifeShopsGoodsDTO;
import com.idianyou.lifecircle.dto.lifeShopsGoods.LifeShopsGoodsSearchDTO;
import com.idianyou.lifecircle.enums.lifeShopsGoods.DataTypeEnum;
import com.idianyou.lifecircle.es.AreaES;
import com.idianyou.lifecircle.es.LifeShopsGoodsES;
import com.idianyou.lifecircle.es.LifeShopsGoodsESExt;
import com.idianyou.lifecircle.es.LifeShopsGoodsESService;
import com.idianyou.lifecircle.exception.LifeCircleException;
import com.idianyou.lifecircle.service.LifeShopsGoodsESDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/4 17:23
 */
@Slf4j
@Service
public class LifeShopsGoodsESDubboServiceImpl implements LifeShopsGoodsESDubboService {

    @Autowired
    private LifeShopsGoodsESService lifeShopsGoodsESService;

    @Override
    public InnerRpcResponse<String> save(LifeShopsGoodsDTO dto) {
        try {
            checkSaveParam(dto);

            LifeShopsGoodsES lifeShopsGoodsES = new LifeShopsGoodsES();

            lifeShopsGoodsES.setId(LifeShopsGoodsESService.getESId(dto.getDataId(), dto.getDataType()));
            lifeShopsGoodsES.setDataId(dto.getDataId());
            lifeShopsGoodsES.setDataType(dto.getDataType().getType());
            lifeShopsGoodsES.setName(dto.getName());
            lifeShopsGoodsES.setMonthSales(dto.getMonthSales());
            lifeShopsGoodsES.setWeight(dto.getWeight());
            lifeShopsGoodsES.setStatus(dto.getStatus().getStatus());
            lifeShopsGoodsES.setOnOffStatus(dto.getOnOffStatus().getStatus());
            lifeShopsGoodsES.setLocation(dto.getLocation());

            if (dto.getArea() != null) {
                AreaES areaES = new AreaES();
                BeanUtils.copyProperties(dto.getArea(), areaES);
                lifeShopsGoodsES.setArea(areaES);
            }

            lifeShopsGoodsES.setCreateTime(dto.getCreateTime());
            lifeShopsGoodsES.setUpdateTime(new Date());

            lifeShopsGoodsESService.save(lifeShopsGoodsES);

            return InnerRpcResponse.success(lifeShopsGoodsES.getId());
        } catch (Exception e) {
            log.error("新增或修改[生活商品/商铺]ES数据出错：dto={}", JSON.toJSONString(dto), e);
            return InnerRpcResponse.fail(e.getMessage());
        }
    }

    private void checkSaveParam(LifeShopsGoodsDTO dto) {
        try {
            Assert.notNull(dto, "参数不能为空");
            Assert.notNull(dto.getDataId(), "参数[dataId]错误");
            Assert.notNull(dto.getDataType(), "参数[dataType]错误");
            Assert.notNull(dto.getName(), "参数[name]错误");
            Assert.notNull(dto.getMonthSales(), "参数[monthSales]错误");
            Assert.notNull(dto.getWeight(), "参数[weight]错误");
            Assert.notNull(dto.getStatus(), "参数[status]错误");
            Assert.notNull(dto.getOnOffStatus(), "参数[onOffStatus]错误");
            Assert.notNull(dto.getCreateTime(), "参数[createTime]错误");

            if (dto.getLocation() != null) {
                Assert.notNull(dto.getLocation().getLon(), "参数[location.lon]错误");
                Assert.notNull(dto.getLocation().getLat(), "参数[location.lat]错误");
            }

            //Assert.notNull(dto.getArea(), "参数[area]错误");
            //Assert.isTrue(StringUtils.isNotBlank(dto.getArea().getTownshipCode()), "参数[area.townshipCode]错误");
        } catch (Exception e) {
            throw new LifeCircleException(e.getMessage());
        }
    }

    @Override
    public InnerRpcResponse<Boolean> delete(Integer dataId, DataTypeEnum dataType) {
        try {
            lifeShopsGoodsESService.delete(LifeShopsGoodsESService.getESId(dataId, dataType));
            return InnerRpcResponse.success(true);
        } catch (Exception e) {
            log.error("删除[生活商品/商铺]ES数据出错：dataId={}, dataType={}", dataId, JSON.toJSONString(dataType), e);
            return InnerRpcResponse.fail(e.getMessage());
        }
    }

    @Override
    public InnerRpcResponse<PageObject<DataObj>> search(LifeShopsGoodsSearchDTO searchDTO) {
        try {
            checkSearchParam(searchDTO);

            // 总数量
            Long total = lifeShopsGoodsESService.count(searchDTO);

            PageObject<DataObj> pageObject = new PageObject<DataObj>(total.intValue(), searchDTO.getPageObj().getCurrentPage(), searchDTO.getPageObj().getPageSize());

            if (total == 0) {
                return InnerRpcResponse.success(pageObject);
            }

            List<LifeShopsGoodsESExt> shopsGoodsESList = lifeShopsGoodsESService.search(searchDTO);
            if (CollectionUtils.isEmpty(shopsGoodsESList)) {
                return InnerRpcResponse.success(pageObject);
            }

            DataObj dataObj = null;
            List<DataObj> dataObjList = new ArrayList<>(shopsGoodsESList.size());

            for (LifeShopsGoodsESExt es : shopsGoodsESList) {
                dataObj = new DataObj();

                dataObj.setDataId(es.getDataId());
                dataObj.setDataType(es.getDataType());
                dataObj.setMatchedKeywords(es.getNameMatchedKeywords());

                dataObjList.add(dataObj);
            }

            pageObject.setDataList(dataObjList);

            return InnerRpcResponse.success(pageObject);
        } catch (Exception e) {
            log.error("搜索[生活商品/商铺]ES数据出错：searchDTO={}", JSON.toJSONString(searchDTO), e);
            return InnerRpcResponse.fail(e.getMessage());
        }
    }

    @Override
    public InnerRpcResponse<Long> searchCount(LifeShopsGoodsSearchDTO searchDTO) {
        try {
            checkSearchParam(searchDTO);

            long total = lifeShopsGoodsESService.count(searchDTO);

            return InnerRpcResponse.success(total);
        } catch (Exception e) {
            log.error("统计搜索[生活商品/商铺]ES数据出错：searchDTO={}", JSON.toJSONString(searchDTO), e);
            return InnerRpcResponse.fail(e.getMessage());
        }
    }

    private void checkSearchParam(LifeShopsGoodsSearchDTO dto) {
        try {
            Assert.notNull(dto, "参数不能为空");
            Assert.isTrue(StringUtils.isNotBlank(dto.getKeywords()), "参数[keywords]错误");
            Assert.notNull(dto.getDataType(), "参数[dataType]错误");
            Assert.notNull(dto.getPageObj(), "参数[pageObj]错误");
            Assert.isTrue(dto.getPageObj().getPageSize() > 0, "参数[pageObj.pageSize]错误");
        } catch (Exception e) {
            throw new LifeCircleException(e.getMessage());
        }
    }

}
