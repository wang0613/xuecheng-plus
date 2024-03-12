package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


public interface CourseBaseService {
    /**
     * 获取分页数据
     * @param pageParams 分页参数
     * @param queryCourseParamsDto 查询的条件
     * @return
     */
    PageResult<CourseBase> getList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * 新增课程信息
     * @param companyId 所属机构的id
     * @param addCourseDto 新增课程基本信息
     * @return
     */
    CourseBaseInfoDto createCourseBase(long companyId, AddCourseDto addCourseDto);

}
