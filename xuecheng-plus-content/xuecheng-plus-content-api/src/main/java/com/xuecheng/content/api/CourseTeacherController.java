package com.xuecheng.content.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(value = "课程教师接口", tags = "课程教师接口")
public class CourseTeacherController {

    @Autowired
    private CourseTeacherService courseTeacherService;

    /**
     * 获取课程主将老师的课程列表
     *
     * @param courseId 课程id
     * @return 分页的课程老师
     */
    @ApiOperation("课程老师查询接口")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getTeacherList(@PathVariable Long courseId) {

        return courseTeacherService.getTeacherList(courseId);
    }

    /**
     * 创建课程老师查询接口
     *
     * @param courseTeacherDto 课程老师信息
     */
    @ApiOperation("创建或者更新课程老师查询接口")
    @PostMapping("/courseTeacher")
    public void createCourseTeacher(@RequestBody CourseTeacherDto courseTeacherDto) {
        courseTeacherService.saveCourseTeacher(courseTeacherDto);
    }

    /**
     * http://localhost:8601/api/content/courseTeacher/course/22/28
     * @param id
     */
    @ApiOperation("删除课程老师查询接口")
    @DeleteMapping("/courseTeacher/course/{courseId}/{id}")
    public void deleteCourseTeacher(@PathVariable(value = "id") Long id) {
        courseTeacherService.deleteCourseTeacher(id);
    }
}
