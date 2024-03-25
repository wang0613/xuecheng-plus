package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeachplanServiceImpl implements TeachplanService {


    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;

    /**
     * 查询课程计划 树形结构
     *
     * @param courseId 课程Id
     * @return List<TeachplanDto>
     */
    @Override
    public List<TeachplanDto> getTeachNodes(Long courseId) {
        return teachplanMapper.queryTreeNode(courseId);
    }

    /**
     * 新增或者修改 课程计划
     *
     * @param teachplanDto
     */
    @Override
    public void createTeachPlan(SaveTeachplanDto teachplanDto) {
        //1：通过课程计划 ID判断 是修改 还是 新增
        Long id = teachplanDto.getId();
        if (id == null) {
            //2.1 新增数据
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(teachplanDto, teachplan);
            //3：设置 order by的值 确定顺序
            //找到同级的节点个数，排序字段就是个数+1
            // SELECT COUNT(1) from teachplan where course_id =117 and parentid = 268;
            Long parentid = teachplanDto.getParentid();
            Long courseId = teachplanDto.getCourseId();
            LambdaQueryWrapper<Teachplan> qw = new LambdaQueryWrapper<>();
            //拼接sql语句
            qw.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentid);
            //执行sql
            Integer count = teachplanMapper.selectCount(qw);
            teachplan.setOrderby(count + 1);

            teachplanMapper.insert(teachplan);


        } else {
            //2:更新数据 先查数据
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto, teachplan); //更新数据

            teachplanMapper.updateById(teachplan);

        }
    }

    /**
     * 根据id 删除课程计划
     * 删除第一级别的大章节时要求大章节下边没有小章节时方可删除。
     * 删除第二级别的小章节的同时需要将teachplan_media表关联的信息也删除
     *
     * @param id
     */
    @Transactional
    @Override
    public void deleteTeachPlan(Long id) {
        //1:判断是第一级还是 第二级 根据parentId进行查询
        //SELECT COUNT(1) from teachplan where parentid = 288;
        LambdaQueryWrapper<Teachplan> qw = new LambdaQueryWrapper<>();
        //拼接sql语句
        qw.eq(Teachplan::getParentid, id);
        //执行sql
        Integer count = teachplanMapper.selectCount(qw);
        if (count != 0) {
            //存在子节点 （不能删除）
            throw new XueChengPlusException("当前章节下存在小章节不能删除！");
        } else {
            //2:删除当前节点  同时删除 teachplan_media
            teachplanMapper.deleteById(id);
            LambdaQueryWrapper<TeachplanMedia> del = new LambdaQueryWrapper<>();
            del.eq(TeachplanMedia::getTeachplanId, id);
            teachplanMediaMapper.delete(del);
        }
    }

    /**
     * 向下或者向上 调整
     *
     * @param mode modedown moveup
     * @param id   课程计划ID
     */
    @Override
    public void move(String mode, Long id) {
        //更新课程的order by字段
        //1:先查询出旧的数据
        Teachplan teachplan = teachplanMapper.selectById(id);
        Integer orderBy = teachplan.getOrderby();

        //查询是否为最后一个（查询同级的节点个数）
        Long parentid = teachplan.getParentid();
        LambdaQueryWrapper<Teachplan> qw = new LambdaQueryWrapper<>();
        //拼接sql语句
        qw.eq(Teachplan::getParentid, parentid);
        //执行sql
        Integer max = teachplanMapper.selectCount(qw);

        //2:orderby+1 或者-1
        if (orderBy > 1 && orderBy < max) {
            if ("movedown".equals(mode)) {
                orderBy = orderBy + 1;
            } else if ("moveup".equals(mode)) {
                orderBy = orderBy - 1;
            }
            //如果当前为第1 只能下移
        } else if (orderBy == 1 && "movedown".equals(mode)) {
            orderBy = orderBy + 1;
            //如果为最后一个 只能上移
        } else if (orderBy.equals(max) && "moveup".equals(mode)) {
            orderBy = orderBy - 1;
        }
        //3:更新数据
        teachplan.setOrderby(orderBy);
        teachplanMapper.updateById(teachplan);

    }

    /**
     * 绑定视频与课程的关系
     *
     * @param teachplanMediaDto
     */
    @Override
    public void associationMedia(BindTeachplanMediaDto teachplanMediaDto) {
        //1:先删除原有的绑定记录
        Long teachplanId = teachplanMediaDto.getTeachplanId();
        int delete = teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, teachplanId));

        //2:然后添加新的记录
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        //根据当前的teachplanId 查到Course_id
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);

        teachplanMedia.setCourseId(teachplan.getCourseId());
        teachplanMedia.setMediaId(teachplanMediaDto.getMediaId());
        teachplanMedia.setMediaFilename(teachplanMediaDto.getFileName());
        teachplanMedia.setTeachplanId(teachplanMediaDto.getTeachplanId());

        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);


    }
}
