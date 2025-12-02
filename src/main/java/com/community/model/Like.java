package com.community.model;

import java.util.Date;

public class Like {
    private Integer id;
    private Integer userId;
    private String targetType; // "post"或"comment"
    private Integer targetId;
    private Date createTime;

    public Like() {
        this.createTime = new Date();
    }

    public Like(Integer userId, String targetType, Integer targetId) {
        this();
        this.userId = userId;
        this.targetType = targetType;
        this.targetId = targetId;
    }

    // Getter和Setter方法
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Like{" +
                "id=" + id +
                ", userId=" + userId +
                ", targetType='" + targetType + '\'' +
                ", targetId=" + targetId +
                '}';
    }
}