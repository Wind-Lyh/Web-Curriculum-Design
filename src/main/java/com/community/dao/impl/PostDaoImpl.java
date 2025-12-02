package com.community.dao.impl;

import com.community.dao.PostDao;
import com.community.model.Post;
import com.community.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDaoImpl implements PostDao {

    @Override
    public int insert(Post post) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO posts (title, content, user_id, section_id, view_count, " +
                    "like_count, comment_count, status, create_time, update_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, post.getTitle());
            pstmt.setString(2, post.getContent());
            pstmt.setInt(3, post.getUserId());
            pstmt.setInt(4, post.getSectionId());
            pstmt.setInt(5, post.getViewCount());
            pstmt.setInt(6, post.getLikeCount());
            pstmt.setInt(7, post.getCommentCount());
            pstmt.setInt(8, post.getStatus());
            pstmt.setTimestamp(9, new Timestamp(post.getCreateTime().getTime()));
            pstmt.setTimestamp(10, new Timestamp(post.getUpdateTime().getTime()));

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
    public Post findById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Post post = null;

        try {
            conn = DBUtil.getConnection();
            // 联表查询，获取用户信息和版块信息
            String sql = "SELECT p.*, u.username, u.nickname, u.avatar_url, s.name as section_name " +
                    "FROM posts p " +
                    "LEFT JOIN users u ON p.user_id = u.id " +
                    "LEFT JOIN sections s ON p.section_id = s.id " +
                    "WHERE p.id = ? AND p.status != 3";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                post = resultSetToPost(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return post;
    }

    @Override
    public int update(Post post) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE posts SET title = ?, content = ?, section_id = ?, " +
                    "view_count = ?, like_count = ?, comment_count = ?, status = ?, " +
                    "update_time = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, post.getTitle());
            pstmt.setString(2, post.getContent());
            pstmt.setInt(3, post.getSectionId());
            pstmt.setInt(4, post.getViewCount());
            pstmt.setInt(5, post.getLikeCount());
            pstmt.setInt(6, post.getCommentCount());
            pstmt.setInt(7, post.getStatus());
            pstmt.setTimestamp(8, new Timestamp(post.getUpdateTime().getTime()));
            pstmt.setInt(9, post.getId());

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
            // 逻辑删除，设置状态为3
            String sql = "UPDATE posts SET status = 3 WHERE id = ?";
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
    public List<Post> findBySectionId(int sectionId, int page, int size) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Post> postList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT p.*, u.username, u.nickname, u.avatar_url, s.name as section_name " +
                    "FROM posts p " +
                    "LEFT JOIN users u ON p.user_id = u.id " +
                    "LEFT JOIN sections s ON p.section_id = s.id " +
                    "WHERE p.section_id = ? AND p.status != 3 " +
                    "ORDER BY p.create_time DESC LIMIT ?, ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sectionId);
            pstmt.setInt(2, (page - 1) * size);
            pstmt.setInt(3, size);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                postList.add(resultSetToPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return postList;
    }

    @Override
    public List<Post> findByUserId(int userId, int page, int size) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Post> postList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT p.*, u.username, u.nickname, u.avatar_url, s.name as section_name " +
                    "FROM posts p " +
                    "LEFT JOIN users u ON p.user_id = u.id " +
                    "LEFT JOIN sections s ON p.section_id = s.id " +
                    "WHERE p.user_id = ? AND p.status != 3 " +
                    "ORDER BY p.create_time DESC LIMIT ?, ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, (page - 1) * size);
            pstmt.setInt(3, size);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                postList.add(resultSetToPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return postList;
    }

    @Override
    public List<Post> findHotPosts(int limit) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Post> postList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT p.*, u.username, u.nickname, u.avatar_url, s.name as section_name " +
                    "FROM posts p " +
                    "LEFT JOIN users u ON p.user_id = u.id " +
                    "LEFT JOIN sections s ON p.section_id = s.id " +
                    "WHERE p.status != 3 " +
                    "ORDER BY p.view_count DESC LIMIT ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                postList.add(resultSetToPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return postList;
    }

    @Override
    public List<Post> findLatestPosts(int limit) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Post> postList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT p.*, u.username, u.nickname, u.avatar_url, s.name as section_name " +
                    "FROM posts p " +
                    "LEFT JOIN users u ON p.user_id = u.id " +
                    "LEFT JOIN sections s ON p.section_id = s.id " +
                    "WHERE p.status != 3 " +
                    "ORDER BY p.create_time DESC LIMIT ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                postList.add(resultSetToPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return postList;
    }

    @Override
    public List<Post> search(String keyword, int page, int size) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Post> postList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT p.*, u.username, u.nickname, u.avatar_url, s.name as section_name " +
                    "FROM posts p " +
                    "LEFT JOIN users u ON p.user_id = u.id " +
                    "LEFT JOIN sections s ON p.section_id = s.id " +
                    "WHERE (p.title LIKE ? OR p.content LIKE ?) AND p.status != 3 " +
                    "ORDER BY p.create_time DESC LIMIT ?, ?";
            pstmt = conn.prepareStatement(sql);
            String likeKeyword = "%" + keyword + "%";
            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setInt(3, (page - 1) * size);
            pstmt.setInt(4, size);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                postList.add(resultSetToPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return postList;
    }

    @Override
    public int increaseViewCount(int postId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE posts SET view_count = view_count + 1 WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public int increaseLikeCount(int postId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE posts SET like_count = like_count + 1 WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public int decreaseLikeCount(int postId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE posts SET like_count = like_count - 1 WHERE id = ? AND like_count > 0";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public int increaseCommentCount(int postId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE posts SET comment_count = comment_count + 1 WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public int decreaseCommentCount(int postId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE posts SET comment_count = comment_count - 1 WHERE id = ? AND comment_count > 0";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public int count() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM posts WHERE status != 3";
            pstmt = conn.prepareStatement(sql);
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
    public int countBySection(int sectionId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM posts WHERE section_id = ? AND status != 3";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sectionId);
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
            String sql = "SELECT COUNT(*) FROM posts WHERE user_id = ? AND status != 3";
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
     * 将ResultSet转换为Post对象
     */
    private Post resultSetToPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getInt("id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setUserId(rs.getInt("user_id"));
        post.setSectionId(rs.getInt("section_id"));
        post.setViewCount(rs.getInt("view_count"));
        post.setLikeCount(rs.getInt("like_count"));
        post.setCommentCount(rs.getInt("comment_count"));
        post.setStatus(rs.getInt("status"));
        post.setCreateTime(rs.getTimestamp("create_time"));
        post.setUpdateTime(rs.getTimestamp("update_time"));

        // 用户信息
        post.setUsername(rs.getString("username"));
        post.setNickname(rs.getString("nickname"));
        post.setAvatarUrl(rs.getString("avatar_url"));

        // 版块信息
        post.setSectionName(rs.getString("section_name"));

        return post;
    }
}