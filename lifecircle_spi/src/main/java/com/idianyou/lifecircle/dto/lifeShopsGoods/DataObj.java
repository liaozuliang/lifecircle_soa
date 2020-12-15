package com.idianyou.lifecircle.dto.lifeShopsGoods;

import com.idianyou.lifecircle.dto.BaseDTO;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/7 16:54
 */
@Data
public class DataObj extends BaseDTO {

    /**
     * 数据ID
     */
    private Integer dataId;

    /**
     * 数据类型
     * com.idianyou.lifecircle.enums.lifeShopsGoods.DataTypeEnum
     */
    private Integer dataType;

    /**
     * 匹配上的关键字
     */
    private List<String> matchedKeywords;
}
