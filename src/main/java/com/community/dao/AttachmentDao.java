package com.community.dao;

import com.community.model.Attachment;
import java.util.List;

public interface AttachmentDao {

    /**
     * 插入附件记录
     * @param attachment 附件对象
     * @return 插入的行数
     */
    int insert(Attachment attachment);

    /**
     * 根据ID查询附件
     * @param id 附件ID
     * @return 附件对象
     */
    Attachment findById(Integer id);

    /**
     * 根据帖子ID查询附件列表
     * @param postId 帖子ID
     * @return 附件列表
     */
    List<Attachment> findByPostId(Integer postId);

    /**
     * 根据评论ID查询附件列表
     * @param commentId 评论ID
     * @return 附件列表
     */
    List<Attachment> findByCommentId(Integer commentId);

    /**
     * 根据用户ID查询附件列表
     * @param userId 用户ID
     * @return 附件列表
     */
    List<Attachment> findByUserId(Integer userId);

    /**
     * 删除附件
     * @param id 附件ID
     * @return 删除的行数
     */
    int delete(Integer id);

    /**
     * 根据用户和帖子删除附件
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 删除的行数
     */
    int deleteByUserAndPost(Integer userId, Integer postId);

    /**
     * 根据用户和评论删除附件
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 删除的行数
     */
    int deleteByUserAndComment(Integer userId, Integer commentId);

    /**
     * 统计帖子的附件数量
     * @param postId 帖子ID
     * @return 附件数量
     */
    int countByPost(Integer postId);

    /**
     * 统计评论的附件数量
     * @param commentId 评论ID
     * @return 附件数量
     */
    int countByComment(Integer commentId);
}