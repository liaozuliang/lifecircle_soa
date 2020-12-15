package com.idianyou.lifecircle.dto.vo;

import com.idianyou.lifecircle.dto.BaseDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/7 21:45
 */
@Data
public class ChannelDataGroupVO extends BaseDTO {

    private int isGroup;

    private String groupName;

    private String groupDesc;

    private Integer hasMore;

    private Integer channelId;

    private Integer moreJumpType;

    private String moreJumpProtocol;

    private List<String> matchKeywordList;

    private List<ChannelDataVO> dataList = new ArrayList<>();
}
