package com.community.dao;

import com.community.model.User;
import java.util.List;
import java.util.Map;

public interface UserDao {
    // 插入用户
    int insert(User user);

    // 根据ID查询用户
    User findById(int id);

    // 根据用户名查询用户
    User findByUsername(String username);

    // 根据邮箱查询用户
    User findByEmail(String email);

    // 更新用户信息
    int update(User user);

    // 更新用户状态
    int updateStatus(int userId, int status);

    // 更新用户密码
    int updatePassword(int userId, String newPassword);

    // 更新用户积分
    int updatePoints(int userId, int points);

    // 更新用户等级
    int updateLevel(int userId, int level);

    // 更新用户最后登录时间
    int updateLastLoginTime(int userId);

    // 查询所有用户（分页）
    List<User> findAll(int page, int size);

    // 搜索用户
    List<User> search(String keyword, int page, int size);

    // 统计用户总数
    int count();

    // 统计符合条件的用户数
    int countByCondition(Map<String, Object> condition);

    // 删除用户（逻辑删除）
    int delete(int id);
}