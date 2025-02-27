package vn.thachnn.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

public class TransCodeGenerator {

    public static String generateTransCode(String format, int length){
        return getCurrentTimeString(format) + "_" + getRandomNumber(length);
    }

    public static String getCurrentTimeString(String format){
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    public static String getRandomNumber(int len){

        long nanoTime = System.nanoTime();
        String nanoTimeStr = String.valueOf(nanoTime);

        Random random = new Random();
        StringBuilder randomStr = new StringBuilder();
        for (int i = 0; i < len - nanoTimeStr.length(); i++) {
            randomStr.append(random.nextInt(10)); // Chỉ lấy số ngẫu nhiên
        }

        return nanoTimeStr.substring(nanoTimeStr.length() - (len - randomStr.length())) + randomStr.toString();
    }
}
