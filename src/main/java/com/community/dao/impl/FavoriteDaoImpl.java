package com.community.dao.impl;

import com.community.dao.FavoriteDao;
import com.community.model.Favorite;
import com.community.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoriteDaoImpl implements FavoriteDao {

    @Override
    public int insert(Favorite favorite) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO favorites (user_id, post_id, create_time) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, favorite.getUserId());
            pstmt.setInt(2, favorite.getPostId());
            pstmt.setTimestamp(3, new Timestamp(favorite.getCreateTime().getTime()));

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
            String sql = "DELETE FROM favorites WHERE id = ?";
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
    public int deleteByUserAndPost(int userId, int postId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM favorites WHERE user_id = ? AND post_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public Favorite findByUserAndPost(int userId, int postId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Favorite favorite = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM favorites WHERE user_id = ? AND post_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                favorite = new Favorite();
                favorite.setId(rs.getInt("id"));
                favorite.setUserId(rs.getInt("user_id"));
                favorite.setPostId(rs.getInt("post_id"));
                favorite.setCreateTime(rs.getTimestamp("create_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return favorite;
    }

    @Override
    public List<Favorite> findByUserId(int userId, int page, int size) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Favorite> favoriteList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT f.*, p.title as post_title FROM favorites f " +
                    "LEFT JOIN posts p ON f.post_id = p.id " +
                    "WHERE f.user_id = ? ORDER BY f.create_time DESC LIMIT ?, ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, (page - 1) * size);
            pstmt.setInt(3, size);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Favorite favorite = new Favorite();
                favorite.setId(rs.getInt("id"));
                favorite.setUserId(rs.getInt("user_id"));
                favorite.setPostId(rs.getInt("post_id"));
                favorite.setCreateTime(rs.getTimestamp("create_time"));
                // 可以设置帖子标题等额外信息
                favoriteList.add(favorite);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return favoriteList;
    }

    @Override
    public int countByUser(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM favorites WHERE user_id = ?";
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