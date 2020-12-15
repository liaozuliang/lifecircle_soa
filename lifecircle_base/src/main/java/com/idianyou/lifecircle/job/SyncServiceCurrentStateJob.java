package com.idianyou.lifecircle.job;

import com.idianyou.lifecircle.constants.RedisKeys;
import com.idianyou.lifecircle.service.local.job.SyncServiceCurrentStateJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @Description: 补偿同步未结束服务的当前状态
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/11 11:36
 */
@Slf4j
@Service
public class SyncServiceCurrentStateJob extends AbstractJob {

    @Autowired
    private SyncServiceCurrentStateJobService syncServiceCurrentStateJobService;

    @Scheduled(cron = "0 0 * * * ?")
    @Override
    public void execute() {
        String jobName = "同步未结束服务的当前状态";
        String redisLockKey = RedisKeys.LOCK_PATH.concat(getClass().getSimpleName());
        Long sleepSeconds = 60L;
        executeJob(jobName, redisLockKey, sleepSeconds);
    }

    @Override
    void doJob() {
        syncServiceCurrentStateJobService.syncServiceCurrentState();
    }
}