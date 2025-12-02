package com.community.dao;

import com.community.model.Post;
import java.util.List;

public interface PostDao {
    // 插入帖子
    int insert(Post post);

    // 根据ID查询帖子
    Post findById(int id);

    // 更新帖子
    int update(Post post);

    // 删除帖子（逻辑删除）
    int delete(int id);

    // 根据版块ID查询帖子（分页）
    List<Post> findBySectionId(int sectionId, int page, int size);

    // 根据用户ID查询帖子（分页）
    List<Post> findByUserId(int userId, int page, int size);

    // 查询热门帖子
    List<Post> findHotPosts(int limit);

    // 查询最新帖子
    List<Post> findLatestPosts(int limit);

    // 搜索帖子
    List<Post> search(String keyword, int page, int size);

    // 增加浏览数
    int increaseViewCount(int postId);

    // 增加点赞数
    int increaseLikeCount(int postId);

    // 减少点赞数
    int decreaseLikeCount(int postId);

    // 增加评论数
    int increaseCommentCount(int postId);

    // 减少评论数
    int decreaseCommentCount(int postId);

    // 统计帖子总数
    int count();

    // 统计版块帖子数
    int countBySection(int sectionId);

    // 统计用户帖子数
    int countByUser(int userId);
}