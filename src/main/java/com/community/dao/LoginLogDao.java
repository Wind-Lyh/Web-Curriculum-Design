package com.community.dao;

import com.community.model.LoginLog;
import java.util.List;
import java.util.Date;

public interface LoginLogDao {
    /**
     * 插入登录日志
     */
    int insert(LoginLog loginLog) throws Exception;

    /**
     * 根据ID查询登录日志
     */
    LoginLog findById(Integer id) throws Exception;

    /**
     * 根据用户ID查询登录日志
     */
    List<LoginLog> findByUserId(Integer userId) throws Exception;

    /**
     * 根据用户ID分页查询登录日志
     */
    List<LoginLog> findByUserIdWithPagination(Integer userId, int page, int size) throws Exception;

    /**
     * 根据时间范围查询登录日志
     */
    List<LoginLog> findByTimeRange(Date startTime, Date endTime) throws Exception;

    List<LoginLog> findByTimeRange(java.sql.Date startTime, java.sql.Date endTime) throws Exception;

    /**
     * 根据IP地址查询登录日志
     */
    List<LoginLog> findByIpAddress(String ipAddress) throws Exception;

    /**
     * 统计用户的登录次数
     */
    int countByUserId(Integer userId) throws Exception;

    /**
     * 获取用户最近一次登录记录
     */
    LoginLog findLatestByUserId(Integer userId) throws Exception;

    /**
     * 删除登录日志
     */
    int delete(Integer id) throws Exception;

    /**
     * 删除用户的所有登录日志
     */
    int deleteByUserId(Integer userId) throws Exception;

    /**
     * 删除指定时间之前的登录日志
     */

    int deleteBeforeTime(java.sql.Date time) throws Exception;
}