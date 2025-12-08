package com.community.service.impl;

import com.community.dao.UserDao;
import com.community.dao.impl.UserDaoImpl;
import com.community.model.User;
import com.community.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户服务实现类
 */
public class UserServiceImpl implements UserService {

    private int currentId = 1;
    private UserDao userDao = new UserDaoImpl();
    private List<User> userDatabase=userDao.findAll(1,200);

    @Override
    public String register(User user) {
        try {
            // 1. 检查用户名是否已存在
            if (checkUsernameExists(user.getUsername())) {
                return "用户名已存在";
            }

            // 2. 检查邮箱是否已存在
            if (user.getEmail() != null && checkEmailExists(user.getEmail())) {
                return "邮箱已被注册";
            }

            // 3. 检查手机是否已存在
            if (user.getPhone() != null && checkPhoneExists(user.getPhone())) {
                return "手机号已被注册";
            }

            // 4. 设置用户默认信息
            user.setId(currentId++);
            user.setCreateTime(new Date());
            user.setAvatarUrl("/static/images/初始化头像.jpg");
            user.setPoints(0);
            user.setLevel(1);
            user.setIs_admin(0); // 普通用户
            user.setStatus(0); // 正常状态

            // 5. 保存用户（实际项目中应该保存到数据库）
            userDatabase.add(user);
            userDao.insert(user);

            return "注册成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "注册失败：" + e.getMessage();
        }
    }

