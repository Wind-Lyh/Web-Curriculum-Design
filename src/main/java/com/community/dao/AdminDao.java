package com.community.dao;

import com.community.model.AdminLog;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 管理员日志数据访问接口
 */
public interface AdminDao {

    // ==================== 管理员日志相关方法 ====================

    /**
     * 添加管理员操作日志
     * @param adminLog 管理员日志对象
     * @return 是否添加成功
     * @throws SQLException SQL异常
     */
    boolean addAdminLog(AdminLog adminLog) throws SQLException;

    /**
     * 根据ID获取管理员日志
     * @param logId 日志ID
     * @return 管理员日志对象，如果不存在返回null
     * @throws SQLException SQL异常
     */
    AdminLog getAdminLogById(int logId) throws SQLException;

    /**
     * 获取管理员操作日志列表（带分页和条件查询）
     * @param adminId 管理员ID，为0时查询所有管理员
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 管理员日志列表
     * @throws SQLException SQL异常
     */
    List<AdminLog> getAdminLogs(int adminId, int page, int pageSize,
                                Date startDate, Date endDate) throws SQLException;

    /**
     * 获取管理员操作日志数量（用于分页）
     * @param adminId 管理员ID，为0时查询所有管理员
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 日志数量
     * @throws SQLException SQL异常
     */
    int getAdminLogCount(int adminId, Date startDate, Date endDate) throws SQLException;

    /**
     * 获取最近N条管理员操作日志
     * @param limit 限制条数
     * @return 最近的管理员日志列表
     * @throws SQLException SQL异常
     */
    List<AdminLog> getRecentAdminLogs(int limit) throws SQLException;

    /**
     * 根据管理员ID获取日志（带管理员姓名）
     * @param adminId 管理员ID
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @return 管理员日志列表
     * @throws SQLException SQL异常
     */
    List<AdminLog> getAdminLogsWithAdminName(int adminId, int page, int pageSize) throws SQLException;

    /**
     * 删除指定ID的管理员日志
     * @param logId 日志ID
     * @return 是否删除成功
     * @throws SQLException SQL异常
     */
    boolean deleteAdminLog(int logId) throws SQLException;

    /**
     * 批量删除管理员日志
     * @param logIds 日志ID列表
     * @return 删除的记录数
     * @throws SQLException SQL异常
     */
    int batchDeleteAdminLogs(List<Integer> logIds) throws SQLException;

    /**
     * 清空所有管理员日志（谨慎使用）
     * @return 是否清空成功
     * @throws SQLException SQL异常
     */
    boolean clearAllAdminLogs() throws SQLException;

    /**
     * 根据操作类型统计日志数量
     * @return 统计结果列表，每个元素是[操作类型, 数量]的数组
     * @throws SQLException SQL异常
     */
    List<String[]> countLogsByActionType() throws SQLException;

    /**
     * 搜索管理员日志（支持模糊搜索）
     * @param keyword 搜索关键词
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @return 搜索结果列表
     * @throws SQLException SQL异常
     */
    List<AdminLog> searchAdminLogs(String keyword, int page, int pageSize) throws SQLException;

    /**
     * 获取指定时间段内的日志统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志数量
     * @throws SQLException SQL异常
     */
    int getLogCountByTimeRange(Date startTime, Date endTime) throws SQLException;
}