package com.idianyou.lifecircle.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/27 17:04
 */
@Data
public class UserInfo extends BaseDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户头像
     */
    private String headImg;

    /**
     * 用户个人签名
     */
    private String idiograph;

    /**
     * 用户身份类型(IdentityTypeEnum)
     */
    private List<Integer> identityTypeList;
}
