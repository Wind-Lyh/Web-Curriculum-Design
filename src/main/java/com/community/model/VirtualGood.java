package com.community.model;

import java.util.Date;

public class VirtualGood {
    private Integer id;           // 道具ID，对应virtual_goods.id，主键自增
    private String name;          // 道具名称，对应virtual_goods.name
    private String description;   // 道具描述，对应virtual_goods.description
    private Integer price;        // 价格，对应virtual_goods.price，所需积分
    private Integer stock;        // 库存，对应virtual_goods.stock，-1表示无限库存
    private Integer isAvailable;  // 可用状态：0不可用，1可用，对应virtual_goods.is_available
    private Date createTime;      // 创建时间，对应virtual_goods.create_time，默认当前时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Integer isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }



    public VirtualGood(Integer id, String name, String description, Integer price, Integer stock, Integer isAvailable, Date createTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.isAvailable = isAvailable;
        this.createTime = createTime;
    }


}