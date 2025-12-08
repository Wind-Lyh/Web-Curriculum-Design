# web.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://java.sun.com/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
         http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
         version="3.0">

    <!-- 显示名称 -->
    <display-name>社区评论系统</display-name>

    <!-- 欢迎页面 -->
    <welcome-file-list>
        <welcome-file>views/index.jsp</welcome-file>
        <welcome-file>home</welcome-file>
    </welcome-file-list>

    <!-- ========== 过滤器配置 ========== -->

    <!-- 1. 字符编码过滤器 -->
    <filter>
        <filter-name>CharacterEncodingFilter</filter-name>
        <filter-class>com.community.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>CharacterEncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <!-- 2. 登录验证过滤器 -->
    <filter>
        <filter-name>LoginFilter</filter-name>
        <filter-class>com.community.filter.LoginFilter</filter-class>
        <init-param>
            <param-name>excludedUrls</param-name>
            <param-value>
                /index.jsp,
                /user/login.jsp,
                /user/register.jsp,
                /views/users/rotateCaptcha.jsp,
                /views/users/slideCaptcha.jsp,
                /captcha,
                /static/,
                /home,
                /user/checkLogin,
                /user/Captcha_Num,
                /user/Captcha_Num_pd,
                /user/rotateCaptcha,
                /user/validateRotateCaptcha,
                /user/slideCaptcha,
                /user/validateSlideCaptcha,
                /user/logout,
                /user/loginWithRole,
                /user/switchRole,
                /user/getUserRoles
            </param-value>
        </init-param>
    </filter>

    <!-- 3. 管理员权限过滤器 -->
    <filter>
        <filter-name>AdminFilter</filter-name>
        <filter-class>com.community.filter.AdminFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>AdminFilter</filter-name>
        <url-pattern>/admin/*</url-pattern>
    </filter-mapping>

    <!-- ========== Servlet配置 ========== -->

    <!-- 用户相关Servlet -->
    <servlet>
        <servlet-name>UserServlet</servlet-name>
        <servlet-class>com.community.controller.UserServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>UserServlet</servlet-name>
        <url-pattern>/user/*</url-pattern>
    </servlet-mapping>

    <!-- 帖子相关Servlet -->
    <servlet>
        <servlet-name>PostServlet</servlet-name>
        <servlet-class>com.community.controller.PostServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>PostServlet</servlet-name>
        <url-pattern>/post/*</url-pattern>
    </servlet-mapping>

    <!-- 评论相关Servlet -->
    <servlet>
        <servlet-name>CommentServlet</servlet-name>
        <servlet-class>com.community.controller.CommentServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>CommentServlet</servlet-name>
        <url-pattern>/comment/*</url-pattern>
    </servlet-mapping>

    <!-- 管理员Servlet -->
    <servlet>
        <servlet-name>AdminServlet</servlet-name>
        <servlet-class>com.community.controller.AdminServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>AdminServlet</servlet-name>
        <url-pattern>/admin/*</url-pattern>
    </servlet-mapping>

    <!-- 首页Servlet -->
    <servlet>
        <servlet-name>HomeServlet</servlet-name>
        <servlet-class>com.community.controller.HomeServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>HomeServlet</servlet-name>
        <url-pattern>/home</url-pattern>
    </servlet-mapping>

    <!-- 版块Servlet -->
    <servlet>
        <servlet-name>SectionServlet</servlet-name>
        <servlet-class>com.community.controller.SectionServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>SectionServlet</servlet-name>
        <url-pattern>/section/*</url-pattern>
    </servlet-mapping>

    <!-- 点赞Servlet -->
    <servlet>
        <servlet-name>LikeServlet</servlet-name>
        <servlet-class>com.community.controller.LikeServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>LikeServlet</servlet-name>
        <url-pattern>/like/*</url-pattern>
    </servlet-mapping>

    <!-- 收藏Servlet -->
    <servlet>
        <servlet-name>FavoriteServlet</servlet-name>
        <servlet-class>com.community.controller.FavoriteServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>FavoriteServlet</servlet-name>
        <url-pattern>/favorite/*</url-pattern>
    </servlet-mapping>

    <!-- 积分Servlet -->
    <servlet>
        <servlet-name>PointsServlet</servlet-name>
        <servlet-class>com.community.controller.PointsServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>PointsServlet</servlet-name>
        <url-pattern>/points/*</url-pattern>
    </servlet-mapping>

    <!-- 文件上传Servlet -->
    <servlet>
        <servlet-name>FileServlet</servlet-name>
        <servlet-class>com.community.controller.FileServlet</servlet-class>
        <multipart-config>
            <max-file-size>10485760</max-file-size> <!-- 10MB -->
            <max-request-size>20971520</max-request-size> <!-- 20MB -->
            <file-size-threshold>5242880</file-size-threshold> <!-- 5MB -->
        </multipart-config>
    </servlet>
    <servlet-mapping>
        <servlet-name>FileServlet</servlet-name>
        <url-pattern>/file/*</url-pattern>
    </servlet-mapping>

    <!-- 退出登录Servlet -->
    <servlet>
        <servlet-name>LogoutServlet</servlet-name>
        <servlet-class>com.community.controller.LogoutServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>LogoutServlet</servlet-name>
        <url-pattern>/logout</url-pattern>
    </servlet-mapping>

    <!-- ========== 监听器配置 ========== -->
    <listener>
        <listener-class>com.community.listener.ContextListener</listener-class>
    </listener>

    <listener>
        <listener-class>com.community.listener.SessionListener</listener-class>
    </listener>

    <!-- ========== 错误页面配置 ========== -->
    <error-page>
        <error-code>404</error-code>
        <location>/views/error/404.jsp</location>
    </error-page>

    <error-page>
        <error-code>500</error-code>
        <location>/views/error/500.jsp</location>
    </error-page>

    <error-page>
        <error-code>403</error-code>
        <location>/views/error/403.jsp</location>
    </error-page>

    <!-- ========== Session配置 ========== -->
    <session-config>
        <session-timeout>30</session-timeout>
    </session-config>

    <!-- ========== 上下文参数 ========== -->
    <context-param>
        <param-name>uploadPath</param-name>
        <param-value>uploads</param-value>
    </context-param>

    <context-param>
        <param-name>defaultAvatar</param-name>
        <param-value>default_avatar.png</param-value>
    </context-param>
</web-app>
```

# 文件树

```
community-system/
├── pom.xml (Maven配置文件)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── community/
│   │   │           ├── controller/          # 控制器层（Servlet）
│   │   │           │   ├── UserServlet.java           # 用户相关Servlet
│   │   │           │   ├── PostServlet.java           # 帖子相关Servlet
│   │   │           │   ├── CommentServlet.java        # 评论相关Servlet
│   │   │           │   ├── LikeServlet.java           # 点赞相关Servlet
│   │   │           │   ├── FavoriteServlet.java       # 收藏相关Servlet
│   │   │           │   ├── PointsServlet.java         # 积分相关Servlet
│   │   │           │   ├── FileServlet.java           # 文件上传下载Servlet
│   │   │           │   ├── AdminServlet.java          # 管理员后台Servlet
│   │   │           │   ├── HomeServlet.java           # 首页Servlet
│   │   │           │   ├── SectionServlet.java        # 版块Servlet
│   │   │           │   ├── NotificationServlet.java   # 通知Servlet
│   │   │           │   ├── LogoutServlet.java         # 退出登录Servlet
│   │   │           │   └── CaptchaServlet.java        # 验证码生成Servlet
│   │   │           ├── service/             # 业务逻辑层
│   │   │           │   ├── UserService.java          # 用户业务接口
│   │   │           │   ├── PostService.java          # 帖子业务接口
│   │   │           │   ├── CommentService.java       # 评论业务接口
│   │   │           │   ├── LikeService.java          # 点赞业务接口
│   │   │           │   ├── FavoriteService.java      # 收藏业务接口
│   │   │           │   ├── PointsService.java        # 积分业务接口
│   │   │           │   ├── NotificationService.java  # 通知业务接口
│   │   │           │   ├── FileService.java          # 文件业务接口
│   │   │           │   ├── AdminService.java         # 管理员业务接口
│   │   │           │   ├── SectionService.java       # 版块业务接口
│   │   │           │   └── impl/                     # 业务实现类
│   │   │           │       ├── UserServiceImpl.java
│   │   │           │       ├── PostServiceImpl.java
│   │   │           │       ├── CommentServiceImpl.java
│   │   │           │       ├── LikeServiceImpl.java
│   │   │           │       ├── FavoriteServiceImpl.java
│   │   │           │       ├── PointsServiceImpl.java
│   │   │           │       ├── NotificationServiceImpl.java
│   │   │           │       ├── FileServiceImpl.java
│   │   │           │       ├── AdminServiceImpl.java
│   │   │           │       └── SectionServiceImpl.java
│   │   │           ├── dao/                 # 数据访问层
│   │   │           │   ├── UserDao.java              # 用户数据访问接口
│   │   │           │   ├── PostDao.java              # 帖子数据访问接口
│   │   │           │   ├── CommentDao.java           # 评论数据访问接口
│   │   │           │   ├── SectionDao.java           # 版块数据访问接口
│   │   │           │   ├── LikeDao.java              # 点赞数据访问接口
│   │   │           │   ├── FavoriteDao.java          # 收藏数据访问接口
│   │   │           │   ├── PointsDao.java            # 积分数据访问接口
│   │   │           │   ├── NotificationDao.java      # 通知数据访问接口
│   │   │           │   ├── FileDao.java              # 文件数据访问接口
│   │   │           │   ├── AdminDao.java             # 管理员数据访问接口
│   │   │           │   ├── BrowseHistoryDao.java     # 浏览历史数据访问接口
│   │   │           │   ├── LoginLogDao.java          # 登录日志数据访问接口
│   │   │           │   ├── VirtualGoodDao.java       # 虚拟道具数据访问接口
│   │   │           │   ├── ExchangeDao.java          # 兑换记录数据访问接口
│   │   │           │   └── impl/                     # JDBC实现类
│   │   │           │       ├── UserDaoImpl.java
│   │   │           │       ├── PostDaoImpl.java
│   │   │           │       ├── CommentDaoImpl.java
│   │   │           │       ├── SectionDaoImpl.java
│   │   │           │       ├── LikeDaoImpl.java
│   │   │           │       ├── FavoriteDaoImpl.java
│   │   │           │       ├── PointsDaoImpl.java
│   │   │           │       ├── NotificationDaoImpl.java
│   │   │           │       ├── FileDaoImpl.java
│   │   │           │       ├── AdminDaoImpl.java
│   │   │           │       ├── BrowseHistoryDaoImpl.java
│   │   │           │       ├── LoginLogDaoImpl.java
│   │   │           │       ├── VirtualGoodDaoImpl.java
│   │   │           │       └── ExchangeDaoImpl.java
│   │   │           ├── model/               # 实体类
│   │   │           │   ├── User.java                 # 用户实体类
│   │   │           │   ├── Section.java              # 版块实体类
│   │   │           │   ├── Post.java                 # 帖子实体类
│   │   │           │   ├── Comment.java              # 评论实体类
│   │   │           │   ├── Like.java                 # 点赞实体类
│   │   │           │   ├── Favorite.java             # 收藏实体类
│   │   │           │   ├── Attachment.java           # 附件实体类
│   │   │           │   ├── Notification.java         # 通知实体类
│   │   │           │   ├── PointsRecord.java         # 积分记录实体类
│   │   │           │   ├── VirtualGood.java          # 虚拟道具实体类
│   │   │           │   ├── Exchange.java             # 兑换记录实体类
│   │   │           │   ├── BrowseHistory.java        # 浏览历史实体类
│   │   │           │   ├── LoginLog.java             # 登录日志实体类
│   │   │           │   └── AdminLog.java             # 管理员操作日志实体类
│   │   │           ├── util/                # 工具类
│   │   │           │   ├── DBUtil.java               # 数据库工具类（JDBC连接管理）
│   │   │           │   ├── StringUtil.java           # 字符串工具类
│   │   │           │   ├── DateUtil.java             # 日期工具类
│   │   │           │   ├── FileUtil.java             # 文件工具类
│   │   │           │   ├── JsonUtil.java             # JSON工具类
│   │   │           │   ├── PageUtil.java             # 分页工具类
│   │   │           │   ├── ImageUtil.java            # 图片处理工具类
│   │   │           │   └── CaptchaUtil.java          # 验证码工具类
│   │   │           ├── filter/              # 过滤器
│   │   │           │   ├── CharacterEncodingFilter.java  # 字符编码过滤器
│   │   │           │   ├── LoginFilter.java          # 登录验证过滤器
│   │   │           │   ├── AdminFilter.java          # 管理员权限过滤器
│   │   │           │   └── LogFilter.java            # 日志记录过滤器
│   │   │           ├── listener/            # 监听器
│   │   │           │   ├── ContextListener.java      # 应用上下文监听器
│   │   │           │   ├── SessionListener.java      # 会话监听器
│   │   │           │   └── RequestListener.java      # 请求监听器
│   │   │           └── exception/           # 异常类
│   │   │               ├── BusinessException.java    # 业务异常类
│   │   │               ├── AuthenticationException.java  # 认证异常类
│   │   │               └── AuthorizationException.java   # 授权异常类
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml                 # Web配置文件（核心配置文件）
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   ├── style.css          # 主样式文件
│   │       │   │   ├── admin.css          # 管理员后台样式
│   │       │   │   ├── post.css           # 帖子详情样式
│   │       │   │   └── user.css           # 用户中心样式
│   │       │   ├── js/
│   │       │   │   ├── common.js          # 公共JavaScript函数
│   │       │   │   ├── validate.js        # 表单验证函数
│   │       │   │   ├── ajax.js            # AJAX相关函数
│   │       │   │   ├── post.js            # 帖子相关JavaScript
│   │       │   │   └── admin.js           # 管理员后台JavaScript
│   │       │   ├── images/
│   │       │   │   ├── 初始化头像.png # 默认头像
│   │       │   └── uploads/
│   │       │       ├── avatars/           # 头像上传目录
│   │       │       ├── posts/             # 帖子附件目录
│   │       │       └── temp/              # 临时文件目录
│   │       └── views/                      # JSP页面
│   │           ├── index.jsp               # 首页
│   │           ├── common/                  # 公共页面
│   │           │   ├── header.jsp          # 页面头部
│   │           │   ├── footer.jsp          # 页面底部
│   │           │   ├── sidebar.jsp         # 侧边栏
│   │           │   └── pagination.jsp      # 分页组件
│   │           ├── user/                    # 用户相关页面
│   │           │   ├── login.jsp           # 登录页面
│   │           │   ├── register.jsp        # 注册页面
│   │           │   ├── profile.jsp         # 个人资料页面
│   │           │   ├── my_posts.jsp        # 我的帖子页面
│   │           │   ├── my_comments.jsp     # 我的评论页面
│   │           │   ├── my_favorites.jsp    # 我的收藏页面
│   │           │   ├── my_notifications.jsp # 我的通知页面
│   │           │   ├── points.jsp          # 积分中心页面
│   │           │   └── password.jsp        # 修改密码页面
│   │           ├── post/                    # 帖子相关页面
│   │           │   ├── index.jsp           # 帖子列表首页
│   │           │   ├── section.jsp         # 版块页面
│   │           │   ├── detail.jsp          # 帖子详情页面
│   │           │   ├── create.jsp          # 发布帖子页面
│   │           │   ├── edit.jsp            # 编辑帖子页面
│   │           │   └── search.jsp          # 搜索结果页面
│   │           ├── admin/                   # 管理员页面
│   │           │   ├── dashboard.jsp       # 管理员仪表板
│   │           │   ├── user_manage.jsp     # 用户管理页面
│   │           │   ├── post_manage.jsp     # 帖子管理页面
│   │           │   ├── comment_manage.jsp  # 评论管理页面
│   │           │   ├── section_manage.jsp  # 版块管理页面
│   │           │   └── system_log.jsp      # 系统日志页面
│   │           └── error/                   # 错误页面
│   │               ├── 404.jsp             # 404页面
│   │               ├── 500.jsp             # 500页面
│   │               └── 403.jsp             # 403页面
```

# RequestListener

```
package com.community.listener;

import com.community.exception.BusinessException;
import com.community.exception.AuthenticationException;
import com.community.exception.AuthorizationException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 请求监听器
 * 功能：
 * 1. 记录请求开始和结束时间
 * 2. 统计请求执行时间
 * 3. 记录请求计数
 * 4. 监控异常请求（支持自定义异常）
 */
