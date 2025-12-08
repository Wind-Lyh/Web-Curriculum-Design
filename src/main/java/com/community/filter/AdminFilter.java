package com.community.filter;

import com.community.model.User;
import java.io.IOException;
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
 * 管理员权限过滤器
 * 检查用户是否具有管理员权限
 */
public class AdminFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("管理员权限过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 获取Session
        HttpSession session = httpRequest.getSession(false);

        if (session == null) {
            // 没有Session，跳转到登录页面
            redirectToLogin(httpRequest, httpResponse);
            return;
        }
    }

    /**
     * 跳转到登录页面
     */
    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String contextPath = request.getContextPath();
        response.sendRedirect(contextPath + "/user?action=login&msg=请先登录管理员账号");
    }

    @Override
    public void destroy() {
        System.out.println("管理员权限过滤器销毁");
    }
}