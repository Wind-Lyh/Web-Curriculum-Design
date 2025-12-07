package com.community.dao;

import com.community.model.PointsRecord;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PointsDao {

    // 1.1 新增积分记录 - 插入单条积分变动记录
    boolean insertPointsRecord(PointsRecord pointsRecord);

    // 1.2 批量新增积分记录
    boolean batchInsertPointsRecords(List<PointsRecord> pointsRecords);

    // 1.3 带事务的积分操作
    boolean processPointsTransaction(Integer userId, String changeType, Integer changeAmount, String description);

    // 2.1 按用户ID查询积分记录
    List<PointsRecord> getPointsRecordsByUserId(Integer userId);

    // 2.2 按变更类型查询积分记录
    List<PointsRecord> getPointsRecordsByType(Integer userId, String changeType);

    // 2.3 按时间范围查询积分记录
    List<PointsRecord> getPointsRecordsByTimeRange(Integer userId, Date startTime, Date endTime);

    // 2.4 分页查询积分记录
    List<PointsRecord> getPointsRecordsByPage(Integer userId, int page, int pageSize);

    // 2.5 联合查询带用户信息的积分记录
    List<Map<String, Object>> getPointsRecordsWithUserInfo(Integer userId);

    // 2.6 多条件组合查询
    List<PointsRecord> getPointsRecordsByConditions(Integer userId, String changeType, Date startTime, Date endTime);

    // 3.1 计算用户当前总积分（累加change_amount）
    Integer calculateUserTotalPoints(Integer userId);

    // 3.2 统计用户积分收入与支出
    Map<String, Integer> calculatePointsIncomeAndExpense(Integer userId);

    // 3.3 按类型统计积分变动
    List<Map<String, Object>> calculatePointsByType(Integer userId);

    // 3.4 时间维度统计积分变动（按日/月/年）
    List<Map<String, Object>> calculatePointsByTimePeriod(Integer userId, String periodType);

    // 3.5 积分排行榜（用户积分排名）
    List<Map<String, Object>> getPointsRanking(int limit);

    // 辅助方法：获取用户积分记录总数
    int countPointsRecordsByUserId(Integer userId);

    // 辅助方法：获取所有积分记录总数（管理员用）
    int countAllPointsRecords();
}