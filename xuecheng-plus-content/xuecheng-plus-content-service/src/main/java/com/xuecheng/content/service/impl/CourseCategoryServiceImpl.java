package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {


    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    /**
     * 根据传入的节点的id 查询节点的信息
     * @param id
     * @return
     */
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> categoryList = new ArrayList<>();
        //1：递归查询分类信息
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.queryTreeNode(id);
        //2:处理信息 拿出1-1 并将1-1-x的信息 封装到DTO下的List
        //将其转换为map key为节点的id value为CourseCategoryTreeDto对象 并过滤掉当前的根节点
        Map<String, CourseCategoryTreeDto> map = courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId()))
                .collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));

        //遍历list 一边遍历 一边寻找存放在父节点的childrenTreeNodes
        courseCategoryTreeDtos.stream().forEach(item -> {
            //如果写入所有的根节点（传入id为1 将所有1-1写入）
            if (item.getParentid().equals(id)) {
                categoryList.add(item); //外边一层list
            }
            //找到当前节点的父节点（并加入子节点 ）
            CourseCategoryTreeDto courseCategoryParent = map.get(item.getParentid());
            if (courseCategoryParent != null) {
                if (courseCategoryParent.getChildrenTreeNodes() == null) {
                    //如果该父节点的childrenTreeNode属性为空
                    courseCategoryParent.setChildrenTreeNodes(new ArrayList<>());
                }
                //将所有根节点的子节点 写入对应的根节点下的list
                courseCategoryParent.getChildrenTreeNodes().add(item); //加入内层list


            }


        });
        return categoryList;
    }
}
