<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.community.model.User" %>
<%
    // 从session获取用户对象 - 使用正确的属性名
    User user = (User) session.getAttribute("user");
    if (user == null) {
        // 如果没有用户信息，跳回登录页
        response.sendRedirect(request.getContextPath() + "/index.jsp");
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
                    <button onclick="rotateImage(-30)">左转30°</button>
                    <button onclick="rotateImage(-5)">左转5°</button>
                    <button onclick="rotateImage(5)">右转5°</button>
                    <button onclick="rotateImage(30)">右转30°</button>
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
    // 全局变量
    const contextPath = '<%= request.getContextPath() %>';
    let currentRotation = 0;
    let targetRotation = 0;
    let isDragging = false;
    let startAngle = 0;
    let startRotation = 0;
    let correctAngle = 0; // 后端返回的正确角度

    // 页面加载时初始化
    window.onload = function () {
        console.log('旋转验证页面加载完成');
        console.log('Context Path:', contextPath);
        console.log('当前用户:', '<%= user.getUsername() %>');

        loadRotateCaptcha();
        setupDragRotation();
    };

    // 修改loadRotateCaptcha函数
    function loadRotateCaptcha() {
        console.log('开始加载旋转验证码...');

        // 显示加载状态
        showLoading('正在加载验证码...');

        // 使用正确的URL - 确保有斜杠
        const url = contextPath + '/user/rotateCaptcha?action=rotateCaptcha';
        console.log('完整请求URL:', url);
        console.log('完整请求地址:', window.location.protocol + '//' + window.location.host + url);

        fetch(url, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(response => {
                console.log('响应状态:', response.status, response.statusText);
                if (!response.ok) {
                    throw new Error(`HTTP错误 ${response.status}: ${response.statusText}`);
                }
                return response.text();
            })
            .then(text => {
                console.log('原始响应文本:', text.substring(0, Math.min(500, text.length)) + '...');

                try {
                    const data = JSON.parse(text);
                    console.log('解析后的JSON:', data);

                    if (data.success) {
                        handleRotateCaptchaResponse(data);
                    } else {
                        throw new Error(data.message || data.error || '验证码加载失败');
                    }
                } catch (e) {
                    console.error('JSON解析失败:', e);
                    console.error('原始文本:', text);
                    throw new Error('服务器返回的数据格式错误');
                }
            })
            .catch(error => {
                console.error('加载验证码失败:', error);
                showMessage('验证码加载失败: ' + error.message, false);
                hideLoading();
            });
    }

    // 修改handleRotateCaptchaResponse函数
    function handleRotateCaptchaResponse(data) {
        console.log('处理旋转验证码响应:', data);

        // 设置目标角度
        targetRotation = data.correctAngle || 0;
        correctAngle = data.correctAngle || 0;

        console.log('目标角度:', targetRotation, '正确角度:', correctAngle);

        // 更新页面显示
        document.getElementById('targetAngle').textContent = targetRotation;

        // 设置图片
        const rotateImage = document.getElementById('rotateImage');
        if (data.rotatedImage && data.rotatedImage.startsWith('data:image')) {
            rotateImage.src = data.rotatedImage;
            console.log('使用rotatedImage，长度:', data.rotatedImage.length);
        } else if (data.base64Image && data.base64Image.startsWith('data:image')) {
            rotateImage.src = data.base64Image;
            console.log('使用base64Image，长度:', data.base64Image.length);
        } else {
            console.error('没有有效的图片数据');
            showMessage('验证码图片数据异常', false);
            hideLoading();
            return;
        }

        // 等待图片加载
        rotateImage.onload = function() {
            console.log('图片加载成功，尺寸:', this.width + 'x' + this.height);
            // 重置当前角度
            currentRotation = data.initialAngle || 0;
            updateRotation();
            hideLoading();
            console.log('旋转验证码初始化完成');
        };

        rotateImage.onerror = function() {
            console.error('图片加载失败');
            showMessage('验证码图片加载失败', false);
            hideLoading();
        };
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

        // 确保角度在0-360范围内
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
    function rotateImage(degrees) {
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

        // 更新图片旋转
        const roundedRotation = Math.round(currentRotation);
        image.style.transform = "rotate(" + roundedRotation + "deg)";

        // 更新角度显示
        angleDisplay.textContent = roundedRotation;

        // 添加轻微动画效果
        image.style.transition = isDragging ? 'none' : 'transform 0.1s ease';
    }

    // 验证旋转
    function validateRotation() {
        console.log('开始旋转验证，用户角度: ' + currentRotation + ', 正确角度: ' + correctAngle);

        showLoading('正在验证...');

        const params = new URLSearchParams();
        params.append('action', 'validateRotateCaptcha');
        params.append('angle', Math.round(currentRotation));

        fetch(contextPath + 'user/validateRotateCaptcha', {
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
                    showMessage(data.message || '验证成功', true, 2000);
                    console.log('旋转验证成功，跳转到主页面');

                    // 使用服务端返回的跳转URL
                    setTimeout(() => {
                        hideLoading();
                        if (data.redirectUrl) {
                            window.location.href = data.redirectUrl;
                        } else {
                            // 获取角色页面
                            const currentRole = <%= session.getAttribute("currentRole") != null ? session.getAttribute("currentRole") : 0 %>;
                            let rolePage = '';
                            switch(currentRole) {
                                case 1: rolePage = 'admin/dashboard.jsp'; break;
                                case 2: rolePage = 'moderator/panel.jsp'; break;
                                default: rolePage = 'user/home.jsp';
                            }
                            window.location.href = contextPath + '/' + rolePage;
                        }
                    }, 2000);
                } else {
                    showMessage(data.message || '验证失败', false, 3000);
                    console.log('旋转验证失败，重新验证');
                    hideLoading();
                    // 验证失败时重新加载验证码
                    setTimeout(() => {
                        loadRotateCaptcha();
                    }, 30000);
                }
            })
            .catch(error => {
                console.error('旋转验证错误:', error);
                showMessage('验证失败: ' + error.message, false, 3000);
                hideLoading();
            });
    }

    // 刷新验证码
    function refreshCaptcha() {
        loadRotateCaptcha();
        showMessage('验证码已刷新', true, 1000);
    }

    // 返回上一步
    function goBack() {
        window.location.href = contextPath + '/index.jsp';
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

    // 显示加载中
    function showLoading(message) {
        showMessage(message || '处理中...', true, 0);
    }

    // 隐藏加载中
    function hideLoading() {
        const messageDiv = document.getElementById('message');
        messageDiv.textContent = '';
        messageDiv.className = 'message';
    }
</script>
</body>
</html>