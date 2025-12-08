package com.community.listener;

import com.community.exception.BusinessException;
import com.community.exception.AuthenticationException;
import com.community.exception.AuthorizationException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 请求监听器
 * 功能：
 * 1. 记录请求开始和结束时间
 * 2. 统计请求执行时间
 * 3. 记录请求计数
 * 4. 监控异常请求（支持自定义异常）
 */
@WebListener
public class RequestListener implements ServletRequestListener {

    // 请求计数器（线程安全）
    private static final AtomicLong totalRequestCount = new AtomicLong(0);
    private static final AtomicLong activeRequestCount = new AtomicLong(0);

    // 存储请求开始时间（线程安全）
    private static final Map<String, RequestContext> requestContexts = new ConcurrentHashMap<>();

    // 性能统计
    private static final Map<String, AtomicLong> uriRequestCount = new ConcurrentHashMap<>();
    private static final Map<String, AtomicLong> uriTotalTime = new ConcurrentHashMap<>();

    // 异常统计（按异常类型）
    private static final Map<Class<? extends Exception>, AtomicLong> exceptionCount = new ConcurrentHashMap<>();
    private static final AtomicLong totalExceptionCount = new AtomicLong(0);

    // HTTP状态码统计
    private static final Map<Integer, AtomicLong> statusCodeCount = new ConcurrentHashMap<>();

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        // 请求开始时记录
        ServletRequest request = sre.getServletRequest();

        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        String requestId = generateRequestId(request);

