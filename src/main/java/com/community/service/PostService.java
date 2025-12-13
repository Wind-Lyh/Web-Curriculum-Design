package com.community.service;

import com.community.model.Post;
import java.util.List;

public interface PostService {
    /**
     * 创建新帖子
     * @param post 帖子对象
     * @return 创建的帖子
     */
    Post createPost(Post post);

    /**
     * 更新帖子内容
     * @param post 帖子对象
     * @return 更新后的帖子
     */
    Post updatePost(Post post);

    /**
     * 删除帖子
     * @param postId 帖子ID
     * @param userId 操作用户ID
     * @param isAdmin 是否为管理员
     * @return 是否成功
     */
    boolean deletePost(int postId, int userId, boolean isAdmin);

    /**
     * 根据ID获取帖子
     * @param postId 帖子ID
     * @return 帖子对象
     */
    Post getPostById(int postId);

    /**
     * 获取用户发布的帖子
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 帖子列表
     */
    List<Post> getPostsByUser(int userId, int page, int pageSize);

    /**
     * 搜索帖子
     * @param keyword 关键词
     * @param page 页码
     * @param pageSize 每页大小
     * @return 帖子列表
     */
    List<Post> searchPosts(String keyword, int page, int pageSize);

    /**
     * 增加帖子浏览数
     * @param postId 帖子ID
     */
    void increaseViewCount(int postId);

    /**
     * 获取帖子详情（包含作者信息等）
     * @param postId 帖子ID
     * @return 帖子详情
     */
    Post getPostWithDetails(int postId);
}