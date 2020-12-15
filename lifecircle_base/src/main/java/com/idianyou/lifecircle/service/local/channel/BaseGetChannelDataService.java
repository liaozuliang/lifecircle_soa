package com.idianyou.lifecircle.service.local.channel;

import com.idianyou.lifecircle.dto.GetChannelDataContext;
import com.idianyou.lifecircle.dto.GetTypeDataContext;
import com.idianyou.lifecircle.dto.TypeNum;
import com.idianyou.lifecircle.dto.redis.Category;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.dto.vo.ChannelDataVO;
import com.idianyou.lifecircle.enums.DataQueryTypeEnum;
import com.idianyou.lifecircle.enums.DataTypeEnum;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.enums.YesNoEnum;
import com.idianyou.lifecircle.exception.LifeCircleException;
import com.idianyou.lifecircle.service.local.FactoryService;
import com.idianyou.lifecircle.service.local.learn.GetLearnDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: 获取频道数据
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/27 15:55
 */
@Slf4j
public class BaseGetChannelDataService extends AbstractGetChannelDataService {

    @Autowired
    private GetLearnDataService getLearnDataService;

    @Override
    public boolean supports(LifeCircleChannelEnum channelEnum) {
        return false;
    }

    public boolean recommendChannelSupports(GetChannelDataContext context) {
        return false;
    }

    public boolean lifeChannelSupports(GetChannelDataContext context) {
        return false;
    }

    public boolean qaAdSupports(GetChannelDataContext context) {
        return false;
    }

    public boolean amusementChannelSupports(GetChannelDataContext context) {
        return false;
    }

    public boolean normalChannelSupports(GetChannelDataContext context) {
        return false;
    }

    @Override
    public List<ChannelDataGroupVO> getData(GetChannelDataContext context) {
        if (context.getUserNativeTypeEnum() == null || context.getTimeDescEnum() == null) {
            log.error("{}, 参数userNativeTypeEnum或timeDescEnum不能为空", context.getBaseLog());
            throw new LifeCircleException("参数userNativeTypeEnum或timeDescEnum不能为空");
        }

        log.info("{}, 当前场景[userNativeTypeEnum={}, timeDescEnum={}]", context.getBaseLog(), context.getUserNativeTypeEnum().getDesc(), context.getTimeDescEnum().getDesc());

        Set<String> passedDataIdSet = context.getPassedDataIdSet();
        Map<DataQueryTypeEnum, List<ChannelDataGroupVO>> type_group_map = new ConcurrentHashMap<>();

        List<DataQueryTypeEnum> dataQueryTypeEnumList = getSortTypeList();

        CountDownLatch countDownLatch = new CountDownLatch(dataQueryTypeEnumList.size());
        int dataNum = 0;
        int groupNum = 0;

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (DataQueryTypeEnum dataQueryTypeEnum : dataQueryTypeEnumList) {
            if (getTypeNum(dataQueryTypeEnum) == null) {
                countDownLatch.countDown();
                continue;
            }
            dataNum = getTypeNum(dataQueryTypeEnum).getDataNum();
            groupNum = getTypeNum(dataQueryTypeEnum).getGroupNum();
            asyncGetTypeData(dataQueryTypeEnum, type_group_map, context, passedDataIdSet, countDownLatch, dataNum, groupNum);
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("{} 等待线程执行完出错：", context.getBaseLog(), e);
        }

        stopWatch.stop();
        log.info("{} 全部分类数据查询完成，共耗时{}ms", context.getBaseLog(), stopWatch.getTotalTimeMillis());

        StringBuilder sb = new StringBuilder();
        sb.append(context.getBaseLog());
        sb.append(" 查询到类型数据");

        for (Map.Entry<DataQueryTypeEnum, List<ChannelDataGroupVO>> entry : type_group_map.entrySet()) {
            sb.append(", " + entry.getKey().getDesc()).append(entry.getValue().size()).append("条");
        }

        log.info(sb.toString());

        removeExtraData(type_group_map);

        sb = new StringBuilder();
        sb.append(context.getBaseLog());
        sb.append(" 合并清除数据后");

        for (Map.Entry<DataQueryTypeEnum, List<ChannelDataGroupVO>> entry : type_group_map.entrySet()) {
            sb.append(", " + entry.getKey().getDesc()).append(entry.getValue().size()).append("条");
        }

        log.info(sb.toString());

        return buildSortedResult(context, type_group_map);
    }

