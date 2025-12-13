package com.community.service;

import com.community.model.AdminLog;
import com.community.model.User;
import java.util.List;

public interface AdminService {
    /**
     * 获取仪表板统计
     * @return 统计信息
     */
    DashboardStats getDashboardStats();

    /**
     * 获取所有用户
     * @param page 页码
     * @param pageSize 每页大小
     * @return 用户列表
     */
    List<User> getAllUsers(int page, int pageSize);

    /**
     * 搜索用户
     * @param keyword 关键词
     * @param status 用户状态
     * @param page 页码
     * @param pageSize 每页大小
     * @return 用户列表
     */
    List<User> searchUsers(String keyword, String status, int page, int pageSize);

    /**
     * 封禁用户
     * @param userId 用户ID
     * @param reason 封禁原因
     * @return 是否成功
     */
    boolean banUser(int userId, String reason);

    /**
     * 解封用户
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean unbanUser(int userId);

    /**
     * 重置用户密码
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean resetUserPassword(int userId);

    /**
     * 删除帖子（管理员）
     * @param postId 帖子ID
     * @return 是否成功
     */
    boolean deletePost(int postId);

    /**
     * 删除评论（管理员）
     * @param commentId 评论ID
     * @return 是否成功
     */
    boolean deleteComment(int commentId);

    /**
     * 获取管理员操作日志
     * @param page 页码
     * @param pageSize 每页大小
     * @return 日志列表
     */
    List<AdminLog> getAdminLogs(int adminId, int page, int pageSize);

    /**
     * 仪表板统计信息类
     */
    class DashboardStats {
        private int totalUsers;
        private int activeUsers;
        private int bannedUsers;
        private int totalPosts;
        private int todayPosts;
        private int totalComments;
        private int dailyVisits;
        private int monthlyVisits;

        // Getters and Setters
        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

        public int getActiveUsers() { return activeUsers; }
        public void setActiveUsers(int activeUsers) { this.activeUsers = activeUsers; }

        public int getBannedUsers() { return bannedUsers; }
        public void setBannedUsers(int bannedUsers) { this.bannedUsers = bannedUsers; }

        public int getTotalPosts() { return totalPosts; }
        public void setTotalPosts(int totalPosts) { this.totalPosts = totalPosts; }

        public int getTodayPosts() { return todayPosts; }
        public void setTodayPosts(int todayPosts) { this.todayPosts = todayPosts; }

        public int getTotalComments() { return totalComments; }
        public void setTotalComments(int totalComments) { this.totalComments = totalComments; }

        public int getDailyVisits() { return dailyVisits; }
        public void setDailyVisits(int dailyVisits) { this.dailyVisits = dailyVisits; }

        public int getMonthlyVisits() { return monthlyVisits; }
        public void setMonthlyVisits(int monthlyVisits) { this.monthlyVisits = monthlyVisits; }
    }
}