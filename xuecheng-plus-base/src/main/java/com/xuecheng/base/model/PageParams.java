package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页查询参数
 */
@Data
public class PageParams {

    //当前页码
    @ApiModelProperty("页码") //swagger用于描述一个字段
    private Long pageNo = 1L;

    //每页记录数默认值
    @ApiModelProperty("每页的记录数")
    private Long pageSize =10L;

    public PageParams(Long pageNo, Long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public PageParams() {
    }

}
