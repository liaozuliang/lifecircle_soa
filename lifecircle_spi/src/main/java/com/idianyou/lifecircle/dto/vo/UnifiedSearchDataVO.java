package com.idianyou.lifecircle.dto.vo;

import com.idianyou.lifecircle.dto.BaseDTO;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/22 9:56
 */
@Data
public class UnifiedSearchDataVO extends BaseDTO {

    private int hasMore;

    private Long lastId;

    private List<ChannelDataGroupVO> dataList;
}
