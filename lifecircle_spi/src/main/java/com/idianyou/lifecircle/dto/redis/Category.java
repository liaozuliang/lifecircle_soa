package com.idianyou.lifecircle.dto.redis;

import com.idianyou.lifecircle.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/9 9:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseDTO {

    private String id;

    private String name;
}
