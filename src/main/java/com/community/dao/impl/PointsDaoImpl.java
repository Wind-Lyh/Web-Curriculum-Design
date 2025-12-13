package com.community.dao.impl;

import com.community.dao.PointsDao;
import com.community.model.PointsRecord;
import com.community.util.DBUtil;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class PointsDaoImpl implements PointsDao {

    // 1.1 新增积分记录 - 插入单条积分变动记录
    @Override
    public boolean insertPointsRecord(PointsRecord pointsRecord) {
        String sql = "INSERT INTO points_records(user_id, change_type, change_amount, description, create_time) VALUES(?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setInt(1, pointsRecord.getUserId());
            ps.setString(2, pointsRecord.getChangeType());
            ps.setInt(3, pointsRecord.getChangeAmount());
            ps.setString(4, pointsRecord.getDescription());
            ps.setTimestamp(5, new Timestamp(pointsRecord.getCreateTime().getTime()));

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 1.2 批量新增积分记录
    @Override
    public boolean batchInsertPointsRecords(List<PointsRecord> pointsRecords) {
        String sql = "INSERT INTO points_records(user_id, change_type, change_amount, description, create_time) VALUES(?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务

            ps = conn.prepareStatement(sql);

            for (PointsRecord record : pointsRecords) {
                ps.setInt(1, record.getUserId());
                ps.setString(2, record.getChangeType());
                ps.setInt(3, record.getChangeAmount());
                ps.setString(4, record.getDescription());
                ps.setTimestamp(5, new Timestamp(record.getCreateTime().getTime()));
                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            conn.commit(); // 提交事务

            // 检查批量执行结果
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // 回滚事务
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBUtil.close(conn, ps, null);
        }
    }

    // 1.3 带事务的积分操作（同时更新用户积分表）
    @Override
    public boolean processPointsTransaction(Integer userId, String changeType, Integer changeAmount, String description) {
        Connection conn = null;
        PreparedStatement psInsert = null;
        PreparedStatement psUpdate = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 步骤1：更新用户表中的积分余额（假设users表有points字段）
            String updateSql = "UPDATE users SET points = points + ? WHERE id = ?";
            psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setInt(1, changeAmount);
            psUpdate.setInt(2, userId);
            int updateResult = psUpdate.executeUpdate();

            if (updateResult <= 0) {
                throw new SQLException("更新用户积分失败");
            }

            // 步骤2：插入积分记录
            String insertSql = "INSERT INTO points_records(user_id, change_type, change_amount, description, create_time) VALUES(?, ?, ?, ?, NOW())";
            psInsert = conn.prepareStatement(insertSql);
            psInsert.setInt(1, userId);
            psInsert.setString(2, changeType);
            psInsert.setInt(3, changeAmount);
            psInsert.setString(4, description);
            int insertResult = psInsert.executeUpdate();

            if (insertResult <= 0) {
                throw new SQLException("插入积分记录失败");
            }

            conn.commit(); // 提交事务
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // 回滚事务
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBUtil.close(conn, psInsert, null);
            DBUtil.close(null, psUpdate, null);
        }
    }

    // 2.1 按用户ID查询积分记录
    @Override
    public List<PointsRecord> getPointsRecordsByUserId(Integer userId) {
        List<PointsRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM points_records WHERE user_id = ? ORDER BY create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                PointsRecord record = extractPointsRecordFromResultSet(rs);
                records.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return records;
    }

    // 2.2 按变更类型查询积分记录
    @Override
    public List<PointsRecord> getPointsRecordsByType(Integer userId, String changeType) {
        List<PointsRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM points_records WHERE user_id = ? AND change_type = ? ORDER BY create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, changeType);
            rs = ps.executeQuery();

            while (rs.next()) {
                PointsRecord record = extractPointsRecordFromResultSet(rs);
                records.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return records;
    }

    // 2.3 按时间范围查询积分记录
    @Override
    public List<PointsRecord> getPointsRecordsByTimeRange(Integer userId, Date startTime, Date endTime) {
        List<PointsRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM points_records WHERE user_id = ? AND create_time BETWEEN ? AND ? ORDER BY create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setTimestamp(2, new Timestamp(startTime.getTime()));
            ps.setTimestamp(3, new Timestamp(endTime.getTime()));
            rs = ps.executeQuery();

            while (rs.next()) {
                PointsRecord record = extractPointsRecordFromResultSet(rs);
                records.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return records;
    }

    // 2.4 分页查询积分记录
    @Override
    public List<PointsRecord> getPointsRecordsByPage(Integer userId, int page, int pageSize) {
        List<PointsRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM points_records WHERE user_id = ? ORDER BY create_time DESC LIMIT ?, ?";
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
                PointsRecord record = extractPointsRecordFromResultSet(rs);
                records.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return records;
    }

    // 2.5 联合查询带用户信息的积分记录
    @Override
    public List<Map<String, Object>> getPointsRecordsWithUserInfo(Integer userId) {
        List<Map<String, Object>> records = new ArrayList<>();
        String sql = "SELECT pr.*, u.username, u.email FROM points_records pr " +
                "LEFT JOIN users u ON pr.user_id = u.id " +
                "WHERE pr.user_id = ? ORDER BY pr.create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                records.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return records;
    }

    // 2.6 多条件组合查询
    @Override
    public List<PointsRecord> getPointsRecordsByConditions(Integer userId, String changeType, Date startTime, Date endTime) {
        List<PointsRecord> records = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM points_records WHERE user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (changeType != null && !changeType.isEmpty()) {
            sqlBuilder.append(" AND change_type = ?");
            params.add(changeType);
        }

        if (startTime != null) {
            sqlBuilder.append(" AND create_time >= ?");
            params.add(new Timestamp(startTime.getTime()));
        }

        if (endTime != null) {
            sqlBuilder.append(" AND create_time <= ?");
            params.add(new Timestamp(endTime.getTime()));
        }

        sqlBuilder.append(" ORDER BY create_time DESC");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sqlBuilder.toString());

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                PointsRecord record = extractPointsRecordFromResultSet(rs);
                records.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return records;
    }

    // 3.1 计算用户当前总积分（累加change_amount）
    @Override
    public Integer calculateUserTotalPoints(Integer userId) {
        String sql = "SELECT SUM(change_amount) as total_points FROM points_records WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                Integer total = rs.getInt("total_points");
                return rs.wasNull() ? 0 : total;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return 0;
    }

    // 3.2 统计用户积分收入与支出
    @Override
    public Map<String, Integer> calculatePointsIncomeAndExpense(Integer userId) {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT " +
                "SUM(CASE WHEN change_amount > 0 THEN change_amount ELSE 0 END) as income, " +
                "SUM(CASE WHEN change_amount < 0 THEN change_amount ELSE 0 END) as expense " +
                "FROM points_records WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                Integer income = rs.getInt("income");
                Integer expense = rs.getInt("expense");

                result.put("income", rs.wasNull() ? 0 : income);
                result.put("expense", rs.wasNull() ? 0 : expense);
                result.put("balance", (rs.wasNull() ? 0 : income) + (rs.wasNull() ? 0 : expense));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return result;
    }

    // 3.3 按类型统计积分变动
    @Override
    public List<Map<String, Object>> calculatePointsByType(Integer userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT change_type, COUNT(*) as count, SUM(change_amount) as total_amount " +
                "FROM points_records WHERE user_id = ? GROUP BY change_type ORDER BY total_amount DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("change_type", rs.getString("change_type"));
                row.put("count", rs.getInt("count"));
                row.put("total_amount", rs.getInt("total_amount"));
                result.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return result;
    }

    // 3.4 时间维度统计积分变动（按日/月/年）
    @Override
    public List<Map<String, Object>> calculatePointsByTimePeriod(Integer userId, String periodType) {
        List<Map<String, Object>> result = new ArrayList<>();
        String dateFormat = "";

        switch (periodType.toLowerCase()) {
            case "day":
                dateFormat = "%Y-%m-%d";
                break;
            case "month":
                dateFormat = "%Y-%m";
                break;
            case "year":
                dateFormat = "%Y";
                break;
            default:
                dateFormat = "%Y-%m-%d"; // 默认为日
        }

        String sql = "SELECT DATE_FORMAT(create_time, ?) as period, " +
                "SUM(change_amount) as total_amount, " +
                "COUNT(*) as transaction_count " +
                "FROM points_records WHERE user_id = ? " +
                "GROUP BY DATE_FORMAT(create_time, ?) " +
                "ORDER BY period DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, dateFormat);
            ps.setInt(2, userId);
            ps.setString(3, dateFormat);
            rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("period", rs.getString("period"));
                row.put("total_amount", rs.getInt("total_amount"));
                row.put("transaction_count", rs.getInt("transaction_count"));
                result.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return result;
    }

    // 3.5 积分排行榜（用户积分排名）
    @Override
    public List<Map<String, Object>> getPointsRanking(int limit) {
        List<Map<String, Object>> ranking = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.nickname, u.avatar_url, u.points, u.level, " +
                "COALESCE(SUM(pr.change_amount), 0) as total_points " +
                "FROM users u " +
                "LEFT JOIN points_records pr ON u.id = pr.user_id " +
                "GROUP BY u.id, u.username, u.nickname, u.avatar_url, u.points, u.level " +
                "ORDER BY total_points DESC " +
                "LIMIT ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("user_id", rs.getInt("id"));
                row.put("username", rs.getString("username"));
                row.put("nickname", rs.getString("nickname"));
                row.put("avatar_url", rs.getString("avatar_url"));
                row.put("points", rs.getInt("points"));
                row.put("level", rs.getInt("level"));
                row.put("total_points", rs.getInt("total_points"));
                ranking.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return ranking;
    }
    // 辅助方法：获取用户积分记录总数
    @Override
    public int countPointsRecordsByUserId(Integer userId) {
        String sql = "SELECT COUNT(*) as count FROM points_records WHERE user_id = ?";
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

    // 辅助方法：获取所有积分记录总数（管理员用）
    @Override
    public int countAllPointsRecords() {
        String sql = "SELECT COUNT(*) as count FROM points_records";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return 0;
    }

    // 辅助方法：从ResultSet中提取PointsRecord对象
    private PointsRecord extractPointsRecordFromResultSet(ResultSet rs) throws SQLException {
        PointsRecord record = new PointsRecord();
        record.setId(rs.getInt("id"));
        record.setUserId(rs.getInt("user_id"));
        record.setChangeType(rs.getString("change_type"));
        record.setChangeAmount(rs.getInt("change_amount"));
        record.setDescription(rs.getString("description"));
        record.setCreateTime(rs.getTimestamp("create_time"));
        return record;
    }
}
