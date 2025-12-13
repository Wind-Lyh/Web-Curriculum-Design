package com.community.model;

import java.util.Date;

public class AdminLog {
    private Integer id;         // 操作日志ID，对应admin_logs.id，主键自增
    private Integer adminId;    // 管理员ID，对应admin_logs.admin_id，外键关联users.id
    private String actionType;  // 操作类型，对应admin_logs.action_type
    private String targetType;  // 目标类型，对应admin_logs.target_type
    private Integer targetId;   // 目标ID，对应admin_logs.target_id
    private String details;     // 操作详情，对应admin_logs.details
    private String ipAddress;   // IP地址，对应admin_logs.ip_address
    private Date createTime;    // 操作时间，对应admin_logs.create_time，默认当前时间

    public AdminLog() {}
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public AdminLog(Integer id, Integer adminId, String actionType, String targetType, Integer targetId, String details, String ipAddress, Date createTime) {
        this.id = id;
        this.adminId = adminId;
        this.actionType = actionType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.details = details;
        this.ipAddress = ipAddress;
        this.createTime = createTime;
    }
}