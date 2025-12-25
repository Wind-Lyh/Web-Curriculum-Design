<%@ page import="java.io.StringWriter" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%

    System.out.println("123456789");

    String contextPath = request.getContextPath();
    String username = (String) request.getAttribute("username");
    String email = (String) request.getAttribute("email");
    String phone = (String) request.getAttribute("phone");
    String nickname = (String) request.getAttribute("nickname");
    String signature = (String) request.getAttribute("signature");
    String error = (String) request.getAttribute("error");

    if (username == null) username = "";
    if (email == null) email = "";
    if (phone == null) phone = "";
    if (nickname == null) nickname = "";
    if (signature == null) signature = "";
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>用户注册 - 社区平台</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
        }

        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }

        .register-container {
            width: 100%;
            max-width: 500px;
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 24px;
            padding: 40px;
            box-shadow:
                    0 20px 60px rgba(0, 0, 0, 0.15),
                    0 0 0 1px rgba(255, 255, 255, 0.1) inset;
            position: relative;
            overflow: hidden;
        }

        .register-container::before {
            content: '';
            position: absolute;
            top: -50%;
            left: -50%;
            width: 200%;
            height: 200%;
            background: linear-gradient(
                    45deg,
                    transparent 30%,
                    rgba(255, 255, 255, 0.1) 50%,
                    transparent 70%
            );
            animation: shine 6s infinite linear;
        }

        @keyframes shine {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
        }

        .logo {
            text-align: center;
            margin-bottom: 30px;
        }

        .logo h1 {
            font-size: 32px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            background-clip: text;
            color: transparent;
            font-weight: 700;
            letter-spacing: -0.5px;
        }

        .logo p {
            color: #666;
            font-size: 14px;
            margin-top: 8px;
            opacity: 0.8;
        }

        .form-group {
            margin-bottom: 24px;
            position: relative;
        }

        .form-label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 500;
            font-size: 14px;
        }

        .form-input {
            width: 100%;
            padding: 16px;
            border: 2px solid #e0e0e0;
            border-radius: 12px;
            font-size: 16px;
            background: rgba(255, 255, 255, 0.9);
            transition: all 0.3s ease;
            outline: none;
        }

        .form-input:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            background: white;
        }

        .form-input.error {
            border-color: #ff4757;
        }

        .form-input.success {
            border-color: #2ed573;
        }

        .error-message {
            color: #ff4757;
            font-size: 13px;
            margin-top: 6px;
            display: flex;
            align-items: center;
            gap: 6px;
            min-height: 20px;
        }

        .success-message {
            color: #2ed573;
            font-size: 13px;
            margin-top: 6px;
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .server-error {
            background: #ff4757;
            color: white;
            padding: 16px;
            border-radius: 12px;
            margin-bottom: 24px;
            text-align: center;
            font-size: 14px;
            animation: shake 0.5s ease-in-out;
        }

        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            25% { transform: translateX(-5px); }
            75% { transform: translateX(5px); }
        }

        .btn {
            width: 100%;
            padding: 18px;
            border: none;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-align: center;
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
        }

        .btn-primary {
            background-color: black; /* 背景颜色设置为黑色 */
            color: white; /* 字体颜色设置为白色 */
            border: none; /* 移除边框 */
            cursor: pointer; /* 鼠标悬停时显示可点击效果 */
            text-align: center;
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
            font-size: 16px;
            font-weight: 600;
        }

        .btn-primary:hover {
            /* 去掉动态效果，保留原样 */
            background-color: black; /* 保持黑色背景 */
            color: white; /* 保持白色字体 */
        }

        .btn-primary:active {
            /* 去掉按下时的动态效果 */
            background-color: black;
            color: white;
        }

        /* 在CSS的.btn-primary规则后添加 */
        .btn-primary:disabled {
            background-color: #cccccc !important;
            color: #666666 !important;
            cursor: not-allowed !important;
            opacity: 0.6;
        }

        .login-link {
            text-align: center;
            margin-top: 24px;
            color: #666;
            font-size: 14px;
        }

        .login-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
            transition: color 0.3s ease;
        }

        .login-link a:hover {
            color: #764ba2;
            text-decoration: underline;
        }

        .loading {
            display: none;
            width: 20px;
            height: 20px;
            border: 2px solid rgba(255, 255, 255, 0.3);
            border-radius: 50%;
            border-top-color: white;
            animation: spin 1s ease-in-out infinite;
        }

        @keyframes spin {
            to { transform: rotate(360deg); }
        }

        .status-icon {
            width: 16px;
            height: 16px;
            display: none;
        }

        @media (max-width: 600px) {
            .register-container {
                padding: 30px 20px;
                border-radius: 20px;
            }

            .logo h1 {
                font-size: 28px;
            }

            .form-input {
                padding: 14px;
            }

            .btn {
                padding: 16px;
            }
        }
    </style>
