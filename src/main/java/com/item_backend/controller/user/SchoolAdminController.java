package com.item_backend.controller.user;

import com.item_backend.model.dto.SubjectDto;
import com.item_backend.model.entity.User;
import com.item_backend.model.pojo.PageResult;
import com.item_backend.model.pojo.Result;
import com.item_backend.model.pojo.StatusCode;
import com.item_backend.service.impl.SchoolAdminServiceImpl;
import com.item_backend.utils.FormatUtil;
import com.item_backend.utils.JwtTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author: Mt.Li
 * @Create: 2020-05-28 11:11
 */
@Api(tags = "school_admin_api", description = "school_admin_api", basePath = "/school_admin")
@RestController
@RequestMapping("/school_admin")
public class SchoolAdminController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    SchoolAdminServiceImpl schoolAdminService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    FormatUtil formatUtil;

    @ApiOperation(value = "获取院级管理员列表", notes = "页数加显示数量")
    @GetMapping("/faculty_admin_List/{schoolId}/{page}/{showCount}")
    public Result searchFacultyAdminList(@PathVariable(value = "schoolId") Integer schoolId,
                                         @PathVariable(value = "page") Integer page, @PathVariable("showCount") Integer showCount){
        // 交于服务层处理
        if (!formatUtil.checkPositive(page, showCount)) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        Integer count = schoolAdminService.getFacultyAdminCount(schoolId);
        List<User> facultyAdminList = schoolAdminService.searchFacultyAdminList(schoolId, page, showCount);
        if(count == 0 || facultyAdminList == null){
            return Result.create(StatusCode.OK, "查询结果为空");
        }
        // 将结果封装到分页结果类
        PageResult<User> pageResult = new PageResult<>(count, facultyAdminList);
        return Result.create(StatusCode.OK, "查询成功", pageResult);
    }

    /**
     * 校级管理员添加院级管理员
     * @param facultyAdmin
     * @return
     */
    @PostMapping("/add_faculty_admin")
    public Result addSubject(@RequestBody User facultyAdmin) {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request,"校级管理员")){
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        if(!schoolAdminService.addFacultyAdmin(facultyAdmin)){
            return Result.create(StatusCode.ERROR,"添加失败");
        }
        return Result.create(StatusCode.OK,"添加成功");
    }

    /**
     * 校级管理员删除院级管理员
     * @param userId
     * @return
     */
    @DeleteMapping("/delete_faculty_admin/{userId}")
    public Result deleteSubject(@PathVariable(value = "userId") Integer userId) {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request,"校级管理员")){
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        // 判断数据是否为空
        if(!formatUtil.checkObjectNull(userId)){
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        if(!schoolAdminService.deleteFacultyAdmin(userId)){
            return Result.create(StatusCode.ERROR,"删除失败");
        }
        return Result.create(StatusCode.OK,"删除成功");
    }
}
