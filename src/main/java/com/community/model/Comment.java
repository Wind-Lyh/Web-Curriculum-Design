package com.community.model;

import java.util.Date;

public class Comment {
    private Integer id;
    private String content;
    private Integer userId;
    private Integer postId;
    private Integer parentId; // 0:直接评论帖子, >0:回复的评论ID
    private Integer likeCount;
    private Integer status; // 0:正常, 1:删除
    private Date createTime;
    private Date updateTime;

    // 关联的用户信息
    private String username;
    private String nickname;
    private String avatarUrl;

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