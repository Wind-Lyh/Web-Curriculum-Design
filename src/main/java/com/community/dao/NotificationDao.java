package com.community.dao;

import com.community.model.Notification;
import java.util.List;

public interface NotificationDao {
    int insert(Notification notification);
    List<Notification> findByUserId(int userId, int page, int size);
    int countUnread(int userId);
    int markAsRead(int id);
    int markAllAsRead(int userId);
}