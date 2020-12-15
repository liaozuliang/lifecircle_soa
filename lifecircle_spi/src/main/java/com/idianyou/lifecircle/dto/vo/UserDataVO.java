package com.idianyou.lifecircle.dto.vo;

import com.idianyou.lifecircle.dto.BaseDTO;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/25 17:23
 */
@Data
public class UserDataVO extends BaseDTO {

    private int hasMore;

    private List<ChannelDataGroupVO> dataList;
}