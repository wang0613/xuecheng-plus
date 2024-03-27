package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;

public interface CoursePublishService {
    /**
     * 课程预览
     *
     * @param courseId
     * @return
     */
    CoursePreviewDto preView(Long courseId);

    /**
     * 设置审核状态为已提交
     *
     * @param companyId
     * @param courseId
     */
    void commitAudit(Long companyId, Long courseId);

    /**
     * 发布课程
     *
     * @param courseId
     */
    void publish(Long courseId);

    /**
     * @param courseId 课程id
     * @description 课程静态化
     */
    File generateCourseHtml(Long courseId);

    /**
     * @param file 静态化文件
     * @description 上传课程静态化页面
     */
    void uploadCourseHtml(Long courseId, File file);

}
