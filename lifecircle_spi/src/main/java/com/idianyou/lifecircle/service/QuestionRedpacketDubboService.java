package com.idianyou.lifecircle.service;

import com.idianyou.lifecircle.dto.vo.QuestionRedpacketVO;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/12 15:00
 */
public interface QuestionRedpacketDubboService {

    /**
     * 获取问答红包
     * @param userId
     * @return
     */
    QuestionRedpacketVO create(Long userId);

    /**
     * 获取问答红包
     * @param userId
     * @return
     */
    QuestionRedpacketVO assembleQuestionRedpacket(Long userId);
}
