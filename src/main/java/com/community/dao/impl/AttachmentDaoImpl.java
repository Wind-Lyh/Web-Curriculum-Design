package com.community.dao.impl;

import com.community.dao.AttachmentDao;
import com.community.model.Attachment;
import com.community.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttachmentDaoImpl implements AttachmentDao {

    @Override
    public int insert(Attachment attachment) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO attachments (filename, file_path, file_type, file_size, user_id, post_id, comment_id, create_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, attachment.getFilename());
            pstmt.setString(2, attachment.getFilePath());
            pstmt.setString(3, attachment.getFileType());
            pstmt.setLong(4, attachment.getFileSize());
            pstmt.setInt(5, attachment.getUserId());

            if (attachment.getPostId() != null) {
                pstmt.setInt(6, attachment.getPostId());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }

            if (attachment.getCommentId() != null) {
                pstmt.setInt(7, attachment.getCommentId());
            } else {
                pstmt.setNull(7, java.sql.Types.INTEGER);
            }

            pstmt.setTimestamp(8, new java.sql.Timestamp(attachment.getCreateTime().getTime()));

            int affectedRows = pstmt.executeUpdate();

            // 获取生成的主键
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    attachment.setId(rs.getInt(1));
                }
            }

            return affectedRows;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public Attachment findById(Integer id) {
        if (id == null) {
            return null;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM attachments WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return resultSetToAttachment(rs);
            }

            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public List<Attachment> findByPostId(Integer postId) {
        if (postId == null) {
            return new ArrayList<>();
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Attachment> attachments = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM attachments WHERE post_id = ? ORDER BY create_time DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                attachments.add(resultSetToAttachment(rs));
            }

            return attachments;
        } catch (SQLException e) {
            e.printStackTrace();
            return attachments;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public List<Attachment> findByCommentId(Integer commentId) {
        if (commentId == null) {
            return new ArrayList<>();
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Attachment> attachments = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM attachments WHERE comment_id = ? ORDER BY create_time DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, commentId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                attachments.add(resultSetToAttachment(rs));
            }

            return attachments;
        } catch (SQLException e) {
            e.printStackTrace();
            return attachments;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public List<Attachment> findByUserId(Integer userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Attachment> attachments = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM attachments WHERE user_id = ? ORDER BY create_time DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                attachments.add(resultSetToAttachment(rs));
            }

            return attachments;
        } catch (SQLException e) {
            e.printStackTrace();
            return attachments;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public int delete(Integer id) {
        if (id == null) {
            return 0;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM attachments WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt);
        }
    }

    @Override
    public int deleteByUserAndPost(Integer userId, Integer postId) {
        if (userId == null || postId == null) {
            return 0;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM attachments WHERE user_id = ? AND post_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt);
        }
    }

    @Override
    public int deleteByUserAndComment(Integer userId, Integer commentId) {
        if (userId == null || commentId == null) {
            return 0;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM attachments WHERE user_id = ? AND comment_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, commentId);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt);
        }
    }

    @Override
    public int countByPost(Integer postId) {
        if (postId == null) {
            return 0;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM attachments WHERE post_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public int countByComment(Integer commentId) {
        if (commentId == null) {
            return 0;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM attachments WHERE comment_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, commentId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 将ResultSet转换为Attachment对象
     * @param rs ResultSet
     * @return Attachment对象
     * @throws SQLException
     */
    private Attachment resultSetToAttachment(ResultSet rs) throws SQLException {
        Attachment attachment = new Attachment();
        attachment.setId(rs.getInt("id"));
        attachment.setFilename(rs.getString("filename"));
        attachment.setFilePath(rs.getString("file_path"));
        attachment.setFileType(rs.getString("file_type"));
        attachment.setFileSize(rs.getLong("file_size"));
        attachment.setUserId(rs.getInt("user_id"));

        int postId = rs.getInt("post_id");
        if (!rs.wasNull()) {
            attachment.setPostId(postId);
        }

        int commentId = rs.getInt("comment_id");
        if (!rs.wasNull()) {
            attachment.setCommentId(commentId);
        }

        attachment.setCreateTime(rs.getTimestamp("create_time"));
        return attachment;
    }
}