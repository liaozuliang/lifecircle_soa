package com.idianyou.lifecircle.service;

import com.idianyou.lifecircle.dto.GetChannelDataDTO;
import com.idianyou.lifecircle.dto.GetUserDataDTO;
import com.idianyou.lifecircle.dto.SearchDTO;
import com.idianyou.lifecircle.dto.request.DelUserDataReq;
import com.idianyou.lifecircle.dto.request.GetChannelServicePortalReq;
import com.idianyou.lifecircle.dto.request.GetDetailsReq;
import com.idianyou.lifecircle.dto.request.GetUserNativeTypeReq;
import com.idianyou.lifecircle.dto.request.SetUserNativeTypeReq;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.dto.vo.ContentDetailsVO;
import com.idianyou.lifecircle.dto.vo.GetUserNativeTypeVO;
import com.idianyou.lifecircle.dto.vo.IconBtnRow;
import com.idianyou.lifecircle.dto.vo.SearchDataVO;
import com.idianyou.lifecircle.dto.vo.UnifiedSearchDataVO;
import com.idianyou.lifecircle.dto.vo.UserDataVO;

import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/6 17:00
 */
public interface AppListDubboService {

    /**
     * 获取频道数据
     * @param dto
     * @return
     */
    List<ChannelDataGroupVO> getChannelData(GetChannelDataDTO  dto);

    /**
     * 获取问答红包广告位数据
     * @param dto
     * @return
     */
    List<ChannelDataGroupVO> getQAAdData(GetChannelDataDTO dto);

    /**
     * 获取频道数据
     * @param searchDTO
     * @return
     */
    UserDataVO search(SearchDTO searchDTO);

    /**
     * 搜索
     * @param searchDTO
     * @return
     */
    SearchDataVO searchV2(SearchDTO searchDTO);

    /**
     * 统一搜索
     * @param searchDTO
     * @return
     */
    UnifiedSearchDataVO unifiedSearch(SearchDTO searchDTO);

    /**
     * 获取动态详情
     * @param req
     * @return
     */
    ContentDetailsVO getContentDetails(GetDetailsReq req);

    /**
     * 我的动态-生活动态
     * @param dto
     * @return
     */
    UserDataVO getUserData(GetUserDataDTO dto);

    /**
     * 我的动态-生活动态-删除
     * @param req
     * @return
     */
    void delUserData(DelUserDataReq req);

    /**
     * 获取用户在本地的情况
     * @param req
     * @return
     */
    GetUserNativeTypeVO getUserNativeType(GetUserNativeTypeReq req);

    /**
     * 设置用户在本地的情况
     * @param req
     */
    void setUserNativeType(SetUserNativeTypeReq req);

    /**
     * 获取频道服务入口
     * @param req
     * @return
     */
    List<IconBtnRow> getChannelServicePortal(GetChannelServicePortalReq req);
}
