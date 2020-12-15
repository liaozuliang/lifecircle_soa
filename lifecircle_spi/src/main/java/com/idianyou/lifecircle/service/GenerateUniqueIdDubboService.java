package com.idianyou.lifecircle.service;

import com.idianyou.lifecircle.enums.TableNameEnum;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/28 9:31
 */
public interface GenerateUniqueIdDubboService {

    /**
     * 获取表自增ID
     * @param tableNameEnum
     * @return
     */
    long generateAutoIncId(TableNameEnum tableNameEnum);
}
