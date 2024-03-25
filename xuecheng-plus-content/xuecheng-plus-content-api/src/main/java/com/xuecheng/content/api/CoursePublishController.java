package com.xuecheng.content.api;


import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Api(value = "课程预览发布", tags = "课程预览发布接口")
@Controller //响应一个页面
public class CoursePublishController {

    @Autowired
    private CoursePublishService coursePublishService;

    /**
     * 查出模版所需的数据 并返回
     *
     * @param courseId 课程id
     * @return org.springframework.web.servlet.ModelAndView
     */
    @ApiOperation("课程预览")
    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preView(@PathVariable("courseId") Long courseId) {
        //1：新建ModelNndView
        ModelAndView modelAndView = new ModelAndView();

        CoursePreviewDto coursePreviewDto = coursePublishService.preView(courseId);
        //2:指定模型
        modelAndView.addObject("model", coursePreviewDto);
        //3:指定模版
        modelAndView.setViewName("course_template"); //根据视图名称添加.ftl

        return modelAndView;
    }

    /**
     * 设置状态为已提交
     *
     * @param courseId
     */
    @ResponseBody
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId) {
        Long companyId = 1232141425L;

        coursePublishService.commitAudit(companyId, courseId);
    }

    @ApiOperation("课程发布")
    @ResponseBody
    @PostMapping("/coursepublish/{courseId}")
    public void coursepublish(@PathVariable("courseId") Long courseId) {

    }


}
