package com.community.dao.impl;

import com.community.dao.BrowseHistoryDao;
import com.community.util.DBUtil;
import com.community.model.BrowseHistory;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowseHistoryDaoImpl implements BrowseHistoryDao {

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
     * 将ResultSet转换为BrowseHistory对象
     */
    private BrowseHistory resultSetToBrowseHistory(ResultSet rs) throws SQLException {
        return new BrowseHistory(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("post_id"),
                rs.getTimestamp("browse_time")
        );
    }

    /**
     * 执行更新操作
     */
    private int executeUpdate(String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            return pstmt.executeUpdate();
        } finally {
            closeResources(conn, pstmt);
        }
    }

    // ==================== 基础操作功能 ====================

    /**
     * 1. 添加浏览记录
     * 功能描述：记录用户浏览帖子的行为
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否添加成功
     * @throws SQLException
     */
    public boolean addBrowseHistory(int userId, int postId) throws SQLException {
        // 先检查是否已存在相同的浏览记录
        if (checkUserViewedPost(userId, postId)) {
            // 如果已存在，更新浏览时间
            String updateSql = "UPDATE browse_history SET browse_time = NOW() WHERE user_id = ? AND post_id = ?";
            return executeUpdate(updateSql, userId, postId) > 0;
        } else {
            // 如果不存在，插入新记录
            String sql = "INSERT INTO browse_history (user_id, post_id, browse_time) VALUES (?, ?, NOW())";
            return executeUpdate(sql, userId, postId) > 0;
        }
    }

    /**
     * 2. 根据ID获取浏览记录
     * 功能描述：通过浏览记录ID查询单条记录详情
     * @param historyId 浏览记录ID
     * @return BrowseHistory对象，不存在则返回null
     * @throws SQLException
     */
    public BrowseHistory getBrowseHistoryById(int historyId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM browse_history WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, historyId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return resultSetToBrowseHistory(rs);
            }
            return null;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    /**
     * 3. 清空用户浏览历史
     * 功能描述：删除指定用户的所有浏览记录
     * @param userId 用户ID
     * @return 删除的记录数量
     * @throws SQLException
     */
    public int clearUserBrowseHistory(int userId) throws SQLException {
        String sql = "DELETE FROM browse_history WHERE user_id = ?";
        return executeUpdate(sql, userId);
    }

    /**
     * 4. 删除浏览记录
     * 功能描述：删除单条浏览记录
     * @param historyId 浏览记录ID
     * @return 是否删除成功
     * @throws SQLException
     */
    public boolean deleteBrowseHistory(int historyId) throws SQLException {
        String sql = "DELETE FROM browse_history WHERE id = ?";
        return executeUpdate(sql, historyId) > 0;
    }

    // ==================== 用户相关查询功能 ====================

    /**
     * 5. 获取用户的浏览记录列表
     * 功能描述：查询指定用户的所有浏览记录
     * @param userId 用户ID
     * @return BrowseHistory对象列表，按浏览时间倒序排列
     * @throws SQLException
     */
    public List<BrowseHistory> getUserBrowseHistoryList(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<BrowseHistory> historyList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM browse_history WHERE user_id = ? ORDER BY browse_time DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                historyList.add(resultSetToBrowseHistory(rs));
            }

            return historyList;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    /**
     * 6. 获取用户最近浏览的帖子
     * 功能描述：获取用户最近浏览的帖子记录
     * @param userId 用户ID
     * @param limit 获取的记录数量
     * @return BrowseHistory对象列表，按浏览时间倒序排列
     * @throws SQLException
     */
    public List<BrowseHistory> getUserRecentBrowseHistory(int userId, int limit) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<BrowseHistory> historyList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM browse_history WHERE user_id = ? ORDER BY browse_time DESC LIMIT ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                historyList.add(resultSetToBrowseHistory(rs));
            }

            return historyList;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    /**
     * 7. 统计用户的浏览记录数量
     * 功能描述：统计指定用户的总浏览记录数
     * @param userId 用户ID
     * @return 浏览记录总数
     * @throws SQLException
     */
    public int countUserBrowseHistory(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM browse_history WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
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
     * 8. 检查用户是否浏览过某帖子
     * 功能描述：判断用户是否已经浏览过指定帖子
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 已浏览返回true，未浏览返回false
     * @throws SQLException
     */
    public boolean checkUserViewedPost(int userId, int postId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM browse_history WHERE user_id = ? AND post_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    // ==================== 帖子相关查询功能 ====================

    /**
     * 9. 获取帖子的浏览记录列表
     * 功能描述：查询某个帖子的所有浏览记录
     * @param postId 帖子ID
     * @return BrowseHistory对象列表，按浏览时间倒序排列
     * @throws SQLException
     */
    public List<BrowseHistory> getPostBrowseHistoryList(int postId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<BrowseHistory> historyList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM browse_history WHERE post_id = ? ORDER BY browse_time DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                historyList.add(resultSetToBrowseHistory(rs));
            }

            return historyList;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    /**
     * 10. 统计帖子的浏览次数
     * 功能描述：统计指定帖子的总浏览次数
     * @param postId 帖子ID
     * @return 浏览总次数
     * @throws SQLException
     */
    public int countPostBrowseTimes(int postId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM browse_history WHERE post_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, postId);
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
     * 11. 获取热门浏览帖子
     * 功能描述：获取被浏览次数最多的帖子
     * @param limit 获取的记录数量
     * @return 包含帖子ID和浏览次数的Map列表
     * @throws SQLException
     */
    public List<Map<String, Object>> getHotBrowsePosts(int limit) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> hotPosts = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT post_id, COUNT(*) as browse_count " +
                    "FROM browse_history " +
                    "GROUP BY post_id " +
                    "ORDER BY browse_count DESC " +
                    "LIMIT ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> postInfo = new HashMap<>();
                postInfo.put("postId", rs.getInt("post_id"));
                postInfo.put("browseCount", rs.getInt("browse_count"));
                hotPosts.add(postInfo);
            }

            return hotPosts;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    /**
     * 12. 获取帖子热度排名
     * 功能描述：获取所有帖子的浏览热度排名
     * @param limit 获取的记录数量（为0表示获取全部）
     * @return 包含帖子ID、帖子标题和浏览次数的Map列表
     * @throws SQLException
     */
    public List<Map<String, Object>> getPostBrowseRanking(int limit) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> rankingList = new ArrayList<>();

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT bh.post_id, p.title, COUNT(bh.id) as browse_count ");
            sql.append("FROM browse_history bh ");
            sql.append("JOIN posts p ON bh.post_id = p.id ");
            sql.append("WHERE p.is_deleted = FALSE ");
            sql.append("GROUP BY bh.post_id, p.title ");
            sql.append("ORDER BY browse_count DESC ");

            if (limit > 0) {
                sql.append("LIMIT ?");
            }

            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            if (limit > 0) {
                pstmt.setInt(1, limit);
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> ranking = new HashMap<>();
                ranking.put("postId", rs.getInt("post_id"));
                ranking.put("title", rs.getString("title"));
                ranking.put("browseCount", rs.getInt("browse_count"));
                rankingList.add(ranking);
            }

            return rankingList;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    // ==================== 系统维护功能 ====================

    /**
     * 13. 删除过期浏览记录
     * 功能描述：清理指定时间之前的旧浏览记录
     * @param days 过期天数（例如：30表示删除30天前的记录）
     * @return 删除的记录数量
     * @throws SQLException
     */
    public int deleteExpiredBrowseHistory(int days) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM browse_history WHERE browse_time < DATE_SUB(NOW(), INTERVAL ? DAY)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, days);

            return pstmt.executeUpdate();
        } finally {
            closeResources(conn, pstmt);
        }
    }

    // ==================== 附加实用功能 ====================

    /**
     * 获取用户的浏览频率统计
     * @param userId 用户ID
     * @return 包含日期和浏览次数的统计列表
     * @throws SQLException
     */
    public List<Map<String, Object>> getUserBrowseFrequency(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> frequencyList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT DATE(browse_time) as browse_date, COUNT(*) as daily_count " +
                    "FROM browse_history " +
                    "WHERE user_id = ? " +
                    "GROUP BY DATE(browse_time) " +
                    "ORDER BY browse_date DESC " +
                    "LIMIT 30"; // 最近30天

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> frequency = new HashMap<>();
                frequency.put("date", rs.getDate("browse_date"));
                frequency.put("count", rs.getInt("daily_count"));
                frequencyList.add(frequency);
            }

            return frequencyList;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    /**
     * 批量添加浏览记录
     * @param userId 用户ID
     * @param postIds 帖子ID列表
     * @return 添加成功地记录数量
     * @throws SQLException
     */
    public int batchAddBrowseHistory(int userId, List<Integer> postIds) throws SQLException {
        if (postIds == null || postIds.isEmpty()) {
            return 0;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        int successCount = 0;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO browse_history (user_id, post_id, browse_time) VALUES (?, ?, NOW())";
            pstmt = conn.prepareStatement(sql);

            for (Integer postId : postIds) {
                // 检查是否已存在
                if (!checkUserViewedPost(userId, postId)) {
                    pstmt.setInt(1, userId);
                    pstmt.setInt(2, postId);
                    pstmt.addBatch();
                    successCount++;
                }
            }

            if (successCount > 0) {
                pstmt.executeBatch();
            }

            conn.commit();
            return successCount;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
            closeResources(conn, pstmt);
        }
    }

    /**
     * 获取用户的浏览帖子ID列表（去重）
     * @param userId 用户ID
     * @return 帖子ID列表
     * @throws SQLException
     */
    public List<Integer> getUserBrowsedPostIds(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Integer> postIds = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT DISTINCT post_id FROM browse_history WHERE user_id = ? ORDER BY MAX(browse_time) DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                postIds.add(rs.getInt("post_id"));
            }

            return postIds;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    /**
     * 获取系统总浏览记录数
     * @return 总浏览记录数
     * @throws SQLException
     */
    public int getTotalBrowseCount() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM browse_history";
            pstmt = conn.prepareStatement(sql);
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
