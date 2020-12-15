package com.idianyou.lifecircle.dubbo;

import com.alibaba.fastjson.JSON;
import com.idianyou.lifecircle.dto.AreaDTO;
import com.idianyou.lifecircle.dto.InnerRpcResponse;
import com.idianyou.lifecircle.dto.Location;
import com.idianyou.lifecircle.dto.PageObject;
import com.idianyou.lifecircle.dto.lifeShopsGoods.DataObj;
import com.idianyou.lifecircle.dto.lifeShopsGoods.LifeShopsGoodsDTO;
import com.idianyou.lifecircle.dto.lifeShopsGoods.LifeShopsGoodsSearchDTO;
import com.idianyou.lifecircle.enums.lifeShopsGoods.DataTypeEnum;
import com.idianyou.lifecircle.enums.lifeShopsGoods.OnOffStatusEnum;
import com.idianyou.lifecircle.enums.lifeShopsGoods.StatusEnum;
import com.idianyou.lifecircle.es.LifeShopsGoodsESExt;
import com.idianyou.lifecircle.es.LifeShopsGoodsESService;
import com.idianyou.lifecircle.service.LifeShopsGoodsESDubboService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/7 12:30
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LifeShopsGoodsESDubboServiceTests {

    @Autowired
    private LifeShopsGoodsESDubboService lifeShopsGoodsESDubboService;

    @Autowired
    private LifeShopsGoodsESService lifeShopsGoodsESService;

    @Test
    public void testSave() {
        InnerRpcResponse<String> response = null;

        LifeShopsGoodsDTO dto = new LifeShopsGoodsDTO();

        Random random = new SecureRandom();

        String[] names = new String[]{
                "中国人商铺",
                "商铺中国",
                "人商铺",
                "在国外的国人商铺",
                "华人商铺",
                "商铺中国人",
                "中商铺国",
                "商铺人",
                "在国外商铺的国人",
                "华商铺人"
        };

        AreaDTO areaDTO = new AreaDTO();
        areaDTO.setProvinceCode("广东省");
        areaDTO.setCityCode("深圳市");
        areaDTO.setAreaCode("南山区");
        areaDTO.setTownshipCode("蛇口");
        areaDTO.setStreetCode("招商街道");

        for (int i = 1; i <= 10; i++) {
            dto.setDataId(i);
            dto.setDataType(DataTypeEnum.SHOPS);
            dto.setName(names[i - 1]);
            dto.setMonthSales(random.nextInt(100));
            dto.setWeight(random.nextDouble());
            dto.setStatus(StatusEnum.VALID);
            dto.setOnOffStatus(OnOffStatusEnum.ON);
            dto.setLocation(new Location(120.35683 + i, 63.32323 + i));
            dto.setArea(areaDTO);
            dto.setCreateTime(new Date());

            response = lifeShopsGoodsESDubboService.save(dto);
            log.info("====testSave===={}======={}", dto.getName(), JSON.toJSONString(response));
        }

        for (int i = 1; i <= 10; i++) {
            dto.setDataId(i);
            dto.setDataType(DataTypeEnum.GOODS);
            dto.setName(names[random.nextInt(10)].replace("铺", "品"));
            dto.setMonthSales(random.nextInt(100));
            dto.setWeight(random.nextDouble());
            dto.setStatus(StatusEnum.VALID);
            dto.setOnOffStatus(OnOffStatusEnum.ON);
            dto.setLocation(new Location(120.35683 + i, 63.32323 + i));
            dto.setArea(areaDTO);
            dto.setCreateTime(new Date());

            response = lifeShopsGoodsESDubboService.save(dto);
            log.info("====testSave===={}======={}", dto.getName(), JSON.toJSONString(response));
        }
    }

    @Test
    public void testDelete() {
        InnerRpcResponse<Boolean> response = lifeShopsGoodsESDubboService.delete(3, DataTypeEnum.GOODS);
        log.info("=======testDelete========={}", JSON.toJSONString(response));
    }

    @Test
    public void testSearch() {
        LifeShopsGoodsSearchDTO searchDTO = new LifeShopsGoodsSearchDTO();
        searchDTO.setKeywords("董怀珍中医诊所");
        searchDTO.setDataType(DataTypeEnum.SHOPS);
        searchDTO.setPageObj(new LifeShopsGoodsSearchDTO.PageObj(1, 10));

        InnerRpcResponse<Long> countResponse = lifeShopsGoodsESDubboService.searchCount(searchDTO);
        log.info("=====searchCount====={}", JSON.toJSONString(countResponse));

        InnerRpcResponse<PageObject<DataObj>> response = lifeShopsGoodsESDubboService.search(searchDTO);
        log.info("=====search====={}", JSON.toJSONString(response));
    }

    @Test
    public void testSearch2() {
        LifeShopsGoodsSearchDTO searchDTO = new LifeShopsGoodsSearchDTO();
        searchDTO.setKeywords("董怀珍中医诊所");
        searchDTO.setDataType(DataTypeEnum.SHOPS);
        searchDTO.setLastSortValues("%5B9.613535%2C0%2C1.0%2C1607926439196%2C%22shops97%22%5D");
        searchDTO.setPageObj(new LifeShopsGoodsSearchDTO.PageObj(1, 10));

        List<LifeShopsGoodsESExt> lifeShopsGoodsESExtList = lifeShopsGoodsESService.search2(searchDTO);
        log.info("=====search2====={}", JSON.toJSONString(lifeShopsGoodsESExtList));
    }
}
