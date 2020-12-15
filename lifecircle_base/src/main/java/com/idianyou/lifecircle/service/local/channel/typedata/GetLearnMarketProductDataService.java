package com.idianyou.lifecircle.service.local.channel.typedata;

import com.idianyou.lifecircle.dto.GetTypeDataContext;
import com.idianyou.lifecircle.dto.QueryTypeDataParam;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.enums.DataQueryTypeEnum;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.MoreJumpTypeEnum;
import com.idianyou.lifecircle.enums.ShowTypeEnum;
import com.idianyou.lifecircle.enums.YesNoEnum;
import com.idianyou.lifecircle.service.local.channel.AbstractGetTypeDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description: 获取[机器学习-赶集-商品]分类数据
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/27 18:42
 */
@Slf4j
@Service
public class GetLearnMarketProductDataService extends AbstractGetTypeDataService {

    @Override
    public boolean supports(GetTypeDataContext context) {
        return context != null
                && Objects.equals(DataQueryTypeEnum.LEARN_MARKET_PRODUCT, context.getQueryTypeEnum())
                && Objects.equals(DataTypeEnum.MARKET, context.getDataTypeEnum())
                && Objects.equals(ShowTypeEnum.MARKET_PRODUCT, context.getShowTypeEnum());
    }

    @Override
    public ChannelDataGroupVO getData(GetTypeDataContext context) {
        QueryTypeDataParam param = getQueryTypeDataParam(context);

        ChannelDataGroupVO dataGroupVO = new ChannelDataGroupVO();
        dataGroupVO.setIsGroup(YesNoEnum.YES.getCode());
        dataGroupVO.setGroupName(context.getCategoryName());
        dataGroupVO.setMoreJumpType(MoreJumpTypeEnum.PROTOCOL.getType());

        return getTypeData(context, param, dataGroupVO);
    }
}
