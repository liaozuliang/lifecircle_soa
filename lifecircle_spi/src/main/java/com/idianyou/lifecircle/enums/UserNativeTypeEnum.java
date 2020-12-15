package com.idianyou.lifecircle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 用户在本地的情况
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/4/24 17:25
 */
@Getter
@AllArgsConstructor
public enum UserNativeTypeEnum {

    NULL(0, "未设置"),
    NATIVE(1, "本地人，在本地发展"),
    NATIVE_OUT(2, "本地人，在外地发展"),
    FRIEND_NATIVE(3, "朋友是本地人"),
    OTHER(4, "其他"),
    ;

    private Integer code;
    private String desc;

    public static UserNativeTypeEnum codeOf(Integer code) {
        for (UserNativeTypeEnum nativeTypeEnum : values()) {
            if (nativeTypeEnum.getCode().equals(code)) {
                return nativeTypeEnum;
            }
        }
        return null;
    }

}
