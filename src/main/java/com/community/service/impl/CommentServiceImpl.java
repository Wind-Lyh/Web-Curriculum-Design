package com.community.service.impl;

import com.community.dao.CommentDao;
import com.community.dao.PostDao;
import com.community.dao.NotificationDao;
import com.community.dao.PointsDao;
import com.community.model.Comment;
import com.community.model.Post;
import com.community.model.Notification;
import com.community.model.PointsRecord;
import com.community.service.CommentService;
import com.community.util.StringUtil;
import java.util.List;
import java.util.Date;

public class CommentServiceImpl implements CommentService {

    private CommentDao commentDao;
    private PostDao postDao;
    private NotificationDao notificationDao;
    private PointsDao pointsDao;

    public CommentServiceImpl(CommentDao commentDao, PostDao postDao,
                              NotificationDao notificationDao, PointsDao pointsDao) {
        this.commentDao = commentDao;
        this.postDao = postDao;
        this.notificationDao = notificationDao;
        this.pointsDao = pointsDao;
    }

    @Override
    public Comment addComment(Comment comment) {
        if (comment == null || StringUtil.isEmpty(comment.getContent())
                || comment.getUserId() == null || comment.getPostId() == null) {
            throw new IllegalArgumentException("评论信息不完整");
        }

        if (comment.getContent().length() > 1000) {
            throw new IllegalArgumentException("评论内容不能超过1000个字符");
        }

        Post post = postDao.findById(comment.getPostId());
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在");
        }

        if (post.getStatus() == 3) {
            throw new IllegalArgumentException("帖子已被删除，不能评论");
        }

        comment.setLikeCount(0);
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());

        if (comment.getParentId() == null) {
            comment.setParentId(0);
        }

        int result = commentDao.insert(comment);
        if (result <= 0) {
            throw new RuntimeException("添加评论失败");
        }

        postDao.increaseCommentCount(comment.getPostId());

        addCommentPoints(comment.getUserId(), 2);

        if (comment.getParentId() == 0) {
            if (!post.getUserId().equals(comment.getUserId())) {
                sendNotification(post.getUserId(), comment.getId(), "reply",
                        "有人评论了你的帖子", comment.getContent());
            }
        } else {
            Comment parentComment = commentDao.findById(comment.getParentId());
            if (parentComment != null && !parentComment.getUserId().equals(comment.getUserId())) {
                sendNotification(parentComment.getUserId(), comment.getId(), "reply",
                        "有人回复了你的评论", comment.getContent());
            }
        }

        return comment;
    }

    @Override
    public Comment updateComment(Comment comment) {
        if (comment == null || comment.getId() == null || comment.getUserId() == null) {
            throw new IllegalArgumentException("评论信息不完整");
        }

        Comment existingComment = commentDao.findById(comment.getId());
        if (existingComment == null) {
            throw new IllegalArgumentException("评论不存在");
        }

        if (!existingComment.getUserId().equals(comment.getUserId())) {
            throw new SecurityException("无权修改此评论");
        }

        if (existingComment.getStatus() == 1) {
            throw new IllegalArgumentException("评论已被删除");
        }

        comment.setUpdateTime(new Date());
        int result = commentDao.update(comment);
        if (result <= 0) {
            throw new RuntimeException("更新评论失败");
        }

        return comment;
    }

    @Override
    public boolean deleteComment(int commentId, int userId, boolean isAdmin) {
        Comment comment = commentDao.findById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }

        if (!isAdmin && !comment.getUserId().equals(userId)) {
            throw new SecurityException("无权删除此评论");
        }

        int result = commentDao.delete(commentId);

        if (result > 0) {
            postDao.decreaseCommentCount(comment.getPostId());
        }

        return result > 0;
    }

    @Override
    public List<Comment> getCommentsByPost(int postId) {
        Post post = postDao.findById(postId);
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在");
        }

        return commentDao.findByPostId(postId);
    }

    @Override
    public List<Comment> getCommentsByUser(int userId, int page, int pageSize) {
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数错误");
        }

        int offset = (page - 1) * pageSize;
        return commentDao.findByUserId(userId, offset, pageSize);
    }

    private void addCommentPoints(int userId, int points) {
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setChangeType("COMMENT_CREATE");
        record.setChangeAmount(points);
        record.setDescription("评论奖励");
        record.setCreateTime(new Date());
        pointsDao.insertPointsRecord(record);
    }

    private void sendNotification(int userId, int relatedId, String type,
                                  String title, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(StringUtil.limitLength(content, 50));
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);
        notification.setCreateTime(new Date());

        notificationDao.insertNotification(notification);
    }
}