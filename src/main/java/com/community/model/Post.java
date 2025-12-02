package com.community.model;

import java.util.Date;

public class Post {
    private Integer id;            // 帖子ID，对应posts.id，主键自增
    private String title;          // 帖子标题，对应posts.title
    private String content;        // 帖子内容，对应posts.content
    private Integer userId;        // 发帖用户ID，对应posts.user_id，外键关联users.id
    private Integer sectionId;     // 所属版块ID，对应posts.section_id，外键关联sections.id
    private Integer viewCount;     // 浏览数，对应posts.view_count，默认0
    private Integer likeCount;     // 点赞数，对应posts.like_count，默认0
    private Integer commentCount;  // 评论数，对应posts.comment_count，默认0
    private Integer status;        // 状态：0正常,1置顶,2精华,3删除，对应posts.is_deleted扩展
    private Date createTime;       // 创建时间，对应posts.create_time，默认当前时间
    private Date updateTime;       // 更新时间，对应posts.update_time，更新时自动更新

    // 关联字段（用于显示，不直接对应数据库表）
    private String username;       // 发帖用户登录名，从users.username关联获取
    private String nickname;       // 发帖用户昵称，从users.nickname关联获取
    private String avatarUrl;      // 发帖用户头像，从users.avatar_url关联获取
    private String sectionName;    // 版块名称，从sections.name关联获取

    public Post() {
        this.viewCount = 0;
        this.likeCount = 0;
        this.commentCount = 0;
        this.status = 0;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    public Post(String title, String content, Integer userId, Integer sectionId) {
        this();
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.sectionId = sectionId;
    }

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
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

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", userId=" + userId +
                ", sectionId=" + sectionId +
                ", viewCount=" + viewCount +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", status=" + status +
                '}';
    }
}