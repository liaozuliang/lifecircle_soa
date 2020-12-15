package com.idianyou.lifecircle.dto.vo;

import com.idianyou.lifecircle.dto.BaseDTO;
import com.idianyou.lifecircle.dto.FromServiceInfo;
import com.idianyou.lifecircle.dto.UserInfo;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/9 14:14
 */
@Data
public class ContentDetailsVO extends BaseDTO {
    /**
     * 动态ID
     */
    private Long id;

    /**
     * 发布人信息
     */
    private UserInfo userInfo;

    /**
     * 前端展示类型(ShowTypeEnum)
     */
    private Integer showType;

    /**
     * 分享语
     */
    private String shareContent;

    /**
     * 来源服务信息
     */
    private FromServiceInfo fromServiceInfo;

    /**
     * 服务业务参数
     */
    private String serviceBizParam;

    /**
     * 点赞数
     */
    private int praiseCount;

    /**
     * 评论数
     */
    private int commentCount;

    /**
     * 本人是否已点赞
     */
    private int isSelfPraised;

    /**
     * 点赞用户
     */
    private List<UserInfo> praiseUserList;

    /**
     * 发布时间
     */
    private String createTimeDesc;
}
