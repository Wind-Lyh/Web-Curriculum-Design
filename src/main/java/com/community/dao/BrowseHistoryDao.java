package com.community.dao;

import com.community.model.BrowseHistory;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface BrowseHistoryDao {

    // ==================== 基础操作功能 ====================

    /**
     * 1. 添加浏览记录
     * 功能描述：记录用户浏览帖子的行为
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 是否添加成功
     * @throws SQLException
     */
    boolean addBrowseHistory(int userId, int postId) throws SQLException;

    /**
     * 2. 根据ID获取浏览记录
     * 功能描述：通过浏览记录ID查询单条记录详情
     * @param historyId 浏览记录ID
     * @return BrowseHistory对象，不存在则返回null
     * @throws SQLException
     */
    BrowseHistory getBrowseHistoryById(int historyId) throws SQLException;

    /**
     * 3. 清空用户浏览历史
     * 功能描述：删除指定用户的所有浏览记录
     * @param userId 用户ID
     * @return 删除的记录数量
     * @throws SQLException
     */
    int clearUserBrowseHistory(int userId) throws SQLException;

    /**
     * 4. 删除浏览记录
     * 功能描述：删除单条浏览记录
     * @param historyId 浏览记录ID
     * @return 是否删除成功
     * @throws SQLException
     */
    boolean deleteBrowseHistory(int historyId) throws SQLException;

    // ==================== 用户相关查询功能 ====================

    /**
     * 5. 获取用户的浏览记录列表
     * 功能描述：查询指定用户的所有浏览记录
     * @param userId 用户ID
     * @return BrowseHistory对象列表，按浏览时间倒序排列
     * @throws SQLException
     */
    List<BrowseHistory> getUserBrowseHistoryList(int userId) throws SQLException;

    /**
     * 6. 获取用户最近浏览的帖子
     * 功能描述：获取用户最近浏览的帖子记录
     * @param userId 用户ID
     * @param limit 获取的记录数量
     * @return BrowseHistory对象列表，按浏览时间倒序排列
     * @throws SQLException
     */
    List<BrowseHistory> getUserRecentBrowseHistory(int userId, int limit) throws SQLException;

    /**
     * 7. 统计用户的浏览记录数量
     * 功能描述：统计指定用户的总浏览记录数
     * @param userId 用户ID
     * @return 浏览记录总数
     * @throws SQLException
     */
    int countUserBrowseHistory(int userId) throws SQLException;

    /**
     * 8. 检查用户是否浏览过某帖子
     * 功能描述：判断用户是否已经浏览过指定帖子
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 已浏览返回true，未浏览返回false
     * @throws SQLException
     */
    boolean checkUserViewedPost(int userId, int postId) throws SQLException;

    // ==================== 帖子相关查询功能 ====================

    /**
     * 9. 获取帖子的浏览记录列表
     * 功能描述：查询某个帖子的所有浏览记录
     * @param postId 帖子ID
     * @return BrowseHistory对象列表，按浏览时间倒序排列
     * @throws SQLException
     */
    List<BrowseHistory> getPostBrowseHistoryList(int postId) throws SQLException;

    /**
     * 10. 统计帖子的浏览次数
     * 功能描述：统计指定帖子的总浏览次数
     * @param postId 帖子ID
     * @return 浏览总次数
     * @throws SQLException
     */
    int countPostBrowseTimes(int postId) throws SQLException;

    /**
     * 11. 获取热门浏览帖子
     * 功能描述：获取被浏览次数最多的帖子
     * @param limit 获取的记录数量
     * @return 包含帖子ID和浏览次数的Map列表
     * @throws SQLException
     */
    List<Map<String, Object>> getHotBrowsePosts(int limit) throws SQLException;

    /**
     * 12. 获取帖子热度排名
     * 功能描述：获取所有帖子的浏览热度排名
     * @param limit 获取的记录数量（为0表示获取全部）
     * @return 包含帖子ID、帖子标题和浏览次数的Map列表
     * @throws SQLException
     */
    List<Map<String, Object>> getPostBrowseRanking(int limit) throws SQLException;

    // ==================== 系统维护功能 ====================

    /**
     * 13. 删除过期浏览记录
     * 功能描述：清理指定时间之前的旧浏览记录
     * @param days 过期天数（例如：30表示删除30天前的记录）
     * @return 删除的记录数量
     * @throws SQLException
     */
    int deleteExpiredBrowseHistory(int days) throws SQLException;

    // ==================== 附加实用功能 ====================

    /**
     * 获取用户的浏览频率统计
     * @param userId 用户ID
     * @return 包含日期和浏览次数的统计列表
     * @throws SQLException
     */
    List<Map<String, Object>> getUserBrowseFrequency(int userId) throws SQLException;

    /**
     * 批量添加浏览记录
     * @param userId 用户ID
     * @param postIds 帖子ID列表
     * @return 添加成功地记录数量
     * @throws SQLException
     */
    int batchAddBrowseHistory(int userId, List<Integer> postIds) throws SQLException;

    /**
     * 获取用户的浏览帖子ID列表（去重）
     * @param userId 用户ID
     * @return 帖子ID列表
     * @throws SQLException
     */
    List<Integer> getUserBrowsedPostIds(int userId) throws SQLException;

    /**
     * 获取系统总浏览记录数
     * @return 总浏览记录数
     * @throws SQLException
     */
    int getTotalBrowseCount() throws SQLException;
}