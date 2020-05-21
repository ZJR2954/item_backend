package com.item_backend.model.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author xiao
 * @Time 2020/5/17
 * @Describe 专业
 **/
@Data
@ToString
public class Major implements Serializable {

    private static final long serialVersionUID = -705205019503328208L;

    @ApiModelProperty(value = "专业id", dataType = "Integer")
    private Integer major_id;//专业唯一标识

    @ApiModelProperty(value = "专业名", dataType = "String")
    private String major_name;//专业名

    @ApiModelProperty(value = "所属院系id", dataType = "Integer")
    private Integer faculty_id;//所属院系id
}
