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
public class SearchDTO extends BaseDTO {

    private Long userId;

    private LifeCircleChannelEnum channelEnum;

    private String keyword;

    private Long lastId;

    private int pageSize = 15;

    private Integer v; // 接口版本号

}
