package com.community.controller;

import com.community.model.User;
import com.community.service.AdminService;
import com.community.service.impl.AdminServiceImpl;
import com.community.dao.impl.UserDaoImpl;
import com.community.dao.impl.PostDaoImpl;
import com.community.dao.impl.CommentDaoImpl;
import com.community.dao.impl.AdminDaoImpl;
import com.community.dao.impl.LoginLogDaoImpl;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class AdminServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        super.init();
        // 初始化AdminService
        adminService = new AdminServiceImpl(
                new UserDaoImpl(),
                new PostDaoImpl(),
                new CommentDaoImpl(),
                new AdminDaoImpl(),
                new LoginLogDaoImpl()
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            showDashboard(request, response);
            return;
        }

        try {
            switch (pathInfo) {
                case "/dashboard":
                    showDashboard(request, response);
                    break;
                case "/users":
                    showUserManage(request, response);
                    break;
                case "/posts":
                    showPostManage(request, response);
                    break;
                case "/comments":
                    showCommentManage(request, response);
                    break;
                default:
                    showDashboard(request, response);
                    break;
            }
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            sendError(response, "INVALID_ACTION", "无效的操作类型");
            return;
        }

        try {
            switch (action) {
                case "banUser":
                    handleBanUser(request, response);
                    break;
                case "unbanUser":
                    handleUnbanUser(request, response);
                    break;
                case "deletePost":
                    handleDeletePost(request, response);
                    break;
                case "deleteComment":
                    handleDeleteComment(request, response);
                    break;
                default:
                    sendError(response, "INVALID_ACTION", "无效的操作类型");
                    break;
            }
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    // 显示管理员仪表板
    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getIs_admin() != 1) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        AdminService.DashboardStats stats = adminService.getDashboardStats();

        request.setAttribute("stats", stats);
        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }

    // 显示用户管理页面
    private void showUserManage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getIs_admin() != 1) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        String pageParam = request.getParameter("page");
        String sizeParam = request.getParameter("size");

        int page = 1;
        int pageSize = 20;

        if (pageParam != null && !pageParam.trim().isEmpty()) {
            page = Integer.parseInt(pageParam);
        }

        if (sizeParam != null && !sizeParam.trim().isEmpty()) {
            pageSize = Integer.parseInt(sizeParam);
        }

        List<User> users = adminService.getAllUsers(page, pageSize);

        request.setAttribute("users", users);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);

        request.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(request, response);
    }

    // 显示帖子管理页面
    private void showPostManage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getIs_admin() != 1) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/admin/posts.jsp").forward(request, response);
    }

    // 显示评论管理页面
    private void showCommentManage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getIs_admin() != 1) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/admin/comments.jsp").forward(request, response);
    }

    // 处理封禁用户请求
    private void handleBanUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getIs_admin() != 1) {
            sendError(response, "NO_PERMISSION", "没有管理员权限");
            return;
        }

        String userIdParam = request.getParameter("userId");
        String reason = request.getParameter("reason");

        if (userIdParam == null || userIdParam.trim().isEmpty()) {
            sendError(response, "USER_ID_EMPTY", "用户ID不能为空");
            return;
        }

        if (reason == null || reason.trim().isEmpty()) {
            reason = "违反社区规则";
        }

        int userId = Integer.parseInt(userIdParam);

        boolean success = adminService.banUser(userId, reason);

        if (success) {
            sendResponse(response, "封禁成功", null);
        } else {
            sendError(response, "BAN_FAILED", "封禁失败");
        }
    }

    // 处理解封用户请求
    private void handleUnbanUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getIs_admin() != 1) {
            sendError(response, "NO_PERMISSION", "没有管理员权限");
            return;
        }

        String userIdParam = request.getParameter("userId");

        if (userIdParam == null || userIdParam.trim().isEmpty()) {
            sendError(response, "USER_ID_EMPTY", "用户ID不能为空");
            return;
        }

        int userId = Integer.parseInt(userIdParam);

        boolean success = adminService.unbanUser(userId);

        if (success) {
            sendResponse(response, "解封成功", null);
        } else {
            sendError(response, "UNBAN_FAILED", "解封失败");
        }
    }

    // 处理删除帖子请求（管理员）
    private void handleDeletePost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getIs_admin() != 1) {
            sendError(response, "NO_PERMISSION", "没有管理员权限");
            return;
        }

        String postIdParam = request.getParameter("postId");

        if (postIdParam == null || postIdParam.trim().isEmpty()) {
            sendError(response, "POST_ID_EMPTY", "帖子ID不能为空");
            return;
        }

        int postId = Integer.parseInt(postIdParam);

        boolean success = adminService.deletePost(postId);

        if (success) {
            sendResponse(response, "删除成功", null);
        } else {
            sendError(response, "DELETE_FAILED", "删除失败");
        }
    }

    // 处理删除评论请求（管理员）
    private void handleDeleteComment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getIs_admin() != 1) {
            sendError(response, "NO_PERMISSION", "没有管理员权限");
            return;
        }

        String commentIdParam = request.getParameter("commentId");

        if (commentIdParam == null || commentIdParam.trim().isEmpty()) {
            sendError(response, "COMMENT_ID_EMPTY", "评论ID不能为空");
            return;
        }

        int commentId = Integer.parseInt(commentIdParam);

        boolean success = adminService.deleteComment(commentId);

        if (success) {
            sendResponse(response, "删除成功", null);
        } else {
            sendError(response, "DELETE_FAILED", "删除失败");
        }
    }

    // 发送成功响应
    private void sendResponse(HttpServletResponse response, String message, String data)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"success\":true,");
        json.append("\"message\":\"").append(escapeJson(message)).append("\"");
        if (data != null) {
            json.append(",\"data\":").append(data);
        }
        json.append("}");

        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }

    // 发送错误响应
    private void sendError(HttpServletResponse response, String errorCode, String message)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"success\":false,");
        json.append("\"errorCode\":\"").append(escapeJson(errorCode)).append("\",");
        json.append("\"message\":\"").append(escapeJson(message)).append("\"");
        json.append("}");

        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }

    // 处理异常
    private void handleError(HttpServletResponse response, Exception e) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (e instanceof IllegalArgumentException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(response, "INVALID_PARAM", e.getMessage());
        } else if (e instanceof SecurityException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            sendError(response, "NO_PERMISSION", e.getMessage());
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(response, "INTERNAL_ERROR", "服务器内部错误");
            e.printStackTrace();
        }
    }

    // 转义JSON字符串
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }

        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}