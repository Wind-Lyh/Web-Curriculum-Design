package com.community.model;

import java.util.Date;

public class Attachment {
    private Integer id;         // 附件ID，对应attachments.id，主键自增
    private String filename;    // 文件名，对应attachments.filename
    private String filePath;    // 文件路径，对应attachments.file_path
    private String fileType;    // 文件类型，对应attachments.file_type
    private Long fileSize;      // 文件大小（字节），对应attachments.file_size
    private Integer userId;     // 上传用户ID，对应attachments.user_id，外键关联users.id
    private Integer postId;     // 关联帖子ID，对应attachments.post_id，外键关联posts.id
    private Integer commentId;  // 关联评论ID，对应attachments.comment_id，外键关联comments.id
    private Date createTime;    // 上传时间，对应attachments.create_time，默认当前时间

    public Attachment() {
        this.createTime = new Date();
    }

    public Attachment(String filename, String filePath, Integer userId) {
        this();
        this.filename = filename;
        this.filePath = filePath;
        this.userId = userId;
    }

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
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

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", userId=" + userId +
                '}';
    }
}