@WebListener
public class RequestListener implements ServletRequestListener {

    // 请求计数器（线程安全）
    private static final AtomicLong totalRequestCount = new AtomicLong(0);
    private static final AtomicLong activeRequestCount = new AtomicLong(0);

    // 存储请求开始时间（线程安全）
    private static final Map<String, RequestContext> requestContexts = new ConcurrentHashMap<>();

    // 性能统计
    private static final Map<String, AtomicLong> uriRequestCount = new ConcurrentHashMap<>();
    private static final Map<String, AtomicLong> uriTotalTime = new ConcurrentHashMap<>();

    // 异常统计（按异常类型）
    private static final Map<Class<? extends Exception>, AtomicLong> exceptionCount = new ConcurrentHashMap<>();
    private static final AtomicLong totalExceptionCount = new AtomicLong(0);

    // HTTP状态码统计
    private static final Map<Integer, AtomicLong> statusCodeCount = new ConcurrentHashMap<>();

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        // 请求开始时记录
        ServletRequest request = sre.getServletRequest();

        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        String requestId = generateRequestId(request);

        // 创建请求上下文
        RequestContext context = new RequestContext(requestId, startTime);
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            context.setUri(httpRequest.getRequestURI());
            context.setMethod(httpRequest.getMethod());
            context.setRemoteAddr(httpRequest.getRemoteAddr());
            context.setUserAgent(httpRequest.getHeader("User-Agent"));
            context.setReferer(httpRequest.getHeader("Referer"));
            context.setSessionId(httpRequest.getSession(false) != null
                    ? httpRequest.getSession().getId() : null);
        }

        requestContexts.put(requestId, context);

        // 增加请求计数
        totalRequestCount.incrementAndGet();
        activeRequestCount.incrementAndGet();

        // 记录URI访问计数
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String uri = httpRequest.getRequestURI();

            uriRequestCount.computeIfAbsent(uri, k -> new AtomicLong(0)).incrementAndGet();

            // 设置请求ID到属性中，供其他组件使用
            request.setAttribute("requestId", requestId);
            request.setAttribute("requestStartTime", startTime);

            // 记录请求日志
            logRequestStart(context);
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        // 请求结束时统计执行时间
        ServletRequest request = sre.getServletRequest();
        String requestId = (String) request.getAttribute("requestId");

        if (requestId != null && requestContexts.containsKey(requestId)) {
            RequestContext context = requestContexts.remove(requestId);
            context.setEndTime(System.currentTimeMillis());

            // 计算执行时间
            long executionTime = context.getExecutionTime();

            // 更新活跃请求数
            activeRequestCount.decrementAndGet();

            // 记录URI执行时间
            String uri = context.getUri();
            if (uri != null) {
                uriTotalTime.computeIfAbsent(uri, k -> new AtomicLong(0))
                        .addAndGet(executionTime);
            }

            // 检查是否有异常
            Object exceptionAttr = request.getAttribute("javax.servlet.error.exception");
            Object statusCodeAttr = request.getAttribute("javax.servlet.error.status_code");

            if (exceptionAttr instanceof Exception) {
                recordException((Exception) exceptionAttr, context);
            } else if (statusCodeAttr != null) {
                int statusCode = (int) statusCodeAttr;
                if (statusCode >= 400) {
                    context.setStatusCode(statusCode);
                    recordError(statusCode, context);
                }
            }

            // 记录请求结束日志
            logRequestEnd(context);
        }
    }

    /**
     * 增加请求计数
     */
    public static void incrementRequestCount() {
        totalRequestCount.incrementAndGet();
    }

    /**
     * 记录自定义异常
     */
    public static void recordException(Exception exception, RequestContext context) {
        if (exception == null) return;

        totalExceptionCount.incrementAndGet();

        // 按异常类型统计
        Class<? extends Exception> exceptionClass = exception.getClass();
        exceptionCount.computeIfAbsent(exceptionClass, k -> new AtomicLong(0))
                .incrementAndGet();

        // 设置异常信息到上下文
        if (context != null) {
            context.setException(exception);
            context.setExceptionMessage(exception.getMessage());
        }

        // 根据异常类型设置状态码
        int statusCode = determineStatusCode(exception);
        if (statusCode > 0) {
            recordError(statusCode, context);
        }
    }

    /**
     * 记录HTTP错误状态码
     */
    public static void recordError(int statusCode, RequestContext context) {
        if (context != null) {
            context.setStatusCode(statusCode);
        }

        statusCodeCount.computeIfAbsent(statusCode, k -> new AtomicLong(0))
                .incrementAndGet();
    }

    /**
     * 根据异常类型确定HTTP状态码
     */
    private static int determineStatusCode(Exception exception) {
        if (exception instanceof BusinessException) {
            return 400; // Bad Request
        } else if (exception instanceof AuthenticationException) {
            return 401; // Unauthorized
        } else if (exception instanceof AuthorizationException) {
            return 403; // Forbidden
        } else if (exception instanceof javax.servlet.ServletException) {
            return 500; // Internal Server Error
        } else {
            return 500; // 默认
        }
    }

    /**
     * 获取请求统计信息
     */
    public static String getRequestStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== 请求统计信息 ===\n");
        stats.append(String.format("总请求数: %d\n", totalRequestCount.get()));
        stats.append(String.format("活跃请求数: %d\n", activeRequestCount.get()));
        stats.append(String.format("总异常数: %d\n", totalExceptionCount.get()));

        stats.append("异常类型统计:\n");
        exceptionCount.forEach((exceptionClass, count) -> {
            String simpleName = exceptionClass.getSimpleName();
            stats.append(String.format("  %s: %d次\n", simpleName, count.get()));
        });

        stats.append("URI访问统计:\n");
        uriRequestCount.forEach((uri, count) -> {
            AtomicLong totalTime = uriTotalTime.get(uri);
            long avgTime = (totalTime != null && count.get() > 0)
                    ? totalTime.get() / count.get()
                    : 0;
            stats.append(String.format("  %s: 访问次数=%d, 平均耗时=%dms\n",
                    uri, count.get(), avgTime));
        });

        stats.append("HTTP状态码统计:\n");
        statusCodeCount.forEach((code, count) -> {
            stats.append(String.format("  %d: %d次\n", code, count.get()));
        });

        return stats.toString();
    }

    /**
     * 获取异常统计报告
     */
    public static Map<String, Object> getExceptionReport() {
        Map<String, Object> report = new ConcurrentHashMap<>();
        report.put("totalExceptions", totalExceptionCount.get());
        report.put("exceptionByType", convertExceptionCountMap());

        // 找出最常见的异常
        if (!exceptionCount.isEmpty()) {
            Class<? extends Exception> mostCommon = null;
            long maxCount = 0;

            for (Map.Entry<Class<? extends Exception>, AtomicLong> entry : exceptionCount.entrySet()) {
                if (entry.getValue().get() > maxCount) {
                    maxCount = entry.getValue().get();
                    mostCommon = entry.getKey();
                }
            }

            if (mostCommon != null) {
                report.put("mostCommonException", mostCommon.getSimpleName());
                report.put("mostCommonExceptionCount", maxCount);
            }
        }

        return report;
    }

    /**
     * 转换异常统计Map为可序列化的格式
     */
    private static Map<String, Long> convertExceptionCountMap() {
        Map<String, Long> result = new ConcurrentHashMap<>();
        exceptionCount.forEach((key, value) -> {
            result.put(key.getSimpleName(), value.get());
        });
        return result;
    }

    /**
     * 生成请求ID
     */
    private String generateRequestId(ServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 1000);

        return String.format("%s-%d-%d-%04d",
                remoteAddr.replace(".", ""),
                timestamp,
                Thread.currentThread().getId(),
                random);
    }

    /**
     * 记录请求开始日志
     */
    private void logRequestStart(RequestContext context) {
        System.out.println(String.format(
                "[%s] [%s] [START] %s %s - From: %s, Session: %s",
                new Date(),
                context.getRequestId(),
                context.getMethod(),
                context.getUri(),
                context.getRemoteAddr(),
                context.getSessionId() != null ? context.getSessionId().substring(0, 8) + "..." : "No Session"
        ));
    }

    /**
     * 记录请求结束日志
     */
    private void logRequestEnd(RequestContext context) {
        String status = "SUCCESS";
        int statusCode = context.getStatusCode();

        if (statusCode >= 400) {
            status = "ERROR";
        } else if (context.getException() != null) {
            status = "EXCEPTION";
        }

        StringBuilder logMessage = new StringBuilder();
        logMessage.append(String.format(
                "[%s] [%s] [END] %s %s - Status: %d (%s), Time: %dms",
                new Date(),
                context.getRequestId(),
                context.getMethod(),
                context.getUri(),
                statusCode,
                status,
                context.getExecutionTime()
        ));

        if (context.getException() != null) {
            logMessage.append(String.format(", Exception: %s - %s",
                    context.getException().getClass().getSimpleName(),
                    context.getExceptionMessage()
            ));
        }

        System.out.println(logMessage.toString());
    }

    /**
     * 请求上下文内部类
     */
    public static class RequestContext {
        private final String requestId;
        private final long startTime;
        private long endTime;
        private String uri;
        private String method;
        private String remoteAddr;
        private String userAgent;
        private String referer;
        private String sessionId;
        private Exception exception;
        private String exceptionMessage;
        private int statusCode = 200;

        public RequestContext(String requestId, long startTime) {
            this.requestId = requestId;
            this.startTime = startTime;
        }

        public long getExecutionTime() {
            return endTime - startTime;
        }

        // Getters and Setters
        public String getRequestId() { return requestId; }
        public long getStartTime() { return startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        public String getUri() { return uri; }
        public void setUri(String uri) { this.uri = uri; }
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public String getRemoteAddr() { return remoteAddr; }
        public void setRemoteAddr(String remoteAddr) { this.remoteAddr = remoteAddr; }
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        public String getReferer() { return referer; }
        public void setReferer(String referer) { this.referer = referer; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public Exception getException() { return exception; }
        public void setException(Exception exception) { this.exception = exception; }
        public String getExceptionMessage() { return exceptionMessage; }
        public void setExceptionMessage(String exceptionMessage) { this.exceptionMessage = exceptionMessage; }
        public int getStatusCode() { return statusCode; }
        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    }
}
```

# SessionListener

```
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
```

#  FileUtil

```
package com.community.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件处理工具类
 * 负责文件上传、下载、删除等操作
 */
public class FileUtil {

    // 文件上传根目录（相对于web应用根目录）
    public static final String UPLOAD_BASE_PATH = "uploads/";

    // 头像上传目录
    public static final String AVATAR_PATH = UPLOAD_BASE_PATH + "avatars/";

    // 帖子附件目录
    public static final String POST_PATH = UPLOAD_BASE_PATH + "posts/";

    // 允许的文件类型（MIME类型）
    public static final String[] ALLOWED_IMAGE_TYPES = {
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    };

    // 允许的文件扩展名
    public static final String[] ALLOWED_IMAGE_EXTENSIONS = {
            ".jpg", ".jpeg", ".png", ".gif"
    };

    /**
     * 创建上传目录（如果不存在）
     * @param realPath 服务器实际路径
     */
    public static void createUploadDirs(String realPath) {
        File baseDir = new File(realPath + UPLOAD_BASE_PATH);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        File avatarDir = new File(realPath + AVATAR_PATH);
        if (!avatarDir.exists()) {
            avatarDir.mkdirs();
        }

        File postDir = new File(realPath + POST_PATH);
        if (!postDir.exists()) {
            postDir.mkdirs();
        }
    }

    /**
     * 生成唯一的文件名
     * @param originalFileName 原始文件名
     * @return 唯一的文件名
     */
    public static String generateUniqueFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isEmpty()) {
            return UUID.randomUUID().toString();
        }

        // 获取文件扩展名
        String extension = getFileExtension(originalFileName);

        // 生成时间戳+随机数的文件名
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timestamp = sdf.format(new Date());
        String random = UUID.randomUUID().toString().substring(0, 8);

        return timestamp + "_" + random + extension;
    }

    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @return 扩展名（包含点），如：.jpg
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }

    /**
     * 验证文件类型是否为允许的图片类型
     * @param contentType MIME类型
     * @param fileName 文件名
     * @return 验证通过返回true，否则返回false
     */
    public static boolean isValidImageType(String contentType, String fileName) {
        if (contentType == null || fileName == null) {
            return false;
        }

        // 检查MIME类型
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equalsIgnoreCase(contentType)) {
                return true;
            }
        }

        // 检查文件扩展名
        String extension = getFileExtension(fileName);
        for (String allowedExt : ALLOWED_IMAGE_EXTENSIONS) {
            if (allowedExt.equalsIgnoreCase(extension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 保存上传的文件
     * @param inputStream 文件输入流
     * @param savePath 保存路径
     * @param fileName 文件名
     * @return 保存成功返回true，否则返回false
     */
    public static boolean saveFile(InputStream inputStream, String savePath, String fileName) {
        OutputStream outputStream = null;

        try {
            // 确保目录存在
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 创建文件输出流
            File file = new File(savePath + File.separator + fileName);
            outputStream = new FileOutputStream(file);

            // 缓冲区
            byte[] buffer = new byte[4096];
            int bytesRead;

            // 读取并写入文件
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            return true;
        } catch (IOException e) {
            System.err.println("保存文件失败: " + e.getMessage());
            return false;
        } finally {
            // 关闭流
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                System.err.println("关闭文件流失败: " + e.getMessage());
            }
        }
    }

    /**
     * 删除文件
     * @param filePath 文件路径
     * @return 删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 获取文件大小（MB）
     * @param filePath 文件路径
     * @return 文件大小（MB），如果文件不存在返回0
     */
    public static double getFileSizeMB(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            long sizeInBytes = file.length();
            return sizeInBytes / (1024.0 * 1024.0);
        }
        return 0;
    }

    /**
     * 复制文件
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @return 复制成功返回true，否则返回false
     */
    public static boolean copyFile(String sourcePath, String destPath) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            File sourceFile = new File(sourcePath);
            File destFile = new File(destPath);

            // 确保目标目录存在
            File destDir = destFile.getParentFile();
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            inputStream = new FileInputStream(sourceFile);
            outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            return true;
        } catch (IOException e) {
            System.err.println("复制文件失败: " + e.getMessage());
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                System.err.println("关闭文件流失败: " + e.getMessage());
            }
        }
    }

    /**
     * 读取文件为字节数组
     * @param filePath 文件路径
     * @return 字节数组，如果文件不存在返回null
     */
    public static byte[] readFileToBytes(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return null;
        }

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputStream.read(buffer);
            return buffer;
        } catch (IOException e) {
            System.err.println("读取文件失败: " + e.getMessage());
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                System.err.println("关闭文件流失败: " + e.getMessage());
            }
        }
    }
}
```

# pom.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.community</groupId>
    <artifactId>community-system</artifactId>
    <version>1.0.0</version>
    <packaging>war</packaging>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- Servlet API (Tomcat 9) -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>4.0.1</version>
            <scope>provided</scope>
        </dependency>

        <!-- JSP API (Tomcat 9) -->
        <dependency>
            <groupId>javax.servlet.jsp</groupId>
            <artifactId>javax.servlet.jsp-api</artifactId>
            <version>2.3.3</version>
            <scope>provided</scope>
        </dependency>

        <!-- JSTL 标签库 -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>jstl</artifactId>
            <version>1.2</version>
        </dependency>

        <!-- MySQL 驱动 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.49</version>
        </dependency>

        <!-- Commons FileUpload 文件上传 -->
        <dependency>
            <groupId>commons-fileupload</groupId>
            <artifactId>commons-fileupload</artifactId>
            <version>1.4</version>
        </dependency>

        <!-- Commons IO -->
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.11.0</version>
        </dependency>

        <!-- Jackson JSON 处理 -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.13.4.2</version>
        </dependency>

        <!-- 日志框架 SLF4J + Logback -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.36</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.2.11</version>
        </dependency>

        <!-- JUnit 测试 -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <finalName>community-system</finalName>
        <plugins>
            <!-- 编译插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.10.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>

            <!-- WAR 打包插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.3.2</version>
                <configuration>
                    <failOnMissingWebXml>false</failOnMissingWebXml>
                </configuration>
            </plugin>

            <!-- Tomcat 插件（可选，用于快速部署） -->
            <plugin>
                <groupId>org.apache.tomcat.maven</groupId>
                <artifactId>tomcat7-maven-plugin</artifactId>
                <version>2.2</version>
                <configuration>
                    <url>http://localhost:8080/manager/text</url>
                    <server>TomcatServer</server>
                    <path>/community-system</path>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