    @Override
    public User login(String username, String password) {
        System.out.println("进入login");
        try {
            // 1. 查找用户
            User user = getUserByUsername(username);
            if (user == null) {
                System.out.println("login用户不存在");
                return null; // 用户不存在
            }

            // 2. 检查用户状态
            if (user.getStatus() == 1) {
                System.out.println("用户被封禁");
                return null; // 用户被封禁
            }

            // 3. 验证密码
            String encryptedPassword =password;
            if (!user.getPassword().equals(encryptedPassword)) {
                System.out.println("密码错误");
                return null; // 密码错误
            }

            // 4. 更新最后登录时间
            System.out.println(12345679);
            user.setLastLoginTime(new Date());

            // 5. 返回用户对象（注意：实际项目中应该返回用户副本，避免返回密码）
            User result = new User();
            result.setId(user.getId());
            result.setUsername(user.getUsername());
            result.setEmail(user.getEmail());
            result.setPhone(user.getPhone());
            result.setAvatarUrl(user.getAvatarUrl());
            result.setNickname(user.getNickname());
            result.setSignature(user.getSignature());
            result.setPoints(user.getPoints());
            result.setLevel(user.getLevel());
            result.setIs_admin(user.getIs_admin());
            result.setStatus(user.getStatus());
            result.setCreateTime(user.getCreateTime());
            result.setLastLoginTime(user.getLastLoginTime());

            return result;
        } catch (Exception e) {
            System.out.println("login错误");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean updateProfile(User user) {
        try {
            // 1. 查找原始用户
            User originalUser = getUserById(user.getId());
            if (originalUser == null) {
                return false; // 用户不存在
            }

            // 2. 更新可修改的字段（密码、状态、角色等不能通过此方法修改）
            if (user.getNickname() != null) {
                originalUser.setNickname(user.getNickname());
            }
            if (user.getSignature() != null) {
                originalUser.setSignature(user.getSignature());
            }
            if (user.getEmail() != null && !user.getEmail().equals(originalUser.getEmail())) {
                // 检查新邮箱是否已被使用
                if (checkEmailExists(user.getEmail())) {
                    return false;
                }
                originalUser.setEmail(user.getEmail());
            }
            if (user.getPhone() != null && !user.getPhone().equals(originalUser.getPhone())) {
                // 检查新手机号是否已被使用
                if (checkPhoneExists(user.getPhone())) {
                    return false;
                }
                originalUser.setPhone(user.getPhone());
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        try {
            // 1. 查找用户
            User user = getUserById(userId);
            if (user == null) {
                return false; // 用户不存在
            }

            // 2. 验证旧密码
            String encryptedOldPassword = oldPassword;
            if (!user.getPassword().equals(encryptedOldPassword)) {
                return false; // 旧密码错误
            }

            // 3. 加密并设置新密码
            String encryptedNewPassword = newPassword;
            user.setPassword(encryptedNewPassword);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean uploadAvatar(int userId, String avatarUrl) {
        try {
            User user = getUserById(userId);
            if (user == null) {
                return false;
            }

            user.setAvatarUrl(avatarUrl);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean bindEmail(int userId, String email) {
        try {
            // 1. 检查邮箱是否已被使用
            if (checkEmailExists(email)) {
                return false;
            }

            // 2. 查找用户并绑定邮箱
            User user = getUserById(userId);
            if (user == null) {
                return false;
            }

            user.setEmail(email);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean bindPhone(int userId, String phone) {
        try {
            // 1. 检查手机号是否已被使用
            if (checkPhoneExists(phone)) {
                return false;
            }

            // 2. 查找用户并绑定手机号
            User user = getUserById(userId);
            if (user == null) {
                return false;
            }

            user.setPhone(phone);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User getUserById(int userId) {
        for (User user : userDatabase) {
            if (user.getId().equals(userId)) {
                // 返回用户副本，不包含密码
                User result = new User();
                result.setId(user.getId());
                result.setUsername(user.getUsername());
                result.setEmail(user.getEmail());
                result.setPhone(user.getPhone());
                result.setAvatarUrl(user.getAvatarUrl());
                result.setNickname(user.getNickname());
                result.setSignature(user.getSignature());
                result.setPoints(user.getPoints());
                result.setLevel(user.getLevel());
                result.setIs_admin(user.getIs_admin());
                result.setStatus(user.getStatus());
                result.setCreateTime(user.getCreateTime());
                result.setLastLoginTime(user.getLastLoginTime());
                return result;
            }
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        for (User user : userDatabase) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        for (User user : userDatabase) {
            if (email.equals(user.getEmail())) {
                // 返回用户副本，不包含密码
                User result = new User();
                result.setId(user.getId());
                result.setUsername(user.getUsername());
                result.setEmail(user.getEmail());
                result.setPhone(user.getPhone());
                result.setAvatarUrl(user.getAvatarUrl());
                result.setNickname(user.getNickname());
                result.setSignature(user.getSignature());
                result.setPoints(user.getPoints());
                result.setLevel(user.getLevel());
                result.setIs_admin(user.getIs_admin());
                result.setStatus(user.getStatus());
                result.setCreateTime(user.getCreateTime());
                result.setLastLoginTime(user.getLastLoginTime());
                return result;
            }
        }
        return null;
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return getUserByUsername(username) != null;
    }

    @Override
    public boolean checkEmailExists(String email) {
        for (User user : userDatabase) {
            if (email.equals(user.getEmail())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkPhoneExists(String phone) {
        for (User user : userDatabase) {
            if (phone.equals(user.getPhone())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean recordLogin(int userId, String ipAddress, String userAgent) {
        // 实际项目中应该将登录日志保存到数据库
        try {
            System.out.println(String.format("用户 %d 登录 - IP: %s, UserAgent: %s",
                    userId, ipAddress, userAgent));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<User> getAllUsers(int page, int pageSize) {
        try {
            // 计算分页
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, userDatabase.size());

            if (start >= userDatabase.size()) {
                return new ArrayList<>(); // 超出范围返回空列表
            }

            // 获取分页数据
            List<User> users = userDatabase.subList(start, end);

            // 创建返回列表（不包含密码）
            List<User> result = new ArrayList<>();
            for (User user : users) {
                User safeUser = new User();
                safeUser.setId(user.getId());
                safeUser.setUsername(user.getUsername());
                safeUser.setEmail(user.getEmail());
                safeUser.setPhone(user.getPhone());
                safeUser.setAvatarUrl(user.getAvatarUrl());
                safeUser.setNickname(user.getNickname());
                safeUser.setSignature(user.getSignature());
                safeUser.setPoints(user.getPoints());
                safeUser.setLevel(user.getLevel());
                safeUser.setIs_admin(user.getIs_admin());
                safeUser.setStatus(user.getStatus());
                safeUser.setCreateTime(user.getCreateTime());
                safeUser.setLastLoginTime(user.getLastLoginTime());
                result.add(safeUser);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean banUser(int userId) {
        try {
            User user = getUserById(userId);
            if (user == null) {
                return false;
            }

            user.setStatus(1); // 设置为封禁状态
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean unbanUser(int userId) {
        try {
            User user = getUserById(userId);
            if (user == null) {
                return false;
            }

            user.setStatus(0); // 设置为正常状态
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getUserCount() {
        return userDatabase.size();
    }
}