package com.community.service.impl;

import com.community.dao.PostDao;
import com.community.dao.UserDao;
import com.community.dao.PointsDao;
import com.community.model.Post;
import com.community.model.User;
import com.community.model.PointsRecord;
import com.community.service.PostService;
import com.community.util.StringUtil;
import java.util.List;
import java.util.Date;

public class PostServiceImpl implements PostService {

    private PostDao postDao;
    private UserDao userDao;
    private PointsDao pointsDao;

    public PostServiceImpl(PostDao postDao, UserDao userDao, PointsDao pointsDao) {
        this.postDao = postDao;
        this.userDao = userDao;
        this.pointsDao = pointsDao;
    }

    @Override
    public Post createPost(Post post) {
        if (post == null || StringUtil.isEmpty(post.getTitle())
                || StringUtil.isEmpty(post.getContent()) || post.getUserId() == null) {
            throw new IllegalArgumentException("帖子信息不完整");
        }

        if (post.getTitle().length() > 100) {
            throw new IllegalArgumentException("标题不能超过100个字符");
        }

        User user = userDao.findById(post.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (user.getStatus() == 1) {
            throw new IllegalArgumentException("用户已被封禁，不能发帖");
        }

        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setStatus(0);
        post.setCreateTime(new Date());
        post.setUpdateTime(new Date());

        int result = postDao.insert(post);
        if (result <= 0) {
            throw new RuntimeException("发帖失败");
        }

        addPostPoints(post.getUserId(), 10);

        return post;
    }

    @Override
    public Post updatePost(Post post) {
        if (post == null || post.getId() == null || post.getUserId() == null) {
            throw new IllegalArgumentException("帖子信息不完整");
        }

        Post existingPost = postDao.findById(post.getId());
        if (existingPost == null) {
            throw new IllegalArgumentException("帖子不存在");
        }

        if (!existingPost.getUserId().equals(post.getUserId())) {
            throw new SecurityException("无权修改此帖子");
        }

        if (existingPost.getStatus() == 3) {
            throw new IllegalArgumentException("帖子已被删除");
        }

        post.setUpdateTime(new Date());
        int result = postDao.update(post);
        if (result <= 0) {
            throw new RuntimeException("更新帖子失败");
        }

        return post;
    }

    @Override
    public boolean deletePost(int postId, int userId, boolean isAdmin) {
        Post post = postDao.findById(postId);
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在");
        }

        if (!isAdmin && !post.getUserId().equals(userId)) {
            throw new SecurityException("无权删除此帖子");
        }

        int result = postDao.delete(postId);
        return result > 0;
    }

    @Override
    public Post getPostById(int postId) {
        Post post = postDao.findById(postId);
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在");
        }

        if (post.getStatus() == 3) {
            throw new IllegalArgumentException("帖子已被删除");
        }

        return post;
    }

    @Override
    public List<Post> getPostsByUser(int userId, int page, int pageSize) {
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数错误");
        }

        int offset = (page - 1) * pageSize;
        return postDao.findByUserId(userId, offset, pageSize);
    }

    @Override
    public List<Post> searchPosts(String keyword, int page, int pageSize) {
        if (StringUtil.isEmpty(keyword)) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }

        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数错误");
        }

        int offset = (page - 1) * pageSize;
        return postDao.search(keyword, offset, pageSize);
    }

    @Override
    public void increaseViewCount(int postId) {
        postDao.increaseViewCount(postId);
    }

    @Override
    public Post getPostWithDetails(int postId) {
        Post post = getPostById(postId);
        if (post == null) {
            return null;
        }

        User author = userDao.findById(post.getUserId());
        return post;
    }

    private void addPostPoints(int userId, int points) {
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setChangeType("POST_CREATE");
        record.setChangeAmount(points);
        record.setDescription("发帖奖励");
        record.setCreateTime(new Date());
        pointsDao.insertPointsRecord(record);

        userDao.updatePoints(userId, points);
    }
}