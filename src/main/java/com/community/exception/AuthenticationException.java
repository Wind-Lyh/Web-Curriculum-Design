package com.community.exception;


/**
 * 认证异常类
 * 用户认证失败时抛出
 */
public class AuthenticationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    // 认证失败类型
    public enum AuthFailureType {
        USER_NOT_FOUND,     // 用户不存在
        INVALID_PASSWORD,   // 密码错误
        ACCOUNT_LOCKED,     // 账户锁定
        ACCOUNT_EXPIRED,    // 账户过期
        CREDENTIALS_EXPIRED, // 凭证过期
        TOKEN_INVALID,      // Token无效
        TOKEN_EXPIRED       // Token过期
    }

    private AuthFailureType failureType;
    private String username;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(AuthFailureType failureType, String message) {
        super(message);
        this.failureType = failureType;
    }

    public AuthenticationException(AuthFailureType failureType, String username, String message) {
        super(message);
        this.failureType = failureType;
        this.username = username;
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(AuthFailureType failureType, String message, Throwable cause) {
        super(message, cause);
        this.failureType = failureType;
    }

    // Getters and Setters
    public AuthFailureType getFailureType() {
        return failureType;
    }

    public void setFailureType(AuthFailureType failureType) {
        this.failureType = failureType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return String.format("AuthenticationException{failureType=%s, username='%s', message='%s'}",
                failureType, username, getMessage());
    }
}