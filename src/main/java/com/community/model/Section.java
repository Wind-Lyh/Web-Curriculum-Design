package com.community.model;

import java.util.Date;

public class Section {
    private Integer id;         // 版块ID，对应sections.id，主键自增
    private String name;        // 版块名称，对应sections.name
    private String description; // 版块描述，对应sections.description
    private Integer postCount;  // 帖子数量（统计字段，数据库无对应列）
    private Date createTime;    // 创建时间，对应sections.create_time，默认当前时间

    public Section() {
        this.postCount = 0;
        this.createTime = new Date();
    }

    public Section(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    // Getter和Setter方法
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

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", postCount=" + postCount +
                '}';
    }
}