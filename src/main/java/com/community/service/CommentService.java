package com.community.service;

import com.community.model.Comment;
import java.util.List;

public interface CommentService {
    /**
     * 添加评论
     * @param comment 评论对象
     * @return 添加的评论
     */
    Comment addComment(Comment comment);

    /**
     * 更新评论
     * @param comment 评论对象
     * @return 更新后的评论
     */
    Comment updateComment(Comment comment);

    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userId 操作用户ID
     * @param isAdmin 是否为管理员
     * @return 是否成功
     */
    boolean deleteComment(int commentId, int userId, boolean isAdmin);

    /**
     * 获取帖子的评论
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<Comment> getCommentsByPost(int postId);

    /**
     * 获取用户的评论
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 评论列表
     */
    List<Comment> getCommentsByUser(int userId, int page, int pageSize);
}