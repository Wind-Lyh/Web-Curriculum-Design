package com.community.service.impl;

import com.community.dao.PointsDao;
import com.community.dao.UserDao;
import com.community.dao.VirtualGoodDao;
import com.community.dao.ExchangeDao;
import com.community.model.PointsRecord;
import com.community.model.User;
import com.community.model.VirtualGood;
import com.community.model.Exchange;
import com.community.service.PointsService;
import com.community.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;

public class PointsServiceImpl implements PointsService {

    private PointsDao pointsDao;
    private UserDao userDao;
    private VirtualGoodDao virtualGoodDao;
    private ExchangeDao exchangeDao;

    public PointsServiceImpl(PointsDao pointsDao, UserDao userDao,
                             VirtualGoodDao virtualGoodDao, ExchangeDao exchangeDao) {
        this.pointsDao = pointsDao;
        this.userDao = userDao;
        this.virtualGoodDao = virtualGoodDao;
        this.exchangeDao = exchangeDao;
    }

    @Override
    public boolean addPoints(int userId, int points, String changeType, String description) {
        if (points <= 0) {
            throw new IllegalArgumentException("积分必须为正数");
        }

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setChangeType(changeType);
        record.setChangeAmount(points);
        record.setDescription(StringUtil.isEmpty(description) ? "积分增加" : description);
        record.setCreateTime(new Date());

        boolean result = pointsDao.insertPointsRecord(record);
        if (result) {
            userDao.updatePoints(userId, points);
        }

        return result;
    }

    @Override
    public boolean deductPoints(int userId, int points, String changeType, String description) {
        if (points <= 0) {
            throw new IllegalArgumentException("积分必须为正数");
        }

        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (user.getPoints() < points) {
            throw new IllegalArgumentException("积分不足");
        }

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setChangeType(changeType);
        record.setChangeAmount(-points);
        record.setDescription(StringUtil.isEmpty(description) ? "积分扣除" : description);
        record.setCreateTime(new Date());

        boolean result = pointsDao.insertPointsRecord(record);
        if (result ) {
            userDao.updatePoints(userId, -points);
        }

        return result;
    }

    @Override
    public int getUserPoints(int userId) {
        User user = userDao.findById(userId);
        return user != null ? user.getPoints() : 0;
    }

    @Override
    public int getUserLevel(int userId) {
        int points = getUserPoints(userId);
        return calculateLevel(points);
    }

    @Override
    public Exchange exchangeGoods(int userId, int goodsId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("兑换数量必须大于0");
        }

        VirtualGood goods = virtualGoodDao.getVirtualGoodById(goodsId);
        if (goods == null) {
            throw new IllegalArgumentException("道具不存在");
        }

        if (goods.getIsAvailable() == 0) {
            throw new IllegalArgumentException("道具不可用");
        }

        if (goods.getStock() != -1 && goods.getStock() < quantity) {
            throw new IllegalArgumentException("库存不足");
        }

        int totalCost = goods.getPrice() * quantity;

        User user = userDao.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (user.getPoints() < totalCost) {
            throw new IllegalArgumentException("积分不足");
        }

        Exchange exchange = new Exchange();
        exchange.setUserId(userId);
        exchange.setGoodsId(goodsId);
        exchange.setQuantity(quantity);
        exchange.setTotalCost(totalCost);
        exchange.setCreateTime(new Date());

        boolean result = exchangeDao.insertExchange(exchange);
        if (!result) {
            throw new RuntimeException("兑换失败");
        }

        deductPoints(userId, totalCost, "GOODS_EXCHANGE",
                "兑换商品：" + goods.getName() + " × " + quantity);

        if (goods.getStock() != -1) {
            virtualGoodDao.decreaseGoodStock(goodsId, quantity);
        }

        return exchange;
    }

    @Override
    public List<User> getRanking(int limit) {
        if (limit <= 0 || limit > 100) {
            limit = 10;
        }

        List<Map<String, Object>> rankingData = pointsDao.getPointsRanking(limit);
        List<User> userList = new ArrayList<>();

        int rank = 1; // 如果需要，您可以在这里添加排名
        for (Map<String, Object> data : rankingData) {
            User user = new User();
            user.setId((Integer) data.get("user_id"));
            user.setUsername((String) data.get("username"));
            user.setNickname((String) data.get("nickname"));
            user.setAvatarUrl((String) data.get("avatar_url"));
            user.setPoints((Integer) data.get("points")); // 当前积分
            user.setLevel((Integer) data.get("level"));

            // 如果需要使用总积分，可以创建一个新字段或临时使用
            // 这里使用 setPoints((Integer) data.get("total_points")) 来使用总积分

            userList.add(user);
            rank++; // 排名递增
        }

        return userList;
    }

    @Override
    public double calculateLevelProgress(int userId) {
        int currentPoints = getUserPoints(userId);
        int currentLevel = getUserLevel(userId);
        int nextLevelPoints = getLevelPoints(currentLevel + 1);

        if (nextLevelPoints <= 0) {
            return 100.0;
        }

        int currentLevelPoints = getLevelPoints(currentLevel);
        int pointsInThisLevel = currentPoints - currentLevelPoints;
        int pointsNeeded = nextLevelPoints - currentLevelPoints;

        return (double) pointsInThisLevel / pointsNeeded * 100;
    }

    private int calculateLevel(int points) {
        if (points < 0) return 1;
        if (points <= 100) return 1;
        if (points <= 500) return 2;
        if (points <= 2000) return 3;
        if (points <= 5000) return 4;
        if (points <= 15000) return 5;
        return 6;
    }

    private int getLevelPoints(int level) {
        switch (level) {
            case 1: return 0;
            case 2: return 101;
            case 3: return 501;
            case 4: return 2001;
            case 5: return 5001;
            case 6: return 15001;
            default: return -1;
        }
    }
}