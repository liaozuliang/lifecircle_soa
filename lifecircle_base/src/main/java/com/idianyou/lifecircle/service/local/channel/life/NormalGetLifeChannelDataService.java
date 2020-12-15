package com.idianyou.lifecircle.service.local.channel.life;

import com.idianyou.lifecircle.dto.GetChannelDataContext;
import com.idianyou.lifecircle.dto.TypeNum;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.enums.DataQueryTypeEnum;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.service.local.channel.BaseGetChannelDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 生活频道
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/9 16:24
 */
@Slf4j
@Service
public class NormalGetLifeChannelDataService extends BaseGetChannelDataService {

    @Override
    public int getDataNum() {
        return 13;
    }

    @Override
    public boolean lifeChannelSupports(GetChannelDataContext context) {
        return false && LifeCircleChannelEnum.LIFE.equals(context.getChannelEnum());
    }

    @Override
    public void removeExtraData(Map<DataQueryTypeEnum, List<ChannelDataGroupVO>> type_group_map) {
        // 农户直播 + 农户 (3条)
        removeExtraData2(type_group_map, Arrays.asList(DataQueryTypeEnum.MARKET_LIVE, DataQueryTypeEnum.MARKET_FARMER), 3);

        // 商户(3组，每组6个)
        removeExtraData2(type_group_map, Arrays.asList(DataQueryTypeEnum.LEARN_MARKET_MERCHANT, DataQueryTypeEnum.MARKET_MERCHANT), 3);

        // 商品(3组，每组6个)
        removeExtraData2(type_group_map, Arrays.asList(DataQueryTypeEnum.LEARN_MARKET_PRODUCT, DataQueryTypeEnum.MARKET_PRODUCT), 3);
    }

    @Override
    public TypeNum getTypeNum(DataQueryTypeEnum dataQueryTypeEnum) {
        Map<DataQueryTypeEnum, TypeNum> type_typeNum_map = new HashMap<>();

        /**
         * 定制服务1条,农户3条，商品3条，商户3条，商城2条，开公司1条
         */

        // 定制服务（1条）
        type_typeNum_map.put(DataQueryTypeEnum.CUSTOMIZED_SERVICE, new TypeNum(1, 1));

        // 农户直播 + 农户 (3条)
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_LIVE, new TypeNum(3, 1));
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_FARMER, new TypeNum(3, 1));

        // 商品(3组，每组6个)
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_PRODUCT, new TypeNum(6, 3));

        // 机器学习-商品(3组，每组6个)
        type_typeNum_map.put(DataQueryTypeEnum.LEARN_MARKET_PRODUCT, new TypeNum(6, 3));

        // 商户(3组，每组6个)
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_MERCHANT, new TypeNum(6, 3));

        // 机器学习-商户(3组，每组6个)
        type_typeNum_map.put(DataQueryTypeEnum.LEARN_MARKET_MERCHANT, new TypeNum(6, 3));

        // 商城(2组，每组6个)
        type_typeNum_map.put(DataQueryTypeEnum.CHI_GUA_MALL, new TypeNum(6, 2));

        // 开公司(1条)
        type_typeNum_map.put(DataQueryTypeEnum.FORM_COMPANY, new TypeNum(1, 1));

        TypeNum typeNum = type_typeNum_map.get(dataQueryTypeEnum);

        return typeNum;
    }

    @Override
    public List<DataQueryTypeEnum> getSortTypeList() {
        List<DataQueryTypeEnum> dataQueryTypeEnumList = new ArrayList<>();

        /**
         * 商品》农户》商城》商户》定制服务》开公司
         */
        dataQueryTypeEnumList.add(DataQueryTypeEnum.LEARN_MARKET_PRODUCT);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_PRODUCT);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_LIVE);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_FARMER);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.CHI_GUA_MALL);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.LEARN_MARKET_MERCHANT);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_MERCHANT);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.CUSTOMIZED_SERVICE);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.FORM_COMPANY);

        return dataQueryTypeEnumList;
    }
}
