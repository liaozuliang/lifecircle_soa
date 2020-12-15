package com.idianyou.lifecircle.service.local.channel.typedata;

import com.idianyou.lifecircle.dto.GetTypeDataContext;
import com.idianyou.lifecircle.dto.QueryTypeDataParam;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.enums.DataQueryTypeEnum;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.ShowTypeEnum;
import com.idianyou.lifecircle.enums.YesNoEnum;
import com.idianyou.lifecircle.service.local.channel.AbstractGetTypeDataService;
import com.idianyou.lifecircle.service.local.learn.GetLearnDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 获取[机器学习-赶集-农户]分类数据
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/27 18:42
 */
@Slf4j
@Service
public class GetLearnMarketFarmerDataService extends AbstractGetTypeDataService {

    @Autowired
    private GetLearnDataService getLearnDataService;

    @Override
    public boolean supports(GetTypeDataContext context) {
        return context != null
                && Objects.equals(DataQueryTypeEnum.LEARN_MARKET_FARMER, context.getQueryTypeEnum())
                && Objects.equals(DataTypeEnum.MARKET, context.getDataTypeEnum())
                && Objects.equals(ShowTypeEnum.MARKET_COMMODITY, context.getShowTypeEnum());
    }

    @Override
    public ChannelDataGroupVO getData(GetTypeDataContext context) {
        List<Long> contentIdList = getLearnDataService.getFarmerContentId(context.getUserId(), context.getDataNum(), context.getBaseLog());
        if (CollectionUtils.isEmpty(contentIdList)) {
            return null;
        }

        QueryTypeDataParam param = getQueryTypeDataParam(context);
        param.setIdList(contentIdList);

        ChannelDataGroupVO dataGroupVO = new ChannelDataGroupVO();
        dataGroupVO.setIsGroup(YesNoEnum.NO.getCode());

        return getTypeData(context, param, dataGroupVO);
    }
}
