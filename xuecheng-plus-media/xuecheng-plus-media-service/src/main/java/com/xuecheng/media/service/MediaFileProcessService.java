package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

public interface MediaFileProcessService {


    /**
     * 更新任务的状态
     *
     * @param taskId   任务id
     * @param status   需要更新状态
     * @param fileId   文件Id
     * @param url      路径
     * @param errorMsg 错误信息
     */
     void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg);



    /**
     * 查询当前分片机器的待处理任务
     * @param shardTotal 机器总数
     * @param shardIndex 分片序号
     * @param count      任务数
     * @return com.xuecheng.media.model.po.MediaProcess
     */
    List<MediaProcess> selectListShardIndex(int shardTotal,
                                            int shardIndex,
                                            int count);

    /**
     * 开启一个任务
     * @param id
     * @return
     */
    Boolean startTask( long id);
}
