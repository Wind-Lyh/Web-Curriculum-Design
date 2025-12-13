package com.community.service.impl;

import com.community.dao.FavoriteDao;
import com.community.dao.PostDao;
import com.community.dao.UserDao;
import com.community.model.Favorite;
import com.community.model.Post;
import com.community.model.User;
import com.community.service.FavoriteService;
import java.util.Date;
import java.util.List;

public class FavoriteServiceImpl implements FavoriteService {

    private FavoriteDao favoriteDao;
    private PostDao postDao;
    private UserDao userDao;

    public FavoriteServiceImpl(FavoriteDao favoriteDao, PostDao postDao, UserDao userDao) {
        this.favoriteDao = favoriteDao;
        this.postDao = postDao;
        this.userDao = userDao;
    }

    @Override
    public boolean addFavorite(int userId, int postId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        Post post = postDao.findById(postId);
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在");
        }

        if (post.getStatus() == 3) {
            throw new IllegalArgumentException("帖子已被删除");
        }

        Favorite existing = favoriteDao.findByUserAndPost(userId, postId);
        if (existing != null) {
            return false;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setPostId(postId);
        favorite.setCreateTime(new Date());

        int result = favoriteDao.insert(favorite);

        return result > 0;
    }

    @Override
    public boolean removeFavorite(int userId, int postId) {
        Favorite favorite = favoriteDao.findByUserAndPost(userId, postId);
        if (favorite == null) {
            return false;
        }

        int result = favoriteDao.delete(favorite.getId());

        return result > 0;
    }

    @Override
    public boolean isFavorite(int userId, int postId) {
        Favorite favorite = favoriteDao.findByUserAndPost(userId, postId);
        return favorite != null;
    }

    @Override
    public List<Post> getFavoritesByUser(int userId, int page, int pageSize) {
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数错误");
        }

        int offset = (page - 1) * pageSize;
        return favoriteDao.findByUserId(userId, offset, pageSize);
    }

    @Override
    public int getFavoriteCount(int postId) {
        return favoriteDao.countByPost(postId);
    }

    @Override
    public int getUserFavoriteCount(int userId) {
        return favoriteDao.countByUser(userId);
    }
}