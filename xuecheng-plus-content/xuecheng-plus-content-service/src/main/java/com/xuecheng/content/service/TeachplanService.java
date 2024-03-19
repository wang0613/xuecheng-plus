package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

public interface TeachplanService {

    /**
     * 查询课程计划 树形结构
     * @param courseId 课程Id
     * @return  List<TeachplanDto>
     */
    List<TeachplanDto> getTeachNodes(Long courseId);


    /**
     * 新增或者课程计划
     * @param teachplanDto
     */
    void createTeachPlan(SaveTeachplanDto teachplanDto);

    /**
     * 根据id 删除课程计划
     * @param id
     */
    void deleteTeachPlan(Long id);

    /**
     * 向下或者向上 调整
     * @param mode modedown moveup
     * @param id 课程计划ID
     */
    void move(String mode, Long id);
}
