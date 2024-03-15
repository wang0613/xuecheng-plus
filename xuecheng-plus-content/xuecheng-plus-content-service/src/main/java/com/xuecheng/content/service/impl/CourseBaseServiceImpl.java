package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
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

        saveCourseMarket(newCourseMarket);//保存

        //3:查出课程的详细信息
        return getCourseInfoDto(newCourseBase.getId());
    }

    /**
     * 根据课程id 从数据库中查询详细信息
     * @param courseId
     * @return
     */
    @Transactional
    @Override
    public CourseBaseInfoDto getCourseInfoDto(Long courseId) {
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
     * 更新课程的基本信息
     * @param companyId 公司id
     * @param editCourseDto 新的数据
     * @return
     */
    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
        //参数的合法性校验（本机构只能修改本机构的课程
        Long courseId = editCourseDto.getId();
        //从数据库中查询出课程的companyId 如果与当前的公司id 不同 则认为本课程不属于当前机构
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            XueChengPlusException.cast("课程不存在");
        }
        if(!courseBase.getCompanyId().equals(companyId)){
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }

        //1:封装数据（course,market）
        BeanUtils.copyProperties(editCourseDto,courseBase); //将传入的数据 放入courseBase
        courseBase.setChangeDate(LocalDateTime.now()); //修改更新时间

        CourseMarket market = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto,market);

        //2:更新数据
        int i = courseBaseMapper.updateById(courseBase);
        if(i <= 0){
            XueChengPlusException.cast("更新课程信息失败！");
        }
        int market1 = saveCourseMarket(market);//更新营销数据

        //返回修改后的课程信息
        return this.getCourseInfoDto(courseId);
    }

    /**
     * 如果存在营销信息则更新 如果不存在 则删除
     * @param courseMarketNew 待新增的课程营销信息
     * @return
     */
    public int saveCourseMarket(CourseMarket courseMarketNew) {
        //收费规则
        String charge = courseMarketNew.getCharge();
        if(StringUtils.isBlank(charge)){
            throw new RuntimeException("收费规则没有选择");
        }
        //收费规则为收费
        if(charge.equals("201001")){
            if(courseMarketNew.getPrice() == null || courseMarketNew.getPrice().floatValue()<=0){
                throw new RuntimeException("课程为收费价格不能为空且必须大于0");
            }
        }
        //根据id从课程营销表查询
        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarketNew.getId());
        if(courseMarketObj == null){
            return courseMarketMapper.insert(courseMarketNew);
        }else{
            BeanUtils.copyProperties(courseMarketNew,courseMarketObj);
            courseMarketObj.setId(courseMarketNew.getId());
            return courseMarketMapper.updateById(courseMarketObj);
        }
    }
}
