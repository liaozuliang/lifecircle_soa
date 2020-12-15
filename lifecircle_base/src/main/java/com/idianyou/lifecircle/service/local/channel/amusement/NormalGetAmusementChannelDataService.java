package com.idianyou.lifecircle.service.local.channel.amusement;

import com.idianyou.lifecircle.dto.GetChannelDataContext;
import com.idianyou.lifecircle.dto.TypeNum;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.enums.DataQueryTypeEnum;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.service.local.channel.BaseGetChannelDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 娱乐频道
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/9 16:24
 */
@Slf4j
@Service
public class NormalGetAmusementChannelDataService extends BaseGetChannelDataService {

    @Override
    public int getDataNum() {
        return 12;
    }

    @Override
    public boolean amusementChannelSupports(GetChannelDataContext context) {
        return LifeCircleChannelEnum.AMUSEMENT.equals(context.getChannelEnum());
    }

    @Override
    public void removeExtraData(Map<DataQueryTypeEnum, List<ChannelDataGroupVO>> type_group_map) {

    }

    @Override
    public TypeNum getTypeNum(DataQueryTypeEnum dataQueryTypeEnum) {
        Map<DataQueryTypeEnum, TypeNum> type_typeNum_map = new HashMap<>();

        /**
         * 小说1条，K歌3条，辩手3条，精彩瞬间5条
         */

        // 小说1条
        type_typeNum_map.put(DataQueryTypeEnum.NOVEL, new TypeNum(6, 1));

        // K歌3条
        type_typeNum_map.put(DataQueryTypeEnum.K_SONG, new TypeNum(3, 1));

        // 辩手3条
        type_typeNum_map.put(DataQueryTypeEnum.BEST_DEBATER, new TypeNum(3, 1));

        // 精彩瞬间5条
        type_typeNum_map.put(DataQueryTypeEnum.WONDERFUL_MOMENTS, new TypeNum(5, 1));

        TypeNum typeNum = type_typeNum_map.get(dataQueryTypeEnum);

        return typeNum;
    }

    @Override
    public List<DataQueryTypeEnum> getSortTypeList() {
        List<DataQueryTypeEnum> dataQueryTypeEnumList = new ArrayList<>();

        /**
         * K歌》精彩瞬间》辩手》小说》
         */
        dataQueryTypeEnumList.add(DataQueryTypeEnum.K_SONG);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.WONDERFUL_MOMENTS);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.BEST_DEBATER);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.NOVEL);

        return dataQueryTypeEnumList;
    }
}
