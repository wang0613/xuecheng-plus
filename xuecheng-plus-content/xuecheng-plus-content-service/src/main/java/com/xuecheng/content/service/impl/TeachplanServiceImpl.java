package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeachplanServiceImpl implements TeachplanService {


    @Autowired
    private TeachplanMapper teachplanMapper;

    /**
     * 查询课程计划 树形结构
     * @param courseId 课程Id
     * @return List<TeachplanDto>
     */
    @Override
    public List<TeachplanDto> getTeachNodes(Long courseId) {
        return teachplanMapper.queryTreeNode(courseId);
    }
}
