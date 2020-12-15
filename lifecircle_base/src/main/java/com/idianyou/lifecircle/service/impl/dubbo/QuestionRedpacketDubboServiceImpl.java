package com.idianyou.lifecircle.service.impl.dubbo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.idianyou.imredpacket.spi.service.QuestionMomentService;
import com.idianyou.lifecircle.constants.RedisConstants;
import com.idianyou.lifecircle.constants.RedisKeys;
import com.idianyou.lifecircle.dto.vo.QuestionRedpacketVO;
import com.idianyou.lifecircle.enums.YesNoEnum;
import com.idianyou.lifecircle.exception.LifeCircleException;
import com.idianyou.lifecircle.service.QuestionRedpacketDubboService;
import com.idianyou.spi.common.InnerRpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/12 15:00
 */
@Slf4j
@Service
public class QuestionRedpacketDubboServiceImpl implements QuestionRedpacketDubboService {

    @Autowired
    private QuestionMomentService questionMomentService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public QuestionRedpacketVO create(Long userId) {
        QuestionRedpacketVO redpacketVO = new QuestionRedpacketVO();
        redpacketVO.setIsShow(YesNoEnum.NO.getCode());

        // 问答红包开关
        String switchValue = (String) redisTemplate.boundValueOps(RedisKeys.QUESTION_REDPACKET_SWITCH).get();
        if (YesNoEnum.YES.getCode().toString().equals(switchValue)) {
            redpacketVO.setIsShow(YesNoEnum.YES.getCode());
        } else {
            return redpacketVO;
        }

        // 问答红包间隔时间(秒)
        String intervalStr = (String) redisTemplate.boundValueOps(RedisKeys.QUESTION_REDPACKET_INTERVAL).get();
        long interval = NumberUtils.toLong(intervalStr, 60);

        // 用户上一个问答红包的创建时间
        String previousCreateTimeStr = (String) redisTemplate.boundValueOps(RedisKeys.QUESTION_REDPACKET_PREVIOUS_CREATE_TIME.concat(userId.toString())).get();
        long previousCreateTime = NumberUtils.toLong(previousCreateTimeStr, 0);
        if (previousCreateTime <= 0) {
            redpacketVO.setLeftTime(interval);
            redisTemplate.boundValueOps(RedisKeys.QUESTION_REDPACKET_PREVIOUS_CREATE_TIME.concat(userId.toString())).set(System.currentTimeMillis() + "", RedisConstants.ONE_DAY, TimeUnit.DAYS);
            return redpacketVO;
        }

        long leftTime = (previousCreateTime + interval * 1000 - System.currentTimeMillis()) / 1000;
        if (leftTime > 0) {
            redpacketVO.setLeftTime(leftTime);
            return redpacketVO;
        }

        redpacketVO.setLeftTime(0);

        // 用户上一个问答红包的问题ID
        String previousQuestionId = (String) redisTemplate.boundValueOps(RedisKeys.QUESTION_REDPACKET_PREVIOUS_QUESTIONID.concat(userId.toString())).get();

        JSONObject extJson = null;
        if (StringUtils.isNotBlank(previousQuestionId)) {
            // 判断上一个问答红包的问题是否已答完
            InnerRpcResponse<Boolean> response = questionMomentService.hasEnded(userId, previousQuestionId);
            if ((Integer.valueOf("0").equals(response.getCode()))) {
                redisTemplate.boundValueOps(RedisKeys.QUESTION_REDPACKET_PREVIOUS_CREATE_TIME.concat(userId.toString())).set(System.currentTimeMillis() + "", RedisConstants.ONE_DAY, TimeUnit.DAYS);
                redisTemplate.delete(RedisKeys.QUESTION_REDPACKET_PREVIOUS_QUESTIONID.concat(userId.toString()));
                redpacketVO.setLeftTime(interval);
                return redpacketVO;
            } else {
                log.error("获取上一个问答红包的问题是否已答完失败：userId={}, previousQuestionId={}, response={}", userId, previousQuestionId, JSON.toJSONString(response));
            }

            extJson = new JSONObject();
            extJson.put("previousQuestionId", previousQuestionId);
        }

        InnerRpcResponse<JSONObject> response = questionMomentService.create(userId, extJson);
        if ((Integer.valueOf("0").equals(response.getCode()) || Integer.valueOf("10178").equals(response.getCode())) && response.getData() != null) {
            redpacketVO.setProtocol(response.getData().getString("protocol"));
            redpacketVO.setQuestionId(response.getData().getString("questionId"));

            if (StringUtils.isBlank(redpacketVO.getQuestionId()) && StringUtils.isNotBlank(previousQuestionId)) {
                redpacketVO.setQuestionId(previousQuestionId);
            }

            if (StringUtils.isAnyBlank(redpacketVO.getProtocol(), redpacketVO.getQuestionId())) {
                log.error("创建问答红包失败：userId={}, extJson={}, response={}", userId, JSON.toJSONString(extJson), JSON.toJSONString(response));
                throw new LifeCircleException("创建问答红包失败");
            }

            if (!redpacketVO.getQuestionId().equals(previousQuestionId)) {
                String newPreviousCreateTime = System.currentTimeMillis() + "";
                if (StringUtils.isBlank(previousQuestionId)) {
                    newPreviousCreateTime = (System.currentTimeMillis() - interval * 10 * 1000) + "";
                }
                redisTemplate.boundValueOps(RedisKeys.QUESTION_REDPACKET_PREVIOUS_CREATE_TIME.concat(userId.toString())).set(newPreviousCreateTime, RedisConstants.ONE_DAY, TimeUnit.DAYS);
                redisTemplate.boundValueOps(RedisKeys.QUESTION_REDPACKET_PREVIOUS_QUESTIONID.concat(userId.toString())).set(redpacketVO.getQuestionId(), RedisConstants.ONE_DAY, TimeUnit.DAYS);
            }
        } else {
            log.error("创建问答红包失败：userId={}, extJson={}, response={}", userId, JSON.toJSONString(extJson), JSON.toJSONString(response));
            throw new LifeCircleException("创建问答红包失败");
        }

        return redpacketVO;
    }

