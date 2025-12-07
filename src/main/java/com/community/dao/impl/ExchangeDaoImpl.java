package com.community.dao.impl;

import com.community.dao.ExchangeDao;
import com.community.model.Exchange;
import com.community.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeDaoImpl implements ExchangeDao {

    // 1. 新增兑换记录
    @Override
    public boolean insertExchange(Exchange exchange) {
        String sql = "INSERT INTO exchanges(user_id, goods_id, quantity, total_cost, create_time) VALUES(?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setInt(1, exchange.getUserId());
            ps.setInt(2, exchange.getGoodsId());
            ps.setInt(3, exchange.getQuantity());
            ps.setInt(4, exchange.getTotalCost());
            ps.setTimestamp(5, new Timestamp(exchange.getCreateTime().getTime()));

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 2. 用户兑换历史查询
    @Override
    public List<Exchange> getExchangesByUserId(Integer userId) {
        List<Exchange> exchanges = new ArrayList<>();
        String sql = "SELECT * FROM exchanges WHERE user_id = ? ORDER BY create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Exchange exchange = extractExchangeFromResultSet(rs);
                exchanges.add(exchange);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return exchanges;
    }

    // 3. 兑换明细报表
    @Override
    public List<Exchange> getAllExchanges() {
        List<Exchange> exchanges = new ArrayList<>();
        String sql = "SELECT * FROM exchanges ORDER BY create_time DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Exchange exchange = extractExchangeFromResultSet(rs);
                exchanges.add(exchange);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return exchanges;
    }

    // 4. 执行兑换流程（包含事务处理）
    @Override
    public boolean executeExchange(Integer userId, Integer goodsId, Integer quantity, Integer price) {
        Connection conn = null;
        PreparedStatement psInsert = null;
        PreparedStatement psUpdate = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 第一步：检查用户积分是否足够
            String checkSql = "SELECT points FROM users WHERE id = ?";
            PreparedStatement psCheck = conn.prepareStatement(checkSql);
            psCheck.setInt(1, userId);
            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) {
                throw new SQLException("用户不存在");
            }

            int userPoints = rs.getInt("points");
            int totalCost = quantity * price;

            if (userPoints < totalCost) {
                throw new SQLException("积分不足");
            }

            // 第二步：扣减用户积分
            String updateSql = "UPDATE users SET points = points - ? WHERE id = ?";
            psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setInt(1, totalCost);
            psUpdate.setInt(2, userId);
            int updateResult = psUpdate.executeUpdate();

            if (updateResult <= 0) {
                throw new SQLException("更新用户积分失败");
            }

            // 第三步：插入兑换记录
            String insertSql = "INSERT INTO exchanges(user_id, goods_id, quantity, total_cost, create_time) VALUES(?, ?, ?, ?, NOW())";
            psInsert = conn.prepareStatement(insertSql);
            psInsert.setInt(1, userId);
            psInsert.setInt(2, goodsId);
            psInsert.setInt(3, quantity);
            psInsert.setInt(4, totalCost);
            int insertResult = psInsert.executeUpdate();

            if (insertResult <= 0) {
                throw new SQLException("插入兑换记录失败");
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

    // 5.1 统计用户兑换次数
    @Override
    public Integer getUserExchangeCount(Integer userId) {
        String sql = "SELECT COUNT(*) as count FROM exchanges WHERE user_id = ?";
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

    // 5.2 统计用户总花费积分
    @Override
    public Integer getUserTotalCost(Integer userId) {
        String sql = "SELECT SUM(total_cost) as total FROM exchanges WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                Integer total = rs.getInt("total");
                return rs.wasNull() ? 0 : total;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return 0;
    }

    // 6. 分页查询
    @Override
    public List<Exchange> getExchangesByPage(int page, int pageSize) {
        List<Exchange> exchanges = new ArrayList<>();
        String sql = "SELECT * FROM exchanges ORDER BY create_time DESC LIMIT ?, ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);

            // 计算起始位置
            int start = (page - 1) * pageSize;
            ps.setInt(1, start);
            ps.setInt(2, pageSize);

            rs = ps.executeQuery();

            while (rs.next()) {
                Exchange exchange = extractExchangeFromResultSet(rs);
                exchanges.add(exchange);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return exchanges;
    }

    // 6. 获取总记录数（用于分页）
    @Override
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) as count FROM exchanges";
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

    // 辅助方法：从ResultSet中提取Exchange对象
    private Exchange extractExchangeFromResultSet(ResultSet rs) throws SQLException {
        Exchange exchange = new Exchange(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("goods_id"),
                rs.getInt("quantity"),
                rs.getInt("total_cost"),
                rs.getTimestamp("create_time")
        );
        return exchange;
    }
}