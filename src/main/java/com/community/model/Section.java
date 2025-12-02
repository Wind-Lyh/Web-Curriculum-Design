package com.community.model;

import java.util.Date;

public class Section {
    private Integer id;
    private String name;
    private String description;
    private Integer postCount;
    private Date createTime;

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