package com.community.listener;

import com.community.util.FileUtil;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 应用上下文监听器
 * 在应用启动和关闭时执行初始化/清理工作
 */
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== 社区评论系统启动 ===");

        ServletContext context = sce.getServletContext();

        // 1. 初始化数据库配置
        System.out.println("初始化数据库配置...");

        // 2. 创建上传目录
        String realPath = context.getRealPath("/");
        System.out.println("应用真实路径: " + realPath);

        FileUtil.createUploadDirs(realPath);
        System.out.println("上传目录创建完成");

        // 3. 设置应用属性
        context.setAttribute("appName", "社区评论系统");
        context.setAttribute("version", "1.0.0");
        context.setAttribute("startTime", System.currentTimeMillis());

        // 4. 加载配置参数
        String defaultAvatar = context.getInitParameter("defaultAvatar");
        if (defaultAvatar != null) {
            context.setAttribute("defaultAvatar", defaultAvatar);
        }

        System.out.println("应用初始化完成");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=== 社区评论系统关闭 ===");

        // 清理工作
        ServletContext context = sce.getServletContext();

        // 记录运行时间
        Long startTime = (Long) context.getAttribute("startTime");
        if (startTime != null) {
            long runTime = System.currentTimeMillis() - startTime;
            long hours = runTime / (1000 * 60 * 60);
            long minutes = (runTime % (1000 * 60 * 60)) / (1000 * 60);
            long seconds = (runTime % (1000 * 60)) / 1000;

            System.out.println(String.format("应用运行时间: %d小时 %d分钟 %d秒",
                    hours, minutes, seconds));
        }

        System.out.println("应用清理完成");
    }
}