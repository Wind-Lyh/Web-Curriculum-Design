package com.community.dao;

import com.community.model.Attachment;
import java.util.List;

public interface FileDao {
    int insert(Attachment attachment);
    Attachment findById(int id);
    List<Attachment> findByPostId(int postId);
    List<Attachment> findByCommentId(int commentId);
    int delete(int id);
}