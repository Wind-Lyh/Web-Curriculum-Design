package com.community.model;

import java.util.Date;

public class Exchange {
    private Integer id;
    private Integer userId;
    private Integer goodsId;
    private Integer quantity;
    private Integer totalCost;
    private Date createTime;

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