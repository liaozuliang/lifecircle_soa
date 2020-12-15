package com.idianyou.lifecircle.dubbo;

import com.alibaba.fastjson.JSON;
import com.idianyou.lifecircle.dto.GetChannelDataDTO;
import com.idianyou.lifecircle.dto.SearchDTO;
import com.idianyou.lifecircle.dto.request.GetChannelServicePortalReq;
import com.idianyou.lifecircle.dto.vo.ChannelDataGroupVO;
import com.idianyou.lifecircle.dto.vo.IconBtnRow;
import com.idianyou.lifecircle.dto.vo.SearchDataVO;
import com.idianyou.lifecircle.dto.vo.UnifiedSearchDataVO;
import com.idianyou.lifecircle.dto.vo.UserDataVO;
import com.idianyou.lifecircle.entity.mongo.LifeCircleChannelServicePortalMongo;
import com.idianyou.lifecircle.enums.LifeCircleChannelEnum;
import com.idianyou.lifecircle.enums.YesNoEnum;
import com.idianyou.lifecircle.mongo.LifeCircleChannelServicePortalRepository;
import com.idianyou.lifecircle.service.AppListDubboService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/3 19:32
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AppListDubboServiceTests {

    @Autowired
    private AppListDubboService appListDubboService;

    @Autowired
    private LifeCircleChannelServicePortalRepository lifeCircleChannelServicePortalRepository;

    @Test
    public void initChannelServicePortalData() {
        LifeCircleChannelServicePortalMongo mongo = null;

        // 生活
        mongo = new LifeCircleChannelServicePortalMongo();
        mongo.setIsDelete(YesNoEnum.NO.getCode());
        mongo.setCreateTime(new Date());
        mongo.setUpdateTime(mongo.getCreateTime());
        mongo.setChannelId(LifeCircleChannelEnum.LIFE.getChannelId());
        mongo.set_id(1);
        mongo.setRowNo(1);
        mongo.setColumnNo(1);
        mongo.setName("赶集逢会");
        mongo.setIcon("http://alcache.chigua.cn/dianyou/data/redpacket/ff8080816ad97964016b69723c060010.png");
        mongo.setProtocol("chigua://defaultpackage/mini_program/entry?eyJjbGllbnRJZCI6ImNnNTVlNGE4N2VjY2E1YWM0NyIsImNsaWVudE5hbWUiOiLotbbpm4YiLCJjbGllbnRQcm9maWxlIjp7Im5hbWUiOiLlsI/nqIvluo8iLCJ2YWx1ZSI6Im1pbmktcHJvZ3JhbSJ9LCJsb2dvVXJpIjoiaHR0cDovL2FsZnMuY2hpZ3VhLmNuL2RpYW55b3UvZGF0YS9hcHBsZXQvaW1hZ2UvMjAyMDA1MDgvanllaDJ3UkdHMnBuZyJ9");
        lifeCircleChannelServicePortalRepository.save(mongo);

        mongo = new LifeCircleChannelServicePortalMongo();
        mongo.setIsDelete(YesNoEnum.NO.getCode());
        mongo.setCreateTime(new Date());
        mongo.setUpdateTime(mongo.getCreateTime());
        mongo.setChannelId(LifeCircleChannelEnum.LIFE.getChannelId());
        mongo.set_id(2);
        mongo.setRowNo(1);
        mongo.setColumnNo(2);
        mongo.setName("定制服务");
        mongo.setIcon("http://alcache.chigua.cn/dianyou/data/redpacket/ff8080816ad97964016b698df5cd0036.png");
        mongo.setProtocol("chigua://defaultpackage/mini_program/entry?eyJjbGllbnRJZCI6ImNnNTVlNGE4N2VjY2E1YWM0NyIsImNsaWVudE5hbWUiOiLotbbpm4YiLCJjbGllbnRQcm9maWxlIjp7Im5hbWUiOiLlsI/nqIvluo8iLCJ2YWx1ZSI6Im1pbmktcHJvZ3JhbSJ9LCJsb2dvVXJpIjoiaHR0cDovL2FsZnMuY2hpZ3VhLmNuL2RpYW55b3UvZGF0YS9hcHBsZXQvaW1hZ2UvMjAyMDA1MDgvanllaDJ3UkdHMnBuZyJ9");
        lifeCircleChannelServicePortalRepository.save(mongo);

        mongo = new LifeCircleChannelServicePortalMongo();
        mongo.setIsDelete(YesNoEnum.NO.getCode());
        mongo.setCreateTime(new Date());
        mongo.setUpdateTime(mongo.getCreateTime());
        mongo.setChannelId(LifeCircleChannelEnum.LIFE.getChannelId());
        mongo.set_id(3);
        mongo.setRowNo(1);
        mongo.setColumnNo(3);
        mongo.setName("生活服务");
        mongo.setIcon("http://alcache.chigua.cn/dianyou/data/redpacket/ff8080816ad97964016b69858a13002d.png");
        mongo.setProtocol("chigua://defaultpackage/mini_program/entry?eyJjbGllbnRJZCI6ImNnNTVlNGE4N2VjY2E1YWM0NyIsImNsaWVudE5hbWUiOiLotbbpm4YiLCJjbGllbnRQcm9maWxlIjp7Im5hbWUiOiLlsI/nqIvluo8iLCJ2YWx1ZSI6Im1pbmktcHJvZ3JhbSJ9LCJsb2dvVXJpIjoiaHR0cDovL2FsZnMuY2hpZ3VhLmNuL2RpYW55b3UvZGF0YS9hcHBsZXQvaW1hZ2UvMjAyMDA1MDgvanllaDJ3UkdHMnBuZyJ9");
        lifeCircleChannelServicePortalRepository.save(mongo);

        mongo = new LifeCircleChannelServicePortalMongo();
        mongo.setIsDelete(YesNoEnum.NO.getCode());
        mongo.setCreateTime(new Date());
        mongo.setUpdateTime(mongo.getCreateTime());
        mongo.setChannelId(LifeCircleChannelEnum.LIFE.getChannelId());
        mongo.set_id(4);
        mongo.setRowNo(1);
        mongo.setColumnNo(4);
        mongo.setName("商城");
        mongo.setIcon("http://alcache.chigua.cn/dianyou/data/redpacket/ff8080816ad97964016b697f930a0025.png");
        mongo.setProtocol("chigua://defaultpackage/mini_program/entry?eyJjbGllbnRJZCI6ImNnYjBkZmQxNTQ3ZTBiMTY1YiIsImNsaWVudE5hbWUiOiLllYbln44iLCJjbGllbnRQcm9maWxlIjp7Im5hbWUiOiLlsI/nqIvluo8iLCJ2YWx1ZSI6Im1pbmktcHJvZ3JhbSJ9LCJsb2dvVXJpIjoiaHR0cDovL2FsY2FjaGUuY2hpZ3VhLmNuL2RpYW55b3UvZGF0YS9pY29ucy9taW5pYXBwcy9neV9zaG9wcGluZy5wbmcifQ==");
        lifeCircleChannelServicePortalRepository.save(mongo);

        // 直播
        mongo = new LifeCircleChannelServicePortalMongo();
        mongo.setIsDelete(YesNoEnum.NO.getCode());
        mongo.setCreateTime(new Date());
        mongo.setUpdateTime(mongo.getCreateTime());
        mongo.setChannelId(LifeCircleChannelEnum.LIVE.getChannelId());
        mongo.set_id(5);
        mongo.setRowNo(1);
        mongo.setColumnNo(1);
        mongo.setName("全民直播");
        mongo.setIcon("http://alcache.chigua.cn/dianyou/data/redpacket/ff8080816ad97964016b69723c060010.png");
        mongo.setProtocol("chigua://defaultpackage/mini_program/entry?eyJjbGllbnRJZCI6ImNnNTVlNGE4N2VjY2E1YWM0NyIsImNsaWVudE5hbWUiOiLotbbpm4YiLCJjbGllbnRQcm9maWxlIjp7Im5hbWUiOiLlsI/nqIvluo8iLCJ2YWx1ZSI6Im1pbmktcHJvZ3JhbSJ9LCJsb2dvVXJpIjoiaHR0cDovL2FsZnMuY2hpZ3VhLmNuL2RpYW55b3UvZGF0YS9hcHBsZXQvaW1hZ2UvMjAyMDA1MDgvanllaDJ3UkdHMnBuZyJ9");
        lifeCircleChannelServicePortalRepository.save(mongo);

        mongo = new LifeCircleChannelServicePortalMongo();
        mongo.setIsDelete(YesNoEnum.NO.getCode());
        mongo.setCreateTime(new Date());
        mongo.setUpdateTime(mongo.getCreateTime());
        mongo.setChannelId(LifeCircleChannelEnum.LIVE.getChannelId());
        mongo.set_id(6);
        mongo.setRowNo(1);
        mongo.setColumnNo(2);
        mongo.setName("boss开讲");
        mongo.setIcon("http://alcache.chigua.cn/dianyou/data/redpacket/ff8080816ad97964016b698df5cd0036.png");
        mongo.setProtocol("chigua://defaultpackage/mini_program/entry?eyJjbGllbnRJZCI6ImNnNTVlNGE4N2VjY2E1YWM0NyIsImNsaWVudE5hbWUiOiLotbbpm4YiLCJjbGllbnRQcm9maWxlIjp7Im5hbWUiOiLlsI/nqIvluo8iLCJ2YWx1ZSI6Im1pbmktcHJvZ3JhbSJ9LCJsb2dvVXJpIjoiaHR0cDovL2FsZnMuY2hpZ3VhLmNuL2RpYW55b3UvZGF0YS9hcHBsZXQvaW1hZ2UvMjAyMDA1MDgvanllaDJ3UkdHMnBuZyJ9");
        lifeCircleChannelServicePortalRepository.save(mongo);

        // 娱乐
        mongo = new LifeCircleChannelServicePortalMongo();
        mongo.setIsDelete(YesNoEnum.NO.getCode());
        mongo.setCreateTime(new Date());
        mongo.setUpdateTime(mongo.getCreateTime());
        mongo.setChannelId(LifeCircleChannelEnum.AMUSEMENT.getChannelId());
        mongo.set_id(7);
        mongo.setRowNo(1);
        mongo.setColumnNo(1);
        mongo.setName("社群");
        mongo.setIcon("http://alcache.chigua.cn/dianyou/data/redpacket/ff8080816ad97964016b69723c060010.png");
        mongo.setProtocol("chigua://defaultpackage/mini_program/entry?eyJjbGllbnRJZCI6ImNnNTVlNGE4N2VjY2E1YWM0NyIsImNsaWVudE5hbWUiOiLotbbpm4YiLCJjbGllbnRQcm9maWxlIjp7Im5hbWUiOiLlsI/nqIvluo8iLCJ2YWx1ZSI6Im1pbmktcHJvZ3JhbSJ9LCJsb2dvVXJpIjoiaHR0cDovL2FsZnMuY2hpZ3VhLmNuL2RpYW55b3UvZGF0YS9hcHBsZXQvaW1hZ2UvMjAyMDA1MDgvanllaDJ3UkdHMnBuZyJ9");
        lifeCircleChannelServicePortalRepository.save(mongo);

        mongo = new LifeCircleChannelServicePortalMongo();
        mongo.setIsDelete(YesNoEnum.NO.getCode());
        mongo.setCreateTime(new Date());
        mongo.setUpdateTime(mongo.getCreateTime());
        mongo.setChannelId(LifeCircleChannelEnum.AMUSEMENT.getChannelId());
        mongo.set_id(8);
        mongo.setRowNo(1);
        mongo.setColumnNo(2);
        mongo.setName("斗地主");
        mongo.setIcon("http://alcache.chigua.cn/dianyou/data/redpacket/ff8080816ad97964016b698df5cd0036.png");
        mongo.setProtocol("chigua://defaultpackage/mini_program/entry?eyJjbGllbnRJZCI6ImNnNTVlNGE4N2VjY2E1YWM0NyIsImNsaWVudE5hbWUiOiLotbbpm4YiLCJjbGllbnRQcm9maWxlIjp7Im5hbWUiOiLlsI/nqIvluo8iLCJ2YWx1ZSI6Im1pbmktcHJvZ3JhbSJ9LCJsb2dvVXJpIjoiaHR0cDovL2FsZnMuY2hpZ3VhLmNuL2RpYW55b3UvZGF0YS9hcHBsZXQvaW1hZ2UvMjAyMDA1MDgvanllaDJ3UkdHMnBuZyJ9");
        lifeCircleChannelServicePortalRepository.save(mongo);

        mongo = new LifeCircleChannelServicePortalMongo();
        mongo.setIsDelete(YesNoEnum.NO.getCode());
        mongo.setCreateTime(new Date());
        mongo.setUpdateTime(mongo.getCreateTime());
        mongo.setChannelId(LifeCircleChannelEnum.AMUSEMENT.getChannelId());
        mongo.set_id(9);
        mongo.setRowNo(1);
        mongo.setColumnNo(3);
        mongo.setName("麻将");
        mongo.setIcon("http://alcache.chigua.cn/dianyou/data/redpacket/ff8080816ad97964016b69858a13002d.png");
        mongo.setProtocol("chigua://defaultpackage/mini_program/entry?eyJjbGllbnRJZCI6ImNnNTVlNGE4N2VjY2E1YWM0NyIsImNsaWVudE5hbWUiOiLotbbpm4YiLCJjbGllbnRQcm9maWxlIjp7Im5hbWUiOiLlsI/nqIvluo8iLCJ2YWx1ZSI6Im1pbmktcHJvZ3JhbSJ9LCJsb2dvVXJpIjoiaHR0cDovL2FsZnMuY2hpZ3VhLmNuL2RpYW55b3UvZGF0YS9hcHBsZXQvaW1hZ2UvMjAyMDA1MDgvanllaDJ3UkdHMnBuZyJ9");
        lifeCircleChannelServicePortalRepository.save(mongo);

        mongo = new LifeCircleChannelServicePortalMongo();
        mongo.setIsDelete(YesNoEnum.NO.getCode());
        mongo.setCreateTime(new Date());
        mongo.setUpdateTime(mongo.getCreateTime());
        mongo.setChannelId(LifeCircleChannelEnum.AMUSEMENT.getChannelId());
        mongo.set_id(10);
        mongo.setRowNo(1);
        mongo.setColumnNo(4);
        mongo.setName("更多");
        mongo.setIcon("http://alcache.chigua.cn/dianyou/data/redpacket/ff8080816ad97964016b697f930a0025.png");
        mongo.setProtocol("chigua://defaultpackage/mini_program/entry?eyJjbGllbnRJZCI6ImNnYjBkZmQxNTQ3ZTBiMTY1YiIsImNsaWVudE5hbWUiOiLllYbln44iLCJjbGllbnRQcm9maWxlIjp7Im5hbWUiOiLlsI/nqIvluo8iLCJ2YWx1ZSI6Im1pbmktcHJvZ3JhbSJ9LCJsb2dvVXJpIjoiaHR0cDovL2FsY2FjaGUuY2hpZ3VhLmNuL2RpYW55b3UvZGF0YS9pY29ucy9taW5pYXBwcy9neV9zaG9wcGluZy5wbmcifQ==");
        lifeCircleChannelServicePortalRepository.save(mongo);
    }

    @Test
    public void testGetChannelServicePortal() {
        GetChannelServicePortalReq req = new GetChannelServicePortalReq();
        req.setChannelId(LifeCircleChannelEnum.LIFE.getChannelId());
        List<IconBtnRow> dataList = appListDubboService.getChannelServicePortal(req);
        log.info("======testGetChannelServicePortal======={}", JSON.toJSONString(dataList));

        req.setChannelId(LifeCircleChannelEnum.LIVE.getChannelId());
        dataList = appListDubboService.getChannelServicePortal(req);
        log.info("======testGetChannelServicePortal======={}", JSON.toJSONString(dataList));

        req.setChannelId(LifeCircleChannelEnum.AMUSEMENT.getChannelId());
        dataList = appListDubboService.getChannelServicePortal(req);
        log.info("======testGetChannelServicePortal======={}", JSON.toJSONString(dataList));
    }

    @Test
    public void testGetChannelData() {
        GetChannelDataDTO dto = new GetChannelDataDTO();
        dto.setUserId(120473L);
        dto.setReqNo(System.currentTimeMillis() + "");
        dto.setChannelEnum(LifeCircleChannelEnum.RECOMMEND);

        List<ChannelDataGroupVO> dataList = appListDubboService.getChannelData(dto);
        log.info("=======testGetChannelData========={}", JSON.toJSONString(dataList));
    }

    @Test
    public void testGetQAAdData() {
        GetChannelDataDTO dto = new GetChannelDataDTO();
        dto.setUserId(120473L);
        dto.setReqNo(System.currentTimeMillis() + "");
        dto.setChannelEnum(LifeCircleChannelEnum.RECOMMEND);

        List<ChannelDataGroupVO> dataList = appListDubboService.getQAAdData(dto);
        log.info("=======testGetQAAdData========={}", JSON.toJSONString(dataList));
    }

    @Test
    public void testSearch() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setUserId(15514633L);
        searchDTO.setChannelEnum(LifeCircleChannelEnum.RECOMMEND);
        searchDTO.setKeyword("转发生活圈");
        UserDataVO userDataVO = appListDubboService.search(searchDTO);
        log.info("==========testSearch=========={}", JSON.toJSONString(userDataVO));
    }

    @Test
    public void testSearch2() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setUserId(15514633L);
        searchDTO.setChannelEnum(LifeCircleChannelEnum.RECOMMEND);
        searchDTO.setKeyword("测试平行线水电费感觉无人机奖励");
        searchDTO.setPageSize(100);
        SearchDataVO searchDataVO = appListDubboService.searchV2(searchDTO);
        log.info("==========testSearch2=========={}", JSON.toJSONString(searchDataVO));
    }

    @Test
    public void testUnifiedSearch() {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setUserId(15514633L);
        searchDTO.setChannelEnum(LifeCircleChannelEnum.RECOMMEND);
        searchDTO.setKeyword("测试平行线水电费感觉无人机奖励");
        UnifiedSearchDataVO searchDataVO = appListDubboService.unifiedSearch(searchDTO);
        log.info("==========testUnifiedSearch=========={}", JSON.toJSONString(searchDataVO));
    }
}
