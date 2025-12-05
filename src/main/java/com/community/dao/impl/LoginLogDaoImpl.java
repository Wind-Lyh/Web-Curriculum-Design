package com.community.dao.impl;

import com.community.dao.LoginLogDao;
import com.community.model.LoginLog;
import com.community.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoginLogDaoImpl implements LoginLogDao {
    @Override
    public int insert(LoginLog loginLog) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO login_logs (user_id, ip_address, user_agent, login_time) VALUES (?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, loginLog.getUserId());
            pstmt.setString(2, loginLog.getIpAddress());
            pstmt.setString(3, loginLog.getUserAgent());
            pstmt.setTimestamp(4, new Timestamp(loginLog.getLoginTime().getTime()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    loginLog.setId(rs.getInt(1));
                }
            }

            return affectedRows;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public LoginLog findById(Integer id) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM login_logs WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToLoginLog(rs);
            }

            return null;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public List<LoginLog> findByUserId(Integer userId) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<LoginLog> logs = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM login_logs WHERE user_id = ? ORDER BY login_time DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                logs.add(mapResultSetToLoginLog(rs));
            }

            return logs;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public List<LoginLog> findByUserIdWithPagination(Integer userId, int page, int size) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<LoginLog> logs = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM login_logs WHERE user_id = ? ORDER BY login_time DESC LIMIT ? OFFSET ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, size);
            pstmt.setInt(3, (page - 1) * size);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                logs.add(mapResultSetToLoginLog(rs));
            }

            return logs;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public List<LoginLog> findByTimeRange(java.util.Date startTime, java.util.Date endTime) throws Exception {
        return Collections.emptyList();
    }

    @Override
    public List<LoginLog> findByTimeRange(Date startTime, Date endTime) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<LoginLog> logs = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM login_logs WHERE login_time BETWEEN ? AND ? ORDER BY login_time DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setTimestamp(1, new Timestamp(startTime.getTime()));
            pstmt.setTimestamp(2, new Timestamp(endTime.getTime()));

            rs = pstmt.executeQuery();

            while (rs.next()) {
                logs.add(mapResultSetToLoginLog(rs));
            }

            return logs;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public List<LoginLog> findByIpAddress(String ipAddress) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<LoginLog> logs = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM login_logs WHERE ip_address = ? ORDER BY login_time DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ipAddress);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                logs.add(mapResultSetToLoginLog(rs));
            }

            return logs;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public int countByUserId(Integer userId) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM login_logs WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public LoginLog findLatestByUserId(Integer userId) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM login_logs WHERE user_id = ? ORDER BY login_time DESC LIMIT 1";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToLoginLog(rs);
            }

            return null;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public int delete(Integer id) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM login_logs WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            return pstmt.executeUpdate();
        } finally {
            
            DBUtil.close( conn,pstmt,null);
        }
    }

    @Override
    public int deleteByUserId(Integer userId) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM login_logs WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            return pstmt.executeUpdate();
        } finally {
            DBUtil.close( conn,pstmt,null);
        }
    }

    @Override
    public int deleteBeforeTime(Date time) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM login_logs WHERE login_time < ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setTimestamp(1, new Timestamp(time.getTime()));

            return pstmt.executeUpdate();
        } finally {
            DBUtil.close( conn,pstmt,null);
        }
    }

    // 辅助方法：将ResultSet映射为LoginLog对象
    private LoginLog mapResultSetToLoginLog(ResultSet rs) throws SQLException {
        LoginLog log = new LoginLog();
        log.setId(rs.getInt("id"));
        log.setUserId(rs.getInt("user_id"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setUserAgent(rs.getString("user_agent"));
        log.setLoginTime(rs.getTimestamp("login_time"));
        return log;
    }
}