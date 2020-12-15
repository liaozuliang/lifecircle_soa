package com.idianyou.lifecircle.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @Description:
 * @version: v1.0.0
 * @author: liaozuliang
 * @date: 2020/5/7 18:18
 */
public class DateUtils {

    public static String getBetweenTime(Date date1, Date date2) {
        Long time1 = date1.getTime();
        Long time2 = date2.getTime();
        Long timeDiffer = time2 - time1;

        //一个小时内 显示分钟
        if (60000 < timeDiffer && timeDiffer <= 3600000) {
            Long minute = timeDiffer / 60000;
            return minute + "m";
        }

        //超过一个小时 小于一天的显示 小时
        if (60 * 60 * 1000 < timeDiffer && timeDiffer <= 24 * 60 * 60 * 1000) {
            Long hour = timeDiffer / 3600000;
            return hour + "h";
        }

        //超过三天的 显示日期
        if (timeDiffer > 86400000) {
            Long day = timeDiffer / 86400000;
            if (day > 3) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date1);

                return String.format("%02d-%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            }
            return day + "d";
        }

        return "";
    }
}
