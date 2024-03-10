package com.xuecheng.content.api;


import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "课程信息管理接口",tags = "课程信息管理接口") //swagger用于描述一个类的作用
public class CourseBaseInfoController {


    /**
     *
     * @param pageParams
     * @param queryCourseParamsDto 并不是必须的
     * @return
     */
    @ApiOperation("课程查询接口") //swagger用于描述一个方法的作用
    @PostMapping("/course/list")
    public PageResult<CourseBase> getList(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto){


        return null;

    }
}
