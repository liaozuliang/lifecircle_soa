package com.idianyou.lifecircle.dto;

import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import lombok.Data;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/6 17:14
 */
@Data
public class GetChannelDataDTO extends BaseDTO {

    private Long userId;

    private String deviceId;

    private LifeCircleChannelEnum channelEnum;

    private String reqNo;

    private int dataNum = 12;
}
