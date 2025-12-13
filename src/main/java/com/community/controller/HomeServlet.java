package com.community.controller;

import com.community.model.Post;
import com.community.service.PostService;
import com.community.service.SearchService;
import com.community.service.impl.PostServiceImpl;
import com.community.service.impl.SearchServiceImpl;
import com.community.dao.impl.PostDaoImpl;
import com.community.dao.impl.UserDaoImpl;
import com.community.dao.impl.PointsDaoImpl;
import com.community.dao.impl.CommentDaoImpl;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class HomeServlet extends HttpServlet {

    private PostService postService;
    private SearchService searchService;

    @Override
    public void init() throws ServletException {
        super.init();
        // 初始化PostService
        postService = new PostServiceImpl(
                new PostDaoImpl(),
                new UserDaoImpl(),
                new PointsDaoImpl()
        );

        // 初始化SearchService
        searchService = new SearchServiceImpl(
                new PostDaoImpl(),
                new UserDaoImpl(),
                new CommentDaoImpl()
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action != null && "search".equals(action)) {
            handleSearch(request, response);
        } else {
            showHomePage(request, response);
        }
    }

    // 显示首页
    private void showHomePage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 获取最新帖子
        List<Post> latestPosts = postService.searchPosts("", 1, 10);

        // 获取热门帖子（这里简化处理，使用最新帖子代替）
        List<Post> hotPosts = postService.searchPosts("", 1, 5);

        request.setAttribute("latestPosts", latestPosts);
        request.setAttribute("hotPosts", hotPosts);

        request.getRequestDispatcher("/WEB-INF/views/home/index.jsp").forward(request, response);
    }

    // 处理搜索请求
    private void handleSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String type = request.getParameter("type");
        String pageParam = request.getParameter("page");
        String sizeParam = request.getParameter("size");

        if (keyword == null) {
            keyword = "";
        }

        if (type == null) {
            type = "all";
        }

        int page = 1;
        int pageSize = 20;

        if (pageParam != null && !pageParam.trim().isEmpty()) {
            page = Integer.parseInt(pageParam);
        }

        if (sizeParam != null && !sizeParam.trim().isEmpty()) {
            pageSize = Integer.parseInt(sizeParam);
        }

        SearchService.SearchResult result = searchService.globalSearch(keyword, type, page, pageSize);

        request.setAttribute("result", result);
        request.setAttribute("keyword", keyword);
        request.setAttribute("type", type);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);

        request.getRequestDispatcher("/WEB-INF/views/home/search.jsp").forward(request, response);
    }
}