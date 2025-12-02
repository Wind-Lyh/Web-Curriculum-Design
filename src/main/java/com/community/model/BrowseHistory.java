package com.community.model;

import java.util.Date;

public class BrowseHistory {
    private Integer id;         // 浏览记录ID，对应browse_history.id，主键自增
    private Integer userId;     // 用户ID，对应browse_history.user_id，外键关联users.id
    private Integer postId;     // 帖子ID，对应browse_history.post_id，外键关联posts.id
    private Date browseTime;    // 浏览时间，对应browse_history.browse_time，默认当前时间

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

    public Date getBrowseTime() {
        return browseTime;
    }

    public void setBrowseTime(Date browseTime) {
        this.browseTime = browseTime;
    }

    public BrowseHistory(Integer id, Integer userId, Integer postId, Date browseTime) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.browseTime = browseTime;
    }



}