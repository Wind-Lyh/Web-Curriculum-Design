package com.community.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类（简化版）
 */
public class DateUtil {

    /**
     * 获取当前时间的格式化字符串
     * @param format 格式，如："yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的日期字符串
     */
    public static String formatCurrentTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    /**
     * 格式化日期为字符串
     * @param date 日期对象
     * @param format 格式
     * @return 格式化后的字符串
     */
    public static String formatDate(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 获取当前时间的标准格式
     * @return yyyy-MM-dd HH:mm:ss格式的当前时间
     */
    public static String getCurrentDateTime() {
        return formatCurrentTime("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取当前日期
     * @return yyyy-MM-dd格式的当前日期
     */
    public static String getCurrentDate() {
        return formatCurrentTime("yyyy-MM-dd");
    }
}