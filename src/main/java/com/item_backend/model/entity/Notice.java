package com.item_backend.model.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@ApiModel("notice")
@Data
@ToString
public class Notice implements Serializable {

    private static final long serialVersionUID = -8227337383042338833L;

    @ApiModelProperty(value="消息id" ,dataType = "Integer")
    private Integer n_id;
    @ApiModelProperty(value = "消息标题", dataType = "String")
    private String n_title;
    @ApiModelProperty(value = "消息内容",dataType = "String")
    private String n_content;
    @ApiModelProperty(value = "发布时间",dataType = "Data")
    private String publish_time;
    @ApiModelProperty(value = "发布消息的用户的名字",dataType = "String")
    private String u_name;
    @ApiModelProperty(value = "用户id",dataType = "Integer")
    private Integer u_id;
}
