package com.community.exception;


/**
 * 授权异常类
 * 权限不足时抛出
 */
public class AuthorizationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    // 请求的资源
    private String resource;

    // 请求的操作
    private String operation;

    // 需要的权限
    private String requiredPermission;

    // 用户当前拥有的权限
    private String currentPermission;

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String resource, String operation, String requiredPermission) {
        super(String.format("访问资源[%s]的操作[%s]需要权限[%s]",
                resource, operation, requiredPermission));
        this.resource = resource;
        this.operation = operation;
        this.requiredPermission = requiredPermission;
    }

    public AuthorizationException(String resource, String operation,
                                  String requiredPermission, String currentPermission) {
        super(String.format("访问资源[%s]的操作[%s]需要权限[%s]，当前权限[%s]",
                resource, operation, requiredPermission, currentPermission));
        this.resource = resource;
        this.operation = operation;
        this.requiredPermission = requiredPermission;
        this.currentPermission = currentPermission;
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    // Getters and Setters
    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public void setRequiredPermission(String requiredPermission) {
        this.requiredPermission = requiredPermission;
    }

    public String getCurrentPermission() {
        return currentPermission;
    }

    public void setCurrentPermission(String currentPermission) {
        this.currentPermission = currentPermission;
    }

    @Override
    public String toString() {
        return String.format("AuthorizationException{resource='%s', operation='%s', requiredPermission='%s'}",
                resource, operation, requiredPermission);
    }
}