package com.idianyou.lifecircle.dto.request;

import com.idianyou.lifecircle.dto.BaseParam;
import lombok.Data;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/6 14:30
 */
@Data
public class GetUserDataReq extends BaseParam {

    private Long userId;

    private Long firstId;

    private Long lastId;
}