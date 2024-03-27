package com.xuecheng.content.service.jobhandler;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

/**
 * 课程发布任务类
 */
@Component
@Slf4j
public class CoursePublishTask extends MessageProcessAbstract {

    @Autowired
    private CoursePublishService coursePublishService;

    /**
     * 课程发布处理任务
     *
     * @throws Exception
     */
    @XxlJob("CoursePublishJobHandler")
    public void videoJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        //调用抽象类方法执行任务
        process(shardIndex, shardTotal, "course_publish", 30, 60);
    }

    /**
     * 课程发布任务逻辑
     *
     * @param mqMessage 执行任务内容
     * @return
     */
    @Override
    @Transactional
    public boolean execute(MqMessage mqMessage) {
        //从mqMessage中拿出课程id
        Long courseId = Long.valueOf(mqMessage.getBusinessKey1());

        //1:课程静态化 上传到mioio
        generateCourseHtml(mqMessage, courseId);
        //2:向es写入索引数据
        saveCourseIndex(mqMessage, courseId);

        //3:向Redis写缓存
        saveRedisCache(mqMessage, courseId);

        return true;
    }

    //TODO 向redis写入数据
    private void saveRedisCache(MqMessage mqMessage, long courseId) {

    }

    //保存课程的索引信息 第二个阶段的任务
    private void saveCourseIndex(MqMessage mqMessage, long courseId) {
        //1：做任务的幂等性处理 取出第二个阶段的
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();

        int stageTwo = mqMessageService.getStageTwo(taskId);
        if (stageTwo > 0) {
            log.debug("课程索引信息已完成");
            return;
        }
        //2: TODO 进行索引的处理

        //3:将任务的处理状态设置为已完成
        mqMessageService.completedStageTwo(courseId);
    }

    //生成课程静态化页面并上传到文件系统
    private void generateCourseHtml(MqMessage mqMessage, long courseId) {
        Long taskId = mqMessage.getId();
        //1:做任务的幂等性处理 （取出第一个阶段，如果完成 将其设置为1）
        MqMessageService mqMessageService = this.getMqMessageService();

        //从数据库中根据Id 查询阶段一
        int stageOne = mqMessageService.getStageOne(taskId);
        if (stageOne > 0) {
            log.debug("课程静态化已完成");
            return;
        }
        //2:开始进行页面静态化处理, 将生成的html 存放到minio
        File file = coursePublishService.generateCourseHtml(courseId);
        if (file == null) {
            XueChengPlusException.cast("生成静态页面失败！课程id:" + courseId);
        }
        coursePublishService.uploadCourseHtml(courseId, file);

        //3:将任务的处理状态设置为已完成
        mqMessageService.completedStageOne(taskId);

    }


}
