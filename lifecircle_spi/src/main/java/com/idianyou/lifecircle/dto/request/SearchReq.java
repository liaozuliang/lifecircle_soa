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
public class SearchReq extends BaseParam {

    private Integer channelId;

    private String keyword;

    private Long lastId;

    /**
     * 版本号(2:频道分组)
     */
    private Integer v;
}
