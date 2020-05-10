package com.item_backend.model.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author: Mt.Li
 * @Create: 2020-05-10 15:30
 */
@Api("测试实体")
@Data
public class TestUser {
    @ApiModelProperty(value = "用户名", dataType = "String")
    private String name;
    @ApiModelProperty(value = "用户密码", dataType = "String")
    private String password;
}
