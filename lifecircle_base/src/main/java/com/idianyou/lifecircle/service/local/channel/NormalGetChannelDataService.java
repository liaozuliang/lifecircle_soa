package com.idianyou.lifecircle.service.local.channel;

import com.alibaba.fastjson.JSONObject;
import com.idianyou.lifecircle.dto.GetChannelDataContext;
import com.idianyou.lifecircle.dto.GetChannelDataDTO;
import com.idianyou.lifecircle.dto.GetTypeDataContext;
import com.idianyou.lifecircle.dto.QueryTypeDataParam;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.dto.vo.ChannelDataVO;
import com.idianyou.lifecircle.entity.mongo.LifeCircleContentMongo;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.service.impl.dubbo.AppListDubboServiceImpl;
import com.idianyou.lifecircle.service.local.channel.typedata.GetPersonalLiveDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 获取频道数据
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/8/24 16:00
 */
@Slf4j
@Service
public class NormalGetChannelDataService extends BaseGetChannelDataService {

    @Autowired
    private AppListDubboServiceImpl appListDubboServiceImpl;

    @Autowired
    private GetPersonalLiveDataService getPersonalLiveDataService;

    @Override
    public boolean normalChannelSupports(GetChannelDataContext context) {
        return LifeCircleChannelEnum.LOCAL.equals(context.getChannelEnum())
                || LifeCircleChannelEnum.LIVE.equals(context.getChannelEnum())
                || LifeCircleChannelEnum.DYNAMIC.equals(context.getChannelEnum());
    }

    @Override
    public List<ChannelDataGroupVO> getData(GetChannelDataContext context) {
        GetTypeDataContext getTypeDataContext = new GetTypeDataContext();

        getTypeDataContext.setUserId(context.getUserId());
        getTypeDataContext.setDeviceId(context.getDeviceId());
        getTypeDataContext.setDataNum(12);
        getTypeDataContext.setIgnoreServiceStatus(false);
        getTypeDataContext.setExcludeDataIdSet(context.getExcludeDataIdSet());
        getTypeDataContext.setPassedDataIdSet(context.getPassedDataIdSet());
        getTypeDataContext.setPassedBizDataIdSet(context.getPassedBizDataIdSet());
        getTypeDataContext.setBaseLog(context.getBaseLog());

        List<Integer> channelIdList = new ArrayList<>();
        channelIdList.add(context.getChannelEnum().getChannelId());
        getTypeDataContext.setChannelIdList(channelIdList);

        QueryTypeDataParam param = AbstractGetTypeDataService.getQueryTypeDataParam(getTypeDataContext);

        List<LifeCircleContentMongo> contentMongoList = getPersonalLiveDataService.getTypeData(getTypeDataContext, param);
        if (CollectionUtils.isEmpty(contentMongoList)) {
            return Collections.EMPTY_LIST;
        }

        GetChannelDataDTO dto = new GetChannelDataDTO();
        dto.setUserId(context.getUserId());
        dto.setChannelEnum(context.getChannelEnum());

        ChannelDataVO dataVO = null;
        List<ChannelDataVO> channelDataVOList = new ArrayList<>();
        Map<Long, JSONObject> contentId_serviceSpecialParam_map = new HashMap<>();

        for (LifeCircleContentMongo contentMongo : contentMongoList) {
            dataVO = appListDubboServiceImpl.transferToChannelDataVO(contentMongo, dto, contentId_serviceSpecialParam_map);
            if (dataVO != null) {
                channelDataVOList.add(dataVO);
            }
        }

        if (CollectionUtils.isEmpty(channelDataVOList)) {
            return Collections.EMPTY_LIST;
        }

        List<ChannelDataGroupVO> dataGroupVOList = new ArrayList<>();
        appListDubboServiceImpl.setGroup(dataGroupVOList, channelDataVOList, dto, contentId_serviceSpecialParam_map);

        return dataGroupVOList;
    }
}
