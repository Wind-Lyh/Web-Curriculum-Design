<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>双重验证系统 - 用户注册</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        /* 样式保持不变 */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', 'Microsoft YaHei', Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        .register-container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
            padding: 40px;
            width: 480px;
            max-width: 100%;
            position: relative;
            overflow: hidden;
        }
        .register-container::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        .register-title {
            text-align: center;
            margin-bottom: 30px;
            color: #333;
            font-size: 28px;
            font-weight: 700;
            position: relative;
            padding-bottom: 15px;
        }
        .register-title::after {
            content: '';
            position: absolute;
            bottom: 0;
            left: 50%;
            transform: translateX(-50%);
            width: 60px;
            height: 3px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 2px;
        }
        .form-group {
            margin-bottom: 24px;
            position: relative;
        }
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #555;
            font-weight: 600;
            font-size: 15px;
        }
        .form-group input {
            width: 100%;
            padding: 14px 16px;
            border: 2px solid #e1e5e9;
            border-radius: 8px;
            font-size: 15px;
            transition: all 0.3s;
            background-color: #f9fafb;
        }
        .form-group input:focus {
            border-color: #667eea;
            outline: none;
            background-color: white;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        .form-group input.valid {
            border-color: #28a745;
        }
        .form-group input.invalid {
            border-color: #dc3545;
        }
        .input-icon {
            position: absolute;
            right: 16px;
            top: 42px;
            font-size: 18px;
            color: #adb5bd;
            opacity: 0;
            transition: opacity 0.3s;
        }
        .input-icon.valid {
            color: #28a745;
            opacity: 1;
        }
        .input-icon.invalid {
            color: #dc3545;
            opacity: 1;
        }
        .password-requirements {
            margin-top: 12px;
            padding: 16px;
            background-color: #f8f9fa;
            border-radius: 8px;
            border-left: 4px solid #667eea;
        }
        .requirement {
            margin-bottom: 8px;
            display: flex;
            align-items: center;
            font-size: 14px;
            color: #495057;
            transition: all 0.3s;
        }
        .requirement-icon {
            margin-right: 10px;
            font-size: 16px;
            width: 20px;
            text-align: center;
        }
        .requirement.valid {
            color: #28a745;
        }
        .requirement.invalid {
            color: #6c757d;
        }
        .requirement.valid .requirement-icon {
            color: #28a745;
        }
        .requirement.invalid .requirement-icon {
            color: #6c757d;
        }
        .password-strength {
            margin-top: 16px;
            height: 6px;
            border-radius: 3px;
            background-color: #e9ecef;
            overflow: hidden;
            position: relative;
        }
        .password-strength-bar {
            height: 100%;
            width: 0;
            transition: all 0.4s ease;
            border-radius: 3px;
        }
        .strength-weak {
            background-color: #dc3545;
            width: 33%;
        }
        .strength-medium {
            background-color: #ffc107;
            width: 66%;
        }
        .strength-strong {
            background-color: #28a745;
            width: 100%;
        }
        .strength-text {
            position: absolute;
            right: 0;
            top: -20px;
            font-size: 12px;
            font-weight: 600;
        }
        .strength-weak-text {
            color: #dc3545;
        }
        .strength-medium-text {
            color: #ffc107;
        }
        .strength-strong-text {
            color: #28a745;
        }
        .btn {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            margin-top: 10px;
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
            position: relative;
            overflow: hidden;
        }
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
        }
        .btn:active {
            transform: translateY(0);
        }
        /*.disablebutton {*/
        /*    background: #adb5bd;*/
        /*    cursor: not-allowed;*/
        /*    transform: none;*/
        /*    box-shadow: none;*/
        /*}*/
        /*.disablebutton:hover {*/
        /*    transform: none;*/
        /*    box-shadow: none;*/
        /*}*/
        .btn-loading {
            pointer-events: none;
            opacity: 0.8;
        }
        .btn-loading::after {
            content: '';
            position: absolute;
            width: 20px;
            height: 20px;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            margin: auto;
            border: 2px solid transparent;
            border-top-color: #ffffff;
            border-radius: 50%;
            animation: button-loading-spinner 1s ease infinite;
        }
        @keyframes button-loading-spinner {
            from { transform: rotate(0turn); }
            to { transform: rotate(1turn); }
        }
        .message {
            padding: 14px;
            border-radius: 8px;
            margin-bottom: 20px;
            text-align: center;
            min-height: 20px;
            font-weight: 500;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .login-link {
            text-align: center;
            margin-top: 24px;
            color: #6c757d;
            font-size: 15px;
        }
        .login-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.2s;
            display: inline-flex;
            align-items: center;
        }
        .login-link a:hover {
            text-decoration: underline;
            color: #5a67d8;
        }
        .login-link a i {
            margin-right: 6px;
        }
        .form-header {
            display: flex;
            align-items: center;
            margin-bottom: 5px;
        }
        .form-header i {
            margin-right: 8px;
            color: #667eea;
            font-size: 16px;
        }
        .debug-info {
            margin-top: 15px;
            padding: 10px;
            background-color: #f8f9fa;
            border-radius: 5px;
            font-size: 12px;
            color: #6c757d;
        }
    </style>
