package com.community.dao.impl;

import com.community.dao.VirtualGoodDao;
import com.community.model.VirtualGood;
import com.community.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VirtualGoodDaoImpl implements VirtualGoodDao {

    // 1.1 新增虚拟道具
    @Override
    public boolean insertVirtualGood(VirtualGood virtualGood) {
        String sql = "INSERT INTO virtual_goods(name, description, price, stock, is_available, create_time) VALUES(?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, virtualGood.getName());
            ps.setString(2, virtualGood.getDescription());
            ps.setInt(3, virtualGood.getPrice());
            ps.setInt(4, virtualGood.getStock());
            ps.setInt(5, virtualGood.getIsAvailable());
            ps.setTimestamp(6, new Timestamp(virtualGood.getCreateTime().getTime()));

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 1.2 更新道具信息
    @Override
    public boolean updateVirtualGood(VirtualGood virtualGood) {
        String sql = "UPDATE virtual_goods SET name = ?, description = ?, price = ?, stock = ?, is_available = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, virtualGood.getName());
            ps.setString(2, virtualGood.getDescription());
            ps.setInt(3, virtualGood.getPrice());
            ps.setInt(4, virtualGood.getStock());
            ps.setInt(5, virtualGood.getIsAvailable());
            ps.setInt(6, virtualGood.getId());

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 1.3 删除道具
    @Override
    public boolean deleteVirtualGood(Integer id) {
        String sql = "DELETE FROM virtual_goods WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 1.4 批量添加道具
    @Override
    public boolean batchInsertVirtualGoods(List<VirtualGood> virtualGoods) {
        String sql = "INSERT INTO virtual_goods(name, description, price, stock, is_available, create_time) VALUES(?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务
            ps = conn.prepareStatement(sql);

            for (VirtualGood good : virtualGoods) {
                ps.setString(1, good.getName());
                ps.setString(2, good.getDescription());
                ps.setInt(3, good.getPrice());
                ps.setInt(4, good.getStock());
                ps.setInt(5, good.getIsAvailable());
                ps.setTimestamp(6, new Timestamp(good.getCreateTime().getTime()));
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

    // 1.5 批量更新道具
    @Override
    public boolean batchUpdateVirtualGoods(List<VirtualGood> virtualGoods) {
        String sql = "UPDATE virtual_goods SET name = ?, description = ?, price = ?, stock = ?, is_available = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务
            ps = conn.prepareStatement(sql);

            for (VirtualGood good : virtualGoods) {
                ps.setString(1, good.getName());
                ps.setString(2, good.getDescription());
                ps.setInt(3, good.getPrice());
                ps.setInt(4, good.getStock());
                ps.setInt(5, good.getIsAvailable());
                ps.setInt(6, good.getId());
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

    // 1.6 批量删除道具
    @Override
    public boolean batchDeleteVirtualGoods(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        StringBuilder sqlBuilder = new StringBuilder("DELETE FROM virtual_goods WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            sqlBuilder.append("?");
            if (i < ids.size() - 1) {
                sqlBuilder.append(",");
            }
        }
        sqlBuilder.append(")");

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sqlBuilder.toString());

            for (int i = 0; i < ids.size(); i++) {
                ps.setInt(i + 1, ids.get(i));
            }

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 2.1 按ID查询道具
    @Override
    public VirtualGood getVirtualGoodById(Integer id) {
        String sql = "SELECT * FROM virtual_goods WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                return extractVirtualGoodFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return null;
    }

    // 2.2 按名称查询道具（支持模糊查询）
    @Override
    public List<VirtualGood> getVirtualGoodsByName(String name) {
        List<VirtualGood> goods = new ArrayList<>();
        String sql = "SELECT * FROM virtual_goods WHERE name LIKE ? ORDER BY create_time DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + name + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                goods.add(extractVirtualGoodFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return goods;
    }

    // 2.3 查询所有道具
    @Override
    public List<VirtualGood> getAllVirtualGoods() {
        List<VirtualGood> goods = new ArrayList<>();
        String sql = "SELECT * FROM virtual_goods ORDER BY create_time DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                goods.add(extractVirtualGoodFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return goods;
    }

    // 2.6 按价格排序查询
    @Override
    public List<VirtualGood> getVirtualGoodsOrderByPrice(String order) {
        List<VirtualGood> goods = new ArrayList<>();
        String sql = "SELECT * FROM virtual_goods ORDER BY price " +
                (order != null && order.equalsIgnoreCase("desc") ? "DESC" : "ASC");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                goods.add(extractVirtualGoodFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return goods;
    }

    // 3.1 上架道具
    @Override
    public boolean setGoodAvailable(Integer id) {
        String sql = "UPDATE virtual_goods SET is_available = 1 WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 3.2 下架道具
    @Override
    public boolean setGoodUnavailable(Integer id) {
        String sql = "UPDATE virtual_goods SET is_available = 0 WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 3.3 批量上架道具
    @Override
    public boolean batchSetGoodsAvailable(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        StringBuilder sqlBuilder = new StringBuilder("UPDATE virtual_goods SET is_available = 1 WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            sqlBuilder.append("?");
            if (i < ids.size() - 1) {
                sqlBuilder.append(",");
            }
        }
        sqlBuilder.append(")");

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sqlBuilder.toString());

            for (int i = 0; i < ids.size(); i++) {
                ps.setInt(i + 1, ids.get(i));
            }

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 3.4 批量下架道具
    @Override
    public boolean batchSetGoodsUnavailable(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        StringBuilder sqlBuilder = new StringBuilder("UPDATE virtual_goods SET is_available = 0 WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            sqlBuilder.append("?");
            if (i < ids.size() - 1) {
                sqlBuilder.append(",");
            }
        }
        sqlBuilder.append(")");

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sqlBuilder.toString());

            for (int i = 0; i < ids.size(); i++) {
                ps.setInt(i + 1, ids.get(i));
            }

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 4.1 查询道具库存
    @Override
    public Integer getGoodStock(Integer id) {
        String sql = "SELECT stock FROM virtual_goods WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("stock");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return null;
    }

    // 4.2 增加道具库存
    @Override
    public boolean increaseGoodStock(Integer id, Integer amount) {
        String sql = "UPDATE virtual_goods SET stock = stock + ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, amount);
            ps.setInt(2, id);

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 4.3 减少道具库存
    @Override
    public boolean decreaseGoodStock(Integer id, Integer amount) {
        // 先检查库存是否足够（有限库存时才检查）
        Integer currentStock = getGoodStock(id);
        if (currentStock != null && currentStock >= 0 && currentStock < amount) {
            return false; // 库存不足
        }

        String sql = "UPDATE virtual_goods SET stock = stock - ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, amount);
            ps.setInt(2, id);

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 4.4 设置道具库存
    @Override
    public boolean setGoodStock(Integer id, Integer stock) {
        String sql = "UPDATE virtual_goods SET stock = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, stock);
            ps.setInt(2, id);

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 5.1 修改道具价格
    @Override
    public boolean updateGoodPrice(Integer id, Integer price) {
        String sql = "UPDATE virtual_goods SET price = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, price);
            ps.setInt(2, id);

            int result = ps.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, ps, null);
        }
    }

    // 7.1 道具总数统计
    @Override
    public int countAllVirtualGoods() {
        String sql = "SELECT COUNT(*) as count FROM virtual_goods";
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

    // 7.2 可用道具统计
    @Override
    public int countAvailableVirtualGoods() {
        String sql = "SELECT COUNT(*) as count FROM virtual_goods WHERE is_available = 1";
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

    // 7.3 库存统计（排除无限库存）
    @Override
    public int calculateTotalStock() {
        String sql = "SELECT SUM(stock) as total FROM virtual_goods WHERE stock >= 0";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                Integer total = rs.getInt("total");
                return rs.wasNull() ? 0 : total;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return 0;
    }

    // 7.4 价格统计
    @Override
    public Map<String, Integer> getPriceStatistics() {
        Map<String, Integer> statistics = new HashMap<>();
        String sql = "SELECT MIN(price) as min_price, MAX(price) as max_price, AVG(price) as avg_price FROM virtual_goods WHERE is_available = 1";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                statistics.put("min_price", rs.getInt("min_price"));
                statistics.put("max_price", rs.getInt("max_price"));
                statistics.put("avg_price", rs.getInt("avg_price"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return statistics;
    }

    // 其他辅助功能
    // 分页查询
    @Override
    public List<VirtualGood> getVirtualGoodsByPage(int page, int pageSize) {
        List<VirtualGood> goods = new ArrayList<>();
        String sql = "SELECT * FROM virtual_goods ORDER BY create_time DESC LIMIT ?, ?";
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
                goods.add(extractVirtualGoodFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return goods;
    }

    // 条件查询
    @Override
    public List<VirtualGood> getVirtualGoodsByConditions(String name, Integer minPrice,
                                                         Integer maxPrice, Boolean isAvailable) {
        List<VirtualGood> goods = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM virtual_goods WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (name != null && !name.trim().isEmpty()) {
            sqlBuilder.append(" AND name LIKE ?");
            params.add("%" + name + "%");
        }

        if (minPrice != null) {
            sqlBuilder.append(" AND price >= ?");
            params.add(minPrice);
        }

        if (maxPrice != null) {
            sqlBuilder.append(" AND price <= ?");
            params.add(maxPrice);
        }

        if (isAvailable != null) {
            sqlBuilder.append(" AND is_available = ?");
            params.add(isAvailable ? 1 : 0);
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
                goods.add(extractVirtualGoodFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }

        return goods;
    }

    // 辅助方法：从ResultSet中提取VirtualGood对象
    private VirtualGood extractVirtualGoodFromResultSet(ResultSet rs) throws SQLException {
        VirtualGood good = new VirtualGood(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("price"),
                rs.getInt("stock"),
                rs.getInt("is_available"),
                rs.getTimestamp("create_time")
        );
        return good;
    }
}