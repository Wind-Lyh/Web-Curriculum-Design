package com.community.service;

import com.community.model.Notification;
import java.util.List;

public interface NotificationService {
    /**
     * 获取用户所有通知
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 通知列表
     */
    List<Notification> getNotificationsByUserId(int userId, int page, int pageSize);

    /**
     * 获取用户未读通知
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 未读通知列表
     */
    List<Notification> getUnreadNotifications(int userId, int page, int pageSize);

    /**
     * 标记通知为已读
     * @param notificationId 通知ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAsRead(int notificationId, int userId);

    /**
     * 批量标记通知为已读
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAllAsRead(int userId);

    /**
     * 获取未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    int countUnreadNotifications(int userId);
}