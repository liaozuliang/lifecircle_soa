package com.idianyou.lifecircle;

import com.alibaba.fastjson.JSON;
import com.idianyou.lifecircle.dto.InnerRpcResponse;
import com.idianyou.lifecircle.dto.LifeCircleChannelDTO;
import com.idianyou.lifecircle.service.LifeCircleChannelDubboService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LifeCircleChannelTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private LifeCircleChannelDubboService circleChannelDubboService;

    @Test
    public void save() {
        LifeCircleChannelDTO dto = new LifeCircleChannelDTO();
        dto.setChannelName("推荐");
        dto.setCreateTime(new Date());
        dto.setUpdateTime(new Date());
        dto.setSortNum(1);
        dto.setShowType(2);
        dto.setFromCode("lifeCircle");
        dto.setRemark("推荐频道");
        dto.setCurrType(1);
        dto.setOperUserId(0);
        dto.setOperUserName("超级管理员");

        InnerRpcResponse<LifeCircleChannelDTO> response = circleChannelDubboService.save(dto);
        System.out.println(JSON.toJSON(response));
        System.out.println("=================");
    }

    @Test
    public void bulkSave() {
        LifeCircleChannelDTO dto = new LifeCircleChannelDTO();
        dto.setChannelName("推荐");
        dto.setCreateTime(new Date());
        dto.setUpdateTime(new Date());
        dto.setSortNum(1);
        dto.setShowType(2);
        dto.setFromCode("lifeCircle");
        dto.setRemark("推荐频道");
        dto.setCurrType(1);
        dto.setOperUserId(0);
        dto.setOperUserName("超级管理员");

        LifeCircleChannelDTO dto2 = new LifeCircleChannelDTO();
        dto2.setChannelName("热点");
        dto2.setCreateTime(new Date());
        dto2.setUpdateTime(new Date());
        dto2.setSortNum(2);
        dto2.setShowType(2);
        dto2.setFromCode("lifeCircle");
        dto2.setRemark("热点频道");
        dto2.setCurrType(1);
        dto2.setOperUserId(0);
        dto2.setOperUserName("超级管理员");

        LifeCircleChannelDTO dto3 = new LifeCircleChannelDTO();
        dto3.setChannelName("生活");
        dto3.setCreateTime(new Date());
        dto3.setUpdateTime(new Date());
        dto3.setSortNum(3);
        dto3.setShowType(2);
        dto3.setFromCode("lifeCircle");
        dto3.setRemark("其它频道");
        dto3.setCurrType(1);
        dto3.setOperUserId(0);
        dto3.setOperUserName("超级管理员");

        LifeCircleChannelDTO dto4 = new LifeCircleChannelDTO();
        dto4.setChannelName("直播");
        dto4.setCreateTime(new Date());
        dto4.setUpdateTime(new Date());
        dto4.setSortNum(4);
        dto4.setShowType(2);
        dto4.setFromCode("lifeCircle");
        dto4.setRemark("其它频道");
        dto4.setCurrType(1);
        dto4.setOperUserId(0);
        dto4.setOperUserName("超级管理员");

        LifeCircleChannelDTO dto5 = new LifeCircleChannelDTO();
        dto5.setChannelName("娱乐");
        dto5.setCreateTime(new Date());
        dto5.setUpdateTime(new Date());
        dto5.setSortNum(5);
        dto5.setShowType(2);
        dto5.setFromCode("lifeCircle");
        dto5.setRemark("娱乐频道");
        dto5.setCurrType(1);
        dto5.setOperUserId(0);
        dto5.setOperUserName("超级管理员");

        LifeCircleChannelDTO dto6 = new LifeCircleChannelDTO();
        dto6.setChannelName("动态");
        dto6.setCreateTime(new Date());
        dto6.setUpdateTime(new Date());
        dto6.setSortNum(6);
        dto6.setShowType(2);
        dto6.setFromCode("lifeCircle");
        dto6.setRemark("动态频道");
        dto6.setCurrType(1);
        dto6.setOperUserId(0);
        dto6.setOperUserName("超级管理员");

        LifeCircleChannelDTO dto7 = new LifeCircleChannelDTO();
        dto7.setChannelName("其它");
        dto7.setCreateTime(new Date());
        dto7.setUpdateTime(new Date());
        dto7.setSortNum(7);
        dto7.setShowType(2);
        dto7.setFromCode("lifeCircle");
        dto7.setRemark("其它频道");
        dto7.setCurrType(1);
        dto7.setOperUserId(0);
        dto7.setOperUserName("超级管理员");

        List<LifeCircleChannelDTO> dtos = new ArrayList<>();
        dtos.add(dto);
        dtos.add(dto2);
        dtos.add(dto3);
        dtos.add(dto4);
        dtos.add(dto5);
        dtos.add(dto6);
        dtos.add(dto7);

        InnerRpcResponse<Integer> response = circleChannelDubboService.bulkSave(dtos);
        System.out.println(JSON.toJSON(response));
        System.out.println("=================");
    }

    @Test
    public void queryById() {

        InnerRpcResponse<LifeCircleChannelDTO> response = circleChannelDubboService.queryById(1);
        System.out.println(JSON.toJSON(response));
        System.out.println("=================");
    }

    @Test
    public void queryByIds() {

        List<Integer> _ids = new ArrayList<>();
        _ids.add(1);
        _ids.add(2);
        _ids.add(3);

        InnerRpcResponse<List<LifeCircleChannelDTO>> response = circleChannelDubboService.queryByIds(_ids);
        System.out.println(JSON.toJSON(response));
        System.out.println("=================");
    }

    @Test
    public void queryAll() {

        InnerRpcResponse<List<LifeCircleChannelDTO>> response = circleChannelDubboService.queryAll();
        System.out.println(JSON.toJSON(response));
        System.out.println("=================");
    }

}
