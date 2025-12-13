package com.community.controller;

import com.community.model.User;
import com.community.service.LikeService;
import com.community.service.impl.LikeServiceImpl;
import com.community.dao.impl.LikeDaoImpl;
import com.community.dao.impl.PostDaoImpl;
import com.community.dao.impl.CommentDaoImpl;
import com.community.dao.impl.PointsDaoImpl;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class LikeServlet extends HttpServlet {

    private LikeService likeService;

    @Override
    public void init() throws ServletException {
        super.init();
        // 初始化LikeService
        likeService = new LikeServiceImpl(
                new LikeDaoImpl(),
                new PostDaoImpl(),
                new CommentDaoImpl(),
                new PointsDaoImpl()
        );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            sendError(response, "INVALID_ACTION", "无效的操作类型");
            return;
        }

        try {
            switch (pathInfo) {
                case "/post":
                    handleLikePost(request, response);
                    break;
                case "/comment":
                    handleLikeComment(request, response);
                    break;
                default:
                    sendError(response, "INVALID_ACTION", "无效的操作类型");
                    break;
            }
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    // 处理点赞帖子请求
    private void handleLikePost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");

        String postIdParam = request.getParameter("postId");
        String action = request.getParameter("action"); // like or unlike

        if (postIdParam == null || postIdParam.trim().isEmpty()) {
            sendError(response, "POST_ID_EMPTY", "帖子ID不能为空");
            return;
        }

        int postId = Integer.parseInt(postIdParam);

        boolean success;
        String message;

        if ("unlike".equals(action)) {
            success = likeService.unlikePost(user.getId(), postId);
            message = success ? "取消点赞成功" : "取消点赞失败";
        } else {
            success = likeService.likePost(user.getId(), postId);
            message = success ? "点赞成功" : "点赞失败或已点过赞";
        }

        if (success) {
            // 获取更新后的点赞数
            int likeCount = likeService.getLikeCount("post", postId);
            sendResponse(response, message, "{\"likeCount\":" + likeCount + "}");
        } else {
            sendError(response, "LIKE_FAILED", message);
        }
    }

    // 处理点赞评论请求
    private void handleLikeComment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");

        String commentIdParam = request.getParameter("commentId");
        String action = request.getParameter("action"); // like or unlike

        if (commentIdParam == null || commentIdParam.trim().isEmpty()) {
            sendError(response, "COMMENT_ID_EMPTY", "评论ID不能为空");
            return;
        }

        int commentId = Integer.parseInt(commentIdParam);

        boolean success;
        String message;

        if ("unlike".equals(action)) {
            success = likeService.unlikeComment(user.getId(), commentId);
            message = success ? "取消点赞成功" : "取消点赞失败";
        } else {
            success = likeService.likeComment(user.getId(), commentId);
            message = success ? "点赞成功" : "点赞失败或已点过赞";
        }

        if (success) {
            // 获取更新后的点赞数
            int likeCount = likeService.getLikeCount("comment", commentId);
            sendResponse(response, message, "{\"likeCount\":" + likeCount + "}");
        } else {
            sendError(response, "LIKE_FAILED", message);
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