package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    /**
     * 查询课程主讲老师信息
     *
     * @param courseId 课程id
     * @return
     */
    @Override
    public List<CourseTeacher> getTeacherList(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    /**
     * 创建或者更新课程老师查询接口
     *
     * @param courseTeacherDto
     */
    @Override
    public void saveCourseTeacher(CourseTeacherDto courseTeacherDto) {
        //1：先查询 是否存在
        Long id = courseTeacherDto.getId();
        CourseTeacher teacher = courseTeacherMapper.selectById(id);
        CourseTeacher courseTeacher = new CourseTeacher();
        if (teacher == null) {
            //2：新增
            BeanUtils.copyProperties(courseTeacherDto, courseTeacher);
            courseTeacher.setCreateDate(LocalDateTime.now());
            courseTeacher.setPhotograph(courseTeacherDto.getPhotograph());

            courseTeacherMapper.insert(courseTeacher);
        } else {
            //2：更新数据
            BeanUtils.copyProperties(courseTeacherDto, courseTeacher);
            courseTeacher.setId(id);
            courseTeacherMapper.updateById(courseTeacher);

        }


    }

    /**
     * 删除课程老师
     * @param id 老师id
     */
    @Override
    public void deleteCourseTeacher(Long id) {
        courseTeacherMapper.deleteById(id);
    }
}
