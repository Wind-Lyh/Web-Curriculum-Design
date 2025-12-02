package com.community.dao;

import com.community.model.Like;

public interface LikeDao {
    // 插入点赞记录
    int insert(Like like);

    // 删除点赞记录
    int delete(int id);

    // 根据用户和目标查询点赞记录
    Like findByUserAndTarget(int userId, String targetType, int targetId);

    // 删除用户的点赞记录
    int deleteByUserAndTarget(int userId, String targetType, int targetId);

    // 统计目标的点赞数
    int countByTarget(String targetType, int targetId);

    // 统计用户的点赞数
    int countByUser(int userId);
}