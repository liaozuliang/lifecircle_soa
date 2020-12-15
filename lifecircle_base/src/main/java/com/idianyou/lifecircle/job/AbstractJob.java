package com.idianyou.lifecircle.job;

import com.idianyou.lifecircle.exception.LifeCircleException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/11 11:38
 */
@Slf4j
public abstract class AbstractJob implements Job {

    @Autowired
    protected RedissonClient redissonClient;

    @Autowired
    protected RedisTemplate redisTemplate;

    abstract void doJob();

    protected void executeJob(String jobName, String redisLockKey, Long sleepSeconds) {
        if (StringUtils.isBlank(jobName)) {
            throw new LifeCircleException("jobName不能为空");
        }

        Boolean tryLock = null;
        RLock lock = null;
        long startTime = System.currentTimeMillis();

        try {
            if (StringUtils.isNotBlank(redisLockKey)) {
                /*lock = redissonClient.getLock(redisLockKey);
                if (!lock.tryLock()) {
                    log.info("正在执行Job[{}]，请稍后", jobName);
                    return;
                }*/

                tryLock = redisTemplate.boundValueOps(redisLockKey).setIfAbsent(System.currentTimeMillis() + "", 30, TimeUnit.MINUTES);
                if (Boolean.FALSE.equals(tryLock)) {
                    log.info("正在执行Job[{}]，请稍后", jobName);
                    return;
                }
            }

            log.info("开始执行Job[{}]", jobName);

            doJob();

            // 休眠N秒，解决计算过快导致多个节点出现重复计算问题
            if (sleepSeconds != null && sleepSeconds > 0) {
                TimeUnit.SECONDS.sleep(sleepSeconds);
            }
        } catch (Exception e) {
            log.error("执行Job[{}]出错：", jobName, e);
        } finally {
            /*if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }*/

            if (Boolean.TRUE.equals(tryLock)) {
                redisTemplate.delete(redisLockKey);
            }
        }

        long takeTime = System.currentTimeMillis() - startTime;
        log.info("Job[{}]执行完成, 总耗时{}ms", jobName, takeTime);
    }
}