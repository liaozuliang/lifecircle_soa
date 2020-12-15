package com.idianyou.lifecircle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 生活圈数据类型
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/24 17:25
 */
@Getter
@AllArgsConstructor
public enum DataTypeEnum {

    FRIEND_STYLE_DATA(10000, "原朋友圈展示样式的数据", ""),
    WONDERFUL_MOMENTS(1, "主群-精彩瞬间", "cg84e4465ee3d42c57"),
    BOSS_LECTURE(2, "BOSS开讲", "cg028ca67de41c70a6"),
    K_SONG_KING(3, "K歌之王", "cg04b83ce323598089"),
    LIVE_FOR_ALL(4, "全民直播", "cg958106ce5eec54e6"),
    FORM_COMPANY(5, "开公司", "cg460347de43168587"),
    APPLY_JOB(6, "找工作", "cg6f977447a87ebed0"),
    CIPHER_REDPACKET(7, "暗号红包", "cg5f0316a7f7327827"),
    BEST_DEBATER(8, "最佳辩手", "cg174331137d692baa"),
    LANDLORDS(9, "斗地主", ""),
    GUESS_WHO_I_AM(10, "猜猜我是谁", "cgf585337b9365f266"),
    MERCHANT(11, "商户", ""),
    MARKET(12, "赶集", "cg55e4a87ecca5ac47"),
    CHIGUA_MALL(13, "吃瓜商城", "cgb0dfd1547e0b165b"),
    NOVEL(14, "小说", ""),
    ;

    private Integer type;
    private String desc;
    private String clientId;

    public static DataTypeEnum typeOf(Integer type) {
        for (DataTypeEnum typeEnum : values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }
}
