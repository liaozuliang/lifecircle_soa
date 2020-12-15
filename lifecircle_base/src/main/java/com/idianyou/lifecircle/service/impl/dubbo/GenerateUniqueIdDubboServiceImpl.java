package com.idianyou.lifecircle.service.impl.dubbo;

import com.idianyou.lifecircle.constants.RedisKeys;
import com.idianyou.lifecircle.enums.TableNameEnum;
import com.idianyou.lifecircle.exception.LifeCircleException;
import com.idianyou.lifecircle.service.GenerateUniqueIdDubboService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/28 9:58
 */
@Slf4j
@Service
public class GenerateUniqueIdDubboServiceImpl implements GenerateUniqueIdDubboService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public long generateAutoIncId(TableNameEnum tableNameEnum) {
        if (tableNameEnum == null) {
            throw new LifeCircleException("创建表自增ID失败：表枚举不能为空");
        }

        String key = RedisKeys.AUTOID_PATH.concat(tableNameEnum.name().toLowerCase());
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());

        return tableNameEnum.getBaseNum() + counter.incrementAndGet();
    }
}
