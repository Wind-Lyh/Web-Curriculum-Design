package com.community.service;

import com.community.model.BrowseHistory;
import java.util.List;

public interface UserProfileService {
    /**
     * 更新个人签名
     * @param userId 用户ID
     * @param signature 签名内容
     * @return 是否成功
     */
    boolean updateSignature(int userId, String signature);

    /**
     * 添加浏览记录
     * @param userId 用户ID
     * @param postId 帖子ID
     */
    void addBrowseHistory(int userId, int postId);

    /**
     * 获取浏览历史
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 浏览记录列表
     */
    List<BrowseHistory> getBrowseHistory(int userId, int page, int pageSize);

    /**
     * 清空浏览历史
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean clearBrowseHistory(int userId);
}