package com.community.exception;


/**
 * 业务异常类
 * 用于业务逻辑错误时抛出
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    // 错误码
    private String errorCode;

    // 错误详情
    private Object errorData;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String message, Object errorData) {
        super(message);
        this.errorCode = errorCode;
        this.errorData = errorData;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    // Getters and Setters
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Object getErrorData() {
        return errorData;
    }

    public void setErrorData(Object errorData) {
        this.errorData = errorData;
    }

    @Override
    public String toString() {
        return String.format("BusinessException{errorCode='%s', message='%s', errorData=%s}",
                errorCode, getMessage(), errorData);
    }
}
