package com.community.model;

import java.util.Date;

public class Comment {
    private Integer id;         // 评论ID，对应comments.id，主键自增
    private String content;     // 评论内容，对应comments.content
    private Integer userId;     // 用户ID，对应comments.user_id，外键关联users.id
    private Integer postId;     // 帖子ID，对应comments.post_id，外键关联posts.id
    private Integer parentId;   // 父评论ID，对应comments.parent_id，0表示直接评论帖子
    private Integer likeCount;  // 点赞数，对应comments.like_count，默认0
    private Integer status;     // 状态：0正常，1删除，对应comments.is_deleted扩展
    private Date createTime;    // 创建时间，对应comments.create_time，默认当前时间
    private Date updateTime;    // 更新时间，对应comments.update_time，更新时自动更新

    // 关联字段（用于显示，不直接对应数据库表）
    private String username;    // 评论用户登录名，从users.username关联获取
    private String nickname;    // 评论用户昵称，从users.nickname关联获取
    private String avatarUrl;   // 评论用户头像，从users.avatar_url关联获取

    public Comment() {
        this.likeCount = 0;
        this.status = 0;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    public Comment(String content, Integer userId, Integer postId) {
        this();
        this.content = content;
        this.userId = userId;
        this.postId = postId;
        this.parentId = 0;
    }

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content.substring(0, Math.min(content.length(), 20)) + "...'" +
                ", userId=" + userId +
                ", postId=" + postId +
                ", parentId=" + parentId +
                ", likeCount=" + likeCount +
                ", status=" + status +
                '}';
    }
}