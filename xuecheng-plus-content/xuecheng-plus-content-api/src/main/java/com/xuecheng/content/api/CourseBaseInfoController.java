package com.xuecheng.content.api;


import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "课程信息管理接口",tags = "课程信息管理接口") //swagger用于描述一个类的作用
public class CourseBaseInfoController {

    @Autowired
    private CourseBaseService courseBaseService;

    /**
     *
     * @param pageParams
     * @param queryCourseParamsDto 并不是必须的
     * @return
     */
    @ApiOperation("课程查询接口") //swagger用于描述一个方法的作用
    @PostMapping("/course/list")
    public PageResult<CourseBase> getList(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto){
        return courseBaseService.getList(pageParams,queryCourseParamsDto);

    }

    /**
     * 新增课程
     * @param addCourseDto addCourseDto
     * @return CourseBaseInfoDto 课程的详细信息
     */
    @ApiOperation("新增课程接口")
    @PostMapping("/content/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody AddCourseDto addCourseDto){

        //1:获取所登录的机构的id
        long companyId= 2121L;
        return courseBaseService.createCourseBase(companyId,addCourseDto);
    }
}
