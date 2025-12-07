package com.community.dao;

import com.community.model.Exchange;
import java.util.List;

public interface ExchangeDao {
    // 1. 新增兑换记录
    boolean insertExchange(Exchange exchange);

    // 2. 用户兑换历史查询
    List<Exchange> getExchangesByUserId(Integer userId);

    // 3. 兑换明细报表
    List<Exchange> getAllExchanges();

    // 4. 执行兑换流程
    boolean executeExchange(Integer userId, Integer goodsId, Integer quantity, Integer price);

    // 5. 统计功能 - 用户兑换统计
    Integer getUserExchangeCount(Integer userId);
    Integer getUserTotalCost(Integer userId);

    // 6. 分页查询
    List<Exchange> getExchangesByPage(int page, int pageSize);
    int getTotalCount();
}