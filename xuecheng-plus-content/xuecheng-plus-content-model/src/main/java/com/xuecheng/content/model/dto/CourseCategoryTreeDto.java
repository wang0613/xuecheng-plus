package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 树形结构的根节点
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {

    //树形树形
    List<CourseCategoryTreeDto> childrenTreeNodes;






}
