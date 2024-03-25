package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaFileProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class MediaFileProcessServiceImpl implements MediaFileProcessService {

    @Autowired
    private MediaProcessMapper mediaProcessMapper;
    @Autowired
    private MediaFilesMapper mediaFilesMapper;
    @Autowired
    private MediaProcessHistoryMapper mediaProcessHistoryMapper;

    /**
     * 更新任务的状态
     *
     * @param taskId   任务id
     * @param status   需要更新状态
     * @param fileId   文件Id
     * @param url      路径
     * @param errorMsg 错误信息
     */
    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        //1:先查询如果存在，则更新任务
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (mediaProcess == null) { //任务不存在
            return;
        }
        //处理失败，更新任务处理结果
        //2:处理失败
        LambdaQueryWrapper<MediaProcess> queryWrapperById = new LambdaQueryWrapper<MediaProcess>().eq(MediaProcess::getId, taskId);
        if (status.equals("3")) {
            MediaProcess mediaProcess_u = new MediaProcess();
            //更新任务为处理结束
            mediaProcess_u.setStatus("3");
            mediaProcess_u.setErrormsg(errorMsg);
            mediaProcess_u.setFailCount(mediaProcess.getFailCount() + 1);
            mediaProcessMapper.update(mediaProcess_u, queryWrapperById);
            log.debug("更新任务处理状态为失败，任务信息:{}", mediaProcess_u);
            return;
        }
        //3:处理成功 更新media_file中的url
        MediaFiles mediaFiles = new MediaFiles();
        mediaFiles.setUrl(url);
        mediaFilesMapper.updateById(mediaFiles);

        //4:更新mediaProcess状态
        mediaProcess.setStatus("2");
        mediaProcess.setFinishDate(LocalDateTime.now());
        mediaProcess.setUrl(url);
        mediaProcessMapper.updateById(mediaProcess);

        //5:将MediaProcess表记录插入到MediaProcessHistory
        MediaProcessHistory history = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess, history);
        mediaProcessHistoryMapper.insert(history);

        //6:删除mediaProcess的信息
        mediaProcessMapper.deleteById(mediaProcess.getId());
    }


    /**
     * 查询待处理任务
     *
     * @param shardTotal 机器总数
     * @param shardIndex 分片序号
     * @param count      任务数
     * @return
     */
    @Override
    public List<MediaProcess> selectListShardIndex(int shardTotal, int shardIndex, int count) {
        return mediaProcessMapper.selectListShardIndex(shardTotal, shardIndex, count);
    }

    /**
     * 开启一个任务
     *
     * @param id
     * @return
     */
    @Override
    public Boolean startTask(long id) {
        int task = mediaProcessMapper.startTask(id);
        return task <= 0 ? false : true;
    }


}
