package com.idianyou.lifecircle.entity.mongo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/24 16:06
 */
@Data
public class BaseMongo<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private T _id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
