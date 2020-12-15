package com.idianyou.lifecircle.dto.vo;

import com.idianyou.lifecircle.dto.BaseDTO;
import lombok.Data;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/12 15:31
 */
@Data
public class QuestionRedpacketVO extends BaseDTO {

    /**
     * 是否显示入口(0:否 1:是)
     */
    private int isShow;

    /**
     * 倒计时(秒)
     */
    private long leftTime;

    /**
     * 题ID
     */
    private String questionId;

    /**
     * 协议
     */
    private String protocol;

    /**
     * 等待协议
     */
    private String waitProtocol;
}