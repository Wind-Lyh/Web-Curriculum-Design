package com.community.listener;

import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * 会话监听器
 * 监控用户会话的创建和销毁
 */
public class SessionListener implements HttpSessionListener {

    // 统计在线用户数
    private static final AtomicInteger activeSessions = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();

        // 增加在线用户计数
        int count = activeSessions.incrementAndGet();

        // 设置Session属性
        session.setAttribute("sessionCreatedTime", System.currentTimeMillis());

        System.out.println("Session创建 - ID: " + session.getId());
        System.out.println("当前在线用户数: " + count);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();

        // 减少在线用户计数
        int count = activeSessions.decrementAndGet();

        // 获取会话信息
        Long createdTime = (Long) session.getAttribute("sessionCreatedTime");
        String username = (String) session.getAttribute("username");

        if (createdTime != null) {
            long sessionAge = System.currentTimeMillis() - createdTime;
            long minutes = sessionAge / (1000 * 60);

            System.out.println("Session销毁 - ID: " + session.getId());
            System.out.println("用户: " + (username != null ? username : "未登录用户"));
            System.out.println("会话持续时间: " + minutes + "分钟");
        }

        System.out.println("当前在线用户数: " + count);
    }

    /**
     * 获取当前在线用户数
     */
    public static int getActiveSessions() {
        return activeSessions.get();
    }
}