package com.xuecheng.content.service.impl;


import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Autowired
    private CourseBaseService courseBaseService;
    @Autowired
    private TeachplanService teachplanService;
    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private CoursePublishPreMapper coursePublishPreMapper;
    @Autowired
    private CoursePublishMapper coursePublishMapper;
    @Autowired
    private MqMessageService mqMessageService;

    /**
     * 课程的基本信息 课程的营销信息 课程计划信息
     *
     * @param courseId 课程的id
     * @return com.xuecheng.content.model.dto.CoursePreviewDto
     */
    @Override
    public CoursePreviewDto preView(Long courseId) {
        CoursePreviewDto dto = new CoursePreviewDto();

        CourseBaseInfoDto courseInfoDto = courseBaseService.getCourseInfoDto(courseId);
        List<TeachplanDto> teachNodes = teachplanService.getTeachNodes(courseId);

        dto.setCourseBase(courseInfoDto);
        dto.setTeachplans(teachNodes);
        return dto;
    }

    /*
     * 设置更新状态
     * @param companyId
     * @param courseId
     */
    @Transactional
    @Override
    public void commitAudit(Long companyId, Long courseId) {
        //如果课程的审核状态为已提交 则不允许提交
        CourseBaseInfoDto courseInfoDto = courseBaseService.getCourseInfoDto(courseId);
        String auditStatus = courseInfoDto.getAuditStatus();
        if ("202003".equals(auditStatus)) {
            XueChengPlusException.cast("课程已提交 等待审核！");
            return;
        }
        //TODO 本机构只允许提交本机构的课程
       /* if(!courseBase.getCompanyId().equals(companyId)){
            XueChengPlusException.cast("不允许提交其它机构的课程。");
            return;
        }*/
        //如果课程的图片 计划信息没有填写 也不允许提交
        String pic = courseInfoDto.getPic();
        if (StringUtils.isEmpty(pic)) {
            XueChengPlusException.cast("提交失败，请上传课程图片");
            return;
        }
        List<TeachplanDto> teachplanDtos = teachplanMapper.queryTreeNode(courseId);
        if (teachplanDtos.size() <= 0) {
            XueChengPlusException.cast("课程计划为空！");
            return;
        }
//        1、查询课程基本信息、课程营销信息（jason）、课程计划信息(json)等课程相关信息，整合为课程预发布信息。
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //设置数据
        BeanUtils.copyProperties(courseInfoDto, coursePublishPre);
        coursePublishPre.setMarket(JSON.toJSONString(courseMarket));
        coursePublishPre.setTeachplan(JSON.toJSONString(teachplanDtos));
        coursePublishPre.setStatus("202003"); //已提交
        coursePublishPre.setCreateDate(LocalDateTime.now());
        coursePublishPre.setCompanyId(companyId);

//        2、向课程预发布表course_publish_pre插入一条记录，如果已经存在则更新，审核状态为：已提交。
        CoursePublishPre pre = coursePublishPreMapper.selectById(courseId);
        if (pre == null) {
            //新增
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            //更新
            coursePublishPreMapper.updateById(coursePublishPre);
        }
//        3、更新课程基本表course_base课程审核状态为：已提交。
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }

    /**
     * 课程发布
     */
    @Override
    @Transactional
    public void publish(Long courseId) {
        //1:先查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (!(coursePublishPre.getStatus().equals("202004"))) {
            XueChengPlusException.cast("课程没有审核通过！");
            return;
        }
        //2:向课程发布表写入数据
        CoursePublish old = coursePublishMapper.selectById(courseId);
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        if (old == null) {
            //新增
            coursePublishMapper.insert(coursePublish);
        } else {
            //修改
            coursePublishMapper.updateById(coursePublish);
        }
        //3:TODO 向mq_message消息表插入一条消息，消息类型为：course_publish
        saveCoursePublishMessage(courseId);
        //4:将预发布表数据删除
        coursePublishPreMapper.deleteById(coursePublishPre);

    }

    /*
    保存消息表信息
     */
    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
        }
    }
}
