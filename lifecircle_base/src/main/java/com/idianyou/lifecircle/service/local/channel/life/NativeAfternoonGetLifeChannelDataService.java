package com.idianyou.lifecircle.service.local.channel.life;

import com.idianyou.lifecircle.dto.GetChannelDataContext;
import com.idianyou.lifecircle.dto.TypeNum;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.enums.DataQueryTypeEnum;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.enums.TimeDescEnum;
import com.idianyou.lifecircle.enums.UserNativeTypeEnum;
import com.idianyou.lifecircle.service.local.channel.BaseGetChannelDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: 生活频道查询场景[赵村人，在家发展, 下午]
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/9 16:23
 */
@Slf4j
@Service
public class NativeAfternoonGetLifeChannelDataService extends BaseGetChannelDataService {

    @Override
    public int getDataNum() {
        return 12;
    }

    @Override
    public boolean lifeChannelSupports(GetChannelDataContext context) {
        return LifeCircleChannelEnum.LIFE.equals(context.getChannelEnum())
                && Objects.equals(context.getUserNativeTypeEnum(), UserNativeTypeEnum.NATIVE)
                && Objects.equals(context.getTimeDescEnum(), TimeDescEnum.AFTERNOON);
    }

    @Override
    public void removeExtraData(Map<DataQueryTypeEnum, List<ChannelDataGroupVO>> type_group_map) {
        // 农户直播 + 农户 + 机器学习-农户 (3条)
        removeExtraData2(type_group_map, Arrays.asList(DataQueryTypeEnum.LEARN_MARKET_FARMER, DataQueryTypeEnum.MARKET_FARMER), 3);
        removeExtraData3(type_group_map, Arrays.asList(DataQueryTypeEnum.MARKET_LIVE, DataQueryTypeEnum.LEARN_MARKET_FARMER, DataQueryTypeEnum.MARKET_FARMER), 3);

        // 商品(5组，每组6个)
        removeExtraData2(type_group_map, Arrays.asList(DataQueryTypeEnum.LEARN_MARKET_PRODUCT, DataQueryTypeEnum.MARKET_PRODUCT), 5);

        // 商户(1组，每组6个)
        removeExtraData2(type_group_map, Arrays.asList(DataQueryTypeEnum.LEARN_MARKET_MERCHANT, DataQueryTypeEnum.MARKET_MERCHANT), 1);
    }

    @Override
    public TypeNum getTypeNum(DataQueryTypeEnum dataQueryTypeEnum) {
        Map<DataQueryTypeEnum, TypeNum> type_typeNum_map = new HashMap<>();

        /**
         * 农户（3）
         * 商品（5组，每组6个）
         * 商户（1组，每组6个）
         * 商城（1组，每组6个）
         * 开公司（1）
         */

        // 农户直播 + 农户 + 机器学习-农户(3条)
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_LIVE, new TypeNum(3, 1));
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_FARMER, new TypeNum(3, 1));
        type_typeNum_map.put(DataQueryTypeEnum.LEARN_MARKET_FARMER, new TypeNum(3, 1));

        // 商品 + 机器学习-商品（5组，每组6个）
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_PRODUCT, new TypeNum(6, 5));
        type_typeNum_map.put(DataQueryTypeEnum.LEARN_MARKET_PRODUCT, new TypeNum(6, 5));

        // 商户 + 机器学习-商户（1组，每组6个）
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_MERCHANT, new TypeNum(6, 1));
        type_typeNum_map.put(DataQueryTypeEnum.LEARN_MARKET_MERCHANT, new TypeNum(6, 1));

        // 商城(1组，每组6个)
        type_typeNum_map.put(DataQueryTypeEnum.CHI_GUA_MALL, new TypeNum(6, 1));

        // 开公司(1条)
        type_typeNum_map.put(DataQueryTypeEnum.FORM_COMPANY, new TypeNum(1, 1));

        TypeNum typeNum = type_typeNum_map.get(dataQueryTypeEnum);

        return typeNum;
    }

    @Override
    public List<DataQueryTypeEnum> getSortTypeList() {
        List<DataQueryTypeEnum> dataQueryTypeEnumList = new ArrayList<>();

        /**
         * 农户》商户》商品》商城》开公司
         */

        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_LIVE);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.LEARN_MARKET_FARMER);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_FARMER);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.LEARN_MARKET_MERCHANT);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_MERCHANT);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.LEARN_MARKET_PRODUCT);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_PRODUCT);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.CHI_GUA_MALL);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.FORM_COMPANY);

        return dataQueryTypeEnumList;
    }
}
