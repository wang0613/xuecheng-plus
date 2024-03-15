package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 课程计划管理相关接口
 */
@RestController
@Api(value = "课程计划管理接口", tags = "课程计划管理接口")
public class TeachplanController {

    @Autowired
    private TeachplanService teachplanService;

    @ApiOperation("查询课程计划树形结构")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTeachNodes(@PathVariable Long courseId) {
        return teachplanService.getTeachNodes(courseId);
    }

//    http://localhost:8601/api/content/teachplan
//{
//    "courseId": 117,
//        "parentid": 0,
//        "grade": 1,
//        "pname": "新章名称 [点击修改]"
//}

}