    public void removeExtraData(Map<DataQueryTypeEnum, List<ChannelDataGroupVO>> type_group_map) {
        // 农户直播 + 农户 (2条)
        removeExtraData(type_group_map, Arrays.asList(DataQueryTypeEnum.MARKET_LIVE, DataQueryTypeEnum.MARKET_FARMER), 2);

        // 孺子牛直播 + 孺子牛个人 (2条)
        removeExtraData(type_group_map, Arrays.asList(DataQueryTypeEnum.PERSONAL_LIVE, DataQueryTypeEnum.PERSONAL), 2);
    }

    public TypeNum getTypeNum(DataQueryTypeEnum dataQueryTypeEnum) {
        Map<DataQueryTypeEnum, TypeNum> type_typeNum_map = new HashMap<>();

        // boss开讲(1组，每组6个)
        type_typeNum_map.put(DataQueryTypeEnum.BOSS_LECTURE, new TypeNum(6, 1));

        // 全民直播(1组，每组6个)
        type_typeNum_map.put(DataQueryTypeEnum.LIVE, new TypeNum(6, 1));

        // 农户直播 + 农户 (2条)
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_LIVE, new TypeNum(2, 1));
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_FARMER, new TypeNum(2, 1));

        // 商户(3组，每组6个)
        type_typeNum_map.put(DataQueryTypeEnum.MARKET_MERCHANT, new TypeNum(6, 3));

        // 精彩瞬间(1条)
        type_typeNum_map.put(DataQueryTypeEnum.WONDERFUL_MOMENTS, new TypeNum(1, 1));

        // 孺子牛直播 + 孺子牛个人 (2条)
        type_typeNum_map.put(DataQueryTypeEnum.PERSONAL_LIVE, new TypeNum(2, 1));
        type_typeNum_map.put(DataQueryTypeEnum.PERSONAL, new TypeNum(2, 1));

        // 商城(1组，每组6个)
        type_typeNum_map.put(DataQueryTypeEnum.CHI_GUA_MALL, new TypeNum(6, 1));

        // K歌之王(1条)
        type_typeNum_map.put(DataQueryTypeEnum.K_SONG, new TypeNum(1, 1));

        // 最佳辩手(1条)
        type_typeNum_map.put(DataQueryTypeEnum.BEST_DEBATER, new TypeNum(1, 1));

        TypeNum typeNum = type_typeNum_map.get(dataQueryTypeEnum);

        return typeNum;
    }

    public List<DataQueryTypeEnum> getSortTypeList() {
        List<DataQueryTypeEnum> dataQueryTypeEnumList = new ArrayList<>();

        dataQueryTypeEnumList.add(DataQueryTypeEnum.BOSS_LECTURE);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.LIVE);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_LIVE);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_FARMER);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.MARKET_MERCHANT);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.WONDERFUL_MOMENTS);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.PERSONAL_LIVE);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.PERSONAL);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.CHI_GUA_MALL);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.K_SONG);
        dataQueryTypeEnumList.add(DataQueryTypeEnum.BEST_DEBATER);

        return dataQueryTypeEnumList;
    }

    /**
     * 删除多余数据(从不同源查询出同一类型数据时, 拿完一种类型再拿下一种类型)
     * @param type_group_map
     * @param dataQueryTypeEnumList
     * @param dataNum 保留条数
     */
    public void removeExtraData2(Map<DataQueryTypeEnum, List<ChannelDataGroupVO>> type_group_map, List<DataQueryTypeEnum> dataQueryTypeEnumList, int dataNum) {
        if (CollectionUtils.isEmpty(dataQueryTypeEnumList) || dataQueryTypeEnumList.size() == 1 || dataNum <= 0) {
            return;
        }

        List<String> selectedIdList = new ArrayList<>();
        List<ChannelDataGroupVO> selectedList = new ArrayList<>(dataNum);
        List<ChannelDataGroupVO> typeDataList = null;

        for (DataQueryTypeEnum dataQueryTypeEnum : dataQueryTypeEnumList) {
            if (selectedList.size() >= dataNum) {
                break;
            }

            typeDataList = type_group_map.get(dataQueryTypeEnum);
            if (CollectionUtils.isEmpty(typeDataList)) {
                continue;
            }

            boolean hasPassedData = false;
            List<ChannelDataVO> dataVOList = null;
            for (ChannelDataGroupVO dataGroupVO : typeDataList) {
                if (selectedList.size() >= dataNum) {
                    break;
                }

                if (CollectionUtils.isEmpty(dataGroupVO.getDataList())) {
                    continue;
                }

                dataVOList = new ArrayList<>(dataGroupVO.getDataList().size());
                for (ChannelDataVO dataVO : dataGroupVO.getDataList()) {
                    if (selectedIdList.contains(dataVO.getId().toString())) {
                        continue;
                    }

                    selectedIdList.add(dataVO.getId().toString());
                    dataVOList.add(dataVO);
                    hasPassedData = true;
                }

                if (hasPassedData) {
                    dataGroupVO.setDataList(dataVOList);
                    selectedList.add(dataGroupVO);
                }
            }
        }

        for (int i = 0; i < dataQueryTypeEnumList.size(); i++) {
            if (i == 0) {
                type_group_map.put(dataQueryTypeEnumList.get(i), selectedList);
            } else {
                type_group_map.get(dataQueryTypeEnumList.get(i)).clear();
            }
        }
    }

    /**
     * 删除多余数据(一条一条循环从每一种类型中拿取数据)
     * @param type_group_map
     * @param dataQueryTypeEnumList
     * @param dataNum 保留条数
     */
    public void removeExtraData(Map<DataQueryTypeEnum, List<ChannelDataGroupVO>> type_group_map, List<DataQueryTypeEnum> dataQueryTypeEnumList, int dataNum) {
        if (CollectionUtils.isEmpty(dataQueryTypeEnumList) || dataNum <= 0) {
            return;
        }

        Map<DataQueryTypeEnum, List<ChannelDataGroupVO>> new_type_group_map = new HashMap<>(dataQueryTypeEnumList.size());
        Set<ChannelDataGroupVO> selectedDataSet = new HashSet<>();

        Map<DataQueryTypeEnum, DataQueryTypeEnum> nextTypeMap = new HashMap<>();
        int listSize = dataQueryTypeEnumList.size();

        for (int i = 0; i < listSize; i++) {
            if (i == listSize - 1) {
                nextTypeMap.put(dataQueryTypeEnumList.get(i), dataQueryTypeEnumList.get(0));
            } else {
                nextTypeMap.put(dataQueryTypeEnumList.get(i), dataQueryTypeEnumList.get(i + 1));
            }

            if (!new_type_group_map.containsKey(dataQueryTypeEnumList.get(i))) {
                new_type_group_map.put(dataQueryTypeEnumList.get(i), new ArrayList<>());
            }
        }

        boolean doLoop = true;
        DataQueryTypeEnum currentType = dataQueryTypeEnumList.get(0);
        List<ChannelDataGroupVO> typeDataGroupVOList = null;
        int failedCount = 0;
        boolean failed = true;

        do {
            if (selectedDataSet.size() >= dataNum) {
                doLoop = false;
                break;
            }

            // 连续3轮拿不到数据
            if (failedCount > nextTypeMap.size() * 3) {
                doLoop = false;
                break;
            }

            typeDataGroupVOList = type_group_map.get(currentType);
            if (CollectionUtils.isEmpty(typeDataGroupVOList)) {
                currentType = nextTypeMap.get(currentType);
                failedCount++;
                continue;
            }

            failed = true;
            for (ChannelDataGroupVO dataGroupVO : typeDataGroupVOList) {
                if (!selectedDataSet.contains(dataGroupVO)) {
                    selectedDataSet.add(dataGroupVO);
                    new_type_group_map.get(currentType).add(dataGroupVO);
                    failed = false;
                    failedCount = 0;
                    break;
                }
            }

            if (failed) {
                failedCount++;
            }

            currentType = nextTypeMap.get(currentType);
        } while (doLoop);

        for (DataQueryTypeEnum dataQueryTypeEnum : dataQueryTypeEnumList) {
            type_group_map.put(dataQueryTypeEnum, new_type_group_map.get(dataQueryTypeEnum));
        }
    }

    /**
     * 删除多余数据(一条一条循环从每一种类型中拿取数据)
     * @param type_group_map
     * @param dataQueryTypeEnumList
     * @param dataNum 保留条数
     */
    public void removeExtraData3(Map<DataQueryTypeEnum, List<ChannelDataGroupVO>> type_group_map, List<DataQueryTypeEnum> dataQueryTypeEnumList, int dataNum) {
        if (CollectionUtils.isEmpty(dataQueryTypeEnumList) || dataNum <= 0) {
            return;
        }

        List<ChannelDataGroupVO> selectedList = new ArrayList<>();

        Map<DataQueryTypeEnum, DataQueryTypeEnum> nextTypeMap = new HashMap<>();
        int listSize = dataQueryTypeEnumList.size();

        for (int i = 0; i < listSize; i++) {
            if (i == listSize - 1) {
                nextTypeMap.put(dataQueryTypeEnumList.get(i), dataQueryTypeEnumList.get(0));
            } else {
                nextTypeMap.put(dataQueryTypeEnumList.get(i), dataQueryTypeEnumList.get(i + 1));
            }
        }

        boolean doLoop = true;
        DataQueryTypeEnum currentType = dataQueryTypeEnumList.get(0);
        List<ChannelDataGroupVO> typeDataGroupVOList = null;
        int failedCount = 0;
        boolean failed = true;

        do {
            if (selectedList.size() >= dataNum) {
                doLoop = false;
                break;
            }

            // 连续3轮拿不到数据
            if (failedCount > nextTypeMap.size() * 3) {
                doLoop = false;
                break;
            }

            typeDataGroupVOList = type_group_map.get(currentType);
            if (CollectionUtils.isEmpty(typeDataGroupVOList)) {
                currentType = nextTypeMap.get(currentType);
                failedCount++;
                continue;
            }

            failed = true;
            for (ChannelDataGroupVO dataGroupVO : typeDataGroupVOList) {
                if (!selectedList.contains(dataGroupVO)) {
                    selectedList.add(dataGroupVO);
                    failed = false;
                    failedCount = 0;
                    break;
                }
            }

            if (failed) {
                failedCount++;
            }

            currentType = nextTypeMap.get(currentType);
        } while (doLoop);

        for (int i = 0; i < dataQueryTypeEnumList.size(); i++) {
            if (i == 0) {
                type_group_map.put(dataQueryTypeEnumList.get(i), selectedList);
            } else {
                type_group_map.put(dataQueryTypeEnumList.get(i), new ArrayList<>());
            }
        }
    }

    public Map<DataQueryTypeEnum, DataQueryTypeEnum> getNextTypeMap() {
        List<DataQueryTypeEnum> dataQueryTypeEnumList = getSortTypeList();

        Map<DataQueryTypeEnum, DataQueryTypeEnum> nextTypeMap = new HashMap<>();
        int listSize = dataQueryTypeEnumList.size();

        for (int i = 0; i < listSize; i++) {
            if (i == listSize - 1) {
                nextTypeMap.put(dataQueryTypeEnumList.get(i), dataQueryTypeEnumList.get(0));
            } else {
                nextTypeMap.put(dataQueryTypeEnumList.get(i), dataQueryTypeEnumList.get(i + 1));
            }
        }

        return nextTypeMap;
    }

    public int getDataNum() {
        return 12;
    }

    /**
     * 结果排序
     * @param context
     * @param type_group_map
     * @return
     */
    public List<ChannelDataGroupVO> buildSortedResult(GetChannelDataContext context, Map<DataQueryTypeEnum, List<ChannelDataGroupVO>> type_group_map) {
        List<ChannelDataGroupVO> dataGroupVOList = new ArrayList<>();

        Map<DataQueryTypeEnum, DataQueryTypeEnum> nextTypeMap = getNextTypeMap();

        boolean doLoop = true;
        DataQueryTypeEnum currentType = getSortTypeList().get(0);
        List<ChannelDataGroupVO> typeDataGroupVOList = null;
        int failedCount = 0;
        boolean failed = true;

        do {
            if (dataGroupVOList.size() >= getDataNum()) {
                doLoop = false;
                break;
            }

            // 连续3轮拿不到数据
            if (failedCount > nextTypeMap.size() * 3) {
                doLoop = false;
                break;
            }

            typeDataGroupVOList = type_group_map.get(currentType);
            if (CollectionUtils.isEmpty(typeDataGroupVOList)) {
                currentType = nextTypeMap.get(currentType);
                failedCount++;
                continue;
            }

            failed = true;
            for (ChannelDataGroupVO dataGroupVO : typeDataGroupVOList) {
                if (!dataGroupVOList.contains(dataGroupVO)) {
                    dataGroupVOList.add(dataGroupVO);
                    failed = false;
                    failedCount = 0;
                    break;
                }
            }

            if (failed) {
                failedCount++;
            }

            currentType = nextTypeMap.get(currentType);
        } while (doLoop);

        return dataGroupVOList;
    }

    public void asyncGetTypeData(DataQueryTypeEnum queryTypeEnum, Map<DataQueryTypeEnum, List<ChannelDataGroupVO>> type_group_map, GetChannelDataContext context, Set<String> passedDataIdSet, CountDownLatch countDownLatch, int dataNum, int groupNum) {
        FactoryService.CHANNEL_LIST_ES.execute(() -> {
            try {
                getTypeData(queryTypeEnum, type_group_map, context, passedDataIdSet, dataNum, groupNum);
            } catch (Exception e) {
                log.error("{} 查询分类数据出错：", context.getBaseLog(), e);
            } finally {
                countDownLatch.countDown();
            }
        });
    }

    public void getTypeData(DataQueryTypeEnum queryTypeEnum, Map<DataQueryTypeEnum, List<ChannelDataGroupVO>> type_group_map, GetChannelDataContext context, Set<String> passedDataIdSet, int dataNum, int groupNum) {
        List<ChannelDataGroupVO> typeDataGroupVOList = type_group_map.get(queryTypeEnum);
        if (typeDataGroupVOList == null) {
            typeDataGroupVOList = new ArrayList<>();
            type_group_map.put(queryTypeEnum, typeDataGroupVOList);
        }

        ChannelDataGroupVO typeDataGroupVO = null;
        List<Category> categoryList = null;

        switch (queryTypeEnum) {
            case MARKET_FARMER:
            case MARKET_LIVE:
            case WONDERFUL_MOMENTS:
            case K_SONG:
            case BEST_DEBATER:
            case LIVE:
            case BOSS_LECTURE:
            case PERSONAL:
            case PERSONAL_LIVE:
            case FORM_COMPANY:
            case CUSTOMIZED_SERVICE:
            case LEARN_MARKET_FARMER:
                typeDataGroupVO = getTypeData(queryTypeEnum, dataNum, null, context, passedDataIdSet, groupNum);
                break;
            case LEARN_MARKET_MERCHANT:
                categoryList = getLearnDataService.getShopsCategory(context.getUserId(), groupNum, context.getBaseLog());
                typeDataGroupVO = categoryQuery(queryTypeEnum, context, passedDataIdSet, dataNum, groupNum, typeDataGroupVOList, typeDataGroupVO, categoryList);
                break;
            case LEARN_MARKET_PRODUCT:
                categoryList = getLearnDataService.getGoodsCategory(context.getUserId(), groupNum, context.getBaseLog());
                typeDataGroupVO = categoryQuery(queryTypeEnum, context, passedDataIdSet, dataNum, groupNum, typeDataGroupVOList, typeDataGroupVO, categoryList);
                break;
            case MARKET_MERCHANT:
            case MARKET_PRODUCT:
            case CHI_GUA_MALL:
            case NOVEL:
                categoryList = redisCacheService.getDataCategory(queryTypeEnum.getDataTypeEnum(), queryTypeEnum.getShowTypeEnum());
                typeDataGroupVO = categoryQuery(queryTypeEnum, context, passedDataIdSet, dataNum, groupNum, typeDataGroupVOList, typeDataGroupVO, categoryList);
                break;
            default:
                throw new LifeCircleException("未知的查询分类");
        }

        if (typeDataGroupVO != null) {
            addToTypeDataGroupVO(typeDataGroupVO, typeDataGroupVOList);
        }
    }

    @Nullable
    private ChannelDataGroupVO categoryQuery(DataQueryTypeEnum queryTypeEnum, GetChannelDataContext context, Set<String> passedDataIdSet, int dataNum, int groupNum, List<ChannelDataGroupVO> typeDataGroupVOList, ChannelDataGroupVO typeDataGroupVO, List<Category> categoryList) {
        return categoryAsyncQuery(queryTypeEnum, context, passedDataIdSet, dataNum, groupNum, typeDataGroupVOList, typeDataGroupVO, categoryList);
    }

    @Nullable
    private ChannelDataGroupVO categorySyncQuery(DataQueryTypeEnum queryTypeEnum, GetChannelDataContext context, Set<String> passedDataIdSet, int dataNum, int groupNum, List<ChannelDataGroupVO> typeDataGroupVOList, ChannelDataGroupVO typeDataGroupVO, List<Category> categoryList) {
        AtomicInteger queryTimes = new AtomicInteger(0);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        if (CollectionUtils.isNotEmpty(categoryList)) {
            String categoryNo = null;
            for (Category category : categoryList) {
                if (typeDataGroupVOList.size() >= groupNum) {
                    break;
                }

                // 过滤已查询过的主题
                categoryNo = queryTypeEnum.getShowTypeEnum().getType() + "_" + category.getId() + "_" + category.getName();
                if (context.getExcludeCategorySet().contains(categoryNo)) {
                    continue;
                }
                context.getExcludeCategorySet().add(categoryNo);

                typeDataGroupVO = getTypeData(queryTypeEnum, dataNum, category, context, passedDataIdSet, 1);
                queryTimes.incrementAndGet();

                if (typeDataGroupVO != null) {
                    addToTypeDataGroupVO(typeDataGroupVO, typeDataGroupVOList);
                    typeDataGroupVO = null;
                }
            }

            if (!queryTypeEnum.getDesc().startsWith("机器学习-")
                    && queryTimes.get() == 0) {
                for (Category category : categoryList) {
                    categoryNo = queryTypeEnum.getShowTypeEnum().getType() + "_" + category.getId() + "_" + category.getName();
                    context.getNeedDelQueryedCategoryList().add(categoryNo);
                }
            }
        }

        stopWatch.stop();
        log.info("{}-[{}({})]按主题查询了{}次，耗时{}ms", context.getBaseLog(), queryTypeEnum.getDesc(), queryTypeEnum.getShowTypeEnum().getType(), queryTimes.get(), stopWatch.getTotalTimeMillis());

        return typeDataGroupVO;
    }

    @Nullable
    private ChannelDataGroupVO categoryAsyncQuery(DataQueryTypeEnum queryTypeEnum, GetChannelDataContext context, Set<String> passedDataIdSet, int dataNum, int groupNum, List<ChannelDataGroupVO> typeDataGroupVOList, ChannelDataGroupVO typeDataGroupVO, List<Category> categoryList) {
        AtomicInteger queryTimes = new AtomicInteger(0);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        if (CollectionUtils.isNotEmpty(categoryList)) {
            String categoryNo = null;

            List<Category> asyncQueryList = new ArrayList<>();
            for (Category category : categoryList) {
                if (asyncQueryList.size() >= groupNum) {
                    break;
                }

                // 过滤已查询过的主题
                categoryNo = queryTypeEnum.getShowTypeEnum().getType() + "_" + category.getId() + "_" + category.getName();
                if (context.getExcludeCategorySet().contains(categoryNo)) {
                    continue;
                }
                context.getExcludeCategorySet().add(categoryNo);

                asyncQueryList.add(category);
            }

            if (asyncQueryList.size() > 0) {
                CountDownLatch countDownLatch = new CountDownLatch(asyncQueryList.size());
                Map<Category, ChannelDataGroupVO> resultMap = new ConcurrentHashMap<>(asyncQueryList.size());

                for (Category category : asyncQueryList) {
                    FactoryService.CHANNEL_LIST_ES.execute(() -> {
                        try {
                            ChannelDataGroupVO groupVO = getTypeData(queryTypeEnum, dataNum, category, context, passedDataIdSet, 1);
                            queryTimes.incrementAndGet();
                            if (groupVO != null) {
                                resultMap.put(category, groupVO);
                            }
                        } catch (Exception e) {
                            log.error("{}-[{}({})]按主题[{}({})]查询出错：", context.getBaseLog(), queryTypeEnum.getDesc(), queryTypeEnum.getShowTypeEnum().getType(), category.getName(), category.getId(), e);
                        } finally {
                            countDownLatch.countDown();
                        }
                    });
                }

                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    log.error("{}-[{}({})]按主题查询出错：", context.getBaseLog(), queryTypeEnum.getDesc(), queryTypeEnum.getShowTypeEnum().getType(), e);
                }

                for (Category category : asyncQueryList) {
                    if (typeDataGroupVOList.size() >= groupNum) {
                        break;
                    }

                    typeDataGroupVO = resultMap.get(category);

                    if (typeDataGroupVO != null) {
                        addToTypeDataGroupVO(typeDataGroupVO, typeDataGroupVOList);
                        typeDataGroupVO = null;
                    }
                }
            }

            if (!queryTypeEnum.getDesc().startsWith("机器学习-")
                    && queryTimes.get() == 0) {
                for (Category category : categoryList) {
                    categoryNo = queryTypeEnum.getShowTypeEnum().getType() + "_" + category.getId() + "_" + category.getName();
                    context.getNeedDelQueryedCategoryList().add(categoryNo);
                }
            }
        }

        stopWatch.stop();
        log.info("{}-[{}({})]按主题查询了{}次，耗时{}ms", context.getBaseLog(), queryTypeEnum.getDesc(), queryTypeEnum.getShowTypeEnum().getType(), queryTimes.get(), stopWatch.getTotalTimeMillis());

        return typeDataGroupVO;
    }

    public void addToTypeDataGroupVO(ChannelDataGroupVO typeDataGroupVO, List<ChannelDataGroupVO> typeDataGroupVOList) {
        if (YesNoEnum.YES.getCode().equals(typeDataGroupVO.getIsGroup())
                || typeDataGroupVO.getDataList().size() == 1) {
            typeDataGroupVOList.add(typeDataGroupVO);
        } else {
            for (ChannelDataVO channelDataVO : typeDataGroupVO.getDataList()) {
                ChannelDataGroupVO groupVO = new ChannelDataGroupVO();
                groupVO.setIsGroup(YesNoEnum.NO.getCode());
                groupVO.getDataList().add(channelDataVO);
                typeDataGroupVOList.add(groupVO);
            }
        }
    }

    public ChannelDataGroupVO getTypeData(DataQueryTypeEnum queryTypeEnum, int dataNum, Category category, GetChannelDataContext context, Set<String> passedDataIdSet, int groupNum) {
        GetTypeDataContext getTypeDataContext = new GetTypeDataContext();

        getTypeDataContext.setQueryTypeEnum(queryTypeEnum);
        getTypeDataContext.setUserId(context.getUserId());
        getTypeDataContext.setDeviceId(context.getDeviceId());
        getTypeDataContext.setDataNum(dataNum);
        getTypeDataContext.setGroupNum(groupNum);
        getTypeDataContext.setIdentityTypeEnum(null);
        getTypeDataContext.setClientId(null);
        getTypeDataContext.setDataTypeEnum(null);
        getTypeDataContext.setShowTypeEnum(null);
        getTypeDataContext.setIgnoreServiceStatus(false);
        getTypeDataContext.setExcludeDataIdSet(context.getExcludeDataIdSet());
        getTypeDataContext.setPassedDataIdSet(passedDataIdSet);
        getTypeDataContext.setPassedBizDataIdSet(context.getPassedBizDataIdSet());
        getTypeDataContext.setBaseLog(context.getBaseLog());

        if (context.getChannelEnum() != null
                && !LifeCircleChannelEnum.RECOMMEND.equals(context.getChannelEnum())) {
            List<Integer> channelIdList = new ArrayList<>();
            channelIdList.add(context.getChannelEnum().getChannelId());
            getTypeDataContext.setChannelIdList(channelIdList);
        }

        if (category != null) {
            getTypeDataContext.setCategoryId(category.getId());
            getTypeDataContext.setCategoryName(category.getName());
        }

        getTypeDataContext.setDataTypeEnum(queryTypeEnum.getDataTypeEnum());
        getTypeDataContext.setShowTypeEnum(queryTypeEnum.getShowTypeEnum());

        switch (queryTypeEnum) {
            case MARKET_FARMER:
                // 赶集农户
                break;
            case LEARN_MARKET_FARMER:
                // 机器学习-赶集农户
                break;
            case MARKET_LIVE:
                // 赶集直播
                getTypeDataContext.setClientId(DataTypeEnum.MARKET.getClientId());
                getTypeDataContext.setNotServiceName("全民直播");
                break;
            case MARKET_MERCHANT:
                // 赶集商户
                break;
            case LEARN_MARKET_MERCHANT:
                // 机器学习-赶集商户
                break;
            case MARKET_PRODUCT:
                // 赶集商品
                break;
            case LEARN_MARKET_PRODUCT:
                // 机器学习-赶集商品
                break;
            case CUSTOMIZED_SERVICE:
                // 定制服务
                getTypeDataContext.setMinCreateTime(DateUtils.addDays(new Date(), -1));
                break;
            case CHI_GUA_MALL:
                // 吃瓜商城
                break;
            case WONDERFUL_MOMENTS:
                // 精彩瞬间
                break;
            case K_SONG:
                // K歌之王
                getTypeDataContext.setIgnoreServiceStatus(true);
                break;
            case BEST_DEBATER:
                // 最佳辩手
                getTypeDataContext.setIgnoreServiceStatus(true);
                break;
            case LIVE:
                // 全民直播
                break;
            case BOSS_LECTURE:
                // BOSS开讲
                break;
            case FORM_COMPANY:
                // 开公司
                break;
            case PERSONAL:
                // 个人动态
                break;
            case PERSONAL_LIVE:
                // 个人直播
                getTypeDataContext.setClientId(DataTypeEnum.MARKET.getClientId());
                getTypeDataContext.setServiceName("全民直播");
                break;
            case NOVEL:
                break;
            default:
                throw new LifeCircleException("未知的查询分类");
        }

        return factoryService.getService(getTypeDataContext).getTypeData(getTypeDataContext);
    }

}
