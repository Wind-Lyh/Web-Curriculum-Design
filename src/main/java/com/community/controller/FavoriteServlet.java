package com.community.controller;

import com.community.model.User;
import com.community.model.Post;
import com.community.service.FavoriteService;
import com.community.service.impl.FavoriteServiceImpl;
import com.community.dao.impl.FavoriteDaoImpl;
import com.community.dao.impl.PostDaoImpl;
import com.community.dao.impl.UserDaoImpl;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class FavoriteServlet extends HttpServlet {

    private FavoriteService favoriteService;

    @Override
    public void init() throws ServletException {
        super.init();
        // 初始化FavoriteService
        favoriteService = new FavoriteServiceImpl(
                new FavoriteDaoImpl(),
                new PostDaoImpl(),
                new UserDaoImpl()
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            showFavorites(request, response);
            return;
        }

        try {
            if ("/list".equals(pathInfo)) {
                showFavorites(request, response);
            } else {
                sendError(response, "INVALID_ACTION", "无效的请求");
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
                case "add":
                    handleAddFavorite(request, response);
                    break;
                case "remove":
                    handleRemoveFavorite(request, response);
                    break;
                default:
                    sendError(response, "INVALID_ACTION", "无效的操作类型");
                    break;
            }
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    // 显示用户收藏列表
    private void showFavorites(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        User user = (User) session.getAttribute("user");

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

        List<Post> favorites = favoriteService.getFavoritesByUser(user.getId(), page, pageSize);

        request.setAttribute("favorites", favorites);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);

        request.getRequestDispatcher("/WEB-INF/views/favorite/list.jsp").forward(request, response);
    }

    // 处理收藏帖子请求
    private void handleAddFavorite(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");

        String postIdParam = request.getParameter("postId");
        if (postIdParam == null || postIdParam.trim().isEmpty()) {
            sendError(response, "POST_ID_EMPTY", "帖子ID不能为空");
            return;
        }

        int postId = Integer.parseInt(postIdParam);

        boolean success = favoriteService.addFavorite(user.getId(), postId);

        if (success) {
            int favoriteCount = favoriteService.getFavoriteCount(postId);
            sendResponse(response, "收藏成功", "{\"favoriteCount\":" + favoriteCount + "}");
        } else {
            sendError(response, "FAVORITE_FAILED", "收藏失败或已收藏");
        }
    }

    // 处理取消收藏请求
    private void handleRemoveFavorite(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");

        String postIdParam = request.getParameter("postId");
        if (postIdParam == null || postIdParam.trim().isEmpty()) {
            sendError(response, "POST_ID_EMPTY", "帖子ID不能为空");
            return;
        }

        int postId = Integer.parseInt(postIdParam);

        boolean success = favoriteService.removeFavorite(user.getId(), postId);

        if (success) {
            int favoriteCount = favoriteService.getFavoriteCount(postId);
            sendResponse(response, "取消收藏成功", "{\"favoriteCount\":" + favoriteCount + "}");
        } else {
            sendError(response, "UNFAVORITE_FAILED", "取消收藏失败");
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