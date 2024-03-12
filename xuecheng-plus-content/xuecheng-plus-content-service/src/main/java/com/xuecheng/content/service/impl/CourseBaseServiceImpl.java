package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CourseBaseServiceImpl implements CourseBaseService {

    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    /**
     * 获取分页数据
     *
     * @param pageParams           分页参数(当前页码和每页参数)
     * @param queryCourseParamsDto (查询条件)
     * @return
     */
    @Override
    public PageResult<CourseBase> getList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {

        //详细分页查询
        //2:拼接查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //根据名称进行模糊查询 sql: course_base.name like dto.getCourseName
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),
                CourseBase::getName, queryCourseParamsDto.getCourseName());
        //3：根据课程审核状态查询
        queryWrapper.eq(StringUtils.isNoneEmpty(queryCourseParamsDto.getAuditStatus()),
                CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        //4：根据发布状态查询
        queryWrapper.eq(StringUtils.isNoneEmpty(queryCourseParamsDto.getPublishStatus()),
                CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());


        //5：创建分页对象
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        //6：开始进行分页
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);


        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), pageParams.getPageNo(), pageParams.getPageSize());
    }

    /**
     * 新增课程的基本信息
     *
     * @param companyId 所属机构的id
     * @param dto       新增课程基本信息
     * @return
     */
    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(long companyId, AddCourseDto dto) {
        //参数的合法性校验
        if (StringUtils.isBlank(dto.getName())) {
            throw new RuntimeException("课程名称为空");
        }
        if (StringUtils.isBlank(dto.getMt())) {
            throw new RuntimeException("课程分类为空");
        }
        if (StringUtils.isBlank(dto.getSt())) {
            throw new RuntimeException("课程分类为空");
        }
        if (StringUtils.isBlank(dto.getGrade())) {
            throw new RuntimeException("课程等级为空");
        }
        if (StringUtils.isBlank(dto.getTeachmode())) {
            throw new RuntimeException("教育模式为空");
        }
        if (StringUtils.isBlank(dto.getUsers())) {
            throw new RuntimeException("适应人群为空");
        }
        if (StringUtils.isBlank(dto.getCharge())) {
            throw new RuntimeException("收费规则为空");
        }
        //待插入的courseBase
        CourseBase newCourseBase = new CourseBase();
        //1:向课程基本信息表course_base写入数据
        BeanUtils.copyProperties(dto, newCourseBase); //将dto的信息赋值到新的对象中(只要属性保持一致)
        newCourseBase.setCompanyId(companyId);
        newCourseBase.setCreateDate(LocalDateTime.now());
        //审核状态默认为 未提交
        newCourseBase.setAuditStatus("202002");
        //发布状态默认为 未发布
        newCourseBase.setStatus("203001");
        //插入数据
        int insert = courseBaseMapper.insert(newCourseBase);
        if (insert <= 0) {
            throw new RuntimeException("添加课程失败！");
        }
        //2:向课程营销表course_market表写入数据或者更新信息
        CourseMarket newCourseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto, newCourseMarket);
        //将其设置为课程信息的id
        newCourseMarket.setId(newCourseBase.getId());

        saveMarket(newCourseMarket);//保存

        //3:查出课程的详细信息
        return getCourseInfoDto(newCourseBase.getId());
    }

    /**
     * 根据课程id 从数据库中查询详细信息
     *
     * @param courseId
     * @return
     */
    public CourseBaseInfoDto getCourseInfoDto(long courseId) {

        //1:从节本信息表中查询数据
        CourseBase courseBase = courseBaseMapper.selectById(courseId);

        //2:从课程营销表中查询信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

        //组装到一起
        CourseBaseInfoDto dto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, dto);
        BeanUtils.copyProperties(courseMarket, dto);

        //3：从数据库中查询到Category 并设置 stName
        String st = courseBase.getSt();
        String mt = courseBase.getMt();

        //查询出分类名称  并设置为St和Mt名称
        dto.setStName(courseCategoryMapper.selectById(st).getName());
        dto.setMtName(courseCategoryMapper.selectById(mt).getName());

        return dto;
    }

    /**
     * 如果存在营销信息则更新 如果不存在 则删除
     *
     * @param courseMarketNew
     * @return
     */
    public int saveMarket(CourseMarket courseMarketNew) {
        //参数的合法性校验
        String charge = courseMarketNew.getCharge();
        if (StringUtils.isEmpty(charge)) {
            //非法数据
            throw new RuntimeException("收费规则为空");
        }
        //若课程为收费 且 价格《=0  非法数据
        if (charge.equals("201001") || (courseMarketNew.getPrice().floatValue() <= 0)) {
            throw new RuntimeException("收费规则为空");

        }
        //1：查询是否存在
        Long id = courseMarketNew.getId();
        CourseMarket courseMarket = courseMarketMapper.selectById(id);
        if (courseMarket == null) {
            //新增数据
            courseMarketMapper.insert(courseMarketNew);
        } else {
            //更新数据
            BeanUtils.copyProperties(courseMarketNew, courseMarket);
            courseMarket.setId(courseMarketNew.getId());

            courseMarketMapper.updateById(courseMarket);
        }
        return 1;
    }
}
