package cn.nono.ridertravel.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/4/25.
 */
public class SimpleDateUtil {
    public static String YMD = "yyyy-MM-dd";
    public static String YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static String formatDate(String format,long date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(new Date(date));
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatDateYMDHMS(Long date) {
        if(null == date || date.longValue() == 0l)
            return "";
        return formatDate(YMDHMS,date);
    }
}
