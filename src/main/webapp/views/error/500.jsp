<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - 服务器内部错误</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
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
            align-items: center;
            justify-content: center;
            padding: 20px;
            color: #333;
        }

        .error-container {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
            padding: 50px 40px;
            max-width: 700px;
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
            background: linear-gradient(90deg, #667eea, #764ba2);
        }

        .error-icon {
            font-size: 80px;
            color: #667eea;
            margin-bottom: 20px;
            animation: pulse 2s infinite;
        }

        @keyframes pulse {
            0% { transform: scale(1); }
            50% { transform: scale(1.1); }
            100% { transform: scale(1); }
        }

        .error-code {
            font-size: 120px;
            font-weight: 900;
            color: #667eea;
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
            border-left: 4px solid #667eea;
            padding: 15px;
            margin: 20px 0 30px;
            text-align: left;
            border-radius: 0 8px 8px 0;
            font-size: 14px;
            color: #555;
            max-height: 200px;
            overflow-y: auto;
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
            background: linear-gradient(90deg, #667eea, #764ba2);
            color: white;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
        }

        .btn-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
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

        .tech-info {
            background: #f0f0f0;
            border-radius: 8px;
            padding: 15px;
            margin-top: 20px;
            font-size: 12px;
            color: #666;
            text-align: left;
            display: none;
        }

        .toggle-tech {
            margin-top: 15px;
            color: #667eea;
            cursor: pointer;
            font-size: 14px;
            display: inline-flex;
            align-items: center;
            gap: 5px;
        }

        .toggle-tech:hover {
            text-decoration: underline;
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
        }
    </style>
</head>
<body>
<div class="error-container">
    <div class="error-icon">
        <i class="fas fa-exclamation-triangle"></i>
    </div>
    <div class="error-code">500</div>
    <h1 class="error-title">服务器内部错误</h1>

    <p class="error-message">
        抱歉，服务器处理您的请求时出现了错误。<br>
        我们的技术团队已经收到通知，正在努力修复此问题。
    </p>

    <div class="error-details">
        <strong>错误信息：</strong><br>
        <%= exception != null ? exception.getMessage() : "未知错误" %>
    </div>

    <div class="toggle-tech" id="toggleTech">
        <i class="fas fa-code"></i> 显示技术详情
    </div>

    <div class="tech-info" id="techInfo">
        <strong>堆栈跟踪：</strong><br>
        <%
            if (exception != null) {
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                exception.printStackTrace(pw);
                out.print(sw.toString().replace("\n", "<br>").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;"));
            } else {
                out.print("无堆栈跟踪信息");
            }
        %>
    </div>

    <div class="error-actions">
        <a href="javascript:location.reload()" class="btn btn-primary">
            <i class="fas fa-redo"></i> 重新尝试
        </a>
        <a href="javascript:history.back()" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> 返回上一页
        </a>
        <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">
            <i class="fas fa-home"></i> 返回首页
        </a>
    </div>

    <div class="request-info">
        <p><strong>请求URL:</strong> ${pageContext.request.requestURL}</p>
        <p><strong>错误时间:</strong> <%= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) %></p>
        <p><strong>服务器:</strong> <%= request.getServerName() %>:<%= request.getServerPort() %></p>
        <p><strong>Servlet名称:</strong> <%= pageContext.getServletConfig().getServletName() %></p>
    </div>
</div>

<script>
    // 显示/隐藏技术详情
    document.getElementById('toggleTech').addEventListener('click', function() {
        const techInfo = document.getElementById('techInfo');
        const isHidden = techInfo.style.display === 'none' || techInfo.style.display === '';

        if (isHidden) {
            techInfo.style.display = 'block';
            this.innerHTML = '<i class="fas fa-code"></i> 隐藏技术详情';
        } else {
            techInfo.style.display = 'none';
            this.innerHTML = '<i class="fas fa-code"></i> 显示技术详情';
        }
    });

    // 10秒后自动刷新页面
    setTimeout(function() {
        window.location.reload();
    }, 10000);

    // 显示倒计时
    let countdown = 10;
    const countdownElement = document.createElement('p');
    countdownElement.innerHTML = `<strong>自动刷新:</strong> <span id="countdown">${countdown}</span> 秒后自动刷新页面`;
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