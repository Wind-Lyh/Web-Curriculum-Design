package com.community.model;

import java.util.Date;

public class PointsRecord {
    private Integer id;            // 积分记录ID，对应points_records.id，主键自增
    private Integer userId;        // 用户ID，对应points_records.user_id，外键关联users.id
    private String changeType;     // 变更类型，对应points_records.change_type的枚举值
    private Integer changeAmount;  // 变更数量，对应points_records.change_amount，正负表示增减
    private String description;    // 变更描述，对应points_records.description
    private Date createTime;       // 创建时间，对应points_records.create_time，默认当前时间

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