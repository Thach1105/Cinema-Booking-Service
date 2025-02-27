package vn.thachnn.util;

import vn.thachnn.common.DayType;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class DateTimeUtils {

    public static DayType checkDayType(LocalDateTime time){
        DayOfWeek dayOfWeek = time.getDayOfWeek();
        if(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY){
            return DayType.WEEKEND;
        } else return DayType.WEEKDAY;
    }
}
