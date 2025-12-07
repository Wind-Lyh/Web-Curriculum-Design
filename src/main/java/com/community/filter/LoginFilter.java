package com.community.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录验证过滤器
 * 检查用户是否已登录，未登录则跳转到登录页面
 */
public class LoginFilter implements Filter {

    // 不需要登录验证的URL集合
    private Set<String> excludedUrls = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 获取不需要过滤的URL配置
        String excludedUrlsParam = filterConfig.getInitParameter("excludedUrls");
        if (excludedUrlsParam != null) {
            String[] urls = excludedUrlsParam.split(",");
            for (String url : urls) {
                excludedUrls.add(url.trim());
            }
        }

        System.out.println("登录验证过滤器初始化，排除URL: " + excludedUrls);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 获取请求的URL
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // 获取查询参数
        String queryString = httpRequest.getQueryString();
        String fullPath = path;
        if (queryString != null) {
            fullPath = path + "?" + queryString;
        }

        System.out.println("=== LoginFilter 开始处理请求 ===");
        System.out.println("请求URI: " + requestURI);
        System.out.println("上下文路径: " + contextPath);
        System.out.println("路径部分: " + path);
        System.out.println("完整路径: " + fullPath);
        System.out.println("查询参数: " + queryString);
        System.out.println("排除URL列表: " + excludedUrls);

        // 检查是否是需要排除的URL
        if (isExcludedUrl(fullPath) || isExcludedUrl(path)) {
            System.out.println("✅ 路径被排除，继续执行过滤器链");
            chain.doFilter(request, response);
            return;
        }

        // 获取Session
        HttpSession session = httpRequest.getSession(false);

        // 检查用户是否已登录
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        if (isLoggedIn) {
            // 用户已登录，继续执行
            System.out.println("✅ 用户已登录，继续执行");
            chain.doFilter(request, response);
        } else {
            // 用户未登录，跳转到登录页面
            System.out.println("❌ 用户未登录，跳转到登录页面，请求路径: " + fullPath);

            // 保存原始请求URL，登录后可以跳转回来
            String originalUrl = httpRequest.getRequestURI();
            if (queryString != null) {
                originalUrl += "?" + queryString;
            }

            // 将原始URL存入Session
            if (session != null) {
                session.setAttribute("originalUrl", originalUrl);
            }

            // 跳转到登录页面
            httpResponse.sendRedirect(contextPath + "/index.jsp");
        }
    }
    /**
     * 判断URL是否不需要登录验证
     */
    private boolean isExcludedUrl(String url) {
        System.out.println("检查URL是否需要排除: " + url);

        // 移除查询参数，只检查路径部分
        String path = url;
        if (url.contains("?")) {
            path = url.substring(0, url.indexOf("?"));
        }

        // 1. 检查完全匹配（不含查询参数）
        if (excludedUrls.contains(path)) {
            System.out.println("完全匹配排除: " + path);
            return true;
        }

        // 2. 检查完全匹配（含完整URL）
        if (excludedUrls.contains(url)) {
            System.out.println("完全匹配排除: " + url);
            return true;
        }

        // 3. 检查前缀匹配（如/static/开头的所有URL）
        for (String excludedUrl : excludedUrls) {
            if (excludedUrl.endsWith("/") && path.startsWith(excludedUrl)) {
                System.out.println("前缀匹配排除: " + path + ", 匹配前缀: " + excludedUrl);
                return true;
            }
        }

        // 4. 检查静态资源文件
        if (path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") ||
                path.endsWith(".jpg") || path.endsWith(".gif") || path.endsWith(".ico")) {
            System.out.println("静态资源排除: " + path);
            return true;
        }

        System.out.println("URL未被排除: " + url);
        return false;
    }

    @Override
    public void destroy() {
        System.out.println("登录验证过滤器销毁");
    }
}