package com.community.service;

import com.community.model.Favorite;
import com.community.model.Post;
import java.util.List;

public interface FavoriteService {
    /**
     * 收藏帖子
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否成功
     */
    boolean addFavorite(int userId, int postId);

    /**
     * 取消收藏帖子
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否成功
     */
    boolean removeFavorite(int userId, int postId);

    /**
     * 检查用户是否已收藏帖子
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否已收藏
     */
    boolean isFavorite(int userId, int postId);

    /**
     * 获取用户的收藏列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 收藏的帖子列表
     */
    List<Favorite> getFavoritesByUser(int userId, int page, int pageSize);

    /**
     * 获取帖子的收藏数
     * @param postId 帖子ID
     * @return 收藏数
     */
    int getFavoriteCount(int postId);

    /**
     * 获取用户的收藏数
     * @param userId 用户ID
     * @return 收藏数
     */
    int getUserFavoriteCount(int userId);
}