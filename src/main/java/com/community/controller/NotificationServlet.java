package com.community.controller;

import com.community.model.User;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class NotificationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            showNotifications(request, response);
            return;
        }

        try {
            switch (pathInfo) {
                case "/list":
                    showNotifications(request, response);
                    break;
                case "/unread":
                    showUnreadNotifications(request, response);
                    break;
                default:
                    sendError(response, "INVALID_ACTION", "无效的请求");
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
            if ("markAsRead".equals(action)) {
                handleMarkAsRead(request, response);
            } else {
                sendError(response, "INVALID_ACTION", "无效的操作类型");
            }
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    // 显示用户所有通知
    private void showNotifications(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        // 这里需要NotificationService，暂时简化处理
        // List<Notification> notifications = notificationService.getNotificationsByUserId(user.getId());

        request.setAttribute("userId", user.getId());
        request.getRequestDispatcher("/WEB-INF/views/notification/list.jsp").forward(request, response);
    }

    // 显示未读通知
    private void showUnreadNotifications(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        // 这里需要NotificationService，暂时简化处理
        // List<Notification> unreadNotifications = notificationService.getUnreadNotifications(user.getId());

        request.setAttribute("userId", user.getId());
        request.getRequestDispatcher("/WEB-INF/views/notification/unread.jsp").forward(request, response);
    }

    // 处理标记通知为已读请求
    private void handleMarkAsRead(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");

        String notificationIdParam = request.getParameter("notificationId");

        if (notificationIdParam == null || notificationIdParam.trim().isEmpty()) {
            sendError(response, "NOTIFICATION_ID_EMPTY", "通知ID不能为空");
            return;
        }

        int notificationId = Integer.parseInt(notificationIdParam);

        // 这里需要NotificationService，暂时简化处理
        // boolean success = notificationService.markAsRead(notificationId, user.getId());
        boolean success = false;

        if (success) {
            sendResponse(response, "标记为已读成功", null);
        } else {
            sendError(response, "MARK_READ_FAILED", "标记为已读失败");
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