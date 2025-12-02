package com.community.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 安全工具类
 * 提供密码加密、验证等安全相关功能
 */
public class SecurityUtil {

    // 密码加密的盐值（固定值，简化实现）
    private static final String SALT = "community_salt_2024";

    /**
     * MD5加密（加盐）
     * @param input 原始字符串
     * @return 加密后的字符串
     */
    public static String md5WithSalt(String input) {
        try {
            // 加盐
            String saltedInput = input + SALT;

            // 创建MD5消息摘要
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 计算哈希值
            byte[] hashBytes = md.digest(saltedInput.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("MD5加密算法不存在: " + e.getMessage());
            // 如果MD5不可用，使用简单的Base64编码
            return Base64.getEncoder().encodeToString((input + SALT).getBytes());
        }
    }

    /**
     * 密码加密（用于用户注册和登录验证）
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String password) {
        return md5WithSalt(password);
    }

    /**
     * 验证密码
     * @param inputPassword 用户输入的密码
     * @param storedPassword 数据库中存储的加密密码
     * @return 验证通过返回true，否则返回false
     */
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        if (inputPassword == null || storedPassword == null) {
            return false;
        }
        String encryptedInput = encryptPassword(inputPassword);
        return encryptedInput.equals(storedPassword);
    }

    /**
     * 简单的Base64编码
     * @param input 原始字符串
     * @return Base64编码后的字符串
     */
    public static String base64Encode(String input) {
        if (input == null) {
            return "";
        }
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    /**
     * Base64解码
     * @param encoded Base64编码字符串
     * @return 解码后的字符串
     */
    public static String base64Decode(String encoded) {
        if (encoded == null) {
            return "";
        }
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encoded);
            return new String(decodedBytes);
        } catch (IllegalArgumentException e) {
            System.err.println("Base64解码失败: " + e.getMessage());
            return "";
        }
    }

    /**
     * 生成简单的token（用于记住我功能）
     * @param username 用户名
     * @return token字符串
     */
    public static String generateToken(String username) {
        if (username == null) {
            return "";
        }
        String rawToken = username + "_" + System.currentTimeMillis() + "_" + SALT;
        return md5WithSalt(rawToken);
    }

    /**
     * 简单的SQL注入检查
     * @param input 用户输入
     * @return 如果包含危险字符返回true，否则返回false
     */
    public static boolean containsSqlInjection(String input) {
        if (isEmpty(input)) {
            return false;
        }

        // 检查常见的SQL注入关键字
        String[] sqlKeywords = {
                "select", "insert", "update", "delete", "drop", "truncate",
                "union", "join", "where", "or", "and", "--", ";", "'", "\""
        };

        String lowerInput = input.toLowerCase();
        for (String keyword : sqlKeywords) {
            if (lowerInput.contains(keyword)) {
                // 简单检查，实际项目中需要更复杂的逻辑
                return true;
            }
        }

        return false;
    }

    /**
     * 安全的SQL字符串处理（使用PreparedStatement替代）
     * @param input 用户输入
     * @return 处理后的字符串（实际应该使用参数化查询）
     */
    public static String sanitizeSql(String input) {
        if (isEmpty(input)) {
            return "";
        }

        // 简单的转义处理（实际应该使用PreparedStatement）
        return input.replace("'", "''").replace(";", "");
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
}