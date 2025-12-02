package com.community.util;

import java.util.regex.Pattern;

/**
 * 字符串处理工具类
 * 提供常用的字符串操作方法
 */
public class StringUtil {

    //判断字符串是否为空或null，为空返回true，否则返回false
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    //判断字符串是否不为空，不为空返回true，否则返回false
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    //验证邮箱格式
    public static boolean isEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        return Pattern.matches(emailRegex, email);
    }

    //验证手机号格式，格式正确返回true
    public static boolean isPhone(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        String phoneRegex = "^1[3-9]\\d{9}$";
        return Pattern.matches(phoneRegex, phone);
    }

    //验证用户名格式（字母数字下划线，3-20位）格式正确返回true
    public static boolean isUsername(String username) {
        if (isEmpty(username)) {
            return false;
        }
        String usernameRegex = "^[a-zA-Z0-9_]{3,20}$";
        return Pattern.matches(usernameRegex, username);
    }

    /**
     * 转义HTML特殊字符，防止XSS攻击
     * @param input 输入字符串
     * @return 转义后的字符串
     */
    public static String escapeHtml(String input) {
        if (isEmpty(input)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#39;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    //去除字符串两端的空格，如果为null则返回空字符串
    public static String trim(String str) {
        return str == null ? "" : str.trim();
    }

    /**
     * 限制字符串长度，超出部分用省略号表示
     * @param str 输入字符串
     * @param maxLength 最大长度
     * @return 截取后的字符串
     */
    public static String limitLength(String str, int maxLength) {
        if (isEmpty(str) || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }

    //生成指定长度的随机字符串
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    //检查密码强度，强度等级：0-弱，1-中，2-强
    public static int checkPasswordStrength(String password) {
        if (isEmpty(password) || password.length() < 6) {
            return 0;
        }
        int strength = 0;
        // 检查是否包含数字
        if (password.matches(".*\\d.*")) {
            strength++;
        }
        // 检查是否包含小写字母
        if (password.matches(".*[a-z].*")) {
            strength++;
        }
        // 检查是否包含大写字母
        if (password.matches(".*[A-Z].*")) {
            strength++;
        }
        // 检查是否包含特殊字符
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            strength++;
        }
        // 根据长度和复杂度判断强度
        if (password.length() >= 8 && strength >= 3) {
            return 2; // 强
        } else if (password.length() >= 6 && strength >= 2) {
            return 1; // 中
        } else {
            return 0; // 弱
        }
    }
}