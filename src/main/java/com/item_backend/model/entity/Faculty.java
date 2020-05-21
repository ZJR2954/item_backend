package com.item_backend.model.entity;

import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * <<<<<<< HEAD
 *
 * @Author xiao
 * @Time 2020/5/19
 * @Description 院系
 **/
@Data
@ToString
public class Faculty implements Serializable {
    private static final long serialVersionUID = -2216503186855758721L;

    @ApiModelProperty(value = "院系id", dataType = "Integer")
    private Integer faculty_id;//院系唯一标识

    @ApiModelProperty(value = "院系名", dataType = "String")
    private String faculty_name;//院系名

    @ApiModelProperty(value = "所属学校", dataType = "String")
    private String school;//所属学校

    @ApiModelProperty(value = "院级管理员id", dataType = "Integer")
    private Integer u_id;//院级管理员id
}
