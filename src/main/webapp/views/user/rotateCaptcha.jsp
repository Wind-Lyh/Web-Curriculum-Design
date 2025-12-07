<%@ page import="com.community.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // 从session获取用户对象
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        // 如果没有用户信息，跳回登录页
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>旋转验证</title>
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
            padding: 20px;
        }

        .container {
            background: white;
            border-radius: 15px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
            width: 900px;
            max-width: 95%;
            overflow: hidden;
            display: flex;
            flex-direction: row;
        }

        .image-section {
            flex: 1;
            padding: 30px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            background: #f8f9fa;
            border-right: 1px solid #eaeaea;
        }

        .control-section {
            flex: 1;
            padding: 30px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }

        .title {
            text-align: center;
            margin-bottom: 20px;
            color: #333;
            font-size: 24px;
            font-weight: bold;
        }

        .image-container {
            position: relative;
            margin: 20px 0;
            padding: 20px;
            border: 2px dashed #ddd;
            border-radius: 10px;
            background: white;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
        }

        #rotateImage {
            transition: transform 0.1s linear;
            width: 300px;
            height: 300px;
            cursor: pointer;
            border-radius: 8px;
            user-select: none;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
        }

        .rotation-controls {
            margin: 20px 0;
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            justify-content: center;
        }

        .rotation-controls button {
            padding: 10px 15px;
            background: #667eea;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            transition: all 0.2s;
            flex: 1;
            min-width: 120px;
        }

        .rotation-controls button:hover {
            background: #5a6fd8;
            transform: translateY(-2px);
        }

        .angle-display {
            margin: 15px 0;
            font-weight: bold;
            color: #333;
            font-size: 16px;
            text-align: center;
        }

        .btn {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            cursor: pointer;
            transition: transform 0.2s;
            margin: 10px 0;
            font-weight: bold;
        }

        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }

        .btn:active {
            transform: translateY(0);
        }

        .close-btn {
            background: #6c757d !important;
        }

        .close-btn:hover {
            box-shadow: 0 5px 15px rgba(108, 117, 125, 0.4);
        }

        .message {
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 15px;
            text-align: center;
            min-height: 20px;
            font-weight: bold;
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

        .drag-hint {
            color: #666;
            font-size: 14px;
            margin-top: 10px;
            text-align: center;
        }

        .target-angle {
            background: #e7f3ff;
            padding: 15px;
            border-radius: 8px;
            margin: 10px 0;
            border-left: 4px solid #1890ff;
            text-align: center;
        }

        .section-title {
            font-size: 18px;
            font-weight: bold;
            color: #333;
            margin-bottom: 15px;
            text-align: center;
        }

        .control-group {
            margin-bottom: 25px;
        }

        .rotation-indicator {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            background: #f0f0f0;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 15px auto;
            position: relative;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            transition: transform 0.1s linear;
        }

        .rotation-indicator::before {
            content: '';
            position: absolute;
            width: 4px;
            height: 40px;
            background: #ff4d4f;
            top: 10px;
            left: 50%;
            transform-origin: bottom center;
            transform: translateX(-50%) rotate(0deg);
            border-radius: 2px;
        }

        @media (max-width: 768px) {
            .container {
                flex-direction: column;
            }

            .image-section {
                border-right: none;
                border-bottom: 1px solid #eaeaea;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <!-- 左侧验证区域 -->
    <div class="image-section">
        <div class="title">旋转验证系统</div>

        <div class="image-container">
            <img id="rotateImage" alt="旋转验证码">
            <div class="drag-hint">提示：点击并拖动鼠标旋转图片</div>
        </div>

        <div class="angle-display">
            当前角度: <span id="currentAngle">0</span>°
        </div>

    </div>

    <!-- 右侧控制区域 -->
    <div class="control-section">
        <div>
            <div class="section-title">验证控制</div>

            <div id="message" class="message"></div>

            <!-- 旋转验证目标 -->
            <div class="target-angle">
                <strong>目标角度：<span id="targetAngle">?</span>°</strong>
                <div style="font-size: 14px; color: #666; margin-top: 5px;">
                    请将图片旋转到目标角度
                </div>
            </div>

            <!-- 旋转验证控制 -->
            <div class="control-group">
                <div class="section-title">旋转控制</div>
                <div class="rotation-controls">
                    <button onclick="RotateImage(-30)">左转30°</button>
                    <button onclick="RotateImage(-5)">左转5°</button>
                    <button onclick="RotateImage(5)">右转5°</button>
                    <button onclick="RotateImage(30)">右转30°</button>
                    <button onclick="resetRotation()">重置角度</button>
                </div>
            </div>
        </div>

        <div>
            <button class="btn" onclick="validateRotation()">确认验证</button>
            <button class="btn close-btn" onclick="goBack()">返回上一步</button>
            <button class="btn" onclick="refreshCaptcha()">刷新验证码</button>
        </div>
    </div>
</div>

<script>
    // 将用户信息传递给JavaScript
    var currentUser = {
        id: <%= currentUser.getId() %>,
        username: '<%= currentUser.getUsername() %>'
    };

    console.log('当前用户:', currentUser);

    // 全局变量 - 旋转验证
    let currentRotation = 0;
    let targetRotation = 0;
    let isDragging = false;
    let startAngle = 0;
    let startRotation = 0;

    // 页面加载时初始化
    window.onload = function () {
        console.log('旋转验证页面加载完成');
        loadRotateCaptcha();
        setupDragRotation();
    };

    // 加载旋转验证码
    function loadRotateCaptcha() {
        console.log('加载旋转验证码');

        const params = new URLSearchParams();
        params.append('action', 'generate');

        fetch('<%= request.getContextPath() %>/imageCaptcha', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: params
        })
            .then(response => {
                console.log('旋转验证码响应状态:', response.status);
                if (!response.ok) {
                    throw new Error('网络响应不正常: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('旋转验证码响应数据:', data);
                if (data.success) {
                    handleRotateCaptchaResponse(data);
                } else {
                    showMessage('验证码加载失败: ' + (data.error || '未知错误'), false);
                }
            })
            .catch(error => {
                console.error('旋转验证码请求错误:', error);
                showMessage('验证码加载失败: ' + error.message, false);
            });
    }

    // 处理旋转验证码响应
    function handleRotateCaptchaResponse(data) {
        // 设置目标角度
        targetRotation = data.targetValue;
        document.getElementById('targetAngle').textContent = targetRotation;

        // 设置图片
        const rotateImage = document.getElementById('rotateImage');
        rotateImage.src = data.imageBase64;

        // 重置当前角度
        currentRotation = data.initialAngle || 0;
        updateRotation();

        console.log('旋转验证码初始化完成，目标角度: ' + targetRotation);
    }

    // 设置拖拽旋转功能
    function setupDragRotation() {
        const image = document.getElementById('rotateImage');

        // 鼠标事件
        image.addEventListener('mousedown', startDrag);
        document.addEventListener('mousemove', drag);
        document.addEventListener('mouseup', stopDrag);

        console.log('拖拽旋转功能已初始化');
    }

    // 旋转验证的拖拽函数
    function startDrag(e) {
        e.preventDefault();
        isDragging = true;
        const image = document.getElementById('rotateImage');
        const rect = image.getBoundingClientRect();
        const centerX = rect.left + rect.width / 2;
        const centerY = rect.top + rect.height / 2;

        // 计算初始角度
        startAngle = Math.atan2(e.clientY - centerY, e.clientX - centerX) * (180 / Math.PI);
        startRotation = currentRotation;

        // 添加抓取效果
        image.style.cursor = 'grabbing';
        image.style.filter = 'brightness(0.95)';

        console.log('开始拖拽，初始角度:', startAngle, '当前旋转:', startRotation);
    }

    function drag(e) {
        if (!isDragging) return;
        e.preventDefault();

        const image = document.getElementById('rotateImage');
        const rect = image.getBoundingClientRect();
        const centerX = rect.left + rect.width / 2;
        const centerY = rect.top + rect.height / 2;

        // 计算当前角度
        const currentAngle = Math.atan2(e.clientY - centerY, e.clientX - centerX) * (180 / Math.PI);

        // 计算角度差并更新旋转
        let angleDiff = currentAngle - startAngle;

        // 平滑旋转，避免跳跃
        if (angleDiff > 180) angleDiff -= 360;
        if (angleDiff < -180) angleDiff += 360;

        currentRotation = startRotation + angleDiff;

        // 确保角度在0-360度范围内
        currentRotation = (currentRotation % 360 + 360) % 360;

        updateRotation();
    }

    function stopDrag() {
        if (!isDragging) return;
        isDragging = false;

        const image = document.getElementById('rotateImage');
        image.style.cursor = 'grab';
        image.style.filter = 'brightness(1)';

        console.log('停止拖拽，最终角度:', currentRotation);
    }

    // 旋转图片
    function RotateImage(degrees) {
        currentRotation += degrees;
        currentRotation = (currentRotation % 360 + 360) % 360;
        updateRotation();

        // 添加旋转动画效果
        const image = document.getElementById('rotateImage');
        image.style.transition = 'transform 0.3s ease';
        setTimeout(() => {
            image.style.transition = 'transform 0.1s linear';
        }, 300);

        console.log('按钮旋转，角度变化:', degrees, '当前角度:', currentRotation);
    }

    // 重置旋转
    function resetRotation() {
        currentRotation = 0;
        updateRotation();

        // 添加旋转动画效果
        const image = document.getElementById('rotateImage');
        image.style.transition = 'transform 0.5s ease';
        setTimeout(() => {
            image.style.transition = 'transform 0.1s linear';
        }, 500);

        console.log('重置角度为0');
    }

    // 更新旋转显示
    function updateRotation() {
        const image = document.getElementById('rotateImage');
        const angleDisplay = document.getElementById('currentAngle');
        console.log(currentRotation);
        // 更新图片旋转
        var value=Math.round(currentRotation);
        image.style.transform = "rotate("+value+"deg)";
        //image.style.willChange = 'transform';
        // 更新角度显示
        angleDisplay.textContent = Math.round(currentRotation);

        // 添加轻微动画效果
        image.style.transition = isDragging ? 'none' : 'transform 0.1s ease';
    }

    // 验证旋转
    function validateRotation() {
        console.log('开始旋转验证，用户角度: ' + currentRotation + ', 目标角度: ' + targetRotation);

        const params = new URLSearchParams();
        params.append('action', 'validate');
        params.append('angle', Math.round(currentRotation));

        fetch('<%= request.getContextPath() %>/imageCaptcha', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: params
        })
            .then(response => {
                console.log('旋转验证响应状态:', response.status);
                if (!response.ok) {
                    throw new Error('网络响应不正常: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('旋转验证响应数据:', data);
                if (data.success) {
                    showMessage(data.message, true, 2000);
                    console.log('旋转验证成功，跳转到主页面');

                    // 使用服务端返回的跳转URL
                    setTimeout(() => {
                        if (data.redirectUrl) {
                            window.location.href = data.redirectUrl;
                        } else {
                            // 备用跳转路径 - 修正为正确的路径
                            window.location.href = '<%= request.getContextPath() %>/jsp/messages.jsp';
                        }
                    }, 2000);
                } else {
                    showMessage(data.message, false, 3000);
                    // 验证失败时重新加载验证码
                    setTimeout(() => {
                        loadRotateCaptcha();
                    }, 1000);
                }
            })
            .catch(error => {
                console.error('旋转验证错误:', error);
                showMessage('验证失败: ' + error.message, false, 3000);
            });
    }

    // 刷新验证码
    function refreshCaptcha() {
        loadRotateCaptcha();
        showMessage('验证码已刷新', true, 1000);
    }

    // 返回上一步
    function goBack() {
        window.location.href = '<%= request.getContextPath() %>/login.jsp';
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