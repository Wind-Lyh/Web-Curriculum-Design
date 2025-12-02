package com.community.model;

import java.util.Date;

public class User {
    private Integer id;            // 用户ID，对应users.id，主键自增
    private String username;       // 用户名，对应users.username，唯一登录标识
    private String password;       // 密码，对应users.password，加密存储
    private String email;          // 邮箱，对应users.email，唯一联系方式
    private String phone;          // 手机号，对应users.phone
    private String avatarUrl;      // 头像地址，对应users.avatar_url，默认default_avatar.png
    private String nickname;       // 昵称，对应users.nickname，用户显示名称
    private String signature;      // 个性签名，对应users.signature
    private Integer points;        // 积分，对应users.points，默认0
    private Integer level;         // 等级，对应users.level，默认1
    private Integer role;          // 角色：0普通用户，1管理员（数据库无直接对应字段）
    private Integer status;        // 状态：0正常，1封禁，对应users.is_banned字段
    private Date createTime;       // 创建时间，对应users.create_time，默认当前时间
    private Date lastLoginTime;    // 最后登录时间，对应users.last_login_time

    // 构造方法
    public User() {
        this.points = 0;
        this.level = 1;
        this.role = 0;
        this.status = 0;
        this.createTime = new Date();
        this.avatarUrl = "default_avatar.png";
    }

    public User(String username, String password, String email, String nickname) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
    }

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", points=" + points +
                ", level=" + level +
                ", role=" + role +
                ", status=" + status +
                '}';
    }
}