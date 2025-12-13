package com.community.service.impl;

import com.community.dao.PostDao;
import com.community.dao.UserDao;
import com.community.dao.CommentDao;
import com.community.model.Post;
import com.community.model.User;
import com.community.model.Comment;
import com.community.service.SearchService;
import com.community.util.StringUtil;
import java.util.ArrayList;
import java.util.List;

public class SearchServiceImpl implements SearchService {

    private PostDao postDao;
    private UserDao userDao;
    private CommentDao commentDao;

    public SearchServiceImpl(PostDao postDao, UserDao userDao, CommentDao commentDao) {
        this.postDao = postDao;
        this.userDao = userDao;
        this.commentDao = commentDao;
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
    public List<User> searchUsers(String keyword, int page, int pageSize) {
        if (StringUtil.isEmpty(keyword)) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }

        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数错误");
        }

        int offset = (page - 1) * pageSize;
        return userDao.search(keyword, offset, pageSize);
    }

    @Override
    public List<Comment> searchComments(String keyword, int page, int pageSize) {
        if (StringUtil.isEmpty(keyword)) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }

        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数错误");
        }

        // 这里假设CommentDao有search方法，暂时返回空列表
        return new ArrayList<>();
    }

    @Override
    public SearchResult globalSearch(String keyword, String type, int page, int pageSize) {
        if (StringUtil.isEmpty(keyword)) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }

        SearchResult result = new SearchResult();

        if ("all".equals(type) || "post".equals(type)) {
            List<Post> posts = searchPosts(keyword, page, pageSize);
            result.setPosts(posts);
            result.setPostCount(posts.size());
        }

        if ("all".equals(type) || "user".equals(type)) {
            List<User> users = searchUsers(keyword, page, pageSize);
            result.setUsers(users);
            result.setUserCount(users.size());
        }

        if ("all".equals(type) || "comment".equals(type)) {
            List<Comment> comments = searchComments(keyword, page, pageSize);
            result.setComments(comments);
            result.setCommentCount(comments.size());
        }

        result.setTotalCount(result.getPostCount() + result.getUserCount() + result.getCommentCount());

        return result;
    }
}