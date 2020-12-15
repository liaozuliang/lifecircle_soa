package com.idianyou.lifecircle.dto.vo;

import com.idianyou.lifecircle.dto.BaseDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/19 16:05
 */
@Data
public class IconBtnRow extends BaseDTO {

    private List<IconBtn> iconBtnList = new ArrayList<>();

    @Data
    public static class IconBtn extends BaseDTO {
        /**
         * 名称
         */
        private String name;

        /**
         * 图标
         */
        private String icon;

        /**
         * 跳转协议
         */
        private String protocol;

    }

}
