package com.community.model;

import java.util.Date;

public class BrowseHistory {
    private Integer id;
    private Integer userId;
    private Integer postId;
    private Date browseTime;

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