package com.community.dao.impl;

import com.community.dao.NotificationDao;
import com.community.model.Notification;
import com.community.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationDaoImpl implements NotificationDao {

    // 1. 创建个人通知
    @Override
    public boolean insertNotification(Notification notification) {
        String sql = "INSERT INTO notifications(user_id, type, title, content, related_id, is_read, create_time) VALUES(?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setInt(1, notification.getUserId());
            ps.setString(2, notification.getType());
            ps.setString(3, notification.getTitle());
            ps.setString(4, notification.getContent());

            if (notification.getRelatedId() != null) {
                ps.setInt(5, notification.getRelatedId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.setInt(6, notification.getIsRead());
            ps.setTimestamp(7, new Timestamp(notification.getCreateTime().getTime()));

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 2. 获取用户所有通知
    @Override
    public List<Notification> getNotificationsByUserId(Integer userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY create_time DESC, is_read ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Notification notification = extractNotificationFromResultSet(rs);
                notifications.add(notification);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return notifications;
    }

    // 2.1 按类型查询通知（无分页）
    @Override
    public List<Notification> getNotificationsByType(Integer userId, String type) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND type = ? ORDER BY create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, type);
            rs = ps.executeQuery();

            while (rs.next()) {
                Notification notification = extractNotificationFromResultSet(rs);
                notifications.add(notification);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return notifications;
    }

    // 2.2 按类型分页查询通知（新增方法）
    @Override
    public List<Notification> getNotificationsByType(int userId, String type, int offset, int limit) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND type = ? ORDER BY create_time DESC LIMIT ?, ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setInt(3, offset);
            ps.setInt(4, limit);
            rs = ps.executeQuery();

            while (rs.next()) {
                Notification notification = extractNotificationFromResultSet(rs);
                notifications.add(notification);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return notifications;
    }

    // 2.3 查询未读通知
    @Override
    public List<Notification> getUnreadNotifications(Integer userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = 0 ORDER BY create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Notification notification = extractNotificationFromResultSet(rs);
                notifications.add(notification);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return notifications;
    }

    // 3. 根据ID获取通知
    @Override
    public Notification getNotificationById(int notificationId) {
        String sql = "SELECT * FROM notifications WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Notification notification = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, notificationId);
            rs = ps.executeQuery();

            if (rs.next()) {
                notification = extractNotificationFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return notification;
    }

    // 4. 删除通知
    @Override
    public int deleteNotification(int notificationId) {
        String sql = "DELETE FROM notifications WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, notificationId);

            return ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // 返回-1表示删除失败
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 5. 标记单条通知为已读
    @Override
    public boolean markAsRead(Integer notificationId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, notificationId);

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 5.1 批量标记用户所有通知为已读
    @Override
    public boolean markAllAsRead(Integer userId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            int result = ps.executeUpdate();
            return result >= 0; // 可能没有未读通知，返回0也是成功的

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 5.2 按类型批量标记已读
    @Override
    public boolean markTypeAsRead(Integer userId, String type) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND type = ? AND is_read = 0";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, type);

            int result = ps.executeUpdate();
            return result >= 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 6. 统计未读通知数量
    @Override
    public Integer countUnreadNotifications(Integer userId) {
        String sql = "SELECT COUNT(*) as count FROM notifications WHERE user_id = ? AND is_read = 0";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return 0;
    }

    // 6.1 按类型统计未读数
    @Override
    public Map<String, Integer> countUnreadByType(Integer userId) {
        Map<String, Integer> typeCounts = new HashMap<>();
        String sql = "SELECT type, COUNT(*) as count FROM notifications WHERE user_id = ? AND is_read = 0 GROUP BY type";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                typeCounts.put(rs.getString("type"), rs.getInt("count"));
            }

            // 确保所有类型都有值（即使为0）
            String[] types = {"reply", "like", "system"};
            for (String type : types) {
                if (!typeCounts.containsKey(type)) {
                    typeCounts.put(type, 0);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return typeCounts;
    }

    // 7. 分页查询用户通知
    @Override
    public List<Notification> getNotificationsByPage(Integer userId, int page, int pageSize) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY create_time DESC LIMIT ?, ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            // 计算起始位置
            int start = (page - 1) * pageSize;
            ps.setInt(2, start);
            ps.setInt(3, pageSize);

            rs = ps.executeQuery();

            while (rs.next()) {
                Notification notification = extractNotificationFromResultSet(rs);
                notifications.add(notification);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return notifications;
    }

    // 7.1 获取用户通知总数
    @Override
    public int countUserNotifications(Integer userId) {
        String sql = "SELECT COUNT(*) as count FROM notifications WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return 0;
    }

    // 7.2 获取用户未读通知分页
    @Override
    public List<Notification> getUnreadNotificationsByPage(Integer userId, int page, int pageSize) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = 0 ORDER BY create_time DESC LIMIT ?, ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            // 计算起始位置
            int start = (page - 1) * pageSize;
            ps.setInt(2, start);
            ps.setInt(3, pageSize);

            rs = ps.executeQuery();

            while (rs.next()) {
                Notification notification = extractNotificationFromResultSet(rs);
                notifications.add(notification);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return notifications;
    }

    // 7.3 按类型分页查询通知（带总数统计）
    @Override
    public int countNotificationsByType(int userId, String type) {
        String sql = "SELECT COUNT(*) as count FROM notifications WHERE user_id = ? AND type = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, type);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return 0;
    }

    // 8. 批量删除通知
    @Override
    public int deleteNotifications(List<Integer> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return 0;
        }

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();

            // 构建IN子句
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < notificationIds.size(); i++) {
                inClause.append("?");
                if (i < notificationIds.size() - 1) {
                    inClause.append(",");
                }
            }

            String sql = "DELETE FROM notifications WHERE id IN (" + inClause + ")";
            ps = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < notificationIds.size(); i++) {
                ps.setInt(i + 1, notificationIds.get(i));
            }

            return ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 8.1 按条件删除通知
    @Override
    public int deleteNotificationsByCondition(int userId, String type, Integer daysAgo) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();

            StringBuilder sql = new StringBuilder("DELETE FROM notifications WHERE user_id = ?");
            List<Object> params = new ArrayList<>();
            params.add(userId);

            if (type != null && !type.isEmpty()) {
                sql.append(" AND type = ?");
                params.add(type);
            }

            if (daysAgo != null && daysAgo > 0) {
                sql.append(" AND create_time < DATE_SUB(NOW(), INTERVAL ? DAY)");
                params.add(daysAgo);
            }

            ps = conn.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                }
            }

            return ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 辅助方法：从ResultSet中提取Notification对象
    private Notification extractNotificationFromResultSet(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getInt("id"));
        notification.setUserId(rs.getInt("user_id"));
        notification.setType(rs.getString("type"));
        notification.setTitle(rs.getString("title"));
        notification.setContent(rs.getString("content"));
        notification.setRelatedId(rs.getInt("related_id"));
        notification.setIsRead(rs.getInt("is_read"));
        notification.setCreateTime(rs.getTimestamp("create_time"));
        return notification;
    }
}