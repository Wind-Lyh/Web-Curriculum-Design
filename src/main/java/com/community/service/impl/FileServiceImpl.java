package com.community.service.impl;

import com.community.dao.AttachmentDao;
import com.community.dao.UserDao;
import com.community.model.Attachment;
import com.community.model.User;
import com.community.service.FileService;
import com.community.util.StringUtil;
import com.community.exception.BusinessException;
import com.community.exception.AuthorizationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletContext;

public class FileServiceImpl implements FileService {

    private AttachmentDao attachmentDao;
    private UserDao userDao;

    // 文件存储根目录
    private String uploadBaseDir;

    // 允许的文件类型
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif"};
    private static final String[] ALLOWED_FILE_TYPES = {"image/jpeg", "image/png", "image/gif",
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"};

    // 文件大小限制（10MB）
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public FileServiceImpl() {
        // 构造函数
    }

    /**
     * 设置ServletContext，初始化上传目录
     * @param context ServletContext
     */
    public void setServletContext(ServletContext context) {
        // 从web.xml中获取uploadPath配置，默认值为uploads
        String uploadPath = context.getInitParameter("uploadPath");
        if (uploadPath == null) {
            uploadPath = "uploads";
        }

        this.uploadBaseDir = context.getRealPath("/") + uploadPath;

        // 创建上传目录
        File uploadDir = new File(uploadBaseDir);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 创建头像子目录
        File avatarDir = new File(uploadBaseDir + File.separator + "avatars");
        if (!avatarDir.exists()) {
            avatarDir.mkdirs();
        }

        // 创建帖子附件子目录
        File postDir = new File(uploadBaseDir + File.separator + "posts");
        if (!postDir.exists()) {
            postDir.mkdirs();
        }

        // 创建评论附件子目录
        File commentDir = new File(uploadBaseDir + File.separator + "comments");
        if (!commentDir.exists()) {
            commentDir.mkdirs();
        }

        System.out.println("文件上传目录已初始化: " + uploadBaseDir);
    }

    // 依赖注入方法
    public void setAttachmentDao(AttachmentDao attachmentDao) {
        this.attachmentDao = attachmentDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Attachment handleUpload(Integer userId, Integer postId, Integer commentId,
                                   String fileName, long fileSize, String fileType,
                                   InputStream inputStream) throws BusinessException {
        // 验证参数
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        if (StringUtil.isEmpty(fileName)) {
            throw new BusinessException("文件名不能为空");
        }

        if (fileSize <= 0) {
            throw new BusinessException("文件大小无效");
        }

        // 检查文件大小
        if (fileSize > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过10MB");
        }

        // 验证文件类型
        if (!isAllowedFileType(fileType)) {
            throw new BusinessException("不支持的文件类型: " + fileType);
        }

        try {
            // 生成唯一文件名
            String fileExtension = getFileExtension(fileName);
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;

            // 确定存储目录和路径
            String subDir = "";
            if (postId != null) {
                subDir = "posts" + File.separator;
            } else if (commentId != null) {
                subDir = "comments" + File.separator;
            }

            String relativePath = subDir + uniqueFileName;
            String filePath = uploadBaseDir + File.separator + relativePath;

            // 保存文件到磁盘
            saveFileToDisk(inputStream, filePath);

            // 创建Attachment对象
            Attachment attachment = new Attachment();
            attachment.setFilename(fileName);
            attachment.setFilePath(relativePath);
            attachment.setFileType(fileType);
            attachment.setFileSize(fileSize);
            attachment.setUserId(userId);
            attachment.setPostId(postId);
            attachment.setCommentId(commentId);
            attachment.setCreateTime(new Date());

            // 保存到数据库
            int result = attachmentDao.insert(attachment);
            if (result <= 0) {
                // 如果数据库保存失败，删除已上传的文件
                deleteFileFromDisk(filePath);
                throw new BusinessException("文件信息保存失败");
            }

            return attachment;

        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public Attachment handleDownload(Integer fileId, OutputStream outputStream) throws BusinessException {
        if (fileId == null) {
            throw new BusinessException("文件ID不能为空");
        }

        FileInputStream fis = null;

        try {
            // 获取附件信息
            Attachment attachment = attachmentDao.findById(fileId);
            if (attachment == null) {
                throw new BusinessException("文件不存在");
            }

            // 构建完整文件路径
            String filePath = uploadBaseDir + File.separator + attachment.getFilePath();
            File file = new File(filePath);

            if (!file.exists()) {
                throw new BusinessException("文件不存在或已被删除");
            }

            // 读取文件并写入输出流
            fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();

            return attachment;

        } catch (IOException e) {
            throw new BusinessException("文件下载失败: " + e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean handleDeleteFile(Integer fileId, Integer userId) throws BusinessException {
        if (fileId == null) {
            throw new BusinessException("文件ID不能为空");
        }

        try {
            // 获取附件信息
            Attachment attachment = attachmentDao.findById(fileId);
            if (attachment == null) {
                throw new BusinessException("文件不存在");
            }

            // 验证权限：只有文件上传者或管理员可以删除
            if (!attachment.getUserId().equals(userId)) {
                // 检查是否是管理员
                User user = userDao.findById(userId);
                if (user == null || user.getIs_admin() != 1) {
                    throw new AuthorizationException("没有权限删除此文件");
                }
            }

            // 构建完整文件路径
            String filePath = uploadBaseDir + File.separator + attachment.getFilePath();

            // 删除物理文件
            deleteFileFromDisk(filePath);

            // 删除数据库记录
            int result = attachmentDao.delete(fileId);
            return result > 0;

        } catch (IOException e) {
            throw new BusinessException("文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public Attachment handleAvatarUpload(Integer userId, String fileName, long fileSize,
                                         String fileType, InputStream inputStream) throws BusinessException {
        // 验证参数
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 验证是否为图片类型
        if (!isImageFileType(fileType)) {
            throw new BusinessException("头像必须是图片格式（JPEG、PNG、GIF）");
        }

        // 检查文件大小
        if (fileSize > MAX_FILE_SIZE) {
            throw new BusinessException("头像大小不能超过10MB");
        }

        try {
            // 生成唯一文件名
            String fileExtension = getFileExtension(fileName);
            String uniqueFileName = "avatar_" + userId + "_" + UUID.randomUUID().toString() + "." + fileExtension;

            // 头像存储路径
            String relativePath = "avatars" + File.separator + uniqueFileName;
            String filePath = uploadBaseDir + File.separator + relativePath;

            // 保存文件到磁盘
            saveFileToDisk(inputStream, filePath);

            // 创建Attachment对象
            Attachment attachment = new Attachment();
            attachment.setFilename(fileName);
            attachment.setFilePath(relativePath);
            attachment.setFileType(fileType);
            attachment.setFileSize(fileSize);
            attachment.setUserId(userId);
            attachment.setCreateTime(new Date());

            // 保存到数据库
            int result = attachmentDao.insert(attachment);
            if (result <= 0) {
                // 如果数据库保存失败，删除已上传的文件
                deleteFileFromDisk(filePath);
                throw new BusinessException("头像信息保存失败");
            }

            // 更新用户的avatar_url字段
            userDao.updateAvatar(userId, relativePath);

            return attachment;

        } catch (IOException e) {
            throw new BusinessException("头像上传失败: " + e.getMessage());
        }
    }

    @Override
    public List<Attachment> getAttachmentsByPost(Integer postId) throws BusinessException {
        if (postId == null) {
            throw new BusinessException("帖子ID不能为空");
        }

        try {
            return attachmentDao.findByPostId(postId);
        } catch (Exception e) {
            throw new BusinessException("获取帖子附件失败: " + e.getMessage());
        }
    }

    @Override
    public List<Attachment> getAttachmentsByComment(Integer commentId) throws BusinessException {
        if (commentId == null) {
            throw new BusinessException("评论ID不能为空");
        }

        try {
            return attachmentDao.findByCommentId(commentId);
        } catch (Exception e) {
            throw new BusinessException("获取评论附件失败: " + e.getMessage());
        }
    }

    @Override
    public Attachment getAttachmentById(Integer fileId) throws BusinessException {
        if (fileId == null) {
            throw new BusinessException("文件ID不能为空");
        }

        try {
            Attachment attachment = attachmentDao.findById(fileId);
            if (attachment == null) {
                throw new BusinessException("文件不存在");
            }
            return attachment;
        } catch (Exception e) {
            throw new BusinessException("获取附件信息失败: " + e.getMessage());
        }
    }

    /**
     * 保存文件到磁盘
     * @param inputStream 文件输入流
     * @param filePath 文件路径
     * @throws IOException
     */
    private void saveFileToDisk(InputStream inputStream, String filePath) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.flush();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从磁盘删除文件
     * @param filePath 文件路径
     * @throws IOException
     */
    private void deleteFileFromDisk(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("无法删除文件: " + filePath);
            }
        }
    }

    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @return 文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    /**
     * 验证文件类型是否允许
     * @param fileType 文件类型
     * @return 是否允许
     */
    private boolean isAllowedFileType(String fileType) {
        if (StringUtil.isEmpty(fileType)) {
            return false;
        }

        for (String allowedType : ALLOWED_FILE_TYPES) {
            if (allowedType.equalsIgnoreCase(fileType)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 验证是否为图片文件类型
     * @param fileType 文件类型
     * @return 是否为图片
     */
    private boolean isImageFileType(String fileType) {
        if (StringUtil.isEmpty(fileType)) {
            return false;
        }

        for (String imageType : ALLOWED_IMAGE_TYPES) {
            if (imageType.equalsIgnoreCase(fileType)) {
                return true;
            }
        }

        return false;
    }
}