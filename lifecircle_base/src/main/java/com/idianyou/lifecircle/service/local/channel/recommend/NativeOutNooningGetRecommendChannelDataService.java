package com.idianyou.lifecircle.service.local.channel.recommend;

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
 * @Description: 推荐频道查询场景[赵村人，在外发展, 中午]
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/9 16:23
 */
@Slf4j
@Service
public class NativeOutNooningGetRecommendChannelDataService extends BaseGetChannelDataService {

    @Override
    public int getDataNum() {
        return 14;
    }

    @Override
    public boolean recommendChannelSupports(GetChannelDataContext context) {
        return LifeCircleChannelEnum.RECOMMEND.equals(context.getChannelEnum())
                && Objects.equals(context.getUserNativeTypeEnum(), UserNativeTypeEnum.NATIVE_OUT)
                && Objects.equals(context.getTimeDescEnum(), TimeDescEnum.NOONING);
    }

    @Override
    public void removeExtraData(Map<DataQueryTypeEnum, List<ChannelDataGroupVO>> type_group_map) {
        // 农户直播 + 农户 + 机器学习-农户 (1条)
        removeExtraData2(type_group_map, Arrays.asList(DataQueryTypeEnum.LEARN_MARKET_FARMER, DataQueryTypeEnum.MARKET_FARMER), 1);
        removeExtraData3(type_group_map, Arrays.asList(DataQueryTypeEnum.MARKET_LIVE, DataQueryTypeEnum.LEARN_MARKET_FARMER, DataQueryTypeEnum.MARKET_FARMER), 1);

        // 孺子牛直播 + 孺子牛个人 (2条)
        removeExtraData3(type_group_map, Arrays.asList(DataQueryTypeEnum.PERSONAL_LIVE, DataQueryTypeEnum.PERSONAL), 2);

        // 商品(2组，每组6个)
        removeExtraData2(type_group_map, Arrays.asList(DataQueryTypeEnum.LEARN_MARKET_PRODUCT, DataQueryTypeEnum.MARKET_PRODUCT), 2);

        // 商户(2组，每组6个)
        removeExtraData2(type_group_map, Arrays.asList(DataQueryTypeEnum.LEARN_MARKET_MERCHANT, DataQueryTypeEnum.MARKET_MERCHANT), 2);
    }

    @Override
    public TypeNum getTypeNum(DataQueryTypeEnum dataQueryTypeEnum) {
        Map<DataQueryTypeEnum, TypeNum> type_typeNum_map = new HashMap<>();

        /**
         * 生活（6条）
         *     农户（1）
         *     商品（2组，每组6个）
         *     商户（2组，每组6个）
         *     商城（1组，每组6个）
         *
         * 娱乐（4条）
         *     精彩瞬间 （2条）
         *     k歌之王  （1条）
         *     最佳辩手 （1条）
         *
         * 本地（2条）
         *     孺子牛个人（2条）
         *
         * 直播（2条）
         *     全民直播（1组，每组6个）
         *     boss开讲（1组，每组6个）
         */

        // 农户直播 + 农户 + 机器学习-农户(1条)
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_LIVE, new TypeNum(1, 1));
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_FARMER, new TypeNum(1, 1));
        type_typeNum_map.put(DataQueryTypeEnum.LEARN_MARKET_FARMER, new TypeNum(1, 1));

        // 商品 + 机器学习-商品（2组，每组6个）
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_PRODUCT, new TypeNum(6, 2));
        type_typeNum_map.put(DataQueryTypeEnum.LEARN_MARKET_PRODUCT, new TypeNum(6, 2));

        // 商户 + 机器学习-商户（2组，每组6个）
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_MERCHANT, new TypeNum(6, 2));
        type_typeNum_map.put(DataQueryTypeEnum.LEARN_MARKET_MERCHANT, new TypeNum(6, 2));

        // 商城(1组，每组6个)
        type_typeNum_map.put(DataQueryTypeEnum.CHI_GUA_MALL, new TypeNum(6, 1));

        // 精彩瞬间(2条)
        type_typeNum_map.put(DataQueryTypeEnum.WONDERFUL_MOMENTS, new TypeNum(2, 1));

        // K歌之王(1条)
        type_typeNum_map.put(DataQueryTypeEnum.K_SONG, new TypeNum(1, 1));

        // 最佳辩手(1条)
        type_typeNum_map.put(DataQueryTypeEnum.BEST_DEBATER, new TypeNum(1, 1));

        // 孺子牛直播 + 孺子牛个人 (2条)
        type_typeNum_map.put(DataQueryTypeEnum.PERSONAL_LIVE, new TypeNum(2, 1));
        type_typeNum_map.put(DataQueryTypeEnum.PERSONAL, new TypeNum(2, 1));

        // 全民直播(1组，每组6个)
        type_typeNum_map.put(DataQueryTypeEnum.LIVE, new TypeNum(6, 1));

        // boss开讲(1组，每组6个)
        type_typeNum_map.put(DataQueryTypeEnum.BOSS_LECTURE, new TypeNum(6, 1));

        TypeNum typeNum = type_typeNum_map.get(dataQueryTypeEnum);

        return typeNum;
    }

    @Override
    public List<DataQueryTypeEnum> getSortTypeList() {
        List<DataQueryTypeEnum> dataQueryTypeEnumList = new ArrayList<>();

        /**
         * 精彩瞬间》boss开讲》直播》商品》农户》商户》商城》孺子牛消息》K歌之王》最佳辩手
         */

        dataQueryTypeEnumList.add(DataQueryTypeEnum.WONDERFUL_MOMENTS);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.BOSS_LECTURE);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.LIVE);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.LEARN_MARKET_PRODUCT);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_PRODUCT);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_LIVE);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.LEARN_MARKET_FARMER);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_FARMER);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.LEARN_MARKET_MERCHANT);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_MERCHANT);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.CHI_GUA_MALL);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.PERSONAL_LIVE);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.PERSONAL);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.K_SONG);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.BEST_DEBATER);

        return dataQueryTypeEnumList;
    }
}
