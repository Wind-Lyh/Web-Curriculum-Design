package com.community.controller;

import com.community.model.Photo;
import com.community.service.UserService;
import com.community.service.impl.UserServiceImpl;
import com.community.model.Captcha;
import com.community.model.User;
import com.community.util.Captcha_Make;
import com.community.util.Image_Num_make;
import com.community.util.SlideImageMaker;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.community.util.Image_photo_make;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * 用户登录Servlet
 * web.xml配置访问路径
 */
public class UserServlet extends HttpServlet {
    private Captcha_Make captchaMake;
    private ObjectMapper mapper;
    private Random random;
    private UserService userService;

    // 角色常量
    private static final int ROLE_USER = 0;      // 普通用户
    private static final int ROLE_ADMIN = 1;     // 管理员
    private static final int ROLE_MODERATOR = 2; // 版主

    // 界面跳转路径常量
    private static final String USER_HOME_PAGE = "user/home.jsp";
    private static final String ADMIN_HOME_PAGE = "admin/dashboard.jsp";
    private static final String MODERATOR_HOME_PAGE = "moderator/panel.jsp";

    @Override
    public void init() throws ServletException {
        super.init();
        // 初始化组件
        captchaMake = new Captcha_Make();
        mapper = new ObjectMapper();
        random = new Random();
        // 初始化UserService，实际项目中应该使用工厂模式或依赖注入
        userService = new UserServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置字符编码
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 初始化图片处理上下文
        ServletContext servletContext = getServletContext();
        Image_photo_make.setServletContext(servletContext);

        // 获取action参数
        String action = request.getParameter("action");
        PrintWriter out = null;

        try {
            out = response.getWriter();

            if (action == null || action.trim().isEmpty()) {
                sendError(out, "缺少action参数");
                return;
            }

            // 根据action执行不同操作
            switch (action) {
                case "Captcha_Num":
                    generateCaptcha(request, response, out);
                    break;
                case "Captcha_Num_pd":
                    validateFirstStep(request, response, out);
                    break;
                case "rotateCaptcha":  // 新增：获取旋转验证码
                    generateRotateCaptcha(request, response, out);
                    break;
                case "slideCaptcha":   // 新增：获取滑动验证码
                    generateSlideCaptcha(request, response, out);
                    break;
                case "validateRotateCaptcha":  // 新增：验证旋转角度
                    validateRotateCaptcha(request, response, out);
                    break;
                case "validateSlideCaptcha":   // 新增：验证滑动位置
                    validateSlideCaptcha(request, response, out);
                    break;
                case "loginWithRole":
                    loginWithRole(request, response, out);
                    break;
                case "logout":
                    logout(request, response, out);
                    break;
                case "checkLogin":
                    checkLoginStatus(request, response, out);
                    break;
                case "switchRole":
                    switchUserRole(request, response, out);
                    break;
                case "getUserRoles":
                    getUserRoles(request, response, out);
                    break;
                default:
                    sendError(out, "未知操作类型");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (out != null) {
                sendError(out, "服务器内部错误: " + e.getMessage());
            }
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 生成普通验证码
     */
    private void generateCaptcha(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws IOException {
        try {
            int captchaType = random.nextInt(4) + 1;
            Captcha captcha = captchaMake.CaptchaResult(captchaType, 4);
            String imageBase64 = Image_Num_make.Image_Num(captcha, 120, 40);

            HttpSession session = request.getSession();
            session.setAttribute("firstStepCaptcha", captcha);
            session.setAttribute("captchaTime", System.currentTimeMillis());

            // 构建JSON响应
            StringBuilder json = new StringBuilder();
            json.append("{\"success\":true,");
            json.append("\"imageBase64\":\"").append(imageBase64).append("\",");
            json.append("\"displayText\":\"").append(captcha.getSee()).append("\"}");

            out.print(json.toString());

        } catch (Exception e) {
            System.err.println("生成验证码异常: " + e.getMessage());
            sendError(out, "生成验证码时发生错误: " + e.getMessage());
        }
    }

    /**
     * 验证第一步（普通验证码）- 带角色选择
     */
    private void validateFirstStep(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws IOException {
        String userInput = request.getParameter("captcha");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String selectedRole = request.getParameter("selectedRole");

        HttpSession session = request.getSession();

        if (isCaptchaExpired(session)) {
            sendResponse(out, false, "验证码已过期，请刷新");
            return;
        }

        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            sendResponse(out, false, "账号密码不能为空");
            return;
        }

        try {
            // 使用UserService进行登录验证
            User user = userService.login(username, password);

            if (user == null) {
                sendResponse(out, false, "账号密码错误或账号已被封禁");
                return;
            }

            // 验证用户状态
            if (user.getStatus() == 1) {
                sendResponse(out, false, "账号已被封禁，请联系管理员");
                return;
            }

            // 检查选择的角色是否合法
            if (selectedRole != null && !selectedRole.isEmpty()) {
                try {
                    int role = Integer.parseInt(selectedRole);
                    if (!isValidRole(role)) {
                        sendResponse(out, false, "选择的角色无效");
                        return;
                    }

                    // 检查用户是否有权限访问该角色界面
                    if (!hasRolePermission(user.getRole(), role)) {
                        sendResponse(out, false, "您没有权限以该角色登录");
                        return;
                    }
                } catch (NumberFormatException e) {
                    sendResponse(out, false, "角色参数格式错误");
                    return;
                }
            }

            Captcha captcha = (Captcha) session.getAttribute("firstStepCaptcha");

            if (captcha != null && captchaMake.pd_finally(captcha, userInput)) {
                // 验证通过，记录登录日志
                String ipAddress = request.getRemoteAddr();
                String userAgent = request.getHeader("User-Agent");
                userService.recordLogin(user.getId(), ipAddress, userAgent);

                // 设置session属性
                session.setAttribute("username", username);
                session.setAttribute("userId", user.getId());
                session.setAttribute("user", user);
                session.setAttribute("firstStepPassed", true);
                session.removeAttribute("firstStepCaptcha");

                // 设置用户当前选择的角色
                if (selectedRole != null && !selectedRole.isEmpty()) {
                    int role = Integer.parseInt(selectedRole);
                    session.setAttribute("currentRole", role);
                    session.setAttribute("rolePage", getRoleHomePage(role));
                } else {
                    // 如果没有选择角色，使用用户默认角色
                    session.setAttribute("currentRole", user.getRole());
                    session.setAttribute("rolePage", getRoleHomePage(user.getRole()));
                }

                // 随机选择第二层验证码类型
                String secondStepType = random.nextBoolean() ? "rotate" : "slide";
                session.setAttribute("captchaType", secondStepType);

                // 修改：使用完整的JSP路径
                String redirectUrl = "rotate".equals(secondStepType) ?
                        "views/users/rotateCaptcha.jsp" : "views/users/slideCaptcha.jsp";

                // 构建成功响应
                StringBuilder json = new StringBuilder();
                json.append("{\"success\":true,");
                json.append("\"message\":\"第一步验证通过，即将进入第二重验证\",");  // 修改消息提示
                json.append("\"redirectUrl\":\"").append(redirectUrl).append("\",");
                json.append("\"captchaType\":\"").append(secondStepType).append("\",");
                json.append("\"userRole\":").append(user.getRole()).append(",");
                json.append("\"currentRole\":").append(session.getAttribute("currentRole")).append(",");
                json.append("\"rolePage\":\"").append(session.getAttribute("rolePage")).append("\",");
                json.append("\"userInfo\":{");
                json.append("\"id\":").append(user.getId()).append(",");
                json.append("\"username\":\"").append(user.getUsername()).append("\",");
                json.append("\"nickname\":\"").append(user.getNickname() != null ? user.getNickname() : "").append("\",");
                json.append("\"avatarUrl\":\"").append(user.getAvatarUrl() != null ? user.getAvatarUrl() : "").append("\"");
                json.append("}}");

                out.print(json.toString());

            } else {
                sendResponse(out, false, "验证码错误");
            }
        } catch (Exception e) {
            System.err.println("验证过程中发生异常: " + e.getMessage());
            sendResponse(out, false, "验证过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 带角色选择的登录
     */
    private void loginWithRole(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String selectedRole = request.getParameter("selectedRole");

        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            sendResponse(out, false, "账号密码不能为空");
            return;
        }

        if (selectedRole == null || selectedRole.trim().isEmpty()) {
            sendResponse(out, false, "请选择登录角色");
            return;
        }

        try {
            // 使用UserService进行登录验证
            User user = userService.login(username, password);

            if (user == null) {
                sendResponse(out, false, "账号密码错误");
                return;
            }

            // 验证用户状态
            if (user.getStatus() == 1) {
                sendResponse(out, false, "账号已被封禁，请联系管理员");
                return;
            }

            // 验证角色
            try {
                int role = Integer.parseInt(selectedRole);
                if (!isValidRole(role)) {
                    sendResponse(out, false, "选择的角色无效");
                    return;
                }

                // 检查用户是否有权限访问该角色界面
                if (!hasRolePermission(user.getRole(), role)) {
                    sendResponse(out, false, "您没有权限以该角色登录");
                    return;
                }

                // 记录登录日志
                HttpSession session = request.getSession();
                String ipAddress = request.getRemoteAddr();
                String userAgent = request.getHeader("User-Agent");
                userService.recordLogin(user.getId(), ipAddress, userAgent);

                // 设置session
                session.setAttribute("username", username);
                session.setAttribute("userId", user.getId());
                session.setAttribute("user", user);
                session.setAttribute("currentRole", role);
                session.setAttribute("rolePage", getRoleHomePage(role));

                // 构建成功响应
                StringBuilder json = new StringBuilder();
                json.append("{\"success\":true,");
                json.append("\"message\":\"登录成功\",");
                json.append("\"redirectUrl\":\"").append(getRoleHomePage(role)).append("\",");
                json.append("\"userRole\":").append(user.getRole()).append(",");
                json.append("\"currentRole\":").append(role).append(",");
                json.append("\"userInfo\":{");
                json.append("\"id\":").append(user.getId()).append(",");
                json.append("\"username\":\"").append(user.getUsername()).append("\",");
                json.append("\"nickname\":\"").append(user.getNickname() != null ? user.getNickname() : "").append("\",");
                json.append("\"email\":\"").append(user.getEmail() != null ? user.getEmail() : "").append("\",");
                json.append("\"phone\":\"").append(user.getPhone() != null ? user.getPhone() : "").append("\",");
                json.append("\"avatarUrl\":\"").append(user.getAvatarUrl() != null ? user.getAvatarUrl() : "").append("\",");
                json.append("\"role\":").append(user.getRole()).append(",");
                json.append("\"points\":").append(user.getPoints());
                json.append("}}");

                out.print(json.toString());

            } catch (NumberFormatException e) {
                sendResponse(out, false, "角色参数格式错误");
            }

        } catch (Exception e) {
            System.err.println("登录过程中发生异常: " + e.getMessage());
            sendResponse(out, false, "登录过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 切换用户角色
     */
    private void switchUserRole(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws IOException {
        HttpSession session = request.getSession(false);
        String newRoleStr = request.getParameter("newRole");

        if (session == null) {
            sendResponse(out, false, "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            sendResponse(out, false, "请先登录");
            return;
        }

        if (newRoleStr == null || newRoleStr.trim().isEmpty()) {
            sendResponse(out, false, "请选择要切换的角色");
            return;
        }

        try {
            int newRole = Integer.parseInt(newRoleStr);

            if (!isValidRole(newRole)) {
                sendResponse(out, false, "选择的角色无效");
                return;
            }

            // 检查用户是否有权限切换到该角色
            if (!hasRolePermission(user.getRole(), newRole)) {
                sendResponse(out, false, "您没有权限切换到该角色");
                return;
            }

            // 切换角色
            session.setAttribute("currentRole", newRole);
            session.setAttribute("rolePage", getRoleHomePage(newRole));

            // 构建成功响应
            StringBuilder json = new StringBuilder();
            json.append("{\"success\":true,");
            json.append("\"message\":\"角色切换成功\",");
            json.append("\"currentRole\":").append(newRole).append(",");
            json.append("\"redirectUrl\":\"").append(getRoleHomePage(newRole)).append("\"}");

            out.print(json.toString());

        } catch (NumberFormatException e) {
            sendResponse(out, false, "角色参数格式错误");
        } catch (Exception e) {
            System.err.println("切换角色过程中发生异常: " + e.getMessage());
            sendResponse(out, false, "切换角色失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户所有可用的角色
     */
    private void getUserRoles(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws IOException {
        HttpSession session = request.getSession(false);

        if (session == null) {
            sendResponse(out, false, "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            sendResponse(out, false, "请先登录");
            return;
        }

        try {
            // 根据用户角色确定可用的角色列表
            StringBuilder json = new StringBuilder();
            json.append("{\"success\":true,");
            json.append("\"userRole\":").append(user.getRole()).append(",");
            json.append("\"currentRole\":").append(session.getAttribute("currentRole")).append(",");
            json.append("\"availableRoles\":[");

            // 默认所有用户都可以使用普通用户角色
            json.append("{\"role\":").append(ROLE_USER).append(",\"name\":\"普通用户\",\"description\":\"普通用户界面\"}");

            // 如果是管理员，可以切换到管理员角色
            if (user.getRole() == ROLE_ADMIN || user.getRole() == ROLE_MODERATOR) {
                json.append(",{\"role\":").append(ROLE_ADMIN).append(",\"name\":\"管理员\",\"description\":\"管理员界面\"}");
            }

            // 如果是版主，可以切换到版主角色
            if (user.getRole() == ROLE_MODERATOR) {
                json.append(",{\"role\":").append(ROLE_MODERATOR).append(",\"name\":\"版主\",\"description\":\"版主界面\"}");
            }

            json.append("],");
            json.append("\"rolePages\":{");
            json.append("\"").append(ROLE_USER).append("\":\"").append(USER_HOME_PAGE).append("\",");
            json.append("\"").append(ROLE_ADMIN).append("\":\"").append(ADMIN_HOME_PAGE).append("\",");
            json.append("\"").append(ROLE_MODERATOR).append("\":\"").append(MODERATOR_HOME_PAGE).append("\"");
            json.append("}}");

            out.print(json.toString());

        } catch (Exception e) {
            System.err.println("获取用户角色过程中发生异常: " + e.getMessage());
            sendResponse(out, false, "获取用户角色失败: " + e.getMessage());
        }
    }

    /**
     * 登出方法
     */
    private void logout(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws IOException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            try {
                // 记录登出时间
                User user = (User) session.getAttribute("user");
                if (user != null) {
                    System.out.println("用户 " + user.getUsername() + " 登出");
                }

                session.invalidate();
                sendResponse(out, true, "登出成功");
            } catch (Exception e) {
                sendResponse(out, false, "登出失败: " + e.getMessage());
            }
        } else {
            sendResponse(out, true, "用户未登录");
        }
    }

    /**
     * 检查登录状态
     */
    private void checkLoginStatus(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws IOException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                // 用户已登录
                Object currentRoleObj = session.getAttribute("currentRole");
                int currentRole = currentRoleObj != null ? (Integer) currentRoleObj : user.getRole();

                StringBuilder json = new StringBuilder();
                json.append("{\"success\":true,");
                json.append("\"isLoggedIn\":true,");
                json.append("\"userRole\":").append(user.getRole()).append(",");
                json.append("\"currentRole\":").append(currentRole).append(",");
                json.append("\"rolePage\":\"").append(session.getAttribute("rolePage")).append("\",");
                json.append("\"userInfo\":{");
                json.append("\"id\":").append(user.getId()).append(",");
                json.append("\"username\":\"").append(user.getUsername()).append("\",");
                json.append("\"nickname\":\"").append(user.getNickname() != null ? user.getNickname() : "").append("\",");
                json.append("\"avatarUrl\":\"").append(user.getAvatarUrl() != null ? user.getAvatarUrl() : "").append("\"");
                json.append("}}");

                out.print(json.toString());
                return;
            }
        }

        // 用户未登录
        StringBuilder json = new StringBuilder();
        json.append("{\"success\":true,");
        json.append("\"isLoggedIn\":false}");
        out.print(json.toString());
    }

    /**
     * 检查验证码是否过期
     */
    private boolean isCaptchaExpired(HttpSession session) {
        Object captchaTimeObj = session.getAttribute("captchaTime");
        if (captchaTimeObj instanceof Long) {
            Long captchaTime = (Long) captchaTimeObj;
            return System.currentTimeMillis() - captchaTime > 5 * 60 * 1000; // 5分钟过期
        }
        return true;
    }

    /**
     * 检查角色是否有效
     */
    private boolean isValidRole(int role) {
        return role == ROLE_USER || role == ROLE_ADMIN || role == ROLE_MODERATOR;
    }

    /**
     * 检查用户是否有权限访问该角色
     */
    private boolean hasRolePermission(int userRole, int requestedRole) {
        // 普通用户只能访问普通用户界面
        if (userRole == ROLE_USER) {
            return requestedRole == ROLE_USER;
        }

        // 管理员可以访问普通用户和管理员界面
        if (userRole == ROLE_ADMIN) {
            return requestedRole == ROLE_USER || requestedRole == ROLE_ADMIN;
        }

        // 版主可以访问所有界面
        if (userRole == ROLE_MODERATOR) {
            return true;
        }

        return false;
    }

    /**
     * 获取角色对应的首页
     */
    private String getRoleHomePage(int role) {
        switch (role) {
            case ROLE_ADMIN:
                return ADMIN_HOME_PAGE;
            case ROLE_MODERATOR:
                return MODERATOR_HOME_PAGE;
            case ROLE_USER:
            default:
                return USER_HOME_PAGE;
        }
    }

    /**
     * 发送响应
     */
    private void sendResponse(PrintWriter out, boolean success, String message) {
        StringBuilder json = new StringBuilder();
        json.append("{\"success\":").append(success).append(",");
        json.append("\"message\":\"").append(message).append("\"}");
        out.print(json.toString());
    }

    /**
     * 发送错误响应
     */
    private void sendError(PrintWriter out, String message) {
        StringBuilder json = new StringBuilder();
        json.append("{\"success\":false,");
        json.append("\"error\":\"").append(message).append("\"}");
        out.print(json.toString());
    }

    /**
     * 生成旋转验证码
     */
    private void generateRotateCaptcha(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws IOException {
        try {
            HttpSession session = request.getSession();

            // 检查第一步是否已通过
            Boolean firstStepPassed = (Boolean) session.getAttribute("firstStepPassed");
            if (firstStepPassed == null || !firstStepPassed) {
                sendResponse(out, false, "请先完成第一步验证");
                return;
            }

            // 使用Image_photo_make生成旋转验证码
            Photo photo = Image_photo_make.Image_photo();

            // 将正确角度保存到session
            session.setAttribute("correctAngle", photo.getCorrectAngle());
            session.setAttribute("photo", photo);

            // 构建JSON响应 - 注意Photo类的getter方法名
            StringBuilder json = new StringBuilder();
            json.append("{\"success\":true,");
            json.append("\"rotatedImage\":\"").append(photo.getBase64Image()).append("\",");
            json.append("\"initialImage\":\"").append(photo.getOriginalImage()).append("\",");
            json.append("\"initialAngle\":").append(photo.getInitialAngle()).append("}");

            out.print(json.toString());

        } catch (Exception e) {
            System.err.println("生成旋转验证码异常: " + e.getMessage());
            sendError(out, "生成旋转验证码时发生错误: " + e.getMessage());
        }
    }

    /**
     * 验证旋转角度
     */
    private void validateRotateCaptcha(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws IOException {
        try {
            HttpSession session = request.getSession();

            // 获取用户输入的角度
            String angleStr = request.getParameter("angle");
            if (angleStr == null || angleStr.trim().isEmpty()) {
                sendResponse(out, false, "请输入旋转角度");
                return;
            }

            int userAngle;
            try {
                userAngle = Integer.parseInt(angleStr);
            } catch (NumberFormatException e) {
                sendResponse(out, false, "角度格式错误");
                return;
            }

            // 获取正确角度
            Integer correctAngle = (Integer) session.getAttribute("correctAngle");
            if (correctAngle == null) {
                sendResponse(out, false, "验证码已过期，请重新开始");
                return;
            }

            // 验证角度（允许±5度的误差）
            boolean isValid = Image_photo_make.pd_angle(userAngle, correctAngle, 5);

            if (isValid) {
                // 清除验证相关session属性
                session.removeAttribute("correctAngle");
                session.removeAttribute("firstStepPassed");

                // 获取角色首页并跳转
                Integer currentRole = (Integer) session.getAttribute("currentRole");
                String rolePage = getRoleHomePage(currentRole != null ? currentRole : ROLE_USER);

                StringBuilder json = new StringBuilder();
                json.append("{\"success\":true,");
                json.append("\"message\":\"旋转验证通过，登录成功\",");
                json.append("\"redirectUrl\":\"").append(rolePage).append("\"}");

                out.print(json.toString());
            } else {
                sendResponse(out, false, "角度不正确，请重新尝试");
            }

        } catch (Exception e) {
            System.err.println("验证旋转角度异常: " + e.getMessage());
            sendResponse(out, false, "验证过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 生成滑动验证码
     */
    private void generateSlideCaptcha(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws IOException {
        try {
            HttpSession session = request.getSession();

            // 检查第一步是否已通过
            Boolean firstStepPassed = (Boolean) session.getAttribute("firstStepPassed");
            if (firstStepPassed == null || !firstStepPassed) {
                sendResponse(out, false, "请先完成第一步验证");
                return;
            }

            // 使用SlideImageMaker生成滑动验证码
            SlideImageMaker.SlideResult slideResult = SlideImageMaker.generateSlideCaptcha();

            // 将目标位置保存到session
            session.setAttribute("targetPosition", slideResult.getTargetPosition());

            // 构建JSON响应
            StringBuilder json = new StringBuilder();
            json.append("{\"success\":true,");
            json.append("\"backgroundImage\":\"").append(slideResult.getBackgroundImage()).append("\",");
            json.append("\"puzzleImage\":\"").append(slideResult.getPuzzleImage()).append("\",");
            json.append("\"targetPosition\":").append(slideResult.getTargetPosition()).append("}");

            out.print(json.toString());

        } catch (Exception e) {
            System.err.println("生成滑动验证码异常: " + e.getMessage());
            sendError(out, "生成滑动验证码时发生错误: " + e.getMessage());
        }
    }

    /**
     * 验证滑动位置
     */
    private void validateSlideCaptcha(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws IOException {
        try {
            HttpSession session = request.getSession();

            // 获取用户输入的位置
            String positionStr = request.getParameter("position");
            if (positionStr == null || positionStr.trim().isEmpty()) {
                sendResponse(out, false, "请输入滑动位置");
                return;
            }

            int userPosition;
            try {
                userPosition = Integer.parseInt(positionStr);
            } catch (NumberFormatException e) {
                sendResponse(out, false, "位置格式错误");
                return;
            }

            // 获取目标位置
            Integer targetPosition = (Integer) session.getAttribute("targetPosition");
            if (targetPosition == null) {
                sendResponse(out, false, "验证码已过期，请重新开始");
                return;
            }

            // 验证位置（允许±3%的误差）
            boolean isValid = SlideImageMaker.validatePosition(userPosition, targetPosition, 3);

            if (isValid) {
                // 清除验证相关session属性
                session.removeAttribute("targetPosition");
                session.removeAttribute("firstStepPassed");

                // 获取角色首页并跳转
                Integer currentRole = (Integer) session.getAttribute("currentRole");
                String rolePage = getRoleHomePage(currentRole != null ? currentRole : ROLE_USER);

                StringBuilder json = new StringBuilder();
                json.append("{\"success\":true,");
                json.append("\"message\":\"滑动验证通过，登录成功\",");
                json.append("\"redirectUrl\":\"").append(rolePage).append("\"}");

                out.print(json.toString());
            } else {
                sendResponse(out, false, "滑动位置不正确，请重新尝试");
            }

        } catch (Exception e) {
            System.err.println("验证滑动位置异常: " + e.getMessage());
            sendResponse(out, false, "验证过程中发生错误: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // GET请求也支持，通常用于检查登录状态
        String action = request.getParameter("action");

        if ("checkLogin".equals(action) || "logout".equals(action) ||
                "switchRole".equals(action) || "getUserRoles".equals(action)) {
            // 这些操作支持GET请求
            doPost(request, response);
        } else {
            // 其他操作默认为POST
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("请使用POST方法访问此接口");
        }
    }
}