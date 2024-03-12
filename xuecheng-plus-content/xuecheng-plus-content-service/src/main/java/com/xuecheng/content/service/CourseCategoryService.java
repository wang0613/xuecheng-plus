package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

public interface CourseCategoryService {
    /**
     * 根据节点id 查询节点的信息
     * @param id
     * @return
     */
    List<CourseCategoryTreeDto> queryTreeNodes(String id);

}
