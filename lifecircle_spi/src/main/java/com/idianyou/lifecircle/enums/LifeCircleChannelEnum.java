package com.idianyou.lifecircle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/6 15:26
 */
@Getter
@AllArgsConstructor
public enum LifeCircleChannelEnum {

    RECOMMEND(1, "推荐"), // 搜索里面代表“全部”
    LOCAL(2, "本地"),
    LIFE(3, "生活"),
    LIVE(4, "直播"),
    AMUSEMENT(5, "娱乐"),
    DYNAMIC(6, "动态"),
    ;

    private Integer channelId;
    private String channelName;

    public static LifeCircleChannelEnum channelIdOf(Integer channelId) {
        for (LifeCircleChannelEnum channelEnum : values()) {
            if (channelEnum.getChannelId().equals(channelId)) {
                return channelEnum;
            }
        }
        return null;
    }
}
