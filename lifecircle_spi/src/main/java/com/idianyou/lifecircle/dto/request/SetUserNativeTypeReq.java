package com.idianyou.lifecircle.dto.request;

import com.idianyou.lifecircle.dto.BaseParam;
import lombok.Data;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/29 11:10
 */
@Data
public class SetUserNativeTypeReq extends BaseParam {

    private Long userId;

    private Integer userNativeType;
}
