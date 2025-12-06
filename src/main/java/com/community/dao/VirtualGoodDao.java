package com.community.dao;

import com.community.model.VirtualGood;
import java.util.List;
import java.util.Map;

public interface VirtualGoodDao {

    // 1. 道具管理
    // 1.1 新增虚拟道具
    boolean insertVirtualGood(VirtualGood virtualGood);

    // 1.2 更新道具信息
    boolean updateVirtualGood(VirtualGood virtualGood);

    // 1.3 删除道具
    boolean deleteVirtualGood(Integer id);

    // 1.4 批量添加道具
    boolean batchInsertVirtualGoods(List<VirtualGood> virtualGoods);

    // 1.5 批量更新道具
    boolean batchUpdateVirtualGoods(List<VirtualGood> virtualGoods);

    // 1.6 批量删除道具
    boolean batchDeleteVirtualGoods(List<Integer> ids);

    // 2. 道具查询
    // 2.1 按ID查询道具
    VirtualGood getVirtualGoodById(Integer id);

    // 2.2 按名称查询道具（支持模糊查询）
    List<VirtualGood> getVirtualGoodsByName(String name);

    // 2.3 查询所有道具
    List<VirtualGood> getAllVirtualGoods();

    // 2.6 按价格排序查询
    List<VirtualGood> getVirtualGoodsOrderByPrice(String order); // asc或desc

    // 3. 状态管理
    // 3.1 上架道具
    boolean setGoodAvailable(Integer id);

    // 3.2 下架道具
    boolean setGoodUnavailable(Integer id);

    // 3.3 批量上架道具
    boolean batchSetGoodsAvailable(List<Integer> ids);

    // 3.4 批量下架道具
    boolean batchSetGoodsUnavailable(List<Integer> ids);

    // 4. 库存管理
    // 4.1 查询道具库存
    Integer getGoodStock(Integer id);

    // 4.2 增加道具库存
    boolean increaseGoodStock(Integer id, Integer amount);

    // 4.3 减少道具库存
    boolean decreaseGoodStock(Integer id, Integer amount);

    // 4.4 设置道具库存
    boolean setGoodStock(Integer id, Integer stock);

    // 5. 价格管理
    // 5.1 修改道具价格
    boolean updateGoodPrice(Integer id, Integer price);

    // 7. 统计与分析
    // 7.1 道具总数统计
    int countAllVirtualGoods();

    // 7.2 可用道具统计
    int countAvailableVirtualGoods();

    // 7.3 库存统计（排除无限库存）
    int calculateTotalStock();

    // 7.4 价格统计
    Map<String, Integer> getPriceStatistics();

    // 其他辅助功能
    // 分页查询
    List<VirtualGood> getVirtualGoodsByPage(int page, int pageSize);

    // 条件查询（价格范围、库存、状态）
    List<VirtualGood> getVirtualGoodsByConditions(String name, Integer minPrice,
                                                  Integer maxPrice, Boolean isAvailable);
}