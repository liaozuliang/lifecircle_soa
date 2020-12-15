package com.idianyou.lifecircle.dto;

import com.idianyou.lifecircle.dto.request.GetUserDataReq;
import lombok.Data;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/6 17:14
 */
@Data
public class GetUserDataDTO extends GetUserDataReq {

    private Long loginUserid;

    private int pageSize = 12;
}