package com.xuecheng.content.model.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/*
课程预览信息
 */
@Data
@ToString
public class CoursePreviewDto {


    //课程的基本信息 课程的营销信息
    private CourseBaseInfoDto courseBase;

    //课程计划信息
    private List<TeachplanDto> teachplans;

    //TODO 课程的十师资信息...


}
