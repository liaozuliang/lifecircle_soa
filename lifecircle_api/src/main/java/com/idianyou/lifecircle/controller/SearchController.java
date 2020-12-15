package com.idianyou.lifecircle.controller;

import com.alibaba.fastjson.JSON;
import com.idianyou.lifecircle.controller.channel.out.LifeCircleChannelOut;
import com.idianyou.lifecircle.dto.InnerRpcResponse;
import com.idianyou.lifecircle.dto.LifeCircleChannelDTO;
import com.idianyou.lifecircle.dto.SearchDTO;
import com.idianyou.lifecircle.dto.request.SearchReq;
import com.idianyou.lifecircle.dto.vo.SearchDataVO;
import com.idianyou.lifecircle.dto.vo.UserDataVO;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.exception.LifeCircleException;
import com.idianyou.lifecircle.service.AppListDubboService;
import com.idianyou.lifecircle.service.LifeCircleChannelDubboService;
import com.idianyou.lifecircle.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/6 15:38
 */
@Slf4j
@RequestMapping("/lifecircle/search")
@RestController
public class SearchController extends BaseController {

    @Autowired
    private LifeCircleChannelDubboService lifeCircleChannelDubboService;

    @Autowired
    private AppListDubboService appListDubboService;

    /**
     * 获取频道
     * @return
     */
    @RequestMapping("getChannelList")
    public Result getLifeCircleChannelList() {
        List<LifeCircleChannelOut> list = new ArrayList<>();

        try {
            InnerRpcResponse<List<LifeCircleChannelDTO>> response = lifeCircleChannelDubboService.queryAll();
            if (response != null && CollectionUtils.isNotEmpty(response.getData())) {
                List<LifeCircleChannelDTO> channelDTOS = response.getData();
                for (LifeCircleChannelDTO dto : channelDTOS) {
                    LifeCircleChannelOut out = new LifeCircleChannelOut();
                    out.setChannelId(dto.get_id());

                    if (LifeCircleChannelEnum.RECOMMEND.getChannelId().equals(dto.get_id())) {
                        out.setChannelName("全部");
                    } else {
                        out.setChannelName(dto.getChannelName());
                    }

                    list.add(out);
                }
            }
        } catch (Exception e) {
            log.error("获取搜索频道列表出错：", e);
        }

        return Result.success(list);
    }

    /**
     * 搜索
     * @param req
     * @return
     */
    @RequestMapping("doSearch")
    public Result doSearch(SearchReq req) {
        try {
            checkSearchReq(req);

            // 校验版本号

            Long userId = getLoginUserId();
            if (userId == null) {
                return Result.notLogin();
            }

            SearchDTO dto = new SearchDTO();
            dto.setUserId(userId);
            dto.setKeyword(req.getKeyword());
            dto.setChannelEnum(LifeCircleChannelEnum.channelIdOf(req.getChannelId()));
            dto.setV(req.getV());

            if (req.getLastId() != null && req.getLastId() <= 0) {
                req.setLastId(null);
            }
            dto.setLastId(req.getLastId());

            if (req.getV() != null && req.getV() >= 2) {
                SearchDataVO searchDataVO = appListDubboService.searchV2(dto);
                return Result.success(searchDataVO);
            } else {
                UserDataVO userDataVO = appListDubboService.search(dto);
                return Result.success(userDataVO);
            }
        } catch (LifeCircleException e) {
            log.error("搜索数据出错：req={}", JSON.toJSONString(req), e);
            return Result.failed(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("搜索数据出错：req={}", JSON.toJSONString(req), e);
            return Result.systemError();
        }
    }

    private void checkSearchReq(SearchReq req) {
        try {
            Assert.notNull(req, "参数不能为空");
            Assert.notNull(LifeCircleChannelEnum.channelIdOf(req.getChannelId()), "参数[channelId]错误");
            Assert.isTrue(StringUtils.isNotBlank(req.getKeyword()), "参数[keyword]错误");
            Assert.isTrue(StringUtils.isNotBlank(req.getAppVersionCode()), "参数[appVersionCode]错误");
        } catch (Exception e) {
            throw new LifeCircleException("501", e.getMessage());
        }
    }

}