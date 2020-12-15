package com.idianyou.lifecircle.service.local.job;

import com.idianyou.lifecircle.dto.InnerRpcResponse;
import com.idianyou.lifecircle.dto.UpdateServiceBizParamDTO;
import com.idianyou.lifecircle.entity.mongo.LifeCircleContentMongo;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.ShowTypeEnum;
import com.idianyou.lifecircle.mongo.LifeCircleContentRepository;
import com.idianyou.lifecircle.service.LifeCircleContentPublishDubboService;
import com.idianyou.lifecircle.service.local.FactoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: 同步未结束服务的当前状态
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/11 11:45
 */
@Slf4j
@Service
public class SyncServiceCurrentStateJobService {

    @Autowired
    private LifeCircleContentRepository lifeCircleContentRepository;

    @Autowired
    private LifeCircleContentPublishDubboService lifeCircleContentPublishDubboService;

    @Autowired
    private FactoryService factoryService;

    private static final ExecutorService ES = new ThreadPoolExecutor(2, 15,
            30L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    @PreDestroy
    public void destroy() {
        ES.shutdown();
    }

    public void syncServiceCurrentState() {
        long beginId = 0;
        int pageSize = 500;
        Date maxUpdateTime = DateUtils.addMinutes(new Date(), -30);

        List<LifeCircleContentMongo> contentMongoList = null;
        boolean doQuery = false;

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);

        do {
            contentMongoList = lifeCircleContentRepository.getRunningServiceData(beginId, pageSize, maxUpdateTime);
            if (CollectionUtils.isEmpty(contentMongoList)) {
                break;
            }

            CountDownLatch countDownLatch = new CountDownLatch(contentMongoList.size());

            for (LifeCircleContentMongo contentMongo : contentMongoList) {
                if (contentMongo.get_id() > beginId) {
                    beginId = contentMongo.get_id();
                }

                ES.execute(() -> {
                    try {
                        UpdateServiceBizParamDTO bizParamDTO = null;

                        if (ShowTypeEnum.MARKET_LIVE.getType().equals(contentMongo.getShowType())) {
                            bizParamDTO = factoryService.getService(DataTypeEnum.LIVE_FOR_ALL).getServiceCurrentState(contentMongo.getServiceDataId());
                        } else {
                            bizParamDTO = factoryService.getService(DataTypeEnum.typeOf(contentMongo.getDataType())).getServiceCurrentState(contentMongo.getServiceDataId());
                        }

                        if (bizParamDTO != null) {
                            InnerRpcResponse response = lifeCircleContentPublishDubboService.updateServiceBizParam(bizParamDTO);
                            if (!response.isSuccess()) {
                                failed.incrementAndGet();
                                log.error("更新未结束服务的状态数据失败：showType={}，contentId={}，serviceDataId={}，{}", contentMongo.getShowType(), contentMongo.get_id(), contentMongo.getServiceDataId(), response.getMessage());
                            } else {
                                success.incrementAndGet();
                            }
                        } else {
                            // 赶集每天都产生新数据，查不到表示是旧数据，可以删除
                            if (DataTypeEnum.MARKET.getType().equals(contentMongo.getDataType())) {
                                lifeCircleContentPublishDubboService.delete(contentMongo.get_id());
                            }
                            failed.incrementAndGet();
                            log.error("获取未结束服务的当前状态数据为空，showType={}，contentId={}，serviceDataId={}", contentMongo.getShowType(), contentMongo.get_id(), contentMongo.getServiceDataId());
                        }
                    } catch (Exception e) {
                        failed.incrementAndGet();
                        log.error("更新未结束服务的状态数据出错：showType={}，contentId={}，serviceDataId={}", contentMongo.getShowType(), contentMongo.get_id(), contentMongo.getServiceDataId(), e);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }

            try {
                countDownLatch.await();
            } catch (Exception e) {
                log.error("等待线程执行完出错：", e);
            }

            if (contentMongoList.size() == pageSize) {
                doQuery = true;
            } else {
                doQuery = false;
            }
        } while (doQuery);

        log.info("同步未结束服务的当前状态完成，成功{}条，失败{}条", success.get(), failed.get());
    }
}