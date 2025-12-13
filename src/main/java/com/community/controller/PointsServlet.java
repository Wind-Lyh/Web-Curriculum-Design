package com.community.controller;

import com.community.model.User;
import com.community.service.PointsService;
import com.community.service.impl.PointsServiceImpl;
import com.community.dao.impl.PointsDaoImpl;
import com.community.dao.impl.UserDaoImpl;
import com.community.dao.impl.VirtualGoodDaoImpl;
import com.community.dao.impl.ExchangeDaoImpl;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class PointsServlet extends HttpServlet {

    private PointsService pointsService;

    @Override
    public void init() throws ServletException {
        super.init();
        // 初始化PointsService
        pointsService = new PointsServiceImpl(
                new PointsDaoImpl(),
                new UserDaoImpl(),
                new VirtualGoodDaoImpl(),
                new ExchangeDaoImpl()
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            sendError(response, "INVALID_ACTION", "无效的请求");
            return;
        }

        try {
            switch (pathInfo) {
                case "/show":
                    showPoints(request, response);
                    break;
                case "/ranking":
                    showRanking(request, response);
                    break;
                case "/exchange":
                    showExchange(request, response);
                    break;
                default:
                    sendError(response, "INVALID_ACTION", "无效的请求");
                    break;
            }
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            sendError(response, "INVALID_ACTION", "无效的操作类型");
            return;
        }

        try {
            if ("exchange".equals(action)) {
                handleExchange(request, response);
            } else {
                sendError(response, "INVALID_ACTION", "无效的操作类型");
            }
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    // 显示积分详情
    private void showPoints(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        int points = pointsService.getUserPoints(user.getId());
        int level = pointsService.getUserLevel(user.getId());
        double progress = pointsService.calculateLevelProgress(user.getId());

        request.setAttribute("points", points);
        request.setAttribute("level", level);
        request.setAttribute("progress", progress);

        request.getRequestDispatcher("/WEB-INF/views/points/show.jsp").forward(request, response);
    }

    // 显示积分排行榜
    private void showRanking(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String limitParam = request.getParameter("limit");
        int limit = 10; // 默认显示前10名

        if (limitParam != null && !limitParam.trim().isEmpty()) {
            limit = Integer.parseInt(limitParam);
        }

        List<User> ranking = pointsService.getRanking(limit);

        request.setAttribute("ranking", ranking);
        request.setAttribute("limit", limit);

        request.getRequestDispatcher("/WEB-INF/views/points/ranking.jsp").forward(request, response);
    }

    // 显示积分兑换页面
    private void showExchange(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/user/login");
            return;
        }

        // 获取所有可兑换的道具
        // 这里需要VirtualGoodService，暂时简化处理
        request.getRequestDispatcher("/WEB-INF/views/points/exchange.jsp").forward(request, response);
    }

    // 处理兑换请求
    private void handleExchange(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");

        String goodsIdParam = request.getParameter("goodsId");
        String quantityParam = request.getParameter("quantity");

        if (goodsIdParam == null || goodsIdParam.trim().isEmpty()) {
            sendError(response, "GOODS_ID_EMPTY", "道具ID不能为空");
            return;
        }

        if (quantityParam == null || quantityParam.trim().isEmpty()) {
            sendError(response, "QUANTITY_EMPTY", "兑换数量不能为空");
            return;
        }

        int goodsId = Integer.parseInt(goodsIdParam);
        int quantity = Integer.parseInt(quantityParam);

        try {
            com.community.model.Exchange exchange = pointsService.exchangeGoods(
                    user.getId(), goodsId, quantity
            );

            sendResponse(response, "兑换成功", createExchangeJson(exchange));
        } catch (Exception e) {
            sendError(response, "EXCHANGE_FAILED", e.getMessage());
        }
    }

    // 构建兑换记录JSON
    private String createExchangeJson(com.community.model.Exchange exchange) {
        if (exchange == null) {
            return "null";
        }

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(exchange.getId()).append(",");
        json.append("\"userId\":").append(exchange.getUserId()).append(",");
        json.append("\"goodsId\":").append(exchange.getGoodsId()).append(",");
        json.append("\"quantity\":").append(exchange.getQuantity()).append(",");
        json.append("\"totalCost\":").append(exchange.getTotalCost()).append(",");
        json.append("\"createTime\":\"").append(exchange.getCreateTime()).append("\"");
        json.append("}");

        return json.toString();
    }

    // 发送成功响应
    private void sendResponse(HttpServletResponse response, String message, String data)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"success\":true,");
        json.append("\"message\":\"").append(escapeJson(message)).append("\"");
        if (data != null) {
            json.append(",\"data\":").append(data);
        }
        json.append("}");

        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }

    // 发送错误响应
    private void sendError(HttpServletResponse response, String errorCode, String message)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"success\":false,");
        json.append("\"errorCode\":\"").append(escapeJson(errorCode)).append("\",");
        json.append("\"message\":\"").append(escapeJson(message)).append("\"");
        json.append("}");

        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }

    // 处理异常
    private void handleError(HttpServletResponse response, Exception e) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (e instanceof IllegalArgumentException) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(response, "INVALID_PARAM", e.getMessage());
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(response, "INTERNAL_ERROR", "服务器内部错误");
            e.printStackTrace();
        }
    }

    // 转义JSON字符串
    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }

        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}