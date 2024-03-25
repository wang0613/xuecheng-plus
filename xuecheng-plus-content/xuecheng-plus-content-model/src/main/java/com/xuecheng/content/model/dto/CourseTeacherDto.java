package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "CourseTeacherDto", description = "新增课程老师基本信息")
public class CourseTeacherDto {


    //    {
//    ”id“
//        "courseId": 117,
//            "teacherName": "111",
//            "position": "111",
//            "introduction": "11111"
//    }
    private Long id; //老师id
    private Long courseId; //课程id
    private String teacherName;
    private String position;
    private String introduction;
    private String photograph;


}
