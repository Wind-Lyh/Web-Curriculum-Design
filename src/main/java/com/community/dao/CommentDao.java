package com.community.dao;

import com.community.model.Comment;
import java.util.List;

public interface CommentDao {
    // 插入评论
    int insert(Comment comment);

    // 根据ID查询评论
    Comment findById(int id);

    // 更新评论
    int update(Comment comment);

    // 删除评论（逻辑删除）
    int delete(int id);

    // 根据帖子ID查询评论
    List<Comment> findByPostId(int postId);

    // 根据用户ID查询评论（分页）
    List<Comment> findByUserId(int userId, int page, int size);

    // 查询父评论下的所有回复
    List<Comment> findByParentId(int parentId);

    // 统计帖子评论数
    int countByPost(int postId);

    // 统计用户评论数
    int countByUser(int userId);

    /**
     * 统计评论总数
     * @return 评论总数
     */
    int countAll();
}