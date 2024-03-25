package com.xuecheng.content.api;


import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "课程信息管理接口", tags = "课程信息管理接口") //swagger用于描述一个类的作用
public class CourseBaseInfoController {

    @Autowired
    private CourseBaseService courseBaseService;

    /**
     * @param pageParams
     * @param queryCourseParamsDto 并不是必须的
     * @return
     */
    @ApiOperation("课程查询接口") //swagger用于描述一个方法的作用
    @PostMapping("/course/list")
    public PageResult<CourseBase> getList(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto) {
        return courseBaseService.getList(pageParams, queryCourseParamsDto);

    }

    /**
     * 新增课程
     *
     * @param addCourseDto addCourseDto
     * @return CourseBaseInfoDto 课程的详细信息
     */
    @ApiOperation("新增课程接口")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated({
            ValidationGroups.Inster.class}) AddCourseDto addCourseDto) {

        //1:获取所登录的机构的id
        long companyId = 1232141425L;
        return courseBaseService.createCourseBase(companyId, addCourseDto);
    }

    /**
     * 根据课程id 查询课程的信息
     *
     * @param courseId 课程id
     * @return
     */
    @ApiOperation("根据课程id查询课程信息接口")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseInfo(@PathVariable Long courseId) {

        return courseBaseService.getCourseInfoDto(courseId);
    }

    /**
     * 根据课程id更新课程信息接口
     *
     * @param editCourseDto
     * @return
     */
    @ApiOperation("根据课程id更新课程信息接口")
    @PutMapping("/course")
    public CourseBaseInfoDto updateCourseInfo(@RequestBody @Validated({
            ValidationGroups.Update.class}) EditCourseDto editCourseDto) {

        Long companyId = 1232141425L;
        return courseBaseService.updateCourseBase(companyId, editCourseDto);
    }

    @ApiOperation("根据课程id删除所关联信息")
    @DeleteMapping("/course/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseBaseService.deleteCourse(id);
    }
}
