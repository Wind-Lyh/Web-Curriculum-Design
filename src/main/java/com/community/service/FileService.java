package com.community.service;

import com.community.model.Attachment;
import com.community.exception.BusinessException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface FileService {

    /**
     * 处理文件上传
     * @param userId 用户ID
     * @param postId 帖子ID（可选，可为null）
     * @param commentId 评论ID（可选，可为null）
     * @param fileName 原始文件名
     * @param fileSize 文件大小
     * @param fileType 文件类型
     * @param inputStream 文件输入流
     * @return 上传的附件信息
     * @throws BusinessException 业务异常
     */
    Attachment handleUpload(Integer userId, Integer postId, Integer commentId,
                            String fileName, long fileSize, String fileType,
                            InputStream inputStream) throws BusinessException;

    /**
     * 处理文件下载
     * @param fileId 文件ID
     * @param outputStream 输出流，用于写入文件数据
     * @return 文件信息（文件名、类型等）
     * @throws BusinessException 业务异常
     */
    Attachment handleDownload(Integer fileId, OutputStream outputStream) throws BusinessException;

    /**
     * 处理文件删除
     * @param fileId 文件ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否删除成功
     * @throws BusinessException 业务异常
     */
    boolean handleDeleteFile(Integer fileId, Integer userId) throws BusinessException;

    /**
     * 专门处理头像上传
     * @param userId 用户ID
     * @param fileName 原始文件名
     * @param fileSize 文件大小
     * @param fileType 文件类型
     * @param inputStream 文件输入流
     * @return 上传的附件信息
     * @throws BusinessException 业务异常
     */
    Attachment handleAvatarUpload(Integer userId, String fileName, long fileSize,
                                  String fileType, InputStream inputStream) throws BusinessException;

    /**
     * 获取帖子的附件列表
     * @param postId 帖子ID
     * @return 附件列表
     * @throws BusinessException 业务异常
     */
    List<Attachment> getAttachmentsByPost(Integer postId) throws BusinessException;

    /**
     * 获取评论的附件列表
     * @param commentId 评论ID
     * @return 附件列表
     * @throws BusinessException 业务异常
     */
    List<Attachment> getAttachmentsByComment(Integer commentId) throws BusinessException;

    /**
     * 根据ID获取附件信息
     * @param fileId 文件ID
     * @return 附件信息
     * @throws BusinessException 业务异常
     */
    Attachment getAttachmentById(Integer fileId) throws BusinessException;
}