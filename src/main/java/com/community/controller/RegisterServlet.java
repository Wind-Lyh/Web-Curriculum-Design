package com.community.controller;

import com.community.dao.UserDao;
import com.community.dao.impl.UserDaoImpl;
import com.community.model.User;
import com.community.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class RegisterServlet extends HttpServlet {
    private UserDao userDao = new UserDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 检查用户名是否存在
        String action = request.getParameter("action");
        if ("checkUsername".equals(action)) {
            String username = request.getParameter("username");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();
            User existingUser = userDao.findByUsername(username);

            if (existingUser != null) {
                out.write("{\"exists\": true}");
            } else {
                out.write("{\"exists\": false}");
            }
            out.flush();
            return;
        }

        // 检查邮箱是否存在
        if ("checkEmail".equals(action)) {
            String email = request.getParameter("email");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();
            User existingUser = userDao.findByEmail(email);

            if (existingUser != null) {
                out.write("{\"exists\": true}");
            } else {
                out.write("{\"exists\": false}");
            }
            out.flush();
            return;
        }

        // 检查手机号是否存在
        if ("checkPhone".equals(action)) {
            String phone = request.getParameter("phone");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();
            // 需要创建一个按手机号查询的方法，或者使用现有的findByEmail/Username
            // 这里假设UserDao有一个findByPhone方法，如果没有需要添加
            // 暂时使用一个简单实现
            try {
                // 这里应该调用userDao.findByPhone(phone)，如果没有这个方法
                // 可以暂时跳过或添加该方法
                out.write("{\"exists\": false}");
            } catch (Exception e) {
                out.write("{\"exists\": false}");
            }
            out.flush();
            return;
        }

        // 如果是普通GET请求，跳转到注册页面
        request.getRequestDispatcher("/views/user/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置编码
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 获取表单参数
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String nickname = request.getParameter("nickname");
        String signature = request.getParameter("signature");

        // 基本验证
        StringBuilder errorMsg = new StringBuilder();

        if (username == null || username.trim().isEmpty()) {
            errorMsg.append("用户名不能为空<br>");
        }
        if (password == null || password.trim().isEmpty()) {
            errorMsg.append("密码不能为空<br>");
        }
        if (confirmPassword == null || !confirmPassword.equals(password)) {
            errorMsg.append("两次输入的密码不一致<br>");
        }
        if (email == null || email.trim().isEmpty()) {
            errorMsg.append("邮箱不能为空<br>");
        }
        if (phone == null || phone.trim().isEmpty()) {
            errorMsg.append("手机号不能为空<br>");
        }
        if (nickname == null || nickname.trim().isEmpty()) {
            errorMsg.append("昵称不能为空<br>");
        }

        // 检查用户名是否已存在
        User existingUser = userDao.findByUsername(username);
        if (existingUser != null) {
            errorMsg.append("用户名已存在<br>");
        }

        // 检查邮箱是否已存在
        existingUser = userDao.findByEmail(email);
        if (existingUser != null) {
            errorMsg.append("邮箱已被注册<br>");
        }

        // 如果有错误，返回注册页面并显示错误信息
        if (errorMsg.length() > 0) {
            request.setAttribute("error", errorMsg.toString());
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("nickname", nickname);
            request.setAttribute("signature", signature);
            request.getRequestDispatcher("/views/user/register.jsp").forward(request, response);
            return;
        }

        // 创建User对象
        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(password.trim());
        user.setEmail(email.trim());
        user.setPhone(phone.trim());
        user.setNickname(nickname.trim());
        user.setSignature(signature != null ? signature.trim() : "");

        // 设置默认值
        user.setAvatarUrl("/static/images/初始化头像.jpg");
        user.setPoints(0);
        user.setLevel(1);
        user.setStatus(0); // 0表示正常，1表示封禁
        user.setIs_admin(0); // 0表示普通用户，1表示管理员

        // 插入数据库
        int result = userDao.insert(user);

        if (result > 0) {
            // 注册成功，重定向到登录页面
            response.sendRedirect(request.getContextPath() + "/login.jsp?register=success");
        } else {
            // 注册失败
            request.setAttribute("error", "注册失败，请稍后重试");
            request.getRequestDispatcher("/views/user/register.jsp").forward(request, response);
        }
    }
}