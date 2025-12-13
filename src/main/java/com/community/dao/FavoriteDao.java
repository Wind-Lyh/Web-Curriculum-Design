package com.community.dao;

import com.community.model.Favorite;
import com.community.model.Post;

import java.util.List;

public interface FavoriteDao {
    // 插入收藏记录
    int insert(Favorite favorite);

    // 删除收藏记录
    int delete(int id);

    // 删除用户的收藏记录
    int deleteByUserAndPost(int userId, int postId);

    // 检查用户是否已收藏
    Favorite findByUserAndPost(int userId, int postId);

    // 查询用户的收藏（分页）
    List<Favorite> findByUserId(int userId, int page, int size);

    // 统计用户收藏数
    int countByUser(int userId);
    /**
     * 根据用户ID查询收藏的帖子列表（带分页）
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数
     * @return 帖子列表
     */
    List<Post> findPostsByUserId(int userId, int offset, int limit);

    /**
     * 统计帖子的收藏数
     * @param postId 帖子ID
     * @return 收藏数
     */
    int countByPost(int postId);
}