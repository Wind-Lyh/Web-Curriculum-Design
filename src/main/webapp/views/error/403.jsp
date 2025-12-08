<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>403 - 禁止访问</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
        }

        body {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            color: #333;
        }

        .error-container {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            padding: 50px 40px;
            max-width: 650px;
            width: 100%;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
            text-align: center;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.3);
            position: relative;
            overflow: hidden;
        }

        .error-container::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 5px;
            background: linear-gradient(90deg, #4facfe, #00f2fe);
        }

        .error-icon {
            font-size: 80px;
            color: #4facfe;
            margin-bottom: 20px;
        }

        .error-code {
            font-size: 120px;
            font-weight: 900;
            color: #4facfe;
            line-height: 1;
            margin-bottom: 10px;
            text-shadow: 3px 3px 0 rgba(0, 0, 0, 0.1);
        }

        .error-title {
            font-size: 32px;
            color: #333;
            margin-bottom: 15px;
        }

        .error-message {
            font-size: 18px;
            color: #666;
            margin-bottom: 30px;
            line-height: 1.6;
        }

        .error-details {
            background: #f8f9fa;
            border-left: 4px solid #4facfe;
            padding: 15px;
            margin: 20px 0 30px;
            text-align: left;
            border-radius: 0 8px 8px 0;
            font-size: 14px;
            color: #555;
        }

        .error-actions {
            display: flex;
            justify-content: center;
            gap: 15px;
            margin-top: 30px;
            flex-wrap: wrap;
        }

        .btn {
            padding: 12px 24px;
            border-radius: 50px;
            text-decoration: none;
            font-weight: 600;
            font-size: 16px;
            transition: all 0.3s ease;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            border: none;
            cursor: pointer;
        }

        .btn-primary {
            background: linear-gradient(90deg, #4facfe, #00f2fe);
            color: white;
            box-shadow: 0 4px 15px rgba(79, 172, 254, 0.3);
        }

        .btn-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 6px 20px rgba(79, 172, 254, 0.4);
        }

        .btn-secondary {
            background: #f8f9fa;
            color: #555;
            border: 1px solid #ddd;
        }

        .btn-secondary:hover {
            background: #e9ecef;
            transform: translateY(-3px);
        }

        .request-info {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px dashed #ddd;
            font-size: 13px;
            color: #888;
            text-align: left;
        }

        .request-info p {
            margin-bottom: 5px;
            word-break: break-all;
        }

        .permission-list {
            display: flex;
            justify-content: center;
            gap: 20px;
            margin: 25px 0;
            flex-wrap: wrap;
        }

        .permission-item {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 10px;
        }

        .permission-icon {
            width: 60px;
            height: 60px;
            background: #f0f8ff;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            color: #4facfe;
            border: 2px solid #4facfe;
        }

        @media (max-width: 768px) {
            .error-code {
                font-size: 80px;
            }

            .error-title {
                font-size: 26px;
            }

            .error-actions {
                flex-direction: column;
            }

            .btn {
                width: 100%;
                justify-content: center;
            }

            .permission-list {
                flex-direction: column;
                align-items: center;
            }
        }
    </style>
</head>
<body>
<div class="error-container">
    <div class="error-icon">
        <i class="fas fa-ban"></i>
    </div>
    <div class="error-code">403</div>
    <h1 class="error-title">禁止访问</h1>

    <p class="error-message">
        抱歉，您没有权限访问此页面。<br>
        如果您认为这是一个错误，请联系管理员获取帮助。
    </p>

    <div class="error-details">
        <strong>可能的原因：</strong><br>
        1. 您没有登录或登录已过期<br>
        2. 您的账户权限不足<br>
        3. 管理员限制了此页面的访问
    </div>

    <div class="permission-list">
        <div class="permission-item">
            <div class="permission-icon">
                <i class="fas fa-user"></i>
            </div>
            <span>需要登录</span>
        </div>
        <div class="permission-item">
            <div class="permission-icon">
                <i class="fas fa-user-shield"></i>
            </div>
            <span>需要特定角色</span>
        </div>
        <div class="permission-item">
            <div class="permission-icon">
                <i class="fas fa-lock"></i>
            </div>
            <span>权限不足</span>
        </div>
    </div>

    <div class="error-actions">
        <a href="javascript:history.back()" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> 返回上一页
        </a>
        <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">
            <i class="fas fa-sign-in-alt"></i> 登录账户
        </a>
        <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">
            <i class="fas fa-home"></i> 返回首页
        </a>
    </div>

    <div class="request-info">
        <p><strong>请求URL:</strong> ${pageContext.request.requestURL}</p>
        <p><strong>错误时间:</strong> <%= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) %></p>
        <p><strong>您的IP:</strong> <%= request.getRemoteAddr() %></p>
    </div>
</div>

<script>
    // 如果没有登录，5秒后跳转到登录页面
    setTimeout(function() {
        window.location.href = "${pageContext.request.contextPath}/login";
    }, 5000);

    // 显示倒计时
    let countdown = 5;
    const countdownElement = document.createElement('p');
    countdownElement.innerHTML = `<strong>自动跳转:</strong> <span id="countdown">${countdown}</span> 秒后自动跳转到登录页面`;
    document.querySelector('.request-info').appendChild(countdownElement);

    const countdownInterval = setInterval(function() {
        countdown--;
        document.getElementById('countdown').textContent = countdown;
        if (countdown <= 0) {
            clearInterval(countdownInterval);
        }
    }, 1000);
</script>
</body>
</html>