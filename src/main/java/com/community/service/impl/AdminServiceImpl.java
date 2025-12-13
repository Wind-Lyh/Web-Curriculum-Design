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

import java.sql.SQLException;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

        // 用户统计
        stats.setTotalUsers(userDao.count());

        // 使用Map条件统计活跃用户（status=0）
        Map<String, Object> activeCondition = new HashMap<>();
        activeCondition.put("status", 0);
        stats.setActiveUsers(userDao.countByCondition(activeCondition));

        // 使用Map条件统计封禁用户（status=1）
        Map<String, Object> bannedCondition = new HashMap<>();
        bannedCondition.put("status", 1);
        stats.setBannedUsers(userDao.countByCondition(bannedCondition));

        // 帖子统计
        stats.setTotalPosts(postDao.count());
        stats.setTodayPosts(getTodayPostsCount());

        // 评论统计
        stats.setTotalComments(commentDao.countAll());

        // 访问统计
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

        // 构建条件Map
        Map<String, Object> condition = new HashMap<>();
        if (!StringUtil.isEmpty(keyword)) {
            condition.put("keyword", keyword);
        }
        if (!StringUtil.isEmpty(status)) {
            condition.put("status", Integer.parseInt(status));
        }

        // 使用search方法
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
            log.setActionType("ban_user");
            log.setTargetType("user");
            log.setTargetId(userId);
            log.setDetails("封禁用户：" + user.getUsername() + "，原因：" + reason);
            log.setCreateTime(new Date());
            try {
                adminDao.addAdminLog(log);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
            log.setActionType("UNBAN_USER");
            log.setTargetType("user");
            log.setTargetId(userId);
            log.setDetails("解封用户：" + user.getUsername());
            log.setCreateTime(new Date());
            try {
                adminDao.addAdminLog(log);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
            log.setActionType("RESET_PASSWORD");
            log.setTargetType("user");
            log.setTargetId(userId);
            log.setDetails("重置用户密码：" + user.getUsername());
            log.setCreateTime(new Date());
            try {
                adminDao.addAdminLog(log);
            } catch (SQLException e) {
                System.out.println("resetUserPassword错误");
                throw new RuntimeException(e);
            }
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
            log.setActionType("DELETE_POST");
            log.setTargetType("post");
            log.setTargetId(postId);
            log.setDetails("删除帖子：" + post.getTitle());
            log.setCreateTime(new Date());
            try {
                adminDao.addAdminLog(log);
            } catch (SQLException e) {
                System.out.println("deletePost错误");
                throw new RuntimeException(e);
            }
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
            log.setActionType("DELETE_COMMENT");
            log.setTargetType("comment");
            log.setTargetId(commentId);
            log.setDetails("删除评论");
            log.setCreateTime(new Date());
            try {
                adminDao.addAdminLog(log);
            } catch (SQLException e) {
                System.out.println("deleteComment错误");
                throw new RuntimeException(e);
            }
        }

        return result > 0;
    }

    @Override
    public List<AdminLog> getAdminLogs(int adminId, int page, int pageSize){
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数错误");
        }

        int offset = (page - 1) * pageSize;
        try {
            return adminDao.getAdminLogsWithAdminName(adminId,page,pageSize);
        } catch (SQLException e) {
            System.out.println("getAdminLogs错误");
            throw new RuntimeException(e);
        }
    }

    private int getTodayPostsCount() {
        // 简化实现，返回0
        return 0;
    }

    private int getCurrentAdminId() {
        // 需要从Session或ThreadLocal中获取当前管理员ID
        // 简化处理，返回1
        return 1;
    }
}