package com.community.dao;

import com.community.model.Section;
import java.util.List;

public interface SectionDao {
    // 插入版块
    int insert(Section section);

    // 根据ID查询版块
    Section findById(int id);

    // 查询所有版块
    List<Section> findAll();

    // 更新版块
    int update(Section section);

    // 删除版块
    int delete(int id);

    // 增加版块帖子数
    int increasePostCount(int sectionId);

    // 减少版块帖子数
    int decreasePostCount(int sectionId);
}