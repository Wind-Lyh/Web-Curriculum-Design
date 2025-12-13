package com.community.service;

public interface LikeService {
    /**
     * 点赞帖子
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否成功
     */
    boolean likePost(int userId, int postId);

    /**
     * 取消点赞帖子
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否成功
     */
    boolean unlikePost(int userId, int postId);

    /**
     * 点赞评论
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 是否成功
     */
    boolean likeComment(int userId, int commentId);

    /**
     * 取消点赞评论
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 是否成功
     */
    boolean unlikeComment(int userId, int commentId);

    /**
     * 检查用户是否已点赞
     * @param userId 用户ID
     * @param targetType 目标类型（post/comment）
     * @param targetId 目标ID
     * @return 是否已点赞
     */
    boolean isLiked(int userId, String targetType, int targetId);

    /**
     * 获取点赞数
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 点赞数
     */
    int getLikeCount(String targetType, int targetId);
}