package com.idianyou.lifecircle.job;

import com.idianyou.lifecircle.constants.RedisKeys;
import com.idianyou.lifecircle.service.local.job.ClearInvalidCategoryJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @Description: 清除无效的主题
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/8/3 10:08
 */
@Slf4j
@Service
public class ClearInvalidCategoryJob extends AbstractJob {

    @Autowired
    private ClearInvalidCategoryJobService clearInvalidCategoryJobService;

    @Scheduled(cron = "0 0 * * * ?")
    @Override
    public void execute() {
        String jobName = "清除无效的主题";
        String redisLockKey = RedisKeys.LOCK_PATH.concat(getClass().getSimpleName());
        Long sleepSeconds = 60L;
        executeJob(jobName, redisLockKey, sleepSeconds);
    }

    @Override
    void doJob() {
        clearInvalidCategoryJobService.doClear();
    }
}
