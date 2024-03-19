package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {
    /**
     * 根据课程id 获取老师列表
     * @param courseId 课程id
     * @return
     */
    List<CourseTeacher> getTeacherList(Long courseId);

    /**
     * 创建课程老师查询接口
     * @param courseTeacherDto
     */
    void saveCourseTeacher(CourseTeacherDto courseTeacherDto);

    /**
     * 删除课程老师
     * @param id
     */
    void deleteCourseTeacher(Long id);
}
