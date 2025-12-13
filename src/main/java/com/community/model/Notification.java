package com.community.model;

import java.util.Date;

public class Notification {
    private Integer id;         // 通知ID，对应notifications.id，主键自增
    private Integer userId;     // 接收用户ID，对应notifications.user_id，外键关联users.id
    private String type;        // 通知类型：reply回复,like点赞,system系统通知，对应notifications.type枚举
    private String title;       // 通知标题，对应notifications.title
    private String content;     // 通知内容，对应notifications.content
    private Integer relatedId;  // 关联ID，对应notifications.related_id，关联帖子/评论ID
    private Integer isRead;     // 已读状态：0未读，1已读，对应notifications.is_read，默认false
    private Date createTime;    // 创建时间，对应notifications.create_time，默认当前时间

    // 无参构造函数
    public Notification() {
    }

    // 全参构造函数
    public Notification(Integer id, Integer userId, String type, String title,
                        String content, Integer relatedId, Integer isRead, Date createTime) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.relatedId = relatedId;
        this.isRead = isRead;
        this.createTime = createTime;
    }

    // Getters and Setters
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(Integer relatedId) {
        this.relatedId = relatedId;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    // toString方法
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", relatedId=" + relatedId +
                ", isRead=" + isRead +
                ", createTime=" + createTime +
                '}';
    }
}