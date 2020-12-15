package com.idianyou.lifecircle.dto;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/12/7 16:39
 */
@Data
public class PageObject<T> extends BaseDTO {

    /**
     * 每页的数据量
     */
    private int pageSize = 10;

    /**
     * 当前页
     */
    private int currentPage = 1;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 总数据量
     */
    private int totalData;

    /**
     * 是否有下一页
     */
    private boolean hasNextPage;

    /**
     * 数据
     */
    private List<T> dataList = Collections.EMPTY_LIST;

    public PageObject() {

    }

    public PageObject(int totalData, int currentPage) {
        this.totalData = totalData;
        this.currentPage = currentPage;
        calcPageInfo();
    }

    public PageObject(int totalData, int currentPage, int pageSize) {
        this.totalData = totalData;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        calcPageInfo();
    }

    /**
     * 计算分页
     */
    private void calcPageInfo() {
        if (totalData <= 0) {
            return;
        }

        if (totalData % pageSize == 0) {
            this.totalPage = totalData / pageSize;
        } else {
            this.totalPage = totalData / pageSize + 1;
        }

        this.hasNextPage = this.currentPage < this.totalPage;
    }

}