        // 创建请求上下文
        RequestContext context = new RequestContext(requestId, startTime);
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            context.setUri(httpRequest.getRequestURI());
            context.setMethod(httpRequest.getMethod());
            context.setRemoteAddr(httpRequest.getRemoteAddr());
            context.setUserAgent(httpRequest.getHeader("User-Agent"));
            context.setReferer(httpRequest.getHeader("Referer"));
            context.setSessionId(httpRequest.getSession(false) != null
                    ? httpRequest.getSession().getId() : null);
        }

        requestContexts.put(requestId, context);

        // 增加请求计数
        totalRequestCount.incrementAndGet();
        activeRequestCount.incrementAndGet();

        // 记录URI访问计数
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String uri = httpRequest.getRequestURI();

            uriRequestCount.computeIfAbsent(uri, k -> new AtomicLong(0)).incrementAndGet();

            // 设置请求ID到属性中，供其他组件使用
            request.setAttribute("requestId", requestId);
            request.setAttribute("requestStartTime", startTime);

            // 记录请求日志
            logRequestStart(context);
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        // 请求结束时统计执行时间
        ServletRequest request = sre.getServletRequest();
        String requestId = (String) request.getAttribute("requestId");

        if (requestId != null && requestContexts.containsKey(requestId)) {
            RequestContext context = requestContexts.remove(requestId);
            context.setEndTime(System.currentTimeMillis());

            // 计算执行时间
            long executionTime = context.getExecutionTime();

            // 更新活跃请求数
            activeRequestCount.decrementAndGet();

            // 记录URI执行时间
            String uri = context.getUri();
            if (uri != null) {
                uriTotalTime.computeIfAbsent(uri, k -> new AtomicLong(0))
                        .addAndGet(executionTime);
            }

            // 检查是否有异常
            Object exceptionAttr = request.getAttribute("javax.servlet.error.exception");
            Object statusCodeAttr = request.getAttribute("javax.servlet.error.status_code");

            if (exceptionAttr instanceof Exception) {
                recordException((Exception) exceptionAttr, context);
            } else if (statusCodeAttr != null) {
                int statusCode = (int) statusCodeAttr;
                if (statusCode >= 400) {
                    context.setStatusCode(statusCode);
                    recordError(statusCode, context);
                }
            }

            // 记录请求结束日志
            logRequestEnd(context);
        }
    }

    /**
     * 增加请求计数
     */
    public static void incrementRequestCount() {
        totalRequestCount.incrementAndGet();
    }

    /**
     * 记录自定义异常
     */
    public static void recordException(Exception exception, RequestContext context) {
        if (exception == null) return;

        totalExceptionCount.incrementAndGet();

        // 按异常类型统计
        Class<? extends Exception> exceptionClass = exception.getClass();
        exceptionCount.computeIfAbsent(exceptionClass, k -> new AtomicLong(0))
                .incrementAndGet();

        // 设置异常信息到上下文
        if (context != null) {
            context.setException(exception);
            context.setExceptionMessage(exception.getMessage());
        }

        // 根据异常类型设置状态码
        int statusCode = determineStatusCode(exception);
        if (statusCode > 0) {
            recordError(statusCode, context);
        }
    }

    /**
     * 记录HTTP错误状态码
     */
    public static void recordError(int statusCode, RequestContext context) {
        if (context != null) {
            context.setStatusCode(statusCode);
        }

        statusCodeCount.computeIfAbsent(statusCode, k -> new AtomicLong(0))
                .incrementAndGet();
    }

    /**
     * 根据异常类型确定HTTP状态码
     */
    private static int determineStatusCode(Exception exception) {
        if (exception instanceof BusinessException) {
            return 400; // Bad Request
        } else if (exception instanceof AuthenticationException) {
            return 401; // Unauthorized
        } else if (exception instanceof AuthorizationException) {
            return 403; // Forbidden
        } else if (exception instanceof javax.servlet.ServletException) {
            return 500; // Internal Server Error
        } else {
            return 500; // 默认
        }
    }

    /**
     * 获取请求统计信息
     */
    public static String getRequestStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== 请求统计信息 ===\n");
        stats.append(String.format("总请求数: %d\n", totalRequestCount.get()));
        stats.append(String.format("活跃请求数: %d\n", activeRequestCount.get()));
        stats.append(String.format("总异常数: %d\n", totalExceptionCount.get()));

        stats.append("异常类型统计:\n");
        exceptionCount.forEach((exceptionClass, count) -> {
            String simpleName = exceptionClass.getSimpleName();
            stats.append(String.format("  %s: %d次\n", simpleName, count.get()));
        });

        stats.append("URI访问统计:\n");
        uriRequestCount.forEach((uri, count) -> {
            AtomicLong totalTime = uriTotalTime.get(uri);
            long avgTime = (totalTime != null && count.get() > 0)
                    ? totalTime.get() / count.get()
                    : 0;
            stats.append(String.format("  %s: 访问次数=%d, 平均耗时=%dms\n",
                    uri, count.get(), avgTime));
        });

        stats.append("HTTP状态码统计:\n");
        statusCodeCount.forEach((code, count) -> {
            stats.append(String.format("  %d: %d次\n", code, count.get()));
        });

        return stats.toString();
    }

    /**
     * 获取异常统计报告
     */
    public static Map<String, Object> getExceptionReport() {
        Map<String, Object> report = new ConcurrentHashMap<>();
        report.put("totalExceptions", totalExceptionCount.get());
        report.put("exceptionByType", convertExceptionCountMap());

        // 找出最常见的异常
        if (!exceptionCount.isEmpty()) {
            Class<? extends Exception> mostCommon = null;
            long maxCount = 0;

            for (Map.Entry<Class<? extends Exception>, AtomicLong> entry : exceptionCount.entrySet()) {
                if (entry.getValue().get() > maxCount) {
                    maxCount = entry.getValue().get();
                    mostCommon = entry.getKey();
                }
            }

            if (mostCommon != null) {
                report.put("mostCommonException", mostCommon.getSimpleName());
                report.put("mostCommonExceptionCount", maxCount);
            }
        }

        return report;
    }

    /**
     * 转换异常统计Map为可序列化的格式
     */
    private static Map<String, Long> convertExceptionCountMap() {
        Map<String, Long> result = new ConcurrentHashMap<>();
        exceptionCount.forEach((key, value) -> {
            result.put(key.getSimpleName(), value.get());
        });
        return result;
    }

    /**
     * 生成请求ID
     */
    private String generateRequestId(ServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 1000);

        return String.format("%s-%d-%d-%04d",
                remoteAddr.replace(".", ""),
                timestamp,
                Thread.currentThread().getId(),
                random);
    }

    /**
     * 记录请求开始日志
     */
    private void logRequestStart(RequestContext context) {
        System.out.println(String.format(
                "[%s] [%s] [START] %s %s - From: %s, Session: %s",
                new Date(),
                context.getRequestId(),
                context.getMethod(),
                context.getUri(),
                context.getRemoteAddr(),
                context.getSessionId() != null ? context.getSessionId().substring(0, 8) + "..." : "No Session"
        ));
    }

    /**
     * 记录请求结束日志
     */
    private void logRequestEnd(RequestContext context) {
        String status = "SUCCESS";
        int statusCode = context.getStatusCode();

        if (statusCode >= 400) {
            status = "ERROR";
        } else if (context.getException() != null) {
            status = "EXCEPTION";
        }

        StringBuilder logMessage = new StringBuilder();
        logMessage.append(String.format(
                "[%s] [%s] [END] %s %s - Status: %d (%s), Time: %dms",
                new Date(),
                context.getRequestId(),
                context.getMethod(),
                context.getUri(),
                statusCode,
                status,
                context.getExecutionTime()
        ));

        if (context.getException() != null) {
            logMessage.append(String.format(", Exception: %s - %s",
                    context.getException().getClass().getSimpleName(),
                    context.getExceptionMessage()
            ));
        }

        System.out.println(logMessage.toString());
    }

    /**
     * 请求上下文内部类
     */
    public static class RequestContext {
        private final String requestId;
        private final long startTime;
        private long endTime;
        private String uri;
        private String method;
        private String remoteAddr;
        private String userAgent;
        private String referer;
        private String sessionId;
        private Exception exception;
        private String exceptionMessage;
        private int statusCode = 200;

        public RequestContext(String requestId, long startTime) {
            this.requestId = requestId;
            this.startTime = startTime;
        }

        public long getExecutionTime() {
            return endTime - startTime;
        }

        // Getters and Setters
        public String getRequestId() { return requestId; }
        public long getStartTime() { return startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        public String getUri() { return uri; }
        public void setUri(String uri) { this.uri = uri; }
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public String getRemoteAddr() { return remoteAddr; }
        public void setRemoteAddr(String remoteAddr) { this.remoteAddr = remoteAddr; }
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        public String getReferer() { return referer; }
        public void setReferer(String referer) { this.referer = referer; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public Exception getException() { return exception; }
        public void setException(Exception exception) { this.exception = exception; }
        public String getExceptionMessage() { return exceptionMessage; }
        public void setExceptionMessage(String exceptionMessage) { this.exceptionMessage = exceptionMessage; }
        public int getStatusCode() { return statusCode; }
        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    }
}