<%@ page import="com.text.project.User" %>
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
    <title>滑动验证</title>
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

        #slideContainer {
            position: relative;
            width: 400px;
            height: 300px;
            margin: 0 auto;
            border: 2px solid #ddd;
            border-radius: 8px;
            overflow: hidden;
            background: #f0f0f0;
        }

        #backgroundImage {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        #puzzlePiece {
            position: absolute;
            top: 110px; /* 默认垂直居中，后端会覆盖这个值 */
            left: 0;
            width: 80px;
            height: 80px;
            background-size: cover;
            border: 3px solid #007bff;
            border-radius: 10px;
            cursor: move;
            box-shadow: 0 4px 12px rgba(0, 123, 255, 0.4);
            z-index: 10;
            transition: left 0.1s ease;
        }

        #targetLine {
            position: absolute;
            top: 0;
            left: 0;
            width: 2px;
            height: 100%;
            background: rgba(255, 0, 0, 0.6);
            z-index: 5;
            pointer-events: none;
            display: none;
        }

        #slideTrack {
            position: relative;
            width: 400px;
            height: 50px;
            background: #e9ecef;
            border-radius: 25px;
            margin: 20px auto;
            overflow: hidden;
        }

        #slideSlider {
            position: absolute;
            top: 0;
            left: 0;
            width: 50px;
            height: 50px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 25px;
            cursor: grab;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 20px;
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
            transition: left 0.1s;
            z-index: 5;
        }

        .slide-controls {
            margin: 20px 0;
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            justify-content: center;
        }

        .slide-controls button {
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

        .slide-controls button:hover {
            background: #5a6fd8;
            transform: translateY(-2px);
        }

        .slide-percent {
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

        .target-position {
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

        .position-guide {
            display: flex;
            justify-content: space-between;
            margin-top: 5px;
            color: #666;
            font-size: 12px;
        }

        @media (max-width: 768px) {
            .container {
                flex-direction: column;
            }

            .image-section {
                border-right: none;
                border-bottom: 1px solid #eaeaea;
            }

            #slideContainer, #slideTrack {
                width: 100%;
                max-width: 400px;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <!-- 左侧验证区域 -->
    <div class="image-section">
        <div class="title">滑动验证系统</div>

        <div class="image-container">
            <div id="slideContainer">
                <img id="backgroundImage" src="" alt="滑动验证背景图">
                <div id="targetLine"></div>
                <div id="puzzlePiece"></div>
            </div>
            <div class="drag-hint">提示：拖动下方滑块移动拼图块到红色目标线位置</div>
            <div class="position-guide">
                <span>0%</span>
                <span>50%</span>
                <span>100%</span>
            </div>
        </div>

        <div class="slide-percent">
            滑动进度: <span id="slidePercent">0</span>%
        </div>

        <div id="slideTrack">
            <div id="slideSlider">→</div>
        </div>
    </div>

    <!-- 右侧控制区域 -->
    <div class="control-section">
        <div>
            <div class="section-title">验证控制</div>

            <div id="message" class="message"></div>

            <!-- 滑动验证目标 -->
            <div class="target-position">
                <strong>目标位置：<span id="targetPosition">?</span>%</strong>
                <div style="font-size: 14px; color: #666; margin-top: 5px;">
                    请将拼图块拖动到红色目标线位置
                </div>
            </div>

            <!-- 滑动验证控制 -->
            <div class="control-group">
                <div class="section-title">滑动控制</div>
                <div class="slide-controls">
                    <button onclick="moveSlider(-10)">左移10%</button>
                    <button onclick="moveSlider(-5)">左移5%</button>
                    <button onclick="moveSlider(5)">右移5%</button>
                    <button onclick="moveSlider(10)">右移10%</button>
                    <button onclick="resetSlider()">重置位置</button>
                </div>
            </div>
        </div>

        <div>
            <button class="btn" onclick="validateSlide()">确认验证</button>
            <button class="btn close-btn" onclick="goBack()">返回上一步</button>
            <button class="btn" onclick="refreshCaptcha()">刷新验证码</button>
        </div>
    </div>
</div>

<script>
    var currentUser = {
        id: <%= currentUser.getId() %>,
        username: '<%= currentUser.getUsername() %>'
    };
    // 全局变量 - 滑动验证
    let currentSlidePosition = 0;
    let targetSlidePosition = 0;
    let puzzleYPosition = 110; // 默认垂直位置
    let isSlideDragging = false;
    let startSlideX = 0;

    // 页面加载时初始化
    window.onload = function () {
        console.log('滑动验证页面加载完成');
        loadSlideCaptcha();
        setupSlideDrag();
    };

    // 加载滑动验证码
    function loadSlideCaptcha() {
        console.log('加载滑动验证码');

        const params = new URLSearchParams();
        params.append('action', 'generate');
        params.append('captchaType', 'slide');

        fetch('<%= request.getContextPath() %>/imageCaptcha', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: params
        })
            .then(response => {
                console.log('滑动验证码响应状态:', response.status);
                if (!response.ok) {
                    throw new Error('网络响应不正常: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('滑动验证码响应数据:', data);
                if (data.success) {
                    handleSlideCaptchaResponse(data);
                } else {
                    showMessage('验证码加载失败: ' + (data.error || '未知错误'), false);
                }
            })
            .catch(error => {
                console.error('滑动验证码请求错误:', error);
                showMessage('验证码加载失败: ' + error.message, false);
            });
    }

    // 处理滑动验证码响应
    function handleSlideCaptchaResponse(data) {
        targetSlidePosition = data.targetValue;
        puzzleYPosition = data.puzzleY || 110; // 使用后端返回的y坐标，默认110

        document.getElementById('targetPosition').textContent = targetSlidePosition;

        // 设置背景图和拼图块
        const background = document.getElementById('backgroundImage');
        const puzzlePiece = document.getElementById('puzzlePiece');
        const targetLine = document.getElementById('targetLine');

        background.src = data.backgroundImage;
        puzzlePiece.style.backgroundImage = `url(${data.puzzleImage})`;
        puzzlePiece.style.backgroundSize = 'cover';

        // 设置拼图块的垂直位置
        puzzlePiece.style.top = puzzleYPosition + 'px';

        // 显示目标线
        targetLine.style.display = 'block';
        updateTargetLinePosition();

        // 重置当前位置
        currentSlidePosition = 0;
        updateSlidePosition();

        console.log('滑动验证码初始化完成，目标位置: ' + targetSlidePosition + '%, Y坐标: ' + puzzleYPosition);
    }

    // 更新目标线位置
    function updateTargetLinePosition() {
        const targetLine = document.getElementById('targetLine');
        const container = document.getElementById('slideContainer');
        const containerWidth = container.offsetWidth;

        const targetPosition = (targetSlidePosition / 100) * containerWidth;
        targetLine.style.left = targetPosition + 'px';
    }

    // 设置滑动拖拽功能
    function setupSlideDrag() {
        const slider = document.getElementById('slideSlider');
        const puzzlePiece = document.getElementById('puzzlePiece');

        // 鼠标事件
        slider.addEventListener('mousedown', startSlideDrag);
        puzzlePiece.addEventListener('mousedown', startSlideDrag);
        document.addEventListener('mousemove', slideDrag);
        document.addEventListener('mouseup', stopSlideDrag);

        console.log('滑动拖拽功能已初始化');
    }

    function startSlideDrag(e) {
        e.preventDefault();
        isSlideDragging = true;
        startSlideX = e.clientX || e.touches[0].clientX;

        // 添加抓取效果
        document.getElementById('slideSlider').style.cursor = 'grabbing';
        document.getElementById('puzzlePiece').style.cursor = 'grabbing';

        console.log('开始滑动拖拽，初始位置:', currentSlidePosition);
    }

    function touchSlideStart(e) {
        e.preventDefault();
        const touch = e.touches[0];
        isSlideDragging = true;
        startSlideX = touch.clientX;

        // 添加抓取效果
        document.getElementById('slideSlider').style.cursor = 'grabbing';
        document.getElementById('puzzlePiece').style.cursor = 'grabbing';
    }

    function slideDrag(e) {
        if (!isSlideDragging) return;
        e.preventDefault();

        const clientX = e.clientX || (e.touches && e.touches[0].clientX);
        if (!clientX) return;

        const track = document.getElementById('slideTrack');
        const trackWidth = track.offsetWidth;

        // 计算移动距离和百分比
        const moveX = clientX - startSlideX;
        const movePercent = (moveX / trackWidth) * 100;

        // 更新位置
        let newPosition = currentSlidePosition + movePercent;
        newPosition = Math.max(0, Math.min(100, newPosition));

        currentSlidePosition = newPosition;
        updateSlidePosition();

        // 更新起始位置
        startSlideX = clientX;
    }


    function stopSlideDrag() {
        if (!isSlideDragging) return;
        isSlideDragging = false;

        // 恢复光标样式
        document.getElementById('slideSlider').style.cursor = 'grab';
        document.getElementById('puzzlePiece').style.cursor = 'grab';

        console.log('停止滑动拖拽，最终位置:', currentSlidePosition);
    }

    // 移动滑块
    function moveSlider(percent) {
        currentSlidePosition += percent;
        currentSlidePosition = Math.max(0, Math.min(100, currentSlidePosition));
        updateSlidePosition();

        console.log('按钮移动，位置变化:', percent, '当前位置:', currentSlidePosition);
    }

    // 重置滑块
    function resetSlider() {
        currentSlidePosition = 0;
        updateSlidePosition();

        console.log('重置位置为0%');
    }

    // 更新滑块位置
    function updateSlidePosition() {
        const slider = document.getElementById('slideSlider');
        const puzzlePiece = document.getElementById('puzzlePiece');
        const percentDisplay = document.getElementById('slidePercent');
        const track = document.getElementById('slideTrack');
        const container = document.getElementById('slideContainer');

        const trackWidth = track.offsetWidth;
        const sliderWidth = slider.offsetWidth;
        const containerWidth = container.offsetWidth;
        const puzzleWidth = puzzlePiece.offsetWidth;

        const maxTrackPosition = trackWidth - sliderWidth;
        const maxPuzzlePosition = containerWidth - puzzleWidth;

        const trackPosition = (currentSlidePosition / 100) * maxTrackPosition;
        const puzzlePosition = (currentSlidePosition / 100) * maxPuzzlePosition;

        // 更新滑块位置
        slider.style.left = trackPosition+"px";

        // 更新拼图块位置 - 保持垂直位置不变
        puzzlePiece.style.left = puzzlePosition+'px';

        // 更新百分比显示
        percentDisplay.textContent = Math.round(currentSlidePosition);

        // 添加轻微动画效果
        if (!isSlideDragging) {
            slider.style.transition = 'left 0.1s ease';
            puzzlePiece.style.transition = 'left 0.1s ease';
        } else {
            slider.style.transition = 'none';
            puzzlePiece.style.transition = 'none';
        }
    }

    // 验证滑动
    function validateSlide() {
        console.log('开始滑动验证，用户位置: ' + currentSlidePosition + '%, 目标位置: ' + targetSlidePosition + '%');

        const params = new URLSearchParams();
        params.append('action', 'validate');
        params.append('position', Math.round(currentSlidePosition));

        fetch('<%= request.getContextPath() %>/imageCaptcha', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: params
        })
            .then(response => {
                console.log('滑动验证响应状态:', response.status);
                if (!response.ok) {
                    throw new Error('网络响应不正常: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('滑动验证响应数据:', data);
                if (data.success) {
                    showMessage(data.message, true, 2000);
                    console.log('滑动验证成功，跳转到主页面');

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
                        loadSlideCaptcha();
                    }, 1000);
                }
            })
            .catch(error => {
                console.error('滑动验证错误:', error);
                showMessage('验证失败: ' + error.message, false, 3000);
            });
    }

    // 刷新验证码
    function refreshCaptcha() {
        loadSlideCaptcha();
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