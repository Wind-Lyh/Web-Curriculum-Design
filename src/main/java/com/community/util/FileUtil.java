package com.community.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件处理工具类
 * 负责文件上传、下载、删除等操作
 */
public class FileUtil {

    // 文件上传根目录（相对于web应用根目录）
    public static final String UPLOAD_BASE_PATH = "uploads/";

    // 头像上传目录
    public static final String AVATAR_PATH = UPLOAD_BASE_PATH + "avatars/";

    // 帖子附件目录
    public static final String POST_PATH = UPLOAD_BASE_PATH + "posts/";

    // 允许的文件类型（MIME类型）
    public static final String[] ALLOWED_IMAGE_TYPES = {
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    };

    // 允许的文件扩展名
    public static final String[] ALLOWED_IMAGE_EXTENSIONS = {
            ".jpg", ".jpeg", ".png", ".gif"
    };

    /**
     * 创建上传目录（如果不存在）
     * @param realPath 服务器实际路径
     */
    public static void createUploadDirs(String realPath) {
        File baseDir = new File(realPath + UPLOAD_BASE_PATH);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        File avatarDir = new File(realPath + AVATAR_PATH);
        if (!avatarDir.exists()) {
            avatarDir.mkdirs();
        }

        File postDir = new File(realPath + POST_PATH);
        if (!postDir.exists()) {
            postDir.mkdirs();
        }
    }

    /**
     * 生成唯一的文件名
     * @param originalFileName 原始文件名
     * @return 唯一的文件名
     */
    public static String generateUniqueFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isEmpty()) {
            return UUID.randomUUID().toString();
        }

        // 获取文件扩展名
        String extension = getFileExtension(originalFileName);

        // 生成时间戳+随机数的文件名
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timestamp = sdf.format(new Date());
        String random = UUID.randomUUID().toString().substring(0, 8);

        return timestamp + "_" + random + extension;
    }

    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @return 扩展名（包含点），如：.jpg
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }

    /**
     * 验证文件类型是否为允许的图片类型
     * @param contentType MIME类型
     * @param fileName 文件名
     * @return 验证通过返回true，否则返回false
     */
    public static boolean isValidImageType(String contentType, String fileName) {
        if (contentType == null || fileName == null) {
            return false;
        }

        // 检查MIME类型
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equalsIgnoreCase(contentType)) {
                return true;
            }
        }

        // 检查文件扩展名
        String extension = getFileExtension(fileName);
        for (String allowedExt : ALLOWED_IMAGE_EXTENSIONS) {
            if (allowedExt.equalsIgnoreCase(extension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 保存上传的文件
     * @param inputStream 文件输入流
     * @param savePath 保存路径
     * @param fileName 文件名
     * @return 保存成功返回true，否则返回false
     */
    public static boolean saveFile(InputStream inputStream, String savePath, String fileName) {
        OutputStream outputStream = null;

        try {
            // 确保目录存在
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 创建文件输出流
            File file = new File(savePath + File.separator + fileName);
            outputStream = new FileOutputStream(file);

            // 缓冲区
            byte[] buffer = new byte[4096];
            int bytesRead;

            // 读取并写入文件
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            return true;
        } catch (IOException e) {
            System.err.println("保存文件失败: " + e.getMessage());
            return false;
        } finally {
            // 关闭流
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                System.err.println("关闭文件流失败: " + e.getMessage());
            }
        }
    }

    /**
     * 删除文件
     * @param filePath 文件路径
     * @return 删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 获取文件大小（MB）
     * @param filePath 文件路径
     * @return 文件大小（MB），如果文件不存在返回0
     */
    public static double getFileSizeMB(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            long sizeInBytes = file.length();
            return sizeInBytes / (1024.0 * 1024.0);
        }
        return 0;
    }

    /**
     * 复制文件
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @return 复制成功返回true，否则返回false
     */
    public static boolean copyFile(String sourcePath, String destPath) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            File sourceFile = new File(sourcePath);
            File destFile = new File(destPath);

            // 确保目标目录存在
            File destDir = destFile.getParentFile();
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            inputStream = new FileInputStream(sourceFile);
            outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            return true;
        } catch (IOException e) {
            System.err.println("复制文件失败: " + e.getMessage());
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                System.err.println("关闭文件流失败: " + e.getMessage());
            }
        }
    }

    /**
     * 读取文件为字节数组
     * @param filePath 文件路径
     * @return 字节数组，如果文件不存在返回null
     */
    public static byte[] readFileToBytes(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return null;
        }

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputStream.read(buffer);
            return buffer;
        } catch (IOException e) {
            System.err.println("读取文件失败: " + e.getMessage());
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                System.err.println("关闭文件流失败: " + e.getMessage());
            }
        }
    }
}