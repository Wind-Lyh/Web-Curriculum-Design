package com.community.model;

import java.util.Date;

public class LoginLog {
    private Integer id;         // 登录记录ID，对应login_logs.id，主键自增
    private Integer userId;     // 用户ID，对应login_logs.user_id，外键关联users.id
    private String ipAddress;   // IP地址，对应login_logs.ip_address
    private String userAgent;   // 用户代理，对应login_logs.user_agent
    private Date loginTime;     // 登录时间，对应login_logs.login_time，默认当前时间

    public LoginLog(Integer id, Integer userId, String ipAddress, String userAgent, Date loginTime) {
        this.id = id;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.loginTime = loginTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public LoginLog() {
        this.id = id;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.loginTime = loginTime;
    }
}