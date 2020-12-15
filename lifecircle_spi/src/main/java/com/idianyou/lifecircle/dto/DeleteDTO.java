package com.idianyou.lifecircle.dto;

import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.ShowTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/7/13 14:20
 */
@Data
public class DeleteDTO extends BaseDTO {

    private DataTypeEnum dataTypeEnum;

    private ShowTypeEnum showTypeEnum;

    private String categoryId;

    private String categoryName;

    private Date maxCreateTime;
}
