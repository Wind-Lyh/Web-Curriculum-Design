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
import java.util.Enumeration;
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
        SlideImageMaker.setServletContext(servletContext);


        System.out.println("\n=== UserServlet.doPost 开始 ===");
        System.out.println("请求URL: " + request.getRequestURL());
        System.out.println("请求URI: " + request.getRequestURI());
        System.out.println("上下文路径: " + request.getContextPath());
        System.out.println("Servlet路径: " + request.getServletPath());
        System.out.println("查询字符串: " + request.getQueryString());

        // 获取action参数
        String action = request.getParameter("action");
        PrintWriter out = null;

        try {
            out = response.getWriter();

            if (action == null || action.trim().isEmpty()) {
                sendError(out, "缺少action参数");
                return;
            }
            System.out.println("doGet action: " + action);
            // 根据action执行不同操作
            switch (action) {
                case "Captcha_Num":
                    generateCaptcha(request, response, out);
                    break;
                case "Captcha_Num_pd":
                    validateFirstStep(request, response, out);
                    break;
                case "rotateCaptcha":  // 处理旋转验证码
                    System.out.println("🔄 处理旋转验证码GET请求");
                    generateRotateCaptcha(request, response, out);
                    break;
                case "slideCaptcha":   // 处理滑动验证码
                    System.out.println("🔄 处理滑动验证码GET请求");
                    generateSlideCaptcha(request, response, out);
                    break;
                case "validateRotateCaptcha":  // 新增：验证旋转角度
                    validateRotateCaptcha(request, response, out);
                    break;
                case "validateSlideCaptcha":   // 新增：验证滑动位置
                    validateSlideCaptcha(request, response, out);
                    break;
                case "logout":
                    logout(request, response, out);
                    break;
                case "checkLogin":
                    checkLoginStatus(request, response, out);
                    break;
                case "getUserRoles":   // 获取用户角色
                    // 这些操作在doPost中已有实现
                    System.out.println("🔄 转发到doPost处理: " + action);
                    doPost(request, response);
                    break;
                default:
                    System.err.println("❌ doGet: 未知操作类型: " + action);
                    sendError(out, "未知操作类型");
                    break;
            }
        } catch (Exception e) {
            System.err.println("❌ doGet异常: " + e.getMessage());
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
     * 生成普通验证码 - 修正版
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

            // 输出调试信息
            System.out.println("生成验证码成功，类型: " + captchaType);
            System.out.println("验证码显示文本: " + captcha.getSee());
            System.out.println("验证码实际代码: " + captcha.getCode());

            // 构建JSON响应 - 确保格式正确
            String json = "{" +
                    "\"success\": true," +
                    "\"imageBase64\": \"" + imageBase64 + "\"," +
                    "\"displayText\": \"" + captcha.getSee().replace("\"", "\\\"") + "\"," +
                    "\"captchaCode\": \"" + captcha.getCode() + "\"" +
                    "}";

            out.print(json);

        } catch (Exception e) {
            System.err.println("生成验证码异常: " + e.getMessage());
            e.printStackTrace();
            // 返回错误信息
            String json = "{" +
                    "\"success\": false," +
                    "\"error\": \"" + e.getMessage().replace("\"", "\\\"") + "\"" +
                    "}";
            out.print(json);
        }
    }

    /**
     * 验证第一步（普通验证码）- 带角色选择
     */
    private void validateFirstStep(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
            throws IOException {

        System.out.println("\n=== 开始第一步验证 ===");

        String userInput = request.getParameter("captcha");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String selectedRole = request.getParameter("selectedRole");


        System.out.println("选择的角色的"+selectedRole);


        System.out.println("接收到的参数:");
        System.out.println("  username: " + (username != null ? username : "null"));
        System.out.println("  password: " + (password != null ? "******" : "null"));
        System.out.println("  captcha: " + (userInput != null ? userInput : "null"));
        System.out.println("  selectedRole: " + (selectedRole != null ? selectedRole : "null"));

        HttpSession session = request.getSession();
        System.out.println("Session ID: " + session.getId());

        // 检查验证码是否存在
        Captcha captcha = (Captcha) session.getAttribute("firstStepCaptcha");
        if (captcha == null) {
            System.err.println("❌ 错误: session中的验证码为null");
            System.err.println("Session属性列表:");
            java.util.Enumeration<String> attrNames = session.getAttributeNames();
            while (attrNames.hasMoreElements()) {
                String name = attrNames.nextElement();
                System.err.println("  " + name + ": " + session.getAttribute(name));
            }
//            sendResponse(out, false, "验证码不存在，请刷新页面重试");
            return;
        }


        // 检查用户名和密码
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            sendResponse(out, false, "账号密码不能为空");
            return;
        }

        // 检查用户输入是否为空
        if (userInput == null || userInput.trim().isEmpty()) {
            sendResponse(out, false, "验证码不能为空");
            return;
        }

        // 尝试清理用户输入
        username = username.trim();
        password = password.trim();
        userInput = userInput.trim();

        try {
            System.out.println("开始验证用户...");
            // 使用UserService进行登录验证
            User user = userService.login(username, password);


            // 添加空值检查
            if (user == null) {
                System.err.println("❌ 用户登录失败: 用户名或密码错误");
                sendResponse(out, false, "账号密码错误");
                return;
            }

            // 验证用户状态
            if (user.getStatus() == 1) {
                System.err.println("❌ 用户状态异常: 账号已被封禁");
                sendResponse(out, false, "账号已被封禁，请联系管理员");
                return;
            }

            // 检查选择的角色是否合法
            if (selectedRole != null && !selectedRole.isEmpty()) {
                try {
                    int role = Integer.parseInt(selectedRole.trim());

                    // 检查用户是否有权限访问该角色界面
                    if (user.getIs_admin()!=role) {
                        System.err.println("❌ 权限不足: 用户角色=" + user.getIs_admin() + ", 请求角色=" + role);
                        sendResponse(out, false, "您没有权限以该角色登录");
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("❌ 角色参数格式错误: " + selectedRole);
                    sendResponse(out, false, "角色参数格式错误");
                    return;
                }
            }


            // 验证验证码
            boolean captchaValid = captchaMake.pd_finally(captcha, userInput);
            System.out.println("验证码验证结果: " + captchaValid);

            if (captchaValid) {
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
                    int role = Integer.parseInt(selectedRole.trim());
                    session.setAttribute("currentRole", role);
                    session.setAttribute("rolePage", getRoleHomePage(role));
                } else {
                    // 如果没有选择角色，使用用户默认角色
                    session.setAttribute("currentRole", user.getIs_admin());
                    session.setAttribute("rolePage", getRoleHomePage(user.getIs_admin()));
                }

                // 随机选择第二层验证码类型
                String secondStepType = random.nextBoolean() ? "rotate" : "slide";
                session.setAttribute("captchaType", secondStepType);

                // 修改这里：使用正确的JSP路径（单数user）
                String redirectUrl = "rotate".equals(secondStepType) ?
                        "views/user/rotateCaptcha.jsp" : "views/user/slideCaptcha.jsp";  // 修改为单数user

                System.out.println("✅ 第一步验证成功，准备跳转到: " + redirectUrl);

                // 构建成功响应 - 简化版
                String simpleResponse = "{\"success\":true,\"message\":\"第一步验证通过，即将进入第二重验证\",\"redirectUrl\":\"" +
                        redirectUrl + "\",\"captchaType\":\"" + secondStepType + "\"}";

                out.print(simpleResponse);

            } else {
                System.err.println("❌ 验证码验证失败");
                System.err.println("  期望: " + captcha.getCode());
                System.err.println("  实际: " + userInput);
                sendResponse(out, false, "验证码错误");
            }
        } catch (Exception e) {
            // 使用上面的详细异常处理
            System.err.println("\n=== 验证过程中发生异常 ===");
            System.err.println("异常类型: " + e.getClass().getName());
            System.err.println("异常消息: " + e.getMessage());
            System.err.println("异常堆栈跟踪:");
            e.printStackTrace(System.err);

            String errorMsg = "验证过程中发生错误: ";
            if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                errorMsg += e.getMessage();
            } else {
                errorMsg += e.getClass().getSimpleName();
            }

            sendResponse(out, false, errorMsg);
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
                int currentRole = currentRoleObj != null ? (Integer) currentRoleObj : user.getIs_admin();

                StringBuilder json = new StringBuilder();
                json.append("{\"success\":true,");
                json.append("\"isLoggedIn\":true,");
                json.append("\"userRole\":").append(user.getIs_admin()).append(",");
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
            System.out.println("🔄 开始生成旋转验证码...");

            HttpSession session = request.getSession();
            System.out.println("Session ID: " + session.getId());

            // 检查第一步是否已通过 - 临时放宽检查，先让功能跑起来
            Boolean firstStepPassed = (Boolean) session.getAttribute("firstStepPassed");
            System.out.println("firstStepPassed: " + firstStepPassed);

             if (firstStepPassed == null || !firstStepPassed) {
                 System.err.println("❌ 第一步验证未通过");
                 sendResponse(out, false, "请先完成第一步验证");
                 return;
             }

            System.out.println("调用 Image_photo_make.Image_photo()...");

            // 使用Image_photo_make生成旋转验证码
            Photo photo = Image_photo_make.Image_photo();

            if (photo == null) {
                System.err.println("❌ 生成的Photo对象为null");
                sendResponse(out, false, "生成验证码失败");
                return;
            }

            System.out.println("✅ 旋转验证码生成成功:");
            System.out.println("  正确角度: " + photo.getCorrectAngle());
            System.out.println("  初始角度: " + photo.getInitialAngle());
            System.out.println("  Base64图片长度: " +
                    (photo.getBase64Image() != null ? photo.getBase64Image().length() : 0));

            // 将正确角度保存到session
            session.setAttribute("correctAngle", photo.getCorrectAngle());
            session.setAttribute("photo", photo);

            // 构建JSON响应 - 注意：Base64字符串中的双引号需要转义
            String rotatedImage = photo.getBase64Image().replace("\"", "\\\"");
            String initialImage = photo.getOriginalImage().replace("\"", "\\\"");

            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"success\":true,");
            json.append("\"correctAngle\":").append(photo.getCorrectAngle()).append(",");
            json.append("\"rotatedImage\":\"").append(rotatedImage).append("\",");
            json.append("\"initialImage\":\"").append(initialImage).append("\",");
            json.append("\"initialAngle\":").append(photo.getInitialAngle());
            json.append("}");

            String jsonStr = json.toString();
            System.out.println("返回JSON数据长度: " + jsonStr.length());
            System.out.println("返回JSON数据（前200字符）: " +
                    jsonStr.substring(0, Math.min(200, jsonStr.length())));

            out.print(jsonStr);

        } catch (Exception e) {
            System.err.println("❌ 生成旋转验证码异常: " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("🔄 开始生成滑动验证码...");

            HttpSession session = request.getSession();
            System.out.println("Session ID: " + session.getId());

            // 检查第一步是否已通过
            Boolean firstStepPassed = (Boolean) session.getAttribute("firstStepPassed");
            System.out.println("firstStepPassed: " + firstStepPassed);

            if (firstStepPassed == null || !firstStepPassed) {
                System.err.println("❌ 第一步验证未通过");
                sendResponse(out, false, "请先完成第一步验证");
                return;
            }

            // 使用SlideImageMaker生成滑动验证码
            SlideImageMaker.SlideResult slideResult = SlideImageMaker.generateSlideCaptcha();

            if (slideResult == null) {
                System.err.println("❌ 生成的SlideResult对象为null");
                sendResponse(out, false, "生成验证码失败");
                return;
            }

            // 将目标位置保存到session
            session.setAttribute("targetPosition", slideResult.getTargetPosition());
            session.setAttribute("puzzleY", slideResult.getPuzzleY());

            System.out.println("✅ 滑动验证码生成成功:");
            System.out.println("  目标位置: " + slideResult.getTargetPosition() + "%");
            System.out.println("  拼图Y坐标: " + slideResult.getPuzzleY() + "px");
            System.out.println("  背景图Base64长度: " + slideResult.getBackgroundImage().length());
            System.out.println("  拼图块Base64长度: " + slideResult.getPuzzleImage().length());

            // 构建JSON响应 - 注意转义双引号
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"success\":true,");
            json.append("\"targetPosition\":").append(slideResult.getTargetPosition()).append(",");
            json.append("\"puzzleY\":").append(slideResult.getPuzzleY()).append(",");
            json.append("\"backgroundImage\":\"").append(slideResult.getBackgroundImage()).append("\",");
            json.append("\"puzzleImage\":\"").append(slideResult.getPuzzleImage()).append("\"");
            json.append("}");

            out.print(json.toString());

        } catch (Exception e) {
            System.err.println("❌ 生成滑动验证码异常: " + e.getMessage());
            e.printStackTrace();
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
        doPost(request, response);
    }
}