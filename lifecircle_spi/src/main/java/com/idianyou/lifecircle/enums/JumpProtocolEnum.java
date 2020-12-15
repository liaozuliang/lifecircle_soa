package com.idianyou.lifecircle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 跳转协议
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/7/1 11:57
 */
@Getter
@AllArgsConstructor
public enum JumpProtocolEnum {

    LIVE_INDEX("全民直播-主页", "chigua://defaultpackage/gamecenter?eyJ0eXBlIjozMTIsInZhbHVlIjp7InJvdXRlcnBhdGgiOiIvbGl2ZS9uYXRpb25hbC9MaXZlSG9tZVBhZ2VQYXRoIiwicm91dGVycGFyYW1zIjoiIn19"),
    BOSS_LECTURE_INDEX("boss开讲-主页", "chigua://defaultpackage/gamecenter?eyJ0eXBlIjozMTIsInZhbHVlIjp7InJvdXRlcnBhdGgiOiIvbGl2ZS9saXZlSG9tZUFjdGl2aXR5UGF0aCIsInJvdXRlcnBhcmFtcyI6IiJ9fQ=="),
    ;

    private String desc;
    private String protocol;

}
