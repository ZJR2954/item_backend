package com.item_backend.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.item_backend.model.entity.User;
import com.item_backend.model.pojo.Result;
import com.item_backend.model.pojo.StatusCode;
import com.item_backend.service.impl.SuperAdminServiceImpl;
import com.item_backend.utils.JwtTokenUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:超级管理员操作用户控制层
 * @Author: Mt.Li
 * @Create: 2020-06-05 16:29
 */
@Api(tags = "super_admin_api", description = "super_admin_api", basePath = "/super_admin")
@RestController
@RequestMapping("/super_admin")
public class SuperAdminController {

    @Autowired
    SuperAdminServiceImpl superAdminService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    HttpServletRequest request;

    /**
     * 超级管理员添加校级管理员
     * @param schoolAdmin
     * @return
     */
    @PostMapping("/add_school_admin")
    public Result addSchoolAdmin(@RequestBody User schoolAdmin) throws JsonProcessingException {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request,"超级管理员")){
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        if(!superAdminService.addSchoolAdmin(schoolAdmin)){
            return Result.create(StatusCode.ERROR,"添加失败");
        }
        return Result.create(StatusCode.OK,"添加成功");
    }
}