    @Override
    public QuestionRedpacketVO assembleQuestionRedpacket(Long userId) {

        QuestionRedpacketVO redpacketVO = new QuestionRedpacketVO();
        redpacketVO.setIsShow(YesNoEnum.NO.getCode());

        // 问答红包开关
        String switchValue = (String) redisTemplate.boundValueOps(RedisKeys.QUESTION_REDPACKET_SWITCH).get();
        if (YesNoEnum.YES.getCode().toString().equals(switchValue)) {
            redpacketVO.setIsShow(YesNoEnum.YES.getCode());
        } else {
            return redpacketVO;
        }

        Date curDate  = new Date();
        long interval = 0;
        try {
            JSONObject extJson = new JSONObject();
            InnerRpcResponse<JSONObject> response = questionMomentService.query(userId,extJson);

            //log.info("test->data:{}",response.getData());
            //返回结果为空，则默认倒计时60秒
            if(response == null || response.getData() == null){
                redpacketVO.setLeftTime(60);
                return  redpacketVO;
            }

            //继续分会结果
            JSONObject resultJSON = response.getData();
            JSONObject currentJSON = resultJSON.getJSONObject("current");
            String period = currentJSON.getString("period");
            int    status = currentJSON.getIntValue("status");


            if(status == 0){
                JSONObject nextJSON = resultJSON.getJSONObject("next");
                //计算倒计时
                String[] times = period.split("~");
                interval = org.apache.commons.lang3.time.DateUtils.parseDate(times[0],"yyyy-MM-dd'T'HH:mm").getTime() - currentJSON.getLongValue("current");
                if(interval < 0){
                    interval = (org.apache.commons.lang3.time.DateUtils.parseDate(times[1],"yyyy-MM-dd'T'HH:mm").getTime() - currentJSON.getLongValue("current"))/1000 ;
                }else{
                    interval = (org.apache.commons.lang3.time.DateUtils.parseDate(times[0],"yyyy-MM-dd'T'HH:mm").getTime() - currentJSON.getLongValue("current"))/1000 ;
                }
                redpacketVO.setLeftTime(interval< 0 ? 60 : interval);
                redpacketVO.setWaitProtocol(currentJSON.getString("waitProtocol"));
                redpacketVO.setQuestionId(nextJSON.getString("questionId"));
                redpacketVO.setProtocol(nextJSON.getString("protocol"));
            }else {
                redpacketVO.setQuestionId(currentJSON.getString("questionId"));
                redpacketVO.setProtocol(currentJSON.getString("protocol"));
            }

        }catch (Exception e){
            log.info("assembleQuestionRedpacket->获取问答红包失败e:{}",e);
        }

        return  redpacketVO;

    }

}