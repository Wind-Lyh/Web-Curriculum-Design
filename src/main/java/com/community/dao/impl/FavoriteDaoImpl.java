package com.community.dao.impl;

import com.community.dao.FavoriteDao;
import com.community.model.Favorite;
import com.community.model.Post;
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

    @Override
    public List<Post> findPostsByUserId(int userId, int offset, int limit) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Post> postList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            // 使用多表连接查询，获取帖子信息和关联的用户、版块信息
            String sql = "SELECT " +
                    "p.id, p.title, p.content, p.user_id, p.section_id, " +
                    "p.view_count, p.like_count, p.comment_count, p.is_deleted, " +
                    "p.create_time, p.update_time, " +
                    "u.username, u.nickname, u.avatar_url " +
                    "FROM favorites f " +
                    "INNER JOIN posts p ON f.post_id = p.id " +
                    "INNER JOIN users u ON p.user_id = u.id " +
                    "WHERE f.user_id = ? " +
                    "ORDER BY f.create_time DESC " +
                    "LIMIT ?, ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, limit);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Post post = new Post();

                // 设置帖子基本属性
                post.setId(rs.getInt("id"));
                post.setTitle(rs.getString("title"));
                post.setContent(rs.getString("content"));
                post.setUserId(rs.getInt("user_id"));
                post.setSectionId(rs.getInt("section_id"));
                post.setViewCount(rs.getInt("view_count"));
                post.setLikeCount(rs.getInt("like_count"));
                post.setCommentCount(rs.getInt("comment_count"));
                post.setStatus(rs.getInt("status"));

                // 设置时间字段
                Timestamp createTime = rs.getTimestamp("create_time");
                if (createTime != null) {
                    post.setCreateTime(new Date(createTime.getTime()));
                }

                Timestamp updateTime = rs.getTimestamp("update_time");
                if (updateTime != null) {
                    post.setUpdateTime(new Date(updateTime.getTime()));
                }

                // 设置关联字段
                post.setUsername(rs.getString("username"));
                post.setNickname(rs.getString("nickname"));
                post.setAvatarUrl(rs.getString("avatar_url"));
                post.setSectionName(rs.getString("section_name"));

                postList.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return postList;
    }

    @Override
    public int countByPost(int postId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM favorites WHERE post_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);
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