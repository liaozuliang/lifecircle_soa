package com.idianyou.lifecircle.enums;

import com.idianyou.lifecircle.exception.LifeCircleException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

/**
 * @Description: 时间段
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/6/9 11:41
 */
@Getter
@AllArgsConstructor
public enum TimeDescEnum {

    MORNING("早上", LocalTime.parse("00:00:00"), LocalTime.parse("10:30:00")),
    NOONING("中午", LocalTime.parse("10:30:00"), LocalTime.parse("17:00:00")),
    AFTERNOON("下午", LocalTime.parse("17:00:00"), LocalTime.MAX);

    private String desc;
    private LocalTime beginTime;
    private LocalTime endTime;

    public static TimeDescEnum getCurrentTimeDescEnum() {
        LocalTime now = LocalTime.now();
        for (TimeDescEnum timeDescEnum : values()) {
            if (now.isAfter(timeDescEnum.getBeginTime()) && !now.isAfter(timeDescEnum.getEndTime())) {
                return timeDescEnum;
            }
        }
        throw new LifeCircleException("获取TimeDescEnum出错");
    }

    public static TimeDescEnum getTestTimeDescEnum() {
        int currentMinute = LocalTime.now().getMinute();
        if (currentMinute >= 0 && currentMinute < 20) {
            return TimeDescEnum.MORNING;
        } else if (currentMinute >= 20 && currentMinute < 40) {
            return TimeDescEnum.NOONING;
        } else {
            return TimeDescEnum.AFTERNOON;
        }
    }

}
