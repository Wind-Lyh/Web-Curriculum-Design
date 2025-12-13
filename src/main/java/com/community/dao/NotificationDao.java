package com.community.dao;

import com.community.model.Notification;
import java.util.List;
import java.util.Map;

public interface NotificationDao {
    // 1. 创建个人通知
    boolean insertNotification(Notification notification);

    // 2. 通知查询 - 获取用户所有通知
    List<Notification> getNotificationsByUserId(Integer userId);

    // 2.1 按类型查询通知
    List<Notification> getNotificationsByType(Integer userId, String type);

    // 2.2 查询未读通知
    List<Notification> getUnreadNotifications(Integer userId);

    // 3. 标记为已读
    boolean markAsRead(Integer notificationId);

    // 3.1 批量标记已读（用户所有通知）
    boolean markAllAsRead(Integer userId);

    // 3.2 按类型批量标记已读
    boolean markTypeAsRead(Integer userId, String type);

    // 4. 未读数统计
    Integer countUnreadNotifications(Integer userId);

    // 4.1 按类型统计未读数
    Map<String, Integer> countUnreadByType(Integer userId);

    // 5. 分页查询
    List<Notification> getNotificationsByPage(Integer userId, int page, int pageSize);

    // 5.1 获取用户通知总数
    int countUserNotifications(Integer userId);

    // 5.2 获取用户未读通知分页
    List<Notification> getUnreadNotificationsByPage(Integer userId, int page, int pageSize);

    /**
     * 根据ID获取通知
     * @param notificationId 通知ID
     * @return 通知对象
     */
    Notification getNotificationById(int notificationId);

    /**
     * 按类型分页查询通知
     * @param userId 用户ID
     * @param type 通知类型
     * @param offset 偏移量
     * @param limit 限制数
     * @return 通知列表
     */
    List<Notification> getNotificationsByType(int userId, String type, int offset, int limit);

    /**
     * 删除通知
     * @param notificationId 通知ID
     * @return 影响的行数
     */
    int deleteNotification(int notificationId);
}