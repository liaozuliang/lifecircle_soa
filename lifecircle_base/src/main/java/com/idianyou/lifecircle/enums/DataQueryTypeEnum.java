package com.idianyou.lifecircle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 数据查询类型
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/28 18:58
 */
@Getter
@AllArgsConstructor
public enum DataQueryTypeEnum {

    MARKET_FARMER("赶集农户", DataTypeEnum.MARKET, ShowTypeEnum.MARKET_COMMODITY, false),
    LEARN_MARKET_FARMER("机器学习-赶集农户", DataTypeEnum.MARKET, ShowTypeEnum.MARKET_COMMODITY, false),
    MARKET_LIVE("赶集直播", DataTypeEnum.MARKET, ShowTypeEnum.MARKET_LIVE, false),
    MARKET_MERCHANT("赶集商户", DataTypeEnum.MARKET, ShowTypeEnum.MARKET_LIFE_SERVICE, true),
    LEARN_MARKET_MERCHANT("机器学习-赶集商户", DataTypeEnum.MARKET, ShowTypeEnum.MARKET_LIFE_SERVICE, true),
    MARKET_PRODUCT("赶集商品", DataTypeEnum.MARKET, ShowTypeEnum.MARKET_PRODUCT, true),
    LEARN_MARKET_PRODUCT("机器学习-赶集商品", DataTypeEnum.MARKET, ShowTypeEnum.MARKET_PRODUCT, true),
    CUSTOMIZED_SERVICE("定制服务", DataTypeEnum.MARKET, ShowTypeEnum.MARKET_CUSTOMIZED_SERVICE, false),
    CHI_GUA_MALL("吃瓜商城", DataTypeEnum.CHIGUA_MALL, ShowTypeEnum.CHIGUA_MALL_SERVICE_DYNAMIC, true),
    WONDERFUL_MOMENTS("精彩瞬间", DataTypeEnum.WONDERFUL_MOMENTS, ShowTypeEnum.WONDERFUL_MOMENTS_SERVICE_DYNAMIC, false),
    K_SONG("K歌之王", DataTypeEnum.K_SONG_KING, ShowTypeEnum.K_SONG_KING_SERVICE_DYNAMIC, false),
    BEST_DEBATER("最佳辩手", DataTypeEnum.BEST_DEBATER, ShowTypeEnum.BEST_DEBATER_SERVICE_DYNAMIC, false),
    LIVE("全民直播", DataTypeEnum.LIVE_FOR_ALL, ShowTypeEnum.LIVE_SERVICE_DYNAMIC, false),
    BOSS_LECTURE("BOSS开讲", DataTypeEnum.BOSS_LECTURE, ShowTypeEnum.BOSS_LECTURE_SERVICE_DYNAMIC, false),
    PERSONAL("个人动态", DataTypeEnum.FRIEND_STYLE_DATA, ShowTypeEnum.FRIEND_STYLE, false),
    PERSONAL_LIVE("个人直播", DataTypeEnum.MARKET, ShowTypeEnum.MARKET_LIVE, false),
    FORM_COMPANY("开公司", DataTypeEnum.FORM_COMPANY, ShowTypeEnum.FORM_COMPANY_SERVICE_DYNAMIC, false),
    NOVEL("小说", DataTypeEnum.NOVEL, ShowTypeEnum.NOVEL_SERVICE_DYNAMIC, true),
    ;

    private String desc;
    private DataTypeEnum dataTypeEnum;
    private ShowTypeEnum showTypeEnum;
    private boolean isCategoryQuery;

}
