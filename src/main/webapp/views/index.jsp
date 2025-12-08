<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <title>双重验证登录系统</title>
    <style>
        :root {
            --primary-color: #667eea;
            --primary-dark: #5a67d8;
            --secondary-color: #764ba2;
            --light-bg: #f8f9fa;
            --dark-text: #333;
            --light-text: #666;
            --border-color: #e0e0e0;
            --error-color: #f56565;
            --success-color: #48bb78;
            --admin-color: #ed8936;
            --user-color: #4299e1;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .login-container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
            overflow: hidden;
            width: 100%;
            max-width: 500px;
            min-height: 600px;
            display: flex;
            flex-direction: column;
            transition: all 0.3s ease;
        }

        .login-header {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            color: white;
            padding: 40px 30px;
            text-align: center;
            position: relative;
            overflow: hidden;
        }

        .login-header::before {
            content: '';
            position: absolute;
            top: -50%;
            left: -50%;
            width: 200%;
            height: 200%;
            background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
            background-size: 20px 20px;
            opacity: 0.1;
        }

        .login-title {
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 10px;
            position: relative;
            z-index: 1;
        }

        .login-subtitle {
            font-size: 16px;
            opacity: 0.9;
            font-weight: 400;
            position: relative;
            z-index: 1;
        }

        .login-content {
            padding: 40px 30px;
            flex: 1;
            display: flex;
            flex-direction: column;
        }

        .role-selection {
            margin-bottom: 30px;
            animation: fadeIn 0.5s ease-out;
        }

        .section-title {
            font-size: 18px;
            color: var(--dark-text);
            margin-bottom: 20px;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .section-title i {
            font-size: 20px;
        }

        .role-cards {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }

        .role-card {
            border: 2px solid var(--border-color);
            border-radius: 15px;
            padding: 25px 20px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }

        .role-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
        }

        .role-card.selected {
            border-color: var(--primary-color);
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
        }

        .role-icon {
            width: 70px;
            height: 70px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 15px;
            font-size: 32px;
            transition: all 0.3s ease;
        }

        .role-card.admin .role-icon {
            background: linear-gradient(135deg, #fed7e2 0%, #fbb6ce 100%);
            color: var(--admin-color);
        }

        .role-card.user .role-icon {
            background: linear-gradient(135deg, #c3dafe 0%, #a3bffa 100%);
            color: var(--user-color);
        }

        .role-name {
            font-size: 18px;
            font-weight: 600;
            margin-bottom: 8px;
            color: var(--dark-text);
        }

        .role-desc {
            font-size: 14px;
            color: var(--light-text);
            line-height: 1.5;
        }

        .login-form {
            flex: 1;
            display: flex;
            flex-direction: column;
            animation: fadeIn 0.5s ease-out;
        }

        .form-group {
            margin-bottom: 25px;
        }

        .form-label {
            display: block;
            margin-bottom: 10px;
            color: var(--dark-text);
            font-weight: 600;
            font-size: 15px;
        }

        .form-input {
            width: 100%;
            padding: 15px 20px;
            border: 2px solid var(--border-color);
            border-radius: 12px;
            font-size: 16px;
            transition: all 0.3s ease;
            background: var(--light-bg);
        }

        .form-input:focus {
            outline: none;
            border-color: var(--primary-color);
            background: white;
            box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
        }

        .captcha-container {
            display: flex;
            gap: 15px;
            align-items: center;
        }

        .captcha-input {
            flex: 1;
        }

        .captcha-image {
            width: 130px;
            height: 50px;
            border-radius: 10px;
            border: 2px solid var(--border-color);
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .captcha-image:hover {
            border-color: var(--primary-color);
        }

        .action-buttons {
            display: flex;
            gap: 15px;
            margin-top: auto;
        }

        .btn {
            flex: 1;
            padding: 16px;
            border: none;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-align: center;
            text-decoration: none;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
        }

        .btn-primary {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
            color: white;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
        }

        .btn-secondary {
            background: var(--light-bg);
            color: var(--light-text);
            border: 2px solid var(--border-color);
        }

        .btn-secondary:hover {
            background: #e9ecef;
            transform: translateY(-2px);
        }

        .message-container {
            margin-bottom: 20px;
            animation: slideDown 0.3s ease-out;
        }

        .message {
            padding: 15px 20px;
            border-radius: 12px;
            text-align: center;
            font-size: 15px;
            font-weight: 500;
        }

        .message.success {
            background-color: rgba(72, 187, 120, 0.1);
            color: var(--success-color);
            border: 1px solid rgba(72, 187, 120, 0.2);
        }

        .register-link {
            text-align: center;
            margin-top: 25px;
            color: var(--light-text);
            font-size: 15px;
        }

        .register-link a {
            color: var(--primary-color);
            text-decoration: none;
            font-weight: 600;
            transition: all 0.2s ease;
        }

        .register-link a:hover {
            text-decoration: underline;
        }

        .back-button {
            position: absolute;
            top: 20px;
            left: 20px;
            background: rgba(255, 255, 255, 0.2);
            border: none;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            color: white;
            font-size: 18px;
            transition: all 0.3s ease;
            z-index: 10;
        }

        .back-button:hover {
            background: rgba(255, 255, 255, 0.3);
            transform: translateX(-3px);
        }

        .role-indicator {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 6px 15px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: 600;
            margin-bottom: 15px;
        }

        .role-indicator.admin {
            background: rgba(237, 137, 54, 0.1);
            color: var(--admin-color);
        }

        .role-indicator.user {
            background: rgba(66, 153, 225, 0.1);
            color: var(--user-color);
        }

        /* 动画 */
        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        @keyframes slideDown {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        /* 响应式设计 */
        @media (max-width: 480px) {
            .login-container {
                border-radius: 15px;
                min-height: 500px;
            }

            .login-header {
                padding: 30px 20px;
            }

            .login-content {
                padding: 30px 20px;
            }

            .role-cards {
                grid-template-columns: 1fr;
                gap: 15px;
            }

            .action-buttons {
                flex-direction: column;
            }

            .captcha-container {
                flex-direction: column;
            }

            .captcha-image {
                width: 100%;
                height: 45px;
            }
        }

        .captcha-image.error {
            border-color: var(--error-color);
            background: rgba(245, 101, 101, 0.1);
        }

        .captcha-image.loading {
            border-color: var(--primary-color);
            background: rgba(102, 126, 234, 0.1);
            position: relative;
        }

        .captcha-image.loading::after {
            content: "加载中...";
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            color: var(--light-text);
            font-size: 14px;

            /* 添加更醒目的错误样式 */
            .message.error {
                background: linear-gradient(135deg, rgba(245, 101, 101, 0.2) 0%, rgba(229, 62, 62, 0.2) 100%);
                color: var(--error-color);
                border: 2px solid var(--error-color);
                font-weight: 600;
                animation: shake 0.5s ease-in-out;
                box-shadow: 0 5px 15px rgba(245, 101, 101, 0.2);
            }

            /* 输入框错误状态 */
            .form-input.error {
                border-color: var(--error-color);
                background: rgba(245, 101, 101, 0.05);
                box-shadow: 0 0 0 3px rgba(245, 101, 101, 0.1);
            }

            /* 震动动画 */
            @keyframes shake {
                0%, 100% { transform: translateX(0); }
                10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
                20%, 40%, 60%, 80% { transform: translateX(5px); }
            }

            /* 高亮错误字段 */
            .highlight-error {
                animation: pulse 2s infinite;
            }

            @keyframes pulse {
                0% { box-shadow: 0 0 0 0 rgba(245, 101, 101, 0.4); }
                70% { box-shadow: 0 0 0 10px rgba(245, 101, 101, 0); }
                100% { box-shadow: 0 0 0 0 rgba(245, 101, 101, 0); }
            }
        }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
<div class="login-container">
    <div class="login-header">
        <button class="back-button" id="backButton" style="display: none;">
            <i class="fas fa-arrow-left"></i>
        </button>
        <h1 class="login-title">双重验证登录系统</h1>
        <p class="login-subtitle">安全登录，双重保障</p>
    </div>

    <div class="login-content">
        <!-- 角色选择区域 -->
        <div id="roleSelection" class="role-selection">
            <div class="section-title">
                <i class="fas fa-user-tag"></i>
                <span>请选择登录角色</span>
            </div>
            <div class="role-cards">
                <div class="role-card user" data-role="0">
                    <div class="role-icon">
                        <i class="fas fa-user"></i>
                    </div>
                    <div class="role-name">普通用户</div>
                    <div class="role-desc">浏览社区内容，发布帖子，参与讨论，享受社区服务</div>
                </div>
                <div class="role-card admin" data-role="1">
                    <div class="role-icon">
                        <i class="fas fa-crown"></i>
                    </div>
                    <div class="role-name">管理员</div>
                    <div class="role-desc">管理系统用户，审核内容，维护秩序，配置系统参数</div>
                </div>
            </div>
        </div>

        <!-- 消息提示区域 -->
        <div id="messageContainer" class="message-container" style="display: none;"></div>

        <!-- 登录表单区域 -->
        <div id="loginForm" class="login-form" style="display: none;">
            <div class="section-title">
                <i class="fas fa-sign-in-alt"></i>
                <span>登录验证</span>
            </div>

            <!-- 角色指示器 -->
            <div id="roleIndicator" class="role-indicator user">
                <i class="fas fa-user"></i>
                <span>普通用户登录</span>
            </div>

            <form id="loginFormElement">
                <input type="hidden" id="selectedRole" name="selectedRole" value="0">

                <div class="form-group">
                    <label for="username" class="form-label">
                        <i class="fas fa-user-circle"></i> 账号
                    </label>
                    <input type="text" id="username" name="username" class="form-input"
                           placeholder="请输入您的账号" required autocomplete="username">
                </div>

                <div class="form-group">
                    <label for="password" class="form-label">
                        <i class="fas fa-lock"></i> 密码
                    </label>
                    <input type="password" id="password" name="password" class="form-input"
                           placeholder="请输入您的密码" required autocomplete="current-password">
                </div>

                <div class="form-group">
                    <label for="captcha" class="form-label">
                        <i class="fas fa-shield-alt"></i> 验证码
                    </label>
                    <div class="captcha-container">
                        <input type="text" id="captcha" name="captcha" class="form-input captcha-input"
                               placeholder="请输入验证码" required>
                        <img id="captchaImg" class="captcha-image" onclick="refreshCaptcha()"
                             title="点击刷新验证码" alt="验证码图片" src="">
                    </div>
                </div>

                <div class="action-buttons">
                    <button type="button" class="btn btn-primary" onclick="validateFirstStep()">
                        <i class="fas fa-check-circle"></i>
                        <span>第一步验证</span>
                    </button>
                    <button type="button" class="btn btn-secondary" onclick="resetToRoleSelection()">
                        <i class="fas fa-redo"></i>
                        <span>重新选择</span>
                    </button>
                </div>
            </form>
        </div>

        <!-- 注册链接 -->
        <div class="register-link">
            还没有账号？ <a href="javascript:void(0);" onclick="goToRegister()">立即注册</a>
        </div>
    </div>
</div>

<%
    String registerSuccess = request.getParameter("register");
    if ("success".equals(registerSuccess)) {
%>
<div style="background: #2ed573; color: white; padding: 12px; border-radius: 8px; margin-bottom: 20px; text-align: center;">
    注册成功！请使用您的账号登录。
</div>
<%
    }
%>

<script>
    const contextPath = '<%= request.getContextPath() %>';
    // UTF-8 安全的 Base64 编码函数
    function utf8_to_b64(str) {
        return window.btoa(unescape(encodeURIComponent(str)));
    }

    function b64_to_utf8(str) {
        return decodeURIComponent(escape(window.atob(str)));
    }
    let currentSelectedRole = 0; // 0: 普通用户, 1: 管理员

    // 页面加载时初始化
    window.onload = function () {
        console.log('页面加载完成');
        // 检测 FontAwesome 是否加载
        if (typeof window.FontAwesome === 'undefined') {
            console.warn('FontAwesome 未加载，使用备用图标');
            // 设置标志，显示时将使用备用字符
        }
        // 绑定角色卡片点击事件
        document.querySelectorAll('.role-card').forEach(card => {
            card.addEventListener('click', function () {
                console.log('选择角色卡片:', this.getAttribute('data-role'));
                selectRole(this);
            });
        });

        // 绑定返回按钮事件
        document.getElementById('backButton').addEventListener('click', function() {
            console.log('点击返回按钮');
            resetToRoleSelection();
        });

        // 绑定表单输入事件，按下Enter键提交
        document.getElementById('loginFormElement').addEventListener('keydown', function (e) {
            if (e.key === 'Enter') {
                console.log('按下Enter键');
                e.preventDefault();
                validateFirstStep();
            }
        });

        // 直接绑定验证按钮点击事件
        const validateBtn = document.querySelector('.btn-primary');
        if (validateBtn) {
            validateBtn.addEventListener('click', function(e) {
                console.log('验证按钮被点击');
                e.preventDefault();
                validateFirstStep();
            });
        } else {
            console.error('未找到验证按钮');
        }

        // 添加快捷键支持（可选）
        document.addEventListener('keydown', function (event) {
            // F5刷新验证码
            if (event.key === 'F5') {
                event.preventDefault();
                refreshCaptcha();
            }
        });
    };

    // 选择角色
    function selectRole(cardElement) {
        // 移除所有卡片的选中状态
        document.querySelectorAll('.role-card').forEach(card => {
            card.classList.remove('selected');
        });

        // 添加当前卡片的选中状态
        cardElement.classList.add('selected');

        // 获取选择的角色值
        currentSelectedRole = parseInt(cardElement.getAttribute('data-role'));
        document.getElementById('selectedRole').value = currentSelectedRole;

        // 更新角色指示器
        const roleIndicator = document.getElementById('roleIndicator');
        if (currentSelectedRole === 1) {
            roleIndicator.innerHTML = '<i class="fas fa-crown"></i><span>管理员登录</span>';
            roleIndicator.className = 'role-indicator admin';
        } else {
            roleIndicator.innerHTML = '<i class="fas fa-user"></i><span>普通用户登录</span>';
            roleIndicator.className = 'role-indicator user';
        }

        // 显示登录表单，隐藏角色选择
        setTimeout(() => {
            document.getElementById('roleSelection').style.display = 'none';
            document.getElementById('loginForm').style.display = 'flex';
            document.getElementById('backButton').style.display = 'flex';

            // 显示加载中的验证码
            document.getElementById('captchaImg').src = 'data:image/svg+xml;charset=utf-8;base64,' +
                window.btoa(unescape(encodeURIComponent(
                    '<svg xmlns="http://www.w3.org/2000/svg" width="130" height="50" viewBox="0 0 130 50">' +
                    '<rect width="130" height="50" fill="#f0f0f0"/>' +
                    '<text x="65" y="30" font-family="Arial" font-size="16" text-anchor="middle" fill="#999">Loading...</text>' +
                    '</svg>'
                )));

            // 获取验证码
            refreshCaptchaSilent();

            // 自动聚焦到用户名输入框
            setTimeout(() => {
                document.getElementById('username').focus();
            }, 100);
        }, 300);
    }

    // 重置到角色选择
    function resetToRoleSelection() {
        // 隐藏登录表单，显示角色选择
        document.getElementById('loginForm').style.display = 'none';
        document.getElementById('roleSelection').style.display = 'block';
        document.getElementById('backButton').style.display = 'none';

        // 隐藏消息
        hideMessage();

        // 清空表单
        document.getElementById('loginFormElement').reset();
    }

    // 刷新验证码
    function refreshCaptchaSilent() {
        console.log('刷新验证码，请求路径:', contextPath + '/user/Captcha_Num');

        fetch(contextPath + '/user/Captcha_Num', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'action=Captcha_Num'
        })
            .then(response => {
                console.log('验证码响应状态:', response.status);
                console.log('响应头:', response.headers.get('Content-Type'));
                if (!response.ok) {
                    throw new Error('网络响应不正常: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('验证码响应数据:', data);
                if (data.success && data.imageBase64) {
                    const captchaImg = document.getElementById('captchaImg');
                    const captchaInput = document.getElementById('captcha');

                    // 设置验证码图片
                    captchaImg.src = data.imageBase64;
                    captchaInput.value = '';

                    // 设置图片加载完成后的回调
                    captchaImg.onload = function () {
                        console.log('验证码图片加载成功');
                        // 可以在这里隐藏加载指示器
                    };

                    captchaImg.onerror = function () {
                        console.error('验证码图片加载失败');
                        showMessage('验证码图片加载失败，请刷新重试', false);
                    };

                    // 调试信息
                    console.log('验证码Base64长度:', data.imageBase64.length);
                    if (data.displayText) {
                        console.log('验证码文字提示:', data.displayText);
                    }
                    if (data.captchaCode) {
                        console.log('验证码实际代码:', data.captchaCode);
                    }
                } else {
                    console.error('验证码生成失败:', data.error);
                    showMessage('验证码生成失败: ' + (data.error || '未知错误'), false);
                }
            })
            .catch(error => {
                console.error('验证码请求错误:', error);
                showMessage('验证码加载失败: ' + error.message, false);

                // 如果是网络错误，显示备用占位符
                document.getElementById('captchaImg').src = 'data:image/svg+xml;base64,' +
                    btoa('<svg xmlns="http://www.w3.org/2000/svg" width="130" height="50" viewBox="0 0 130 50">' +
                        '<rect width="130" height="50" fill="#f0f0f0"/>' +
                        '<text x="65" y="30" font-family="Arial" font-size="16" text-anchor="middle" fill="#999">点击刷新</text>' +
                        '</svg>');
            });
    }

    function refreshCaptcha() {
        const captchaImg = document.getElementById('captchaImg');
        captchaImg.classList.add('loading');

        refreshCaptchaSilent();

        // 移除loading状态
        setTimeout(() => {
            captchaImg.classList.remove('loading');
        }, 1000);
    }

    // 跳转到注册页面
    function goToRegister() {
        console.log('跳转到注册页面');
        window.location.href = contextPath + '/views/user/register.jsp';
    }

    // 表单输入验证
    function validateFormInputs() {
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value.trim();
        const captcha = document.getElementById('captcha').value.trim();

        console.log('验证表单输入:', {username, password, captcha});

        removeErrorHighlights();

        let isValid = true;
        let errorMessage = '';

        if (!username) {
            errorMessage = '请输入账号';
            document.getElementById('username').focus();
            isValid = false;
        } else if (!password) {
            errorMessage = '请输入密码';
            document.getElementById('password').focus();
            isValid = false;
        } else if (!captcha) {
            errorMessage = '请输入验证码';
            document.getElementById('captcha').focus();
            isValid = false;
        }

        if (!isValid) {
            console.log('表单验证失败:', errorMessage);
            showMessage(errorMessage, false);
        } else {
            console.log('表单验证成功');
        }

        return isValid;
    }

    // 第一步验证
    function validateFirstStep() {
        console.log('=== 开始第一步验证 ===');

        // 先移除所有错误高亮
        removeErrorHighlights();

        // 先进行表单验证
        if (!validateFormInputs()) {
            console.log('表单验证失败');
            return;
        }
        console.log('表单验证通过');

        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value.trim();
        const captcha = document.getElementById('captcha').value.trim();

        const params = new URLSearchParams();
        params.append('action', 'Captcha_Num_pd');
        params.append('username', username);
        params.append('password', password);
        params.append('captcha', captcha);
        params.append('selectedRole', currentSelectedRole);

        // 禁用按钮，防止重复提交
        const submitBtn = document.querySelector('.btn-primary');
        const originalText = submitBtn.innerHTML;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i><span>验证中...</span>';
        submitBtn.disabled = true;

        fetch(contextPath + '/user/Captcha_Num_pd', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: params
        })
            .then(response => {
                console.log('第一步验证响应状态:', response.status);
                if (!response.ok) {
                    throw new Error('网络响应不正常: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('第一步验证响应数据:', data);
                // 恢复按钮状态
                submitBtn.innerHTML = originalText;
                submitBtn.disabled = false;

                if (data.success) {
                    showMessage(data.message, true);
                    console.log('第一步验证成功，跳转到第二层验证页面');
                    setTimeout(() => {
                        // 确保URL正确拼接
                        let redirectUrl = data.redirectUrl;
                        console.log('最终跳转URL:', redirectUrl);
                        // 跳转到第二层验证页面
                        window.location.href = redirectUrl;
                    }, 1500);

                } else {
                    let errorMessage = data.message || '登录失败';
                    console.log('服务器返回的错误消息:', data.message);

                    // 显示服务器返回的错误消息
                    showMessage(errorMessage, false);

                    // 如果错误信息包含账号密码相关，高亮用户名和密码输入框
                    if (errorMessage.includes('账号') || errorMessage.includes('密码') ||
                        errorMessage.includes('用户名') || errorMessage.includes('用户')) {
                        const usernameInput = document.getElementById('username');
                        const passwordInput = document.getElementById('password');

                        if (usernameInput) usernameInput.classList.add('error', 'highlight-error');
                        if (passwordInput) passwordInput.classList.add('error');

                        // 清空密码和验证码，保留用户名
                        document.getElementById('password').value = '';
                        document.getElementById('captcha').value = '';
                        document.getElementById('username').focus();
                    }

                    // 延迟静默刷新验证码
                    setTimeout(() => {
                        refreshCaptchaSilent();
                    }, 1000);
                }
            })
            .catch(error => {
                console.error('第一步验证错误:', error);

                // 恢复按钮状态
                submitBtn.innerHTML = originalText;
                submitBtn.disabled = false;
                showMessage('网络请求失败: ' + error.message, false);
                setTimeout(() => {
                    refreshCaptchaSilent();
                }, 1000);
            });
    }

       // 显示消息
    function showMessage(message, isSuccess, duration = 3000) {
        const messageContainer = document.getElementById('messageContainer');
        if (!messageContainer) {
            console.error('找不到消息容器');
            return;
        }

        // 清空并显示容器
        messageContainer.innerHTML = '';
        messageContainer.style.display = 'block';

        // 创建消息元素
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${isSuccess ? 'success' : 'error'}`;

        // 如果 FontAwesome 加载失败，使用纯文本符号
        const icon = isSuccess ?
            (window.FontAwesome ? '<i class="fas fa-check-circle"></i> ' : '✓ ') :
            (window.FontAwesome ? '<i class="fas fa-exclamation-circle"></i> ' : '⚠ ');

        messageDiv.innerHTML = icon + message;

        // 设置基本样式（以防CSS加载失败）
        if (isSuccess) {
            messageDiv.style.cssText = 'background-color: rgba(72, 187, 120, 0.1); ' +
                'color: #48bb78; ' +
                'padding: 15px 20px; ' +
                'border-radius: 12px; ' +
                'text-align: center; ' +
                'font-size: 15px; ' +
                'font-weight: 500; ' +
                'margin-bottom: 20px;';
        } else {
            messageDiv.style.cssText = 'background-color: rgba(245, 101, 101, 0.1); ' +
                'color: #f56565; ' +
                'padding: 15px 20px; ' +
                'border-radius: 12px; ' +
                'text-align: center; ' +
                'font-size: 15px; ' +
                'font-weight: 500; ' +
                'margin-bottom: 20px;' +
                'animation: shake 0.5s ease-in-out;';

            // 添加抖动动画的内联定义
            const style = document.createElement('style');
            style.textContent = `
            @keyframes shake {
                0%, 100% { transform: translateX(0); }
                10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
                20%, 40%, 60%, 80% { transform: translateX(5px); }
            }
        `;
            document.head.appendChild(style);
        }

        messageContainer.appendChild(messageDiv);

        // 滚动到消息位置
        setTimeout(() => {
            messageDiv.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }, 100);

        // 如果不是成功消息，高亮相关输入框
        if (!isSuccess) {
            highlightErrorFields(message);
        }

        if (duration > 0) {
            setTimeout(() => {
                hideMessage();
                removeErrorHighlights();
            }, duration);
        }
    }

    // 高亮错误字段
    function highlightErrorFields(errorMessage) {
        // 移除之前的高亮
        removeErrorHighlights();

        // 根据错误信息判断高亮哪个字段
        if (errorMessage.includes('账号') || errorMessage.includes('用户名') ||
            errorMessage.includes('用户') || errorMessage.includes('账号密码')) {
            const usernameInput = document.getElementById('username');
            if (usernameInput) {
                usernameInput.classList.add('error', 'highlight-error');
                usernameInput.focus();
            }

            const passwordInput = document.getElementById('password');
            if (passwordInput) {
                passwordInput.classList.add('error');
            }
        } else if (errorMessage.includes('验证码') || errorMessage.includes('验证')) {
            const captchaInput = document.getElementById('captcha');
            if (captchaInput) {
                captchaInput.classList.add('error', 'highlight-error');
                captchaInput.focus();
            }

            const captchaImg = document.getElementById('captchaImg');
            if (captchaImg) {
                captchaImg.classList.add('error');
            }
        } else if (errorMessage.includes('密码')) {
            const passwordInput = document.getElementById('password');
            if (passwordInput) {
                passwordInput.classList.add('error', 'highlight-error');
                passwordInput.focus();
            }
        }
    }

    // 移除错误高亮
    function removeErrorHighlights() {
        document.querySelectorAll('.form-input.error').forEach(input => {
            input.classList.remove('error', 'highlight-error');
        });

        document.querySelectorAll('.captcha-image.error').forEach(img => {
            img.classList.remove('error');
        });
    }

    // 隐藏消息
    function hideMessage() {
        const messageContainer = document.getElementById('messageContainer');
        messageContainer.style.display = 'none';
        messageContainer.innerHTML = '';
    }
</script>
</body>
</html>