</head>
<body>
<div class="register-container">
    <div class="logo">
        <h1>注册新账号</h1>
        <p>加入我们，开启全新的社区体验</p>
    </div>

    <% if (error != null && !error.isEmpty()) { %>
    <div class="server-error">
        <%= error %>
    </div>
    <% } %>

    <form id="registerForm" action="<%= contextPath %>/register" method="post">
        <div class="form-group">
            <label class="form-label">用户名 *</label>
            <input type="text"
                   name="username"
                   class="form-input"
                   placeholder="请输入用户名"
                   value="<%= username %>"
                   required
                   oninput="checkUsername(this.value)">
            <div class="error-message" id="usernameError"></div>
        </div>

        <div class="form-group">
            <label class="form-label">密码 *</label>
            <input type="password"
                   name="password"
                   class="form-input"
                   placeholder="请输入密码"
                   required
                   oninput="checkPassword()">
            <div class="error-message" id="passwordError"></div>
        </div>

        <div class="form-group">
            <label class="form-label">确认密码 *</label>
            <input type="password"
                   name="confirmPassword"
                   class="form-input"
                   placeholder="请再次输入密码"
                   required
                   oninput="checkPassword()">
            <div class="error-message" id="confirmPasswordError"></div>
        </div>

        <div class="form-group">
            <label class="form-label">邮箱 *</label>
            <input type="email"
                   name="email"
                   class="form-input"
                   placeholder="请输入邮箱"
                   value="<%= email %>"
                   required
                   oninput="checkEmail(this.value)">
            <div class="error-message" id="emailError"></div>
        </div>

        <div class="form-group">
            <label class="form-label">手机号 *</label>
            <input type="tel"
                   name="phone"
                   class="form-input"
                   placeholder="请输入手机号"
                   value="<%= phone %>"
                   required>
            <div class="error-message" id="phoneError"></div>
        </div>

        <div class="form-group">
            <label class="form-label">昵称 *</label>
            <input type="text"
                   name="nickname"
                   class="form-input"
                   placeholder="请输入昵称"
                   value="<%= nickname %>"
                   required>
            <div class="error-message" id="nicknameError"></div>
        </div>

        <div class="form-group">
            <label class="form-label">个性签名</label>
            <textarea name="signature"
                      class="form-input"
                      placeholder="介绍一下自己吧（可选）"
                      rows="3"><%= signature %></textarea>
        </div>

        <button type="submit" class="btn btn-primary" id="submitBtn">
            <span>立即注册</span>
            <div class="loading" id="submitLoading"></div>
        </button>
    </form>

    <div class="login-link">
        已有账号？ <a href="<%= contextPath %>/login.jsp">立即登录</a>
    </div>
</div>

<%
    System.out.println("123456789");
%>

