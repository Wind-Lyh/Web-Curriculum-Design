package com.community.dao.impl;

import com.community.dao.LikeDao;
import com.community.model.Like;
import com.community.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LikeDaoImpl implements LikeDao {

    @Override
    public int insert(Like like) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO likes (user_id, target_type, target_id, create_time) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, like.getUserId());
            pstmt.setString(2, like.getTargetType());
            pstmt.setInt(3, like.getTargetId());
            pstmt.setTimestamp(4, new Timestamp(like.getCreateTime().getTime()));

            int result = pstmt.executeUpdate();

            if (result > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public int delete(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM likes WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public Like findByUserAndTarget(int userId, String targetType, int targetId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Like like = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM likes WHERE user_id = ? AND target_type = ? AND target_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, targetType);
            pstmt.setInt(3, targetId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                like = new Like();
                like.setId(rs.getInt("id"));
                like.setUserId(rs.getInt("user_id"));
                like.setTargetType(rs.getString("target_type"));
                like.setTargetId(rs.getInt("target_id"));
                like.setCreateTime(rs.getTimestamp("create_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return like;
    }

    @Override
    public int deleteByUserAndTarget(int userId, String targetType, int targetId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM likes WHERE user_id = ? AND target_type = ? AND target_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, targetType);
            pstmt.setInt(3, targetId);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public int countByTarget(String targetType, int targetId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM likes WHERE target_type = ? AND target_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, targetType);
            pstmt.setInt(2, targetId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return 0;
    }

    @Override
    public int countByUser(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM likes WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return 0;
    }
}