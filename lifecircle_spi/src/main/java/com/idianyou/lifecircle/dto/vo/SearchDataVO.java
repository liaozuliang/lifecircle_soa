package com.idianyou.lifecircle.dto.vo;

import com.idianyou.lifecircle.dto.BaseDTO;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/8 11:36
 */
@Data
public class SearchDataVO extends BaseDTO {

    private int hasMore;

    private Long lastId;

    private List<ChannelGroup> channelGroupList;

    @Data
    public static class ChannelGroup extends BaseDTO {

        private int isGroup;

        private String channelName;

        private Integer channelId;

        private List<ChannelDataGroupVO> dataList;
    }
}
