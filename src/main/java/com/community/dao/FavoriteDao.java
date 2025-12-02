package com.community.dao;

import com.community.model.Favorite;
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
}