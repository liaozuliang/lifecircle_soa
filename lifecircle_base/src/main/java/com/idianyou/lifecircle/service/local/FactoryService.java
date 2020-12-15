package com.idianyou.lifecircle.service.local;

import com.alibaba.fastjson.JSON;
import com.idianyou.lifecircle.dto.GetChannelDataContext;
import com.idianyou.lifecircle.dto.GetTypeDataContext;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.exception.LifeCircleException;
import com.idianyou.lifecircle.service.local.channel.AbstractGetChannelDataService;
import com.idianyou.lifecircle.service.local.channel.AbstractGetTypeDataService;
import com.idianyou.lifecircle.service.local.channel.BaseGetChannelDataService;
import com.idianyou.lifecircle.service.local.third.AbstractGetServiceCurrentStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/29 20:47
 */
@Service
public class FactoryService {

    public static final ExecutorService CHANNEL_LIST_ES = new ThreadPoolExecutor(20, 100,
            5L, TimeUnit.MINUTES,
            new LinkedBlockingQueue<Runnable>());

    @Autowired
    private List<AbstractGetServiceCurrentStateService> abstractGetServiceCurrentStateServiceList;

    @Autowired
    private List<AbstractGetChannelDataService> abstractGetChannelDataServiceList;

    @Autowired
    private List<AbstractGetTypeDataService> abstractGetTypeDataServiceList;

    @Autowired
    private List<BaseGetChannelDataService> baseGetChannelDataServiceList;

    public AbstractGetServiceCurrentStateService getService(DataTypeEnum dataTypeEnum) {
        for (AbstractGetServiceCurrentStateService stateService : abstractGetServiceCurrentStateServiceList) {
            if (stateService.supports(dataTypeEnum)) {
                return stateService;
            }
        }
        throw new LifeCircleException("未找到对应服务类，dataTypeEnum=" + JSON.toJSONString(dataTypeEnum));
    }

    public AbstractGetChannelDataService getService(LifeCircleChannelEnum channelEnum) {
        for (AbstractGetChannelDataService service : abstractGetChannelDataServiceList) {
            if (service.supports(channelEnum)) {
                return service;
            }
        }

        throw new LifeCircleException("未找到对应服务类，channelEnum=" + JSON.toJSONString(channelEnum));
    }

    public AbstractGetTypeDataService getService(GetTypeDataContext context) {
        for (AbstractGetTypeDataService service : abstractGetTypeDataServiceList) {
            if (service.supports(context)) {
                return service;
            }
        }

        throw new LifeCircleException("未找到对应服务类，context=" + JSON.toJSONString(context));
    }

    public BaseGetChannelDataService getServiceForRecommendChannel(GetChannelDataContext context) {
        for (BaseGetChannelDataService baseGetChannelDataService : baseGetChannelDataServiceList) {
            if (baseGetChannelDataService.recommendChannelSupports(context)) {
                return baseGetChannelDataService;
            }
        }
        throw new LifeCircleException("未找到对应服务类，context=" + JSON.toJSONString(context));
    }

    public BaseGetChannelDataService getServiceForLifeChannel(GetChannelDataContext context) {
        for (BaseGetChannelDataService baseGetChannelDataService : baseGetChannelDataServiceList) {
            if (baseGetChannelDataService.lifeChannelSupports(context)) {
                return baseGetChannelDataService;
            }
        }
        throw new LifeCircleException("未找到对应服务类，context=" + JSON.toJSONString(context));
    }

    public BaseGetChannelDataService getServiceForAmusementChannel(GetChannelDataContext context) {
        for (BaseGetChannelDataService baseGetChannelDataService : baseGetChannelDataServiceList) {
            if (baseGetChannelDataService.amusementChannelSupports(context)) {
                return baseGetChannelDataService;
            }
        }
        throw new LifeCircleException("未找到对应服务类，context=" + JSON.toJSONString(context));
    }

    public BaseGetChannelDataService getServiceForQAAd(GetChannelDataContext context) {
        for (BaseGetChannelDataService baseGetChannelDataService : baseGetChannelDataServiceList) {
            if (baseGetChannelDataService.qaAdSupports(context)) {
                return baseGetChannelDataService;
            }
        }
        throw new LifeCircleException("未找到对应服务类，context=" + JSON.toJSONString(context));
    }

    public BaseGetChannelDataService getServiceForNormalChannel(GetChannelDataContext context) {
        for (BaseGetChannelDataService service : baseGetChannelDataServiceList) {
            if (service.normalChannelSupports(context)) {
                return service;
            }
        }

        throw new LifeCircleException("未找到对应服务类，context=" + JSON.toJSONString(context));
    }
}
