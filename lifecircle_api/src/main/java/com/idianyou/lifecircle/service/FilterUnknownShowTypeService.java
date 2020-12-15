package com.idianyou.lifecircle.service;

import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.dto.vo.ChannelDataVO;
import com.idianyou.lifecircle.dto.vo.SearchDataVO;
import com.idianyou.lifecircle.dto.vo.UserDataVO;
import com.idianyou.lifecircle.enums.ShowTypeEnum;
import com.idianyou.lifecircle.enums.YesNoEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 过滤客户端不识别的展示类型
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/7/14 17:43
 */
@Slf4j
@Service
public class FilterUnknownShowTypeService {

    /**
     * 过滤掉小说类型
     * @param dataList
     */
    public void filterNovel(List<ChannelDataGroupVO> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        List<ChannelDataGroupVO> needRemoveList = new ArrayList<>();

        for (ChannelDataGroupVO dataGroupVO : dataList) {
            if (YesNoEnum.NO.getCode().equals(dataGroupVO.getIsGroup())) {
                continue;
            }

            if (CollectionUtils.isEmpty(dataGroupVO.getDataList())) {
                continue;
            }

            if (Objects.equals(dataGroupVO.getDataList().get(0).getShowType(), ShowTypeEnum.NOVEL_SERVICE_DYNAMIC.getType())) {
                needRemoveList.add(dataGroupVO);
            }
        }

        if (needRemoveList.size() > 0) {
            dataList.removeAll(needRemoveList);
            log.info("过滤了{}组小说数据", needRemoveList.size());
        }
    }

    /**
     * 过滤掉小说类型
     * @param searchDataVO
     */
    public void filterNovel(SearchDataVO searchDataVO) {
        if (searchDataVO == null || CollectionUtils.isEmpty(searchDataVO.getChannelGroupList())) {
            return;
        }

        List<ChannelDataGroupVO> needRemoveList = new ArrayList<>();

        for (SearchDataVO.ChannelGroup channelGroup : searchDataVO.getChannelGroupList()) {
            if (CollectionUtils.isEmpty(channelGroup.getDataList())) {
                continue;
            }

            needRemoveList.clear();
            for (ChannelDataGroupVO dataGroupVO : channelGroup.getDataList()) {
                if (YesNoEnum.NO.getCode().equals(dataGroupVO.getIsGroup())) {
                    continue;
                }

                if (CollectionUtils.isEmpty(dataGroupVO.getDataList())) {
                    continue;
                }

                if (Objects.equals(dataGroupVO.getDataList().get(0).getShowType(), ShowTypeEnum.NOVEL_SERVICE_DYNAMIC.getType())) {
                    needRemoveList.add(dataGroupVO);
                }
            }

            if (needRemoveList.size() > 0) {
                channelGroup.getDataList().removeAll(needRemoveList);
                log.info("过滤了{}组小说数据", needRemoveList.size());
            }
        }

        List<SearchDataVO.ChannelGroup> needRemoveChannelGroupList = new ArrayList<>();
        for (SearchDataVO.ChannelGroup channelGroup : searchDataVO.getChannelGroupList()) {
            if (CollectionUtils.isEmpty(channelGroup.getDataList())) {
                needRemoveChannelGroupList.add(channelGroup);
            }
        }

        if (needRemoveChannelGroupList.size() > 0) {
            searchDataVO.getChannelGroupList().removeAll(needRemoveChannelGroupList);
        }
    }

    /**
     * 过滤掉小说类型
     * @param userDataVO
     */
    public void filterNovel(UserDataVO userDataVO) {
        if (userDataVO == null || CollectionUtils.isEmpty(userDataVO.getDataList())) {
            return;
        }

        List<ChannelDataVO> needRemoveList2 = new ArrayList<>();
        int removeCount = 0;

        for (ChannelDataGroupVO dataGroupVO : userDataVO.getDataList()) {
            if (CollectionUtils.isEmpty(dataGroupVO.getDataList())) {
                continue;
            }

            needRemoveList2.clear();
            for (ChannelDataVO dataVO : dataGroupVO.getDataList()) {
                if (Objects.equals(dataVO.getShowType(), ShowTypeEnum.NOVEL_SERVICE_DYNAMIC.getType())) {
                    needRemoveList2.add(dataVO);
                    removeCount++;
                }
            }

            if (needRemoveList2.size() > 0) {
                dataGroupVO.getDataList().removeAll(needRemoveList2);
            }

        }

        List<ChannelDataGroupVO> needRemoveList = new ArrayList<>();
        for (ChannelDataGroupVO dataGroupVO : userDataVO.getDataList()) {
            if (CollectionUtils.isEmpty(dataGroupVO.getDataList())) {
                needRemoveList.add(dataGroupVO);
            }
        }

        if (needRemoveList.size() > 0) {
            userDataVO.getDataList().removeAll(needRemoveList);
        }

        if (removeCount > 0) {
            log.info("过滤了{}条小说数据", removeCount);
        }
    }
}
