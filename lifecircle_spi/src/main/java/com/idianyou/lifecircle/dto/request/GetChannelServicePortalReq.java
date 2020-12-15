package com.idianyou.lifecircle.dto.request;

import com.idianyou.lifecircle.dto.BaseParam;
import lombok.Data;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/19 15:36
 */
@Data
public class GetChannelServicePortalReq extends BaseParam {

    private Integer channelId;
}