<%@ page import="java.io.StringWriter" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%

//    if (exception != null) {
//        out.println("<h3>错误信息：</h3>");
//        out.println("<pre>" + exception.getMessage() + "</pre>");
//        out.println("<h3>堆栈跟踪：</h3>");
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter(sw);
//        exception.printStackTrace(pw);
//        out.println("<pre>" + sw.toString() + "</pre>");
//    }
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
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            margin-top: 10px;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
        }

        .btn-primary:active {
            transform: translateY(0);
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

<script>
    const contextPath = '<%= contextPath %>';
    let usernameExists = false;
    let emailExists = false;

    // 检查用户名是否存在
    function checkUsername(username) {
        const errorElement = document.getElementById('usernameError');
        const inputElement = document.querySelector('input[name="username"]');

        if (!username.trim()) {
            errorElement.textContent = '用户名不能为空';
            inputElement.classList.add('error');
            inputElement.classList.remove('success');
            usernameExists = false;
            return;
        }

        if (username.length < 3) {
            errorElement.textContent = '用户名至少3个字符';
            inputElement.classList.add('error');
            inputElement.classList.remove('success');
            usernameExists = false;
            return;
        }

        // 显示检查中
        errorElement.innerHTML = '<span style="color:#666;">检查中...</span>';
        inputElement.classList.remove('error');
        inputElement.classList.remove('success');

        // 发送AJAX请求检查用户名 - 修改为字符串拼接
        fetch(contextPath + '/register?action=checkUsername&username=' + encodeURIComponent(username))
            .then(response => response.json())
            .then(data => {
                if (data.exists) {
                    errorElement.textContent = '用户名已存在';
                    inputElement.classList.add('error');
                    inputElement.classList.remove('success');
                    usernameExists = true;
                } else {
                    errorElement.innerHTML = '<span style="color:#2ed573;">✓ 用户名可用</span>';
                    inputElement.classList.remove('error');
                    inputElement.classList.add('success');
                    usernameExists = false;
                }
            })
            .catch(error => {
                errorElement.textContent = '检查失败，请稍后重试';
                inputElement.classList.add('error');
                inputElement.classList.remove('success');
                usernameExists = false;
            });
    }

    // 检查邮箱是否存在
    function checkEmail(email) {
        const errorElement = document.getElementById('emailError');
        const inputElement = document.querySelector('input[name="email"]');

        if (!email.trim()) {
            errorElement.textContent = '邮箱不能为空';
            inputElement.classList.add('error');
            inputElement.classList.remove('success');
            emailExists = false;
            return;
        }

        // 简单的邮箱格式验证
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            errorElement.textContent = '邮箱格式不正确';
            inputElement.classList.add('error');
            inputElement.classList.remove('success');
            emailExists = false;
            return;
        }

        // 显示检查中
        errorElement.innerHTML = '<span style="color:#666;">检查中...</span>';
        inputElement.classList.remove('error');
        inputElement.classList.remove('success');

        // 发送AJAX请求检查邮箱 - 修改为字符串拼接
        fetch(contextPath + '/register?action=checkEmail&email=' + encodeURIComponent(email))
            .then(response => response.json())
            .then(data => {
                if (data.exists) {
                    errorElement.textContent = '邮箱已被注册';
                    inputElement.classList.add('error');
                    inputElement.classList.remove('success');
                    emailExists = true;
                } else {
                    errorElement.innerHTML = '<span style="color:#2ed573;">✓ 邮箱可用</span>';
                    inputElement.classList.remove('error');
                    inputElement.classList.add('success');
                    emailExists = false;
                }
            })
            .catch(error => {
                errorElement.textContent = '检查失败，请稍后重试';
                inputElement.classList.add('error');
                inputElement.classList.remove('success');
                emailExists = false;
            });
    }

    // 表单提交验证
    document.getElementById('registerForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const submitBtn = document.getElementById('submitBtn');
        const loading = document.getElementById('submitLoading');
        const btnText = submitBtn.querySelector('span');

        // 禁用提交按钮，显示加载动画
        submitBtn.disabled = true;
        btnText.textContent = '注册中...';
        loading.style.display = 'block';

        // 收集表单数据
        const formData = new FormData(this);

        // 发送注册请求 - 修改为字符串拼接
        fetch(contextPath + '/register', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (response.redirected) {
                    // 服务器返回了重定向
                    window.location.href = response.url;
                } else {
                    return response.text();
                }
            })
            .then(html => {
                // 如果返回的是HTML（注册失败），替换当前页面
                document.open();
                document.write(html);
                document.close();
            })
            .catch(error => {
                console.error('注册失败:', error);
                alert('注册失败，请稍后重试');

                // 恢复按钮状态
                submitBtn.disabled = false;
                btnText.textContent = '立即注册';
                loading.style.display = 'none';
            });
    });
</script>
<%
    System.out.println("123456789");
%>
</body>
</html>