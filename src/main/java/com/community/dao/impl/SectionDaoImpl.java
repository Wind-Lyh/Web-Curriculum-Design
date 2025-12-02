package com.community.dao.impl;

import com.community.dao.SectionDao;
import com.community.model.Section;
import com.community.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDaoImpl implements SectionDao {

    @Override
    public int insert(Section section) {
        // 实现插入逻辑...
        return 0;
    }

    @Override
    public Section findById(int id) {
        // 实现查询逻辑...
        return null;
    }

    @Override
    public List<Section> findAll() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Section> sectionList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM sections ORDER BY create_time ASC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Section section = new Section();
                section.setId(rs.getInt("id"));
                section.setName(rs.getString("name"));
                section.setDescription(rs.getString("description"));
                section.setPostCount(rs.getInt("post_count"));
                section.setCreateTime(rs.getTimestamp("create_time"));
                sectionList.add(section);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return sectionList;
    }

    @Override
    public int update(Section section) {
        // 实现更新逻辑...
        return 0;
    }

    @Override
    public int delete(int id) {
        // 实现删除逻辑...
        return 0;
    }

    @Override
    public int increasePostCount(int sectionId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE sections SET post_count = post_count + 1 WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sectionId);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public int decreasePostCount(int sectionId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE sections SET post_count = post_count - 1 WHERE id = ? AND post_count > 0";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sectionId);

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
}