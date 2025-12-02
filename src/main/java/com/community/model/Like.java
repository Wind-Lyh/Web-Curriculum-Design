package com.community.model;

import java.util.Date;

public class Like {
    private Integer id;         // 点赞记录ID，对应likes.id，主键自增
    private Integer userId;     // 用户ID，对应likes.user_id，外键关联users.id
    private String targetType;  // 点赞对象类型：post帖子,comment评论，对应likes.target_type枚举
    private Integer targetId;   // 目标ID，对应likes.target_id，帖子ID或评论ID
    private Date createTime;    // 创建时间，对应likes.create_time，默认当前时间

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