package com.community.controller;

import com.community.util.FileUtil;
import com.community.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;

@MultipartConfig(
        maxFileSize = 10485760, // 10MB
        maxRequestSize = 20971520, // 20MB
        fileSizeThreshold = 5242880 // 5MB
)
public class FileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            sendError(response, "INVALID_ACTION", "无效的请求");
            return;
        }

        try {
            if ("/download".equals(pathInfo)) {
                handleDownload(request, response);
            } else {
                sendError(response, "INVALID_ACTION", "无效的请求");
            }
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            sendError(response, "INVALID_ACTION", "无效的操作类型");
            return;
        }

        try {
            switch (pathInfo) {
                case "/upload":
                    handleUpload(request, response);
                    break;
                case "/avatar":
                    handleAvatarUpload(request, response);
                    break;
                case "/delete":
                    handleDeleteFile(request, response);
                    break;
                default:
                    sendError(response, "INVALID_ACTION", "无效的操作类型");
                    break;
            }
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    // 处理文件上传
    private void handleUpload(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");

        Part filePart = request.getPart("file");
        if (filePart == null || filePart.getSize() == 0) {
            sendError(response, "FILE_EMPTY", "请选择要上传的文件");
            return;
        }

        // 验证文件类型
        String fileName = filePart.getSubmittedFileName();
        String fileExtension = FileUtil.getFileExtension(fileName);

        if (!FileUtil.isValidImageType(fileExtension,fileName)) {
            sendError(response, "INVALID_FILE_TYPE", "不支持的文件类型");
            return;
        }

        // 生成唯一文件名
        String uniqueFileName = FileUtil.generateUniqueFileName(fileExtension);

        // 获取上传目录
        String uploadDir = getServletContext().getRealPath("/") + "uploads/";
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        // 保存文件
        String filePath = uploadDir + uniqueFileName;
        filePart.write(filePath);

        // 返回文件信息
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"fileName\":\"").append(uniqueFileName).append("\",");
        json.append("\"originalName\":\"").append(fileName).append("\",");
        json.append("\"fileSize\":").append(filePart.getSize()).append(",");
        json.append("\"filePath\":\"").append("/uploads/").append(uniqueFileName).append("\"");
        json.append("}");

        sendResponse(response, "上传成功", json.toString());
    }

    // 处理头像上传
    private void handleAvatarUpload(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");

        Part filePart = request.getPart("avatar");
        if (filePart == null || filePart.getSize() == 0) {
            sendError(response, "FILE_EMPTY", "请选择头像文件");
            return;
        }

        // 验证文件类型（只能是图片）
        String fileName = filePart.getSubmittedFileName();
        String fileExtension = FileUtil.getFileExtension(fileName);

        if (!FileUtil.isValidImageType(fileExtension,fileName)) {
            sendError(response, "INVALID_FILE_TYPE", "头像必须是图片文件");
            return;
        }

        // 验证文件大小（头像不能太大）
        if (filePart.getSize() > 2 * 1024 * 1024) { // 2MB
            sendError(response, "FILE_TOO_LARGE", "头像文件不能超过2MB");
            return;
        }

        // 生成唯一文件名
        String uniqueFileName = "avatar_" + user.getId() + "_" +
                UUID.randomUUID().toString().substring(0, 8) + fileExtension;

        // 获取上传目录
        String uploadDir = getServletContext().getRealPath("/") + "uploads/avatars/";
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        // 保存文件
        String filePath = uploadDir + uniqueFileName;
        filePart.write(filePath);

        // 更新用户头像路径
        String avatarUrl = "/uploads/avatars/" + uniqueFileName;

        // 这里需要调用UserService更新头像，暂时简化处理
        // userService.updateAvatar(user.getId(), avatarUrl);

        // 更新session中的用户信息
        user.setAvatarUrl(avatarUrl);
        session.setAttribute("user", user);

        sendResponse(response, "头像上传成功", "{\"avatarUrl\":\"" + avatarUrl + "\"}");
    }

    // 处理文件下载
    private void handleDownload(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String fileName = request.getParameter("fileName");
        if (fileName == null || fileName.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "文件名不能为空");
            return;
        }

        // 防止目录遍历攻击
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "非法文件名");
            return;
        }

        // 获取文件路径
        String filePath = getServletContext().getRealPath("/") + "uploads/" + fileName;
        File file = new File(filePath);

        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
            return;
        }

        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentLength((int) file.length());

        // 读取文件并写入响应
        try (InputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    // 处理文件删除
    private void handleDeleteFile(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, "NOT_LOGIN", "请先登录");
            return;
        }

        User user = (User) session.getAttribute("user");

        String fileName = request.getParameter("fileName");
        if (fileName == null || fileName.trim().isEmpty()) {
            sendError(response, "FILE_NAME_EMPTY", "文件名不能为空");
            return;
        }

        // 防止目录遍历攻击
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            sendError(response, "INVALID_FILE_NAME", "非法文件名");
            return;
        }

        // 获取文件路径
        String filePath = getServletContext().getRealPath("/") + "uploads/" + fileName;
        File file = new File(filePath);

        if (!file.exists()) {
            sendError(response, "FILE_NOT_FOUND", "文件不存在");
            return;
        }

        // 检查权限（这里简化处理，实际应该检查文件所属用户）
        // 只有管理员或文件上传者才能删除文件

        boolean success = file.delete();

        if (success) {
            sendResponse(response, "文件删除成功", null);
        } else {
            sendError(response, "DELETE_FAILED", "文件删除失败");
        }
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
        } else if (e instanceof SecurityException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            sendError(response, "NO_PERMISSION", e.getMessage());
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