package com.idianyou.lifecircle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 前端展示类型
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/24 17:25
 */
@Getter
@AllArgsConstructor
public enum ShowTypeEnum {

    FRIEND_STYLE(100000, "原朋友圈展示样式", ContentTypeEnum.PERSONAL),

    WONDERFUL_MOMENTS_SHARE(100101, "精彩瞬间-分享形式(支持动态数据)", ContentTypeEnum.PERSONAL),
    WONDERFUL_MOMENTS_SERVICE_DYNAMIC(100102, "精彩瞬间-服务动态形式", ContentTypeEnum.SERVICE),

    LIVE_SHARE(100201, "全民直播-分享形式(支持动态数据)", ContentTypeEnum.PERSONAL),
    LIVE_SERVICE_DYNAMIC(100202, "全民直播-服务动态形式", ContentTypeEnum.SERVICE),

    BOSS_LECTURE_SHARE(100301, "BOSS开讲-分享形式(支持动态数据)", ContentTypeEnum.PERSONAL),
    BOSS_LECTURE_SERVICE_DYNAMIC(100302, "BOSS开讲-服务动态形式", ContentTypeEnum.SERVICE),

    K_SONG_KING_SHARE(100401, "K歌之王-分享形式(支持动态数据)", ContentTypeEnum.PERSONAL),
    K_SONG_KING_SERVICE_DYNAMIC(100402, "K歌之王-服务动态形式", ContentTypeEnum.SERVICE),

    BEST_DEBATER_SHARE(100501, "最佳辩手-分享形式(支持动态数据)", ContentTypeEnum.PERSONAL),
    BEST_DEBATER_SERVICE_DYNAMIC(100502, "最佳辩手-服务动态形式", ContentTypeEnum.SERVICE),

    FORM_COMPANY_SHARE(100601, "开公司-分享形式(支持动态数据)", ContentTypeEnum.PERSONAL),
    FORM_COMPANY_SERVICE_DYNAMIC(100602, "开公司-服务动态形式", ContentTypeEnum.SERVICE),

    APPLY_JOB_SHARE(100701, "找工作-分享形式(支持动态数据)", ContentTypeEnum.PERSONAL),
    APPLY_JOB_SERVICE_DYNAMIC(100702, "找工作-服务动态形式", ContentTypeEnum.SERVICE),

    CIPHER_REDPACKET_SHARE(100801, "暗号红包-分享形式(支持动态数据)", ContentTypeEnum.PERSONAL),
    CIPHER_REDPACKET_SERVICE_DYNAMIC(100802, "暗号红包-服务动态形式", ContentTypeEnum.SERVICE),

    MERCHANT_SHARE(100901, "商户-分享形式(支持动态数据)", ContentTypeEnum.PERSONAL),
    MERCHANT_SERVICE_DYNAMIC(100902, "商户-服务动态形式", ContentTypeEnum.SERVICE),

    MARKET_SHARE(101001, "赶集-分享形式(支持动态数据)", ContentTypeEnum.PERSONAL),
    MARKET_COMMODITY(101002, "赶集-农户-服务动态形式", ContentTypeEnum.SERVICE),
    MARKET_LIFE_SERVICE(101003, "赶集-商户-服务动态形式", ContentTypeEnum.SERVICE),
    MARKET_LIVE(101004, "赶集-直播-服务动态形式", ContentTypeEnum.SERVICE),
    MARKET_PRODUCT(101005, "赶集-商品-服务动态形式", ContentTypeEnum.SERVICE),
    MARKET_CUSTOMIZED_SERVICE(101006, "赶集-定制服务", ContentTypeEnum.SERVICE),

    LANDLORDS_SHARE(101101, "斗地主-分享形式(支持动态数据)", ContentTypeEnum.PERSONAL),
    LANDLORDS_SERVICE_DYNAMIC(101102, "斗地主-服务动态形式", ContentTypeEnum.SERVICE),

    GUESS_WHO_I_AM_SHARE(101201, "猜猜我是谁-分享形式(支持动态数据)", ContentTypeEnum.PERSONAL),
    GUESS_WHO_I_AM_SERVICE_DYNAMIC(101202, "猜猜我是谁-服务动态形式", ContentTypeEnum.SERVICE),

    CHIGUA_MALL_SERVICE_DYNAMIC(101302, "吃瓜商城-服务动态形式", ContentTypeEnum.SERVICE),

    NOVEL_SERVICE_DYNAMIC(101402, "小说-服务动态形式", ContentTypeEnum.SERVICE),
    ;

    private Integer type;
    private String desc;
    private ContentTypeEnum contentTypeEnum;

    public static ShowTypeEnum typeOf(Integer type) {
        for (ShowTypeEnum typeEnum : values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }
}
