package com.community.service.impl;

import com.community.dao.NotificationDao;
import com.community.model.Notification;
import com.community.service.NotificationService;
import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    private NotificationDao notificationDao;

    public NotificationServiceImpl(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    @Override
    public List<Notification> getNotificationsByUserId(int userId, int page, int pageSize) {
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数错误");
        }

        int offset = (page - 1) * pageSize;
        return notificationDao.getNotificationsByPage(userId, offset, pageSize);
    }

    @Override
    public List<Notification> getUnreadNotifications(int userId, int page, int pageSize) {
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数错误");
        }

        int offset = (page - 1) * pageSize;
        return notificationDao.getUnreadNotificationsByPage(userId, offset, pageSize);
    }

    @Override
    public boolean markAsRead(int notificationId, int userId) {
        // 先检查通知是否存在且属于该用户
        Notification notification = notificationDao.getNotificationById(notificationId);
        if (notification == null) {
            throw new IllegalArgumentException("通知不存在");
        }

        if (notification.getUserId() != userId) {
            throw new SecurityException("无权操作此通知");
        }

        int result = notificationDao.markAsRead(notificationId);
        return result > 0;
    }

    @Override
    public boolean markAllAsRead(int userId) {
        int result = notificationDao.markAllAsRead(userId);
        return result > 0;
    }

    @Override
    public int countUnreadNotifications(int userId) {
        return notificationDao.countUnreadNotifications(userId);
    }

    /**
     * 发送通知（扩展方法）
     * @param notification 通知对象
     * @return 是否成功
     */
    public boolean sendNotification(Notification notification) {
        if (notification == null || notification.getUserId() == null) {
            throw new IllegalArgumentException("通知信息不完整");
        }

        int result = notificationDao.insertNotification(notification);
        return result > 0;
    }

    /**
     * 获取按类型分组的通知（扩展方法）
     * @param userId 用户ID
     * @param type 通知类型
     * @param page 页码
     * @param pageSize 每页大小
     * @return 通知列表
     */
    public List<Notification> getNotificationsByType(int userId, String type, int page, int pageSize) {
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("分页参数错误");
        }

        int offset = (page - 1) * pageSize;
        return notificationDao.getNotificationsByType(userId, type, offset, pageSize);
    }
}