<script type="text/javascript">
    // ===== 基础变量 =====
    var contextPath = '<%= contextPath %>';

    // 修改：初始化状态为false
    var state = {
        username: false,
        email: false,
        password: false,
        confirmPassword: false
    };

    // 添加：页面加载完成后执行的初始化
    document.addEventListener('DOMContentLoaded', function() {
        // 初始触发验证
        checkUsername(document.getElementsByName('username')[0].value);
        checkEmail(document.getElementsByName('email')[0].value);
        checkPassword();
        updateSubmitState();

        // 确保按钮可用（如果需要立即可用，可以注释下面这行）
        // submitBtn.disabled = false;
    });

    var usernameTimer = null;
    var emailTimer = null;

    var form = document.getElementById('registerForm');
    var submitBtn = document.getElementById('submitBtn');
    var loading = document.getElementById('submitLoading');
    var btnText = submitBtn.getElementsByTagName('span')[0];

    // ===== 工具方法 =====
    function setError(input, msg, errorId) {
        input.className = 'form-input error';
        document.getElementById(errorId).innerHTML = msg;
    }

    function setSuccess(input, msg, errorId) {
        input.className = 'form-input success';
        document.getElementById(errorId).innerHTML = msg || '';
    }

    function updateSubmitState() {
        var ok = true;
        for (var k in state) {
            if (!state[k]) {
                ok = false;
                break;
            }
        }
        submitBtn.disabled = !ok;
    }

    // ===== 用户名校验（防抖）=====
    window.checkUsername = function (value) {
        clearTimeout(usernameTimer);

        var input = document.getElementsByName('username')[0];
        var errorId = 'usernameError';

        if (!value || value.trim() === '') {
            state.username = false;
            setError(input, '用户名不能为空', errorId);
            updateSubmitState();
            return;
        }

        if (value.length < 3) {
            state.username = false;
            setError(input, '至少 3 个字符', errorId);
            updateSubmitState();
            return;
        }

        document.getElementById(errorId).innerHTML = '检查中...';

        usernameTimer = setTimeout(function () {
            var url = contextPath + '/register?action=checkUsername&username='
                + encodeURIComponent(value);

            fetch(url)
                .then(function (r) { return r.json(); })
                .then(function (d) {
                    if (d.exists) {
                        state.username = false;
                        setError(input, '用户名已存在', errorId);
                    } else {
                        state.username = true;
                        setSuccess(input, '✓ 用户名可用', errorId);
                    }
                    updateSubmitState();
                })
                .catch(function () {
                    state.username = false;
                    setError(input, '校验失败', errorId);
                    updateSubmitState();
                });
        }, 300);
    };

    // ===== 邮箱校验（防抖）=====
    window.checkEmail = function (value) {
        clearTimeout(emailTimer);

        var input = document.getElementsByName('email')[0];
        var errorId = 'emailError';

        // var regex = /^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/;
        var regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!regex.test(value)) {
            state.email = false;
            setError(input, '邮箱格式不正确', errorId);
            updateSubmitState();
            return;
        }

        document.getElementById(errorId).innerHTML = '检查中...';

        emailTimer = setTimeout(function () {
            var url = contextPath + '/register?action=checkEmail&email='
                + encodeURIComponent(value);

            fetch(url)
                .then(function (r) { return r.json(); })
                .then(function (d) {
                    if (d.exists) {
                        state.email = false;
                        setError(input, '邮箱已被注册', errorId);
                    } else {
                        state.email = true;
                        setSuccess(input, '✓ 邮箱可用', errorId);
                    }
                    updateSubmitState();
                })
                .catch(function () {
                    state.email = false;
                    setError(input, '校验失败', errorId);
                    updateSubmitState();
                });
        }, 300);
    };

    // ===== 密码校验 =====
    window.checkPassword = function () {
        var pwd = document.getElementsByName('password')[0].value;
        var cpwd = document.getElementsByName('confirmPassword')[0].value;

        state.password = pwd.length >= 6;
        state.confirmPassword = pwd === cpwd && cpwd.length > 0;

        updateSubmitState();
    };

    // ===== 提交（不 fetch，不假死）=====
    form.onsubmit = function () {
        submitBtn.disabled = true;
        btnText.innerHTML = '注册中...';
        loading.style.display = 'block';
        return true; // 让浏览器原生提交
    };
    console.log('contextPath==', contextPath);
</script>
</body>
</html>