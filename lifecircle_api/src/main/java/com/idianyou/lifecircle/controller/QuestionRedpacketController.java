package com.idianyou.lifecircle.controller;

import com.idianyou.lifecircle.dto.vo.QuestionRedpacketVO;
import com.idianyou.lifecircle.exception.LifeCircleException;
import com.idianyou.lifecircle.service.QuestionRedpacketDubboService;
import com.idianyou.lifecircle.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 问答红包
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/12 14:42
 */
@Slf4j
@RequestMapping("/lifecircle/questionRedpacket")
@RestController
public class QuestionRedpacketController extends BaseController {

    @Autowired
    private QuestionRedpacketDubboService questionRedpacketDubboService;

    /**
     * 获取问答红包
     * @return
     */
    @RequestMapping("create")
    public Result create() {
        Long userId = null;
        try {
            userId = getLoginUserId();
            if (userId == null) {
                return Result.notLogin();
            }

            QuestionRedpacketVO redpacketVO = questionRedpacketDubboService.assembleQuestionRedpacket(userId);

            return Result.success(redpacketVO);
        } catch (LifeCircleException e) {
            log.error("获取问答红包出错：userId={}", userId, e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("获取问答红包出错：userId={}", userId, e);
            return Result.systemError();
        }
    }


    /**
     * 获取问答红包
     * @return
     */
    @RequestMapping("assembleQuestionRedpacket")
    public Result assembleQuestionRedpacket() {
        Long userId = null;
        try {
            userId = getLoginUserId();
            if (userId == null) {
                return Result.notLogin();
            }
            QuestionRedpacketVO redpacketVO = questionRedpacketDubboService.assembleQuestionRedpacket(userId);

            return Result.success(redpacketVO);
        } catch (LifeCircleException e) {
            log.error("assembleQuestionRedpacket->获取问答红包出错：userId={}", userId, e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("assembleQuestionRedpacket->获取问答红包出错：userId={}", userId, e);
            return Result.systemError();
        }
    }

}
