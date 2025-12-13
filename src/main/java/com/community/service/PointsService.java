package com.community.service;

import com.community.model.Exchange;
import com.community.model.User;
import java.util.List;

public interface PointsService {
    /**
     * 增加用户积分
     * @param userId 用户ID
     * @param points 积分数量
     * @param changeType 变更类型
     * @param description 描述
     * @return 是否成功
     */
    boolean addPoints(int userId, int points, String changeType, String description);

    /**
     * 扣除用户积分
     * @param userId 用户ID
     * @param points 积分数量
     * @param changeType 变更类型
     * @param description 描述
     * @return 是否成功
     */
    boolean deductPoints(int userId, int points, String changeType, String description);

    /**
     * 获取用户积分
     * @param userId 用户ID
     * @return 积分数量
     */
    int getUserPoints(int userId);

    /**
     * 获取用户等级
     * @param userId 用户ID
     * @return 用户等级
     */
    int getUserLevel(int userId);

    /**
     * 兑换虚拟道具
     * @param userId 用户ID
     * @param goodsId 道具ID
     * @param quantity 兑换数量
     * @return 兑换记录
     */
    Exchange exchangeGoods(int userId, int goodsId, int quantity);

    /**
     * 获取积分排行榜
     * @param limit 排名数量
     * @return 用户列表
     */
    List<User> getRanking(int limit);

    /**
     * 计算等级进度
     * @param userId 用户ID
     * @return 等级进度百分比
     */
    double calculateLevelProgress(int userId);
}