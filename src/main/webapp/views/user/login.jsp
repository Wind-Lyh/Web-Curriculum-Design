<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录系统 - 第一步</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Microsoft YaHei', Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .login-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
            padding: 40px;
            width: 400px;
        }
        .login-title {
            text-align: center;
            margin-bottom: 30px;
            color: #333;
            font-size: 24px;
            font-weight: bold;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            color: #555;
            font-weight: bold;
        }
        .form-group input {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        .form-group input:focus {
            border-color: #667eea;
            outline: none;
        }
        .captcha-group {
            display: flex;
            gap: 10px;
            align-items: center;
        }
        .captcha-group input {
            flex: 1;
        }
        .captcha-img {
            width: 120px;
            height: 40px;
            border: 1px solid #ddd;
            border-radius: 5px;
            cursor: pointer;
            object-fit: cover;
        }
        .button-group {
            display: flex;
            gap: 15px;
            margin-top: 10px;
        }
        .btn {
            flex: 1;
            padding: 12px;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            transition: all 0.3s ease;
            text-align: center;
            text-decoration: none;
            display: inline-block;
        }
        .btn-login {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        .btn-register {
            background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
        }
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
        }
        .btn:active {
            transform: translateY(0);
        }
        .message {
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 15px;
            text-align: center;
            min-height: 20px;
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
        .register-link {
            text-align: center;
            margin-top: 20px;
            color: #666;
        }
        .register-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: bold;
        }
        .register-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="login-container">
    <div class="login-title">社区平台交流</div>

    <div id="message" class="message"></div>

    <form id="loginForm">
        <div class="form-group">
            <label for="username">账号</label>
            <input type="text" id="username" name="username" placeholder="请输入账号" required>
        </div>
        <div class="form-group">
            <label for="password">密码</label>
            <input type="password" id="password" name="password" placeholder="请输入密码" required>
        </div>
        <div class="form-group">
            <label for="captcha">验证码</label>
            <div class="captcha-group">
                <input type="text" id="captcha" name="captcha" placeholder="请输入验证码" required>
                <img id="captchaImg" class="captcha-img" onclick="refreshCaptcha()"
                     title="点击刷新验证码" alt="验证码图片" src="">
            </div>
        </div>

        <div class="button-group">
            <button type="button" class="btn btn-login" onclick="validateFirstStep()">第一步验证</button>
            <button type="button" class="btn btn-register" onclick="goToRegister()">注册</button>
        </div>
    </form>

    <div class="register-link">
        还没有账号？ <a href="javascript:void(0);" onclick="goToRegister()">立即注册</a>
    </div>
</div>

<script>
    const contextPath = '<%= request.getContextPath() %>';

    // 页面加载时生成验证码
    window.onload = function() {
        console.log('页面加载完成，开始初始化验证码');
        refreshCaptcha();
    };

    // 刷新验证码
    function refreshCaptchaSilent() {
        console.log('静默刷新验证码');

        fetch(contextPath + '/login?action=Captcha_Num&type=arithmetic')
            .then(response => {
                console.log('验证码响应状态:', response.status, response.statusText);
                if (!response.ok) {
                    throw new Error('网络响应不正常: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('验证码响应数据:', data);
                if (data.success && data.imageBase64) {
                    document.getElementById('captchaImg').src = data.imageBase64;
                    document.getElementById('captcha').value = '';
                } else {
                    showMessage('验证码生成失败: ' + (data.error || '未知错误'), false);
                }
            })
            .catch(error => {
                console.error('验证码请求错误:', error);
                showMessage('验证码加载失败: ' + error.message, false);
            });
    }

    function refreshCaptcha() {
        refreshCaptchaSilent();
        showMessage('验证码已刷新', true, 1000);
    }

    // 跳转到注册页面
    function goToRegister() {
        console.log('跳转到注册页面');
        // 跳转到注册页面，假设注册页面为 register.jsp
        window.location.href = contextPath + '/register.jsp';
    }

    // 第一步验证
    function validateFirstStep() {
        console.log('开始第一步验证');

        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value.trim();
        const captcha = document.getElementById('captcha').value.trim();

        if (!username || !password || !captcha) {
            showMessage('请填写完整信息', false, 2000);
            return;
        }

        const params = new URLSearchParams();
        params.append('action', 'Captcha_Num_pd');
        params.append('username', username);
        params.append('password', password);
        params.append('captcha', captcha);

        fetch(contextPath + '/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: params
        })
            .then(response => {
                console.log('第一步验证响应状态:', response.status, response.statusText);
                if (!response.ok) {
                    throw new Error('网络响应不正常: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('第一步验证响应数据:', data);
                if (data.success) {
                    showMessage(data.message, true, 1500);
                    console.log('第一步验证成功，跳转到验证页面');
                    // 跳转到验证页面
                    setTimeout(() => {
                        // 确保URL正确拼接
                        let redirectUrl = data.redirectUrl;
                        if (!redirectUrl.startsWith(contextPath)) {
                            redirectUrl = contextPath + '/' + redirectUrl;
                        }
                        window.location.href = redirectUrl;
                    }, 1500);



                } else {
                    let errorMessage = data.message;
                    console.log('服务器返回的错误消息:', data.message);

                    if (data.message.includes('账号密码') || data.message.includes('密码错误')) {
                        errorMessage = "账号或密码错误，请检查后重新输入";
                    } else if (data.message.includes('验证码')) {
                        errorMessage = "验证码错误，请重新输入";
                    } else if (data.message.includes('过期')) {
                        errorMessage = "验证码已过期，请刷新验证码";
                    }

                    console.log('最终显示的错误消息:', errorMessage);
                    showMessage(errorMessage, false, 2000);

                    // 延迟静默刷新验证码
                    setTimeout(() => {
                        refreshCaptchaSilent();
                    }, 1000);
                }
            })
            .catch(error => {
                console.error('第一步验证错误:', error);
                showMessage('验证失败: ' + error.message, false, 2000);

                setTimeout(() => {
                    refreshCaptchaSilent();
                }, 1000);
            });
    }

    // 显示消息
    function showMessage(message, isSuccess, duration = 3000) {
        const messageDiv = document.getElementById('message');
        messageDiv.textContent = message;
        messageDiv.className = 'message ' + (isSuccess ? 'success' : 'error');
        setTimeout(() => {
            if (messageDiv.textContent === message) {
                messageDiv.textContent = '';
                messageDiv.className = 'message';
            }
        }, duration);
    }
</script>
</body>
</html>