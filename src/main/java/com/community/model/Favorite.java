package com.community.model;

import java.util.Date;

public class Favorite {
    private Integer id;
    private Integer userId;
    private Integer postId;
    private Date createTime;

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