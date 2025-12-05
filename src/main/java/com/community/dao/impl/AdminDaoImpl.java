package com.community.dao.impl;

import com.community.dao.AdminDao;
import com.community.model.AdminLog;
import com.community.util.DBUtil;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class AdminDaoImpl implements AdminDao {

    // ==================== 辅助方法 ====================

    /**
     * 关闭数据库资源
     */
    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        DBUtil.close(conn, stmt, rs);
    }

    /**
     * 关闭数据库资源（不带ResultSet）
     */
    private void closeResources(Connection conn, Statement stmt) {
        closeResources(conn, stmt, null);
    }

    /**
     * 将ResultSet转换为AdminLog对象
     */
    private AdminLog resultSetToAdminLog(ResultSet rs) throws SQLException {
        AdminLog adminLog = new AdminLog(
                rs.getInt("id"),
                rs.getInt("admin_id"),
                rs.getString("action_type"),
                rs.getString("target_type"),
                rs.getInt("target_id"),
                rs.getString("details"),
                rs.getString("ip_address"),
                rs.getTimestamp("create_time")
        );
        return adminLog;
    }

    // ==================== 管理员日志相关方法 ====================

    /**
     * 添加管理员操作日志
     */
    @Override
    public boolean addAdminLog(AdminLog adminLog) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO admin_logs (admin_id, action_type, target_type, target_id, details, ip_address, create_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, adminLog.getAdminId());
            pstmt.setString(2, adminLog.getActionType());
            pstmt.setString(3, adminLog.getTargetType());

            // 处理可能为空的targetId
            if (adminLog.getTargetId() != null) {
                pstmt.setInt(4, adminLog.getTargetId());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }

            pstmt.setString(5, adminLog.getDetails());
            pstmt.setString(6, adminLog.getIpAddress());
            pstmt.setTimestamp(7, new Timestamp(adminLog.getCreateTime().getTime()));

            int result = pstmt.executeUpdate();
            return result > 0;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    /**
     * 根据ID获取管理员日志
     */
    @Override
    public AdminLog getAdminLogById(int logId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM admin_logs WHERE id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, logId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return resultSetToAdminLog(rs);
            }
            return null;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    /**
     * 获取管理员操作日志列表（带分页和条件查询）
     */
    @Override
    public List<AdminLog> getAdminLogs(int adminId, int page, int pageSize, Date startDate, Date endDate) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<AdminLog> adminLogs = new ArrayList<>();

        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM admin_logs WHERE 1=1");
            List<Object> params = new ArrayList<>();

            if (adminId > 0) {
                sql.append(" AND admin_id = ?");
                params.add(adminId);
            }

            if (startDate != null) {
                sql.append(" AND create_time >= ?");
                params.add(new Timestamp(startDate.getTime()));
            }

            if (endDate != null) {
                sql.append(" AND create_time <= ?");
                params.add(new Timestamp(endDate.getTime()));
            }

            sql.append(" ORDER BY create_time DESC LIMIT ? OFFSET ?");
            params.add(pageSize);
            params.add((page - 1) * pageSize);

            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                adminLogs.add(resultSetToAdminLog(rs));
            }

            return adminLogs;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    /**
     * 获取管理员操作日志数量（用于分页）
     */
    @Override
    public int getAdminLogCount(int adminId, Date startDate, Date endDate) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM admin_logs WHERE 1=1");
            List<Object> params = new ArrayList<>();

            if (adminId > 0) {
                sql.append(" AND admin_id = ?");
                params.add(adminId);
            }

            if (startDate != null) {
                sql.append(" AND create_time >= ?");
                params.add(new Timestamp(startDate.getTime()));
            }

            if (endDate != null) {
                sql.append(" AND create_time <= ?");
                params.add(new Timestamp(endDate.getTime()));
            }

            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    /**
     * 获取最近N条管理员操作日志
     */
    @Override
    public List<AdminLog> getRecentAdminLogs(int limit) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<AdminLog> adminLogs = new ArrayList<>();

        try {
            String sql = "SELECT al.* FROM admin_logs al " +
                    "ORDER BY al.create_time DESC LIMIT ?";

            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                AdminLog adminLog = resultSetToAdminLog(rs);
                adminLogs.add(adminLog);
            }

            return adminLogs;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    /**
     * 根据管理员ID获取日志（带管理员姓名）
     */
    @Override
    public List<AdminLog> getAdminLogsWithAdminName(int adminId, int page, int pageSize) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<AdminLog> adminLogs = new ArrayList<>();

        try {
            String sql = "SELECT al.* FROM admin_logs al " +
                    "WHERE al.admin_id = ? " +
                    "ORDER BY al.create_time DESC LIMIT ? OFFSET ?";

            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, adminId);
            pstmt.setInt(2, pageSize);
            pstmt.setInt(3, (page - 1) * pageSize);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                AdminLog adminLog = resultSetToAdminLog(rs);
                adminLogs.add(adminLog);
            }

            return adminLogs;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    /**
     * 删除指定ID的管理员日志
     */
    @Override
    public boolean deleteAdminLog(int logId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM admin_logs WHERE id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, logId);

            int result = pstmt.executeUpdate();
            return result > 0;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    /**
     * 批量删除管理员日志
     */
    @Override
    public int batchDeleteAdminLogs(List<Integer> logIds) throws SQLException {
        if (logIds == null || logIds.isEmpty()) {
            return 0;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < logIds.size(); i++) {
                if (i > 0) placeholders.append(",");
                placeholders.append("?");
            }

            String sql = "DELETE FROM admin_logs WHERE id IN (" + placeholders.toString() + ")";

            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            for (int i = 0; i < logIds.size(); i++) {
                pstmt.setInt(i + 1, logIds.get(i));
            }

            return pstmt.executeUpdate();
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    /**
     * 清空所有管理员日志（谨慎使用）
     */
    @Override
    public boolean clearAllAdminLogs() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM admin_logs";

            pstmt = conn.prepareStatement(sql);
            int result = pstmt.executeUpdate();
            return result >= 0;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }

    /**
     * 根据操作类型统计日志数量
     */
    @Override
    public List<String[]> countLogsByActionType() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String[]> result = new ArrayList<>();

        try {
            String sql = "SELECT action_type, COUNT(*) as count FROM admin_logs GROUP BY action_type ORDER BY count DESC";

            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String[] item = new String[2];
                item[0] = rs.getString("action_type");
                item[1] = String.valueOf(rs.getInt("count"));
                result.add(item);
            }

            return result;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    /**
     * 搜索管理员日志（支持模糊搜索）
     */
    @Override
    public List<AdminLog> searchAdminLogs(String keyword, int page, int pageSize) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<AdminLog> adminLogs = new ArrayList<>();

        try {
            String sql = "SELECT al.* FROM admin_logs al " +
                    "WHERE al.action_type LIKE ? OR al.target_type LIKE ? OR al.details LIKE ? " +
                    "ORDER BY al.create_time DESC LIMIT ? OFFSET ?";

            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            String likeKeyword = "%" + keyword + "%";

            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);
            pstmt.setInt(4, pageSize);
            pstmt.setInt(5, (page - 1) * pageSize);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                adminLogs.add(resultSetToAdminLog(rs));
            }

            return adminLogs;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    /**
     * 获取指定时间段内的日志统计
     */
    @Override
    public int getLogCountByTimeRange(Date startTime, Date endTime) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT COUNT(*) FROM admin_logs WHERE create_time BETWEEN ? AND ?";

            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setTimestamp(1, new Timestamp(startTime.getTime()));
            pstmt.setTimestamp(2, new Timestamp(endTime.getTime()));

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }
}