package com.community.model;

import java.util.Date;

public class Exchange {
    private Integer id;         // 兑换记录ID，对应exchanges.id，主键自增
    private Integer userId;     // 用户ID，对应exchanges.user_id，外键关联users.id
    private Integer goodsId;    // 道具ID，对应exchanges.goods_id，外键关联virtual_goods.id
    private Integer quantity;   // 兑换数量，对应exchanges.quantity，默认1
    private Integer totalCost;  // 总花费积分，对应exchanges.total_cost
    private Date createTime;    // 兑换时间，对应exchanges.create_time，默认当前时间

    public Exchange(Integer id, Integer userId, Integer goodsId, Integer quantity, Integer totalCost, Date createTime) {
        this.id = id;
        this.userId = userId;
        this.goodsId = goodsId;
        this.quantity = quantity;
        this.totalCost = totalCost;
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }




}