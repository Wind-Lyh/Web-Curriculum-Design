package com.community.service.impl;

import com.community.dao.LikeDao;
import com.community.dao.PostDao;
import com.community.dao.CommentDao;
import com.community.dao.PointsDao;
import com.community.model.Like;
import com.community.model.Post;
import com.community.model.Comment;
import com.community.model.PointsRecord;
import com.community.service.LikeService;
import java.util.Date;

public class LikeServiceImpl implements LikeService {

    private LikeDao likeDao;
    private PostDao postDao;
    private CommentDao commentDao;
    private PointsDao pointsDao;

    public LikeServiceImpl(LikeDao likeDao, PostDao postDao,
                           CommentDao commentDao, PointsDao pointsDao) {
        this.likeDao = likeDao;
        this.postDao = postDao;
        this.commentDao = commentDao;
        this.pointsDao = pointsDao;
    }

    @Override
    public boolean likePost(int userId, int postId) {
        Like existingLike = likeDao.findByUserAndTarget(userId, "post", postId);
        if (existingLike != null) {
            return false;
        }

        Like like = new Like();
        like.setUserId(userId);
        like.setTargetType("post");
        like.setTargetId(postId);
        like.setCreateTime(new Date());

        int result = likeDao.insert(like);
        if (result > 0) {
            postDao.increaseLikeCount(postId);

            Post post = postDao.findById(postId);
            if (post != null && post.getUserId() != userId) {
                addLikePoints(post.getUserId(), 5, "POST_LIKED");
            }
        }

        return result > 0;
    }

    @Override
    public boolean unlikePost(int userId, int postId) {
        int result = likeDao.deleteByUserAndTarget(userId, "post", postId);
        if (result > 0) {
            postDao.decreaseLikeCount(postId);
        }

        return result > 0;
    }

    @Override
    public boolean likeComment(int userId, int commentId) {
        Like existingLike = likeDao.findByUserAndTarget(userId, "comment", commentId);
        if (existingLike != null) {
            return false;
        }

        Like like = new Like();
        like.setUserId(userId);
        like.setTargetType("comment");
        like.setTargetId(commentId);
        like.setCreateTime(new Date());

        int result = likeDao.insert(like);
        if (result > 0) {
            Comment comment = commentDao.findById(commentId);
            comment.setLikeCount(comment.getLikeCount() + 1);
            commentDao.update(comment);

            if (comment != null && comment.getUserId() != userId) {
                addLikePoints(comment.getUserId(), 3, "COMMENT_LIKED");
            }
        }

        return result > 0;
    }

    @Override
    public boolean unlikeComment(int userId, int commentId) {
        int result = likeDao.deleteByUserAndTarget(userId, "comment", commentId);
        if (result > 0) {
            Comment comment = commentDao.findById(commentId);
            comment.setLikeCount(comment.getLikeCount() - 1);
            commentDao.update(comment);
        }

        return result > 0;
    }

    @Override
    public boolean isLiked(int userId, String targetType, int targetId) {
        Like like = likeDao.findByUserAndTarget(userId, targetType, targetId);
        return like != null;
    }

    @Override
    public int getLikeCount(String targetType, int targetId) {
        int result = likeDao.countByTarget(targetType, targetId);
        return result;
    }

    private void addLikePoints(int userId, int points, String type) {
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setChangeType(type);
        record.setChangeAmount(points);
        record.setDescription("被点赞奖励");
        record.setCreateTime(new Date());
        pointsDao.insertPointsRecord(record);
    }
}