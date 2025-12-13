package com.community.controller;

import com.community.model.Comment;
import com.community.model.User;
import com.community.service.CommentService;
import com.community.service.impl.CommentServiceImpl;
import com.community.dao.impl.CommentDaoImpl;
import com.community.dao.impl.PostDaoImpl;
import com.community.dao.impl.NotificationDaoImpl;
import com.community.dao.impl.PointsDaoImpl;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

public class CommentServlet extends HttpServlet {

    private CommentService commentService;

    @Override
    public void init() throws ServletException {
        super.init();
        // 初始化CommentService
        commentService = new CommentServiceImpl(
                new CommentDaoImpl(),
                new PostDaoImpl(),
                new NotificationDaoImpl(),
                new PointsDaoImpl()
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            sendError(response, "INVALID_ACTION", "无效的请求");
            return;
        }

        try {
            if ("/list".equals(pathInfo)) {
                showComments(request, response);
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
                    handleAddComment(request, response);
                    break;
                case "reply":
                    handleReplyComment(request, response);
                    break;
                case "edit":
                    handleEditComment(request, response);
                    break;
                case "delete":
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

    // 显示评论列表
    private void showComments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String postIdParam = request.getParameter("postId");
        if (postIdParam == null || postIdParam.trim().isEmpty()) {
            throw new IllegalArgumentException("帖子ID不能为空");
        }

        int postId = Integer.parseInt(postIdParam);
        List<Comment> comments = commentService.getCommentsByPost(postId);

        request.setAttribute("comments", comments);
        request.setAttribute("postId", postId);

        request.getRequestDispatcher("/WEB-INF/views/comment/list.jsp").forward(request, response);
    }

    // 处理添加评论请求
    private void handleAddComment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");

        String postIdParam = request.getParameter("postId");
        String content = request.getParameter("content");
        String parentIdParam = request.getParameter("parentId");

        if (postIdParam == null || postIdParam.trim().isEmpty()) {
            sendError(response, "POST_ID_EMPTY", "帖子ID不能为空");
            return;
        }

        if (content == null || content.trim().isEmpty()) {
            sendError(response, "CONTENT_EMPTY", "评论内容不能为空");
            return;
        }

        int postId = Integer.parseInt(postIdParam);
        Integer parentId = null;

        if (parentIdParam != null && !parentIdParam.trim().isEmpty()) {
            parentId = Integer.parseInt(parentIdParam);
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(user.getId());
        comment.setContent(content.trim());
        comment.setParentId(parentId);
        comment.setCreateTime(new Date());

        Comment addedComment = commentService.addComment(comment);

        sendResponse(response, "评论成功", createCommentJson(addedComment));
    }

    // 处理回复评论请求
    private void handleReplyComment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        handleAddComment(request, response); // 逻辑与添加评论相同
    }

    // 处理编辑评论请求
    private void handleEditComment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");

        String idParam = request.getParameter("id");
        String content = request.getParameter("content");

        if (idParam == null || idParam.trim().isEmpty()) {
            sendError(response, "ID_EMPTY", "评论ID不能为空");
            return;
        }

        if (content == null || content.trim().isEmpty()) {
            sendError(response, "CONTENT_EMPTY", "评论内容不能为空");
            return;
        }

        int commentId = Integer.parseInt(idParam);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setContent(content.trim());
        comment.setUserId(user.getId());
        comment.setUpdateTime(new Date());

        Comment updatedComment = commentService.updateComment(comment);

        sendResponse(response, "编辑成功", createCommentJson(updatedComment));
    }

    // 处理删除评论请求
    private void handleDeleteComment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            sendError(response, "ID_EMPTY", "评论ID不能为空");
            return;
        }

        int commentId = Integer.parseInt(idParam);
        boolean isAdmin = user.getIs_admin() == 1;

        boolean success = commentService.deleteComment(commentId, user.getId(), isAdmin);

        if (success) {
            sendResponse(response, "删除成功", null);
        } else {
            sendError(response, "DELETE_FAILED", "删除失败");
        }
    }

    // 构建评论JSON
    private String createCommentJson(Comment comment) {
        if (comment == null) {
            return "null";
        }

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(comment.getId()).append(",");
        json.append("\"content\":\"").append(escapeJson(comment.getContent())).append("\",");
        json.append("\"userId\":").append(comment.getUserId()).append(",");
        json.append("\"postId\":").append(comment.getPostId()).append(",");
        json.append("\"parentId\":").append(comment.getParentId()).append(",");
        json.append("\"likeCount\":").append(comment.getLikeCount()).append(",");
        json.append("\"createTime\":\"").append(comment.getCreateTime()).append("\"");
        json.append("}");

        return json.toString();
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