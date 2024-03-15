package com.xuecheng;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseCategoryService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class ContentMapperTest {

    @Autowired
    private CourseBaseMapper baseMapper;


    @Autowired
    private CourseCategoryService courseCategoryService;

    @Autowired
    private TeachplanMapper teachplanMapper;


    /**
     * 根据传入的节点的id 查询节点的信息
     * @return
     */
    @Test
    void queryTreeNodes() {
        List<TeachplanDto> teachplanDtos = teachplanMapper.queryTreeNode(117L);
        System.out.println(teachplanDtos.toString());

    }

    @Test
    void testContentMapper() {

        //详细分页查询
        //1:查询条件
        QueryCourseParamsDto courseParamsDto = new QueryCourseParamsDto();
        courseParamsDto.setCourseName("java");

        //2:拼接查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //根据名称进行模糊查询 sql: course_base.name like dto.getCourseName
        queryWrapper.like(StringUtils.isNotEmpty(courseParamsDto.getCourseName()),CourseBase::getName,courseParamsDto.getCourseName());
        //3：根据课程审核状态查询
        queryWrapper.eq(StringUtils.isNoneEmpty(courseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,courseParamsDto.getAuditStatus());

        //4：设置分页数据
        PageParams pageParams = new PageParams();
        pageParams.setPageNo(1L);
        pageParams.setPageSize(2L);

        //5：创建分页对象
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(),pageParams.getPageSize());
        //6：开始进行分页
        Page<CourseBase> pageResult = baseMapper.selectPage(page, queryWrapper);

        //返回数据
        PageResult<CourseBase> result = new PageResult<>(pageResult.getRecords(),pageResult.getTotal(),pageParams.getPageNo(),pageParams.getPageSize());

        System.out.println(result);

    }
}
