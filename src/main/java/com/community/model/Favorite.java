package com.community.model;

import java.util.Date;

public class Favorite {
    private Integer id;         // 收藏记录ID，对应favorites.id，主键自增
    private Integer userId;     // 用户ID，对应favorites.user_id，外键关联users.id
    private Integer postId;     // 帖子ID，对应favorites.post_id，外键关联posts.id
    private Date createTime;    // 收藏时间，对应favorites.create_time，默认当前时间

    public Favorite() {
        this.createTime = new Date();
    }

    public Favorite(Integer userId, Integer postId) {
        this();
        this.userId = userId;
        this.postId = postId;
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

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "id=" + id +
                ", userId=" + userId +
                ", postId=" + postId +
                '}';
    }
}