package com.idianyou.lifecircle.service.local.channel.typedata;

import com.idianyou.lifecircle.dto.GetTypeDataContext;
import com.idianyou.lifecircle.dto.QueryTypeDataParam;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.enums.DataQueryTypeEnum;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.enums.MoreJumpTypeEnum;
import com.idianyou.lifecircle.enums.ShowTypeEnum;
import com.idianyou.lifecircle.enums.YesNoEnum;
import com.idianyou.lifecircle.service.local.channel.AbstractGetTypeDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description: 获取[Boss开讲]分类数据
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/27 18:42
 */
@Slf4j
@Service
public class GetBossLectureDataService extends AbstractGetTypeDataService {

    @Override
    public boolean supports(GetTypeDataContext context) {
        return context != null
                && Objects.equals(DataQueryTypeEnum.BOSS_LECTURE, context.getQueryTypeEnum())
                && Objects.equals(DataTypeEnum.BOSS_LECTURE, context.getDataTypeEnum())
                && Objects.equals(ShowTypeEnum.BOSS_LECTURE_SERVICE_DYNAMIC, context.getShowTypeEnum());
    }

    @Override
    public ChannelDataGroupVO getData(GetTypeDataContext context) {
        QueryTypeDataParam param = getQueryTypeDataParam(context);

        ChannelDataGroupVO dataGroupVO = new ChannelDataGroupVO();
        dataGroupVO.setIsGroup(YesNoEnum.YES.getCode());
        dataGroupVO.setGroupName("Boss开讲");
        dataGroupVO.setMoreJumpType(MoreJumpTypeEnum.CHANNEL.getType());
        dataGroupVO.setChannelId(LifeCircleChannelEnum.LIVE.getChannelId());

        return getTypeData(context, param, dataGroupVO);
    }
}