</head>
<body>
<div class="register-container">
    <div class="register-title">用户注册</div>

    <div id="message" class="message"></div>

    <form id="registerForm">
        <div class="form-group">
            <div class="form-header">
                <i class="fas fa-user"></i>
                <label for="username">用户名</label>
            </div>
            <input type="text" id="username" name="username" placeholder="请输入用户名（至少3个字符）" required>
            <span class="input-icon"><i class="fas fa-check-circle"></i></span>
        </div>

        <div class="form-group">
            <div class="form-header">
                <i class="fas fa-envelope"></i>
                <label for="email">邮箱</label>
            </div>
            <input type="email" id="email" name="email" placeholder="请输入邮箱地址" required>
            <span class="input-icon"><i class="fas fa-check-circle"></i></span>
        </div>

        <div class="form-group">
            <div class="form-header">
                <i class="fas fa-lock"></i>
                <label for="password">密码</label>
            </div>
            <input type="password" id="password" name="password" placeholder="请输入密码" required>
            <span class="input-icon"><i class="fas fa-check-circle"></i></span>

            <div class="password-requirements">
                <div class="requirement invalid" id="req-length">
                    <span class="requirement-icon"><i class="fas fa-times"></i></span>
                    <span>长度至少8位</span>
                </div>
                <div class="requirement invalid" id="req-lowercase">
                    <span class="requirement-icon"><i class="fas fa-times"></i></span>
                    <span>包含小写字母</span>
                </div>
                <div class="requirement invalid" id="req-uppercase">
                    <span class="requirement-icon"><i class="fas fa-times"></i></span>
                    <span>包含大写字母</span>
                </div>
                <div class="requirement invalid" id="req-number">
                    <span class="requirement-icon"><i class="fas fa-times"></i></span>
                    <span>包含数字</span>
                </div>
                <div class="requirement invalid" id="req-special">
                    <span class="requirement-icon"><i class="fas fa-times"></i></span>
                    <span>包含特殊字符 (!@#$%^&*等)</span>
                </div>
            </div>

            <div class="password-strength">
                <div class="password-strength-bar" id="password-strength-bar"></div>
                <div class="strength-text" id="strength-text">密码强度</div>
            </div>
        </div>

        <div class="form-group">
            <div class="form-header">
                <i class="fas fa-lock"></i>
                <label for="confirmPassword">确认密码</label>
            </div>
            <input type="password" id="confirmPassword" name="confirmPassword" placeholder="请再次输入密码" required>
            <span class="input-icon"><i class="fas fa-check-circle"></i></span>
        </div>

        <button type="button" class="btn" id="registerBtn" onclick="checkFormValidity()">
            <span id="btn-text">注册</span>
        </button>

        <!-- 添加调试信息区域 -->
        <div class="debug-info" id="debug-info">
            <strong>调试信息：</strong><br>
            <span id="debug-status">等待输入...</span>
        </div>
    </form>

    <div class="login-link">
        <a href="login.jsp"><i class="fas fa-arrow-left"></i> 返回登录</a>
    </div>
</div>

<script>
    // 密码要求
    const requirements = {
        length: { element: document.getElementById('req-length'), regex: /.{8,}/ },
        lowercase: { element: document.getElementById('req-lowercase'), regex: /[a-z]/ },
        uppercase: { element: document.getElementById('req-uppercase'), regex: /[A-Z]/ },
        number: { element: document.getElementById('req-number'), regex: /[0-9]/ },
        special: { element: document.getElementById('req-special'), regex: /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/ }
    };

    // 输入框图标
    const inputIcons = {
        username: document.querySelector('#username + .input-icon'),
        email: document.querySelector('#email + .input-icon'),
        password: document.querySelector('#password + .input-icon'),
        confirmPassword: document.querySelector('#confirmPassword + .input-icon')
    };

    // 调试信息元素
    const debugInfo = document.getElementById('debug-info');
    const debugStatus = document.getElementById('debug-status');

    // 初始化
    document.addEventListener('DOMContentLoaded', function() {
        console.log('页面加载完成，开始初始化注册表单');

        // 为所有输入框添加事件监听
        document.getElementById('username').addEventListener('input', validateUsername);
        document.getElementById('email').addEventListener('input', validateEmail);
        document.getElementById('password').addEventListener('input', validatePassword);
        document.getElementById('confirmPassword').addEventListener('input', validatePasswordMatch);

        // 初始检查表单有效性
        //checkFormValidity();
    });

    // 验证用户名
    function validateUsername() {
        const username = document.getElementById('username').value.trim();
        const icon = inputIcons.username;

        if (username.length >= 3) {
            icon.className = 'input-icon valid';
            return true;
        } else {
            icon.className = 'input-icon';
            return false;
        }
    }

    // 验证邮箱
    function validateEmail() {
        const email = document.getElementById('email').value.trim();
        const icon = inputIcons.email;
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (emailRegex.test(email)) {
            icon.className = 'input-icon valid';
            return true;
        } else {
            icon.className = 'input-icon';
            return false;
        }
    }

    // 验证密码强度
    function validatePassword() {
        const password = document.getElementById('password').value;
        const icon = inputIcons.password;

        let validCount = 0;
        let totalCount = Object.keys(requirements).length;

        // 检查每个要求
        for (let key in requirements) {
            if (requirements[key].regex.test(password)) {
                requirements[key].element.classList.remove('invalid');
                requirements[key].element.classList.add('valid');
                requirements[key].element.querySelector('.requirement-icon').innerHTML = '<i class="fas fa-check"></i>';
                validCount++;
            } else {
                requirements[key].element.classList.remove('valid');
                requirements[key].element.classList.add('invalid');
                requirements[key].element.querySelector('.requirement-icon').innerHTML = '<i class="fas fa-times"></i>';
            }
        }

        // 更新密码强度条
        const strengthBar = document.getElementById('password-strength-bar');
        const strengthText = document.getElementById('strength-text');
        strengthBar.className = 'password-strength-bar';
        strengthText.className = 'strength-text';

        if (validCount === totalCount) {
            strengthBar.classList.add('strength-strong');
            strengthText.classList.add('strength-strong-text');
            strengthText.textContent = '强';
            icon.className = 'input-icon valid';
        } else if (validCount >= totalCount * 0.6) {
            strengthBar.classList.add('strength-medium');
            strengthText.classList.add('strength-medium-text');
            strengthText.textContent = '中';
            icon.className = 'input-icon';
        } else if (validCount > 0) {
            strengthBar.classList.add('strength-weak');
            strengthText.classList.add('strength-weak-text');
            strengthText.textContent = '弱';
            icon.className = 'input-icon';
        } else {
            strengthText.textContent = '密码强度';
            icon.className = 'input-icon';
        }

        // 更新调试信息
        updateDebugInfo();

        return validCount === totalCount;
    }

    // 验证密码匹配
    function validatePasswordMatch() {
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const confirmInput = document.getElementById('confirmPassword');
        const icon = inputIcons.confirmPassword;

        if (confirmPassword && password === confirmPassword) {
            confirmInput.classList.add('valid');
            confirmInput.classList.remove('invalid');
            icon.className = 'input-icon valid';
            return true;
        } else if (confirmPassword) {
            confirmInput.classList.add('invalid');
            confirmInput.classList.remove('valid');
            icon.className = 'input-icon invalid';
            return false;
        } else {
            confirmInput.classList.remove('valid', 'invalid');
            icon.className = 'input-icon';
            return false;
        }
    }

    // 检查表单是否有效 - 修复逻辑错误
    function checkFormValidity() {
        const usernameValid = validateUsername();
        const emailValid = validateEmail();
        const passwordValid = validatePassword();
        const passwordMatch = validatePasswordMatch();

        // 启用或禁用注册按钮 - 修复这里的逻辑
        const registerBtn = document.getElementById('registerBtn');
        if (usernameValid && emailValid && passwordValid && passwordMatch) {
            //registerBtn.classList.remove("btndisabled");
            //registerBtn.
            //registerBtn.disabled = false; // 条件满足时启用按钮
            //console.log('所有条件满足，注册按钮已启用');
            registerUser();
        } else {
            //registerBtn.classList.add("btndisabled");
            //registerBtn.disabled = true; // 条件不满足时禁用按钮
            showMessage("failed",false);
        }

        // 更新调试信息
        //updateDebugInfo();
    }

    // 更新调试信息
    function updateDebugInfo() {
        return;
        const usernameValid = validateUsername();
        const emailValid = validateEmail();
        const passwordValid = validatePassword();
        const passwordMatch = validatePasswordMatch();

        const statusText = `用户名: ${usernameValid ? '✓' : '✗'} | 邮箱: ${emailValid ? '✓' : '✗'} | 密码: ${passwordValid ? '✓' : '✗'} | 确认密码: ${passwordMatch ? '✓' : '✗'}`;
        debugStatus.textContent = statusText;

        // 如果所有条件都满足，显示成功信息
        if (usernameValid && emailValid && passwordValid && passwordMatch) {
            debugInfo.style.backgroundColor = '#d4edda';
            debugInfo.style.color = '#155724';
            debugInfo.style.border = '1px solid #c3e6cb';
        } else {
            debugInfo.style.backgroundColor = '#f8f9fa';
            debugInfo.style.color = '#6c757d';
            debugInfo.style.border = 'none';
        }
    }

    // 注册用户 - 改进数据发送方式
    function registerUser() {
        const username = document.getElementById('username').value.trim();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;

        // 显示注册中状态
        const registerBtn = document.getElementById('registerBtn');
        const btnText = document.getElementById('btn-text');
        registerBtn.classList.add('btn-loading');
        btnText.textContent = '注册中...';

        // 显示注册中消息
        showMessage('正在提交注册信息，请稍候...', true, 0);

        // 使用URLSearchParams而不是FormData
        const params = new URLSearchParams();
        params.append('username', username);
        params.append('email', email);
        params.append('password', password);

        console.log('发送的数据:', {
            username: username,
            email: email,
            password: password
        });
        console.log(params);
        // 发送注册请求到后端
        fetch('register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: params
        })
            .then(response => {
                console.log('注册响应状态:', response.status, response.statusText);
                if (!response.ok) {
                    throw new Error('网络响应不正常: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('注册响应数据:', data);
                if (data.success) {
                    showMessage(data.message, true, 0);
                    // 注册成功，跳转到登录页面
                    setTimeout(() => {
                        window.location.href = 'login.jsp';
                    }, 2000);
                } else {
                    showMessage(data.message, false, 5000);
                    // 恢复按钮状态
                    registerBtn.classList.remove('btn-loading');
                    btnText.textContent = '注册';
                }
            })
            .catch(error => {
                console.error('注册请求错误:', error);
                showMessage('注册失败，请检查网络连接: ' + error.message, false, 5000);
                // 恢复按钮状态
                registerBtn.classList.remove('btn-loading');
                btnText.textContent = '注册';
            });
    }

    // 显示消息
    function showMessage(message, isSuccess, duration = 3000) {
        const messageDiv = document.getElementById('message');
        messageDiv.textContent = message;
        messageDiv.className = 'message ' + (isSuccess ? 'success' : 'error');

        if (duration > 0) {
            setTimeout(() => {
                if (messageDiv.textContent === message) {
                    messageDiv.textContent = '';
                    messageDiv.className = 'message';
                }
            }, duration);
        }
    }
</script>
</body>
</html>