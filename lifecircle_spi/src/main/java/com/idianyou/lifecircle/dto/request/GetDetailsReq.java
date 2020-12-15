package com.idianyou.lifecircle.dto.request;

import com.idianyou.lifecircle.dto.BaseParam;
import lombok.Data;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/9 12:28
 */
@Data
public class GetDetailsReq extends BaseParam {

    /**
     * 登录用户ID
     */
    private Long userId;

    /**
     * 数据ID
     */
    private Long contentId;
}
