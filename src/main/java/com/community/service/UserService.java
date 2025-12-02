package com.community.service;

import com.community.model.User;
import java.util.List;

/**
 * 用户业务逻辑接口
 */
public interface UserService {

    /**
     * 用户注册
     * @param user 用户对象
     * @return 注册结果（成功/失败信息）
     */
    String register(User user);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（成功返回用户对象，失败返回null）
     */
    User login(String username, String password);

    /**
     * 更新用户资料
     * @param user 更新后的用户对象
     * @return 更新结果
     */
    boolean updateProfile(User user);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    boolean changePassword(int userId, String oldPassword, String newPassword);

    /**
     * 上传头像
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return 上传结果
     */
    boolean uploadAvatar(int userId, String avatarUrl);

    /**
     * 绑定邮箱
     * @param userId 用户ID
     * @param email 邮箱地址
     * @return 绑定结果
     */
    boolean bindEmail(int userId, String email);

    /**
     * 绑定手机
     * @param userId 用户ID
     * @param phone 手机号码
     * @return 绑定结果
     */
    boolean bindPhone(int userId, String phone);

    /**
     * 根据ID获取用户信息
     * @param userId 用户ID
     * @return 用户对象
     */
    User getUserById(int userId);

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户对象
     */
    User getUserByUsername(String username);

    /**
     * 根据邮箱获取用户信息
     * @param email 邮箱地址
     * @return 用户对象
     */
    User getUserByEmail(String email);

    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean checkUsernameExists(String username);

    /**
     * 检查邮箱是否已存在
     * @param email 邮箱地址
     * @return 是否存在
     */
    boolean checkEmailExists(String email);

    /**
     * 检查手机是否已存在
     * @param phone 手机号码
     * @return 是否存在
     */
    boolean checkPhoneExists(String phone);

    /**
     * 记录用户登录日志
     * @param userId 用户ID
     * @param ipAddress IP地址
     * @param userAgent 用户代理信息
     * @return 记录结果
     */
    boolean recordLogin(int userId, String ipAddress, String userAgent);

    /**
     * 获取所有用户（分页）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 用户列表
     */
    List<User> getAllUsers(int page, int pageSize);

    /**
     * 封禁用户
     * @param userId 用户ID
     * @return 封禁结果
     */
    boolean banUser(int userId);

    /**
     * 解封用户
     * @param userId 用户ID
     * @return 解封结果
     */
    boolean unbanUser(int userId);

    /**
     * 重置用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 重置结果
     */
    boolean resetPassword(int userId, String newPassword);

    /**
     * 获取用户总数
     * @return 用户数量
     */
    int getUserCount();
}