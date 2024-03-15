package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

public interface TeachplanService {

    /**
     * 查询课程计划 树形结构
     * @param courseId 课程Id
     * @return  List<TeachplanDto>
     */
    List<TeachplanDto> getTeachNodes(Long courseId);


}
