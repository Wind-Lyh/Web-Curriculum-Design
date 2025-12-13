package com.community.controller;

import com.community.model.Post;
import com.community.model.User;
import com.community.service.PostService;
import com.community.service.impl.PostServiceImpl;
import com.community.dao.impl.PostDaoImpl;
import com.community.dao.impl.UserDaoImpl;
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

public class PostServlet extends HttpServlet {

    private PostService postService;

    @Override
    public void init() throws ServletException {
        super.init();
        // 初始化PostService
        postService = new PostServiceImpl(
                new PostDaoImpl(),
                new UserDaoImpl(),
                new PointsDaoImpl()
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            showPostList(request, response);
            return;
        }

        try {
            switch (pathInfo) {
                case "/detail":
                    showPostDetail(request, response);
                    break;
                case "/create":
                    showCreatePostPage(request, response);
                    break;
                case "/edit":
                    showEditPostPage(request, response);
                    break;
                case "/list":
                    showPostList(request, response);
                    break;
                case "/search":
                    searchPosts(request, response);
                    break;
                default:
                    showPostList(request, response);
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
                case "create":
                    handleCreatePost(request, response);
                    break;
                case "edit":
                    handleEditPost(request, response);
                    break;
                case "delete":
                    handleDeletePost(request, response);
                    break;
                default:
                    sendError(response, "INVALID_ACTION", "无效的操作类型");
                    break;
            }
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    // 显示帖子详情
    private void showPostDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                throw new IllegalArgumentException("帖子ID不能为空");
            }

            int postId = Integer.parseInt(idParam);
            Post post = postService.getPostById(postId);

            // 增加浏览量
            postService.increaseViewCount(postId);

            request.setAttribute("post", post);
            request.getRequestDispatcher("/WEB-INF/views/post/detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("帖子ID格式错误");
        }
    }

    // 显示创建帖子页面
    private void showCreatePostPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/post/create.jsp").forward(request, response);
    }

    // 显示编辑帖子页面
    private void showEditPostPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            throw new IllegalArgumentException("帖子ID不能为空");
        }

        int postId = Integer.parseInt(idParam);
        Post post = postService.getPostById(postId);

        // 验证权限：只能编辑自己的帖子
        User currentUser = (User) session.getAttribute("user");
        if (post.getUserId() != currentUser.getId()) {
            throw new SecurityException("无权编辑此帖子");
        }

        request.setAttribute("post", post);
        request.getRequestDispatcher("/WEB-INF/views/post/edit.jsp").forward(request, response);
    }

    // 显示帖子列表
    private void showPostList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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

        // 显示最新帖子
        List<Post> posts = postService.searchPosts("", page, pageSize);

        request.setAttribute("posts", posts);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);

        request.getRequestDispatcher("/WEB-INF/views/post/list.jsp").forward(request, response);
    }

    // 搜索帖子
    private void searchPosts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String pageParam = request.getParameter("page");
        String sizeParam = request.getParameter("size");

        if (keyword == null) {
            keyword = "";
        }

        int page = 1;
        int pageSize = 20;

        if (pageParam != null && !pageParam.trim().isEmpty()) {
            page = Integer.parseInt(pageParam);
        }

        if (sizeParam != null && !sizeParam.trim().isEmpty()) {
            pageSize = Integer.parseInt(sizeParam);
        }

        List<Post> posts = postService.searchPosts(keyword, page, pageSize);

        request.setAttribute("posts", posts);
        request.setAttribute("keyword", keyword);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);

        request.getRequestDispatcher("/WEB-INF/views/post/search.jsp").forward(request, response);
    }

    // 处理创建帖子请求
    private void handleCreatePost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");

        String title = request.getParameter("title");
        String content = request.getParameter("content");

        if (title == null || title.trim().isEmpty()) {
            sendError(response, "TITLE_EMPTY", "标题不能为空");
            return;
        }

        if (content == null || content.trim().isEmpty()) {
            sendError(response, "CONTENT_EMPTY", "内容不能为空");
            return;
        }

        Post post = new Post();
        post.setTitle(title.trim());
        post.setContent(content.trim());
        post.setUserId(user.getId());
        post.setCreateTime(new Date());

        Post createdPost = postService.createPost(post);

        sendResponse(response, "发帖成功", createPostJson(createdPost));
    }

    // 处理编辑帖子请求
    private void handleEditPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");

        String idParam = request.getParameter("id");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        if (idParam == null || idParam.trim().isEmpty()) {
            sendError(response, "ID_EMPTY", "帖子ID不能为空");
            return;
        }

        int postId = Integer.parseInt(idParam);

        // 验证权限：先获取原帖子
        Post originalPost = postService.getPostById(postId);
        if (originalPost.getUserId() != user.getId()) {
            sendError(response, "NO_PERMISSION", "无权编辑此帖子");
            return;
        }

        Post post = new Post();
        post.setId(postId);
        post.setTitle(title.trim());
        post.setContent(content.trim());
        post.setUserId(user.getId());
        post.setUpdateTime(new Date());

        Post updatedPost = postService.updatePost(post);

        sendResponse(response, "编辑成功", createPostJson(updatedPost));
    }

    // 处理删除帖子请求
    private void handleDeletePost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            sendError(response, "ID_EMPTY", "帖子ID不能为空");
            return;
        }

        int postId = Integer.parseInt(idParam);
        boolean isAdmin = user.getIs_admin() == 1;

        boolean success = postService.deletePost(postId, user.getId(), isAdmin);

        if (success) {
            sendResponse(response, "删除成功", null);
        } else {
            sendError(response, "DELETE_FAILED", "删除失败");
        }
    }

    // 构建帖子JSON
    private String createPostJson(Post post) {
        if (post == null) {
            return "null";
        }

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(post.getId()).append(",");
        json.append("\"title\":\"").append(escapeJson(post.getTitle())).append("\",");
        json.append("\"content\":\"").append(escapeJson(post.getContent())).append("\",");
        json.append("\"userId\":").append(post.getUserId()).append(",");
        json.append("\"viewCount\":").append(post.getViewCount()).append(",");
        json.append("\"likeCount\":").append(post.getLikeCount()).append(",");
        json.append("\"commentCount\":").append(post.getCommentCount());
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