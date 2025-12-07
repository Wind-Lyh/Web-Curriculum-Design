package com.community.dao;

import com.community.model.PointsRecord;
import java.util.List;
import java.util.Map;

public interface PointsDao {
    int insert(PointsRecord record);
    List<PointsRecord> findByUserId(int userId, int page, int size);
    int getTotalPoints(int userId);
    List<Map<String, Object>> getRanking(int limit);
}