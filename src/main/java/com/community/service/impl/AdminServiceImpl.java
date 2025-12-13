package com.community.service.impl;

import com.community.dao.UserDao;
import com.community.dao.PostDao;
import com.community.dao.CommentDao;
import com.community.dao.AdminDao;
import com.community.dao.LoginLogDao;
import com.community.model.AdminLog;
import com.community.model.User;
import com.community.model.Post;
import com.community.model.Comment;
import com.community.service.AdminService;
import com.community.util.StringUtil;
import java.util.List;
import java.util.Date;

public class AdminServiceImpl implements AdminService {

    private UserDao userDao;
    private PostDao postDao;
    private CommentDao commentDao;
    private AdminDao adminDao;
    private LoginLogDao loginLogDao;

    public AdminServiceImpl(UserDao userDao, PostDao postDao, CommentDao commentDao,
                            AdminDao adminDao, LoginLogDao loginLogDao) {
        this.userDao = userDao;
        this.postDao = postDao;
        this.commentDao = commentDao;
        this.adminDao = adminDao;
        this.loginLogDao = loginLogDao;
    }

    @Override
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        stats.setTotalUsers(userDao.count());
        stats.setActiveUsers(userDao.countByCondition("status = 0"));
        stats.setBannedUsers(userDao.countByCondition("status = 1"));

        stats.setTotalPosts(postDao.count());
        stats.setTodayPosts(getTodayPostsCount());
        stats.setTotalComments(commentDao.countAll());

        stats.setDailyVisits(0);
        stats.setMonthlyVisits(0);

        return stats;
    }

    @Override
    public List<User> getAllUsers(int page, int pageSize) {
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数错误");
        }

        int offset = (page - 1) * pageSize;
        return userDao.findAll(offset, pageSize);
    }

    @Override
    public List<User> searchUsers(String keyword, String status, int page, int pageSize) {
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数错误");
        }

        int offset = (page - 1) * pageSize;

        StringBuilder condition = new StringBuilder();
        if (!StringUtil.isEmpty(keyword)) {
            condition.append("(username LIKE '%").append(keyword).append("%' OR ")
                    .append("nickname LIKE '%").append(keyword).append("%' OR ")
                    .append("email LIKE '%").append(keyword).append("%')");
        }

        if (!StringUtil.isEmpty(status)) {
            if (condition.length() > 0) {
                condition.append(" AND ");
            }
            condition.append("status = ").append(status);
        }

        return userDao.search(keyword, offset, pageSize);
    }

    @Override
    public boolean banUser(int userId, String reason) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (user.getStatus() == 1) {
            return true;
        }

        int result = userDao.updateStatus(userId, 1);

        if (result > 0) {
            AdminLog log = new AdminLog();
            log.setAdminId(getCurrentAdminId());
            log.setAction("BAN_USER");
            log.setTargetType("user");
            log.setTargetId(userId);
            log.setDescription("封禁用户：" + user.getUsername() + "，原因：" + reason);
            log.setCreateTime(new Date());
            adminDao.addAdminLog(log);
        }

        return result > 0;
    }

    @Override
    public boolean unbanUser(int userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (user.getStatus() == 0) {
            return true;
        }

        int result = userDao.updateStatus(userId, 0);

        if (result > 0) {
            AdminLog log = new AdminLog();
            log.setAdminId(getCurrentAdminId());
            log.setAction("UNBAN_USER");
            log.setTargetType("user");
            log.setTargetId(userId);
            log.setDescription("解封用户：" + user.getUsername());
            log.setCreateTime(new Date());
            adminDao.addAdminLog(log);
        }

        return result > 0;
    }

    @Override
    public boolean resetUserPassword(int userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        String newPassword = StringUtil.generateRandomString(8);

        int result = userDao.updatePassword(userId, newPassword);

        if (result > 0) {
            AdminLog log = new AdminLog();
            log.setAdminId(getCurrentAdminId());
            log.setAction("RESET_PASSWORD");
            log.setTargetType("user");
            log.setTargetId(userId);
            log.setDescription("重置用户密码：" + user.getUsername());
            log.setCreateTime(new Date());
            adminDao.addAdminLog(log);
        }

        return result > 0;
    }

    @Override
    public boolean deletePost(int postId) {
        Post post = postDao.findById(postId);
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在");
        }

        int result = postDao.delete(postId);

        if (result > 0) {
            AdminLog log = new AdminLog();
            log.setAdminId(getCurrentAdminId());
            log.setAction("DELETE_POST");
            log.setTargetType("post");
            log.setTargetId(postId);
            log.setDescription("删除帖子：" + post.getTitle());
            log.setCreateTime(new Date());
            adminDao.addAdminLog(log);
        }

        return result > 0;
    }

    @Override
    public boolean deleteComment(int commentId) {
        Comment comment = commentDao.findById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }

        int result = commentDao.delete(commentId);

        if (result > 0) {
            postDao.decreaseCommentCount(comment.getPostId());

            AdminLog log = new AdminLog();
            log.setAdminId(getCurrentAdminId());
            log.setAction("DELETE_COMMENT");
            log.setTargetType("comment");
            log.setTargetId(commentId);
            log.setDescription("删除评论");
            log.setCreateTime(new Date());
            adminDao.addAdminLog(log);
        }

        return result > 0;
    }

    @Override
    public List<AdminLog> getAdminLogs(int page, int pageSize) {
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数错误");
        }

        int offset = (page - 1) * pageSize;
        return adminDao.getAdminLogs(offset, pageSize);
    }

    private int getTodayPostsCount() {
        return 0;
    }

    private int getCurrentAdminId() {
        return 1;
    }
}