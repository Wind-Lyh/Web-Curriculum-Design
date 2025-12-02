package com.community.dao.impl;

import com.community.dao.CommentDao;
import com.community.model.Comment;
import com.community.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDaoImpl implements CommentDao {

    @Override
    public int insert(Comment comment) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO comments (content, user_id, post_id, parent_id, like_count, " +
                    "status, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, comment.getContent());
            pstmt.setInt(2, comment.getUserId());
            pstmt.setInt(3, comment.getPostId());
            pstmt.setInt(4, comment.getParentId());
            pstmt.setInt(5, comment.getLikeCount());
            pstmt.setInt(6, comment.getStatus());
            pstmt.setTimestamp(7, new Timestamp(comment.getCreateTime().getTime()));
            pstmt.setTimestamp(8, new Timestamp(comment.getUpdateTime().getTime()));

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
    public Comment findById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Comment comment = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT c.*, u.username, u.nickname, u.avatar_url FROM comments c " +
                    "LEFT JOIN users u ON c.user_id = u.id WHERE c.id = ? AND c.status != 1";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                comment = resultSetToComment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return comment;
    }

    @Override
    public int update(Comment comment) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE comments SET content = ?, like_count = ?, status = ?, " +
                    "update_time = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, comment.getContent());
            pstmt.setInt(2, comment.getLikeCount());
            pstmt.setInt(3, comment.getStatus());
            pstmt.setTimestamp(4, new Timestamp(comment.getUpdateTime().getTime()));
            pstmt.setInt(5, comment.getId());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public int delete(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            // 逻辑删除，设置状态为1
            String sql = "UPDATE comments SET status = 1 WHERE id = ?";
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
    public List<Comment> findByPostId(int postId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Comment> commentList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT c.*, u.username, u.nickname, u.avatar_url FROM comments c " +
                    "LEFT JOIN users u ON c.user_id = u.id " +
                    "WHERE c.post_id = ? AND c.status != 1 " +
                    "ORDER BY c.create_time ASC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                commentList.add(resultSetToComment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return commentList;
    }

    @Override
    public List<Comment> findByUserId(int userId, int page, int size) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Comment> commentList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT c.*, u.username, u.nickname, u.avatar_url FROM comments c " +
                    "LEFT JOIN users u ON c.user_id = u.id " +
                    "WHERE c.user_id = ? AND c.status != 1 " +
                    "ORDER BY c.create_time DESC LIMIT ?, ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, (page - 1) * size);
            pstmt.setInt(3, size);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                commentList.add(resultSetToComment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return commentList;
    }

    @Override
    public List<Comment> findByParentId(int parentId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Comment> commentList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT c.*, u.username, u.nickname, u.avatar_url FROM comments c " +
                    "LEFT JOIN users u ON c.user_id = u.id " +
                    "WHERE c.parent_id = ? AND c.status != 1 " +
                    "ORDER BY c.create_time ASC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, parentId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                commentList.add(resultSetToComment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return commentList;
    }

    @Override
    public int countByPost(int postId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM comments WHERE post_id = ? AND status != 1";
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

    @Override
    public int countByUser(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM comments WHERE user_id = ? AND status != 1";
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

    /**
     * 将ResultSet转换为Comment对象
     */
    private Comment resultSetToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getInt("id"));
        comment.setContent(rs.getString("content"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setPostId(rs.getInt("post_id"));
        comment.setParentId(rs.getInt("parent_id"));
        comment.setLikeCount(rs.getInt("like_count"));
        comment.setStatus(rs.getInt("status"));
        comment.setCreateTime(rs.getTimestamp("create_time"));
        comment.setUpdateTime(rs.getTimestamp("update_time"));

        // 用户信息
        comment.setUsername(rs.getString("username"));
        comment.setNickname(rs.getString("nickname"));
        comment.setAvatarUrl(rs.getString("avatar_url"));

        return comment;
    }
}