package com.idianyou.lifecircle.service.local.channel.ad;

import com.idianyou.lifecircle.dto.GetChannelDataContext;
import com.idianyou.lifecircle.dto.TypeNum;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.enums.DataQueryTypeEnum;
import com.idianyou.lifecircle.enums.UserNativeTypeEnum;
import com.idianyou.lifecircle.service.local.channel.BaseGetChannelDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: 非本地人-问答红包广告位
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/7/6 11:34
 */
@Slf4j
@Service
public class NotNativeQAAdGetDataService extends BaseGetChannelDataService {

    @Override
    public int getDataNum() {
        return 5;
    }

    @Override
    public boolean qaAdSupports(GetChannelDataContext context) {
        return !Objects.equals(context.getUserNativeTypeEnum(), UserNativeTypeEnum.NATIVE)
                && !Objects.equals(context.getUserNativeTypeEnum(), UserNativeTypeEnum.NATIVE_OUT);
    }

    @Override
    public void removeExtraData(Map<DataQueryTypeEnum, List<ChannelDataGroupVO>> type_group_map) {

    }

    @Override
    public TypeNum getTypeNum(DataQueryTypeEnum dataQueryTypeEnum) {
        Map<DataQueryTypeEnum, TypeNum> type_typeNum_map = new HashMap<>();

        /**
         * 农户（1条），商品（1组），商城（3组）
         */

        // 农户 (1条)
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_FARMER, new TypeNum(1, 1));

        // 商品(1组，每组6个)
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_PRODUCT, new TypeNum(6, 1));

        // 商城(3组，每组6个)
        type_typeNum_map.put(DataQueryTypeEnum.CHI_GUA_MALL, new TypeNum(6, 3));

        TypeNum typeNum = type_typeNum_map.get(dataQueryTypeEnum);

        return typeNum;
    }

    @Override
    public List<DataQueryTypeEnum> getSortTypeList() {
        List<DataQueryTypeEnum> dataQueryTypeEnumList = new ArrayList<>();

        /**
         * 商城》农户》商品
         */
        dataQueryTypeEnumList.add(DataQueryTypeEnum.CHI_GUA_MALL);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_FARMER);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_PRODUCT);

        return dataQueryTypeEnumList;
    }
}
