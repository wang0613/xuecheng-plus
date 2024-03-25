package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 新增或者修改 课程计划
     *
     * @param teachplanDto
     */
    @ApiOperation("新增课程计划")
    @PostMapping("/teachplan")
    public void createTeachPlan(@RequestBody SaveTeachplanDto teachplanDto) {
        teachplanService.createTeachPlan(teachplanDto);
    }

    /**
     * 删除章或者是节（id）
     *
     * @param id 计划的id
     */
    @ApiOperation("删除课程计划")
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachPlan(@PathVariable Long id) {
        teachplanService.deleteTeachPlan(id);
    }

    @ApiOperation("调整顺序")
    @PostMapping("/teachplan/{movedown}/{id}")
    public void move(@PathVariable(value = "movedown") String mode, @PathVariable(value = "id") Long id) {
        teachplanService.move(mode, id);
    }

    /**
     * 绑定章节对应的视频
     * @param teachplanMediaDto
     */
    @ApiOperation("绑定章节对应的视频")
    @PostMapping("/teachplan/association/media")
    public void bindAssociation(@RequestBody BindTeachplanMediaDto teachplanMediaDto){
        teachplanService.associationMedia(teachplanMediaDto);

    }



}
