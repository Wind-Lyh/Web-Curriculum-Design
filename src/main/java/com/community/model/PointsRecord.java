package com.community.model;

import java.util.Date;

public class PointsRecord {
    private Integer id;
    private Integer userId;
    private String changeType; // "register","login","post","comment","like_received","exchange"
    private Integer changeAmount; // 正数:增加, 负数:减少
    private String description;
    private Date createTime;

    public PointsRecord() {
        this.createTime = new Date();
    }

    public PointsRecord(Integer userId, String changeType, Integer changeAmount, String description) {
        this();
        this.userId = userId;
        this.changeType = changeType;
        this.changeAmount = changeAmount;
        this.description = description;
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

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public Integer getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(Integer changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "PointsRecord{" +
                "id=" + id +
                ", userId=" + userId +
                ", changeType='" + changeType + '\'' +
                ", changeAmount=" + changeAmount +
                ", description='" + description + '\'' +
                '}';
    }
}