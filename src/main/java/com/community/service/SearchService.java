package com.community.service;

import com.community.model.Comment;
import com.community.model.Post;
import com.community.model.User;
import java.util.List;

public interface SearchService {
    /**
     * 搜索帖子
     * @param keyword 关键词
     * @param page 页码
     * @param pageSize 每页大小
     * @return 帖子列表
     */
    List<Post> searchPosts(String keyword, int page, int pageSize);

    /**
     * 搜索用户
     * @param keyword 关键词
     * @param page 页码
     * @param pageSize 每页大小
     * @return 用户列表
     */
    List<User> searchUsers(String keyword, int page, int pageSize);

    /**
     * 搜索评论
     * @param keyword 关键词
     * @param page 页码
     * @param pageSize 每页大小
     * @return 评论列表
     */
    List<Comment> searchComments(String keyword, int page, int pageSize);

    /**
     * 全局搜索
     * @param keyword 关键词
     * @param type 搜索类型（all/post/user/comment）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 搜索结果
     */
    SearchResult globalSearch(String keyword, String type, int page, int pageSize);

    /**
     * 搜索结果包装类
     */
    class SearchResult {
        private List<Post> posts;
        private List<User> users;
        private List<Comment> comments;
        private int postCount;
        private int userCount;
        private int commentCount;
        private int totalCount;

        // Getters and Setters
        public List<Post> getPosts() { return posts; }
        public void setPosts(List<Post> posts) { this.posts = posts; }

        public List<User> getUsers() { return users; }
        public void setUsers(List<User> users) { this.users = users; }

        public List<Comment> getComments() { return comments; }
        public void setComments(List<Comment> comments) { this.comments = comments; }

        public int getPostCount() { return postCount; }
        public void setPostCount(int postCount) { this.postCount = postCount; }

        public int getUserCount() { return userCount; }
        public void setUserCount(int userCount) { this.userCount = userCount; }

        public int getCommentCount() { return commentCount; }
        public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    }
}