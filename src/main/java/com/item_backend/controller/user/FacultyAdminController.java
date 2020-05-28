package com.item_backend.controller.user;

import com.item_backend.model.dto.TeacherDto;
import com.item_backend.model.entity.User;
import com.item_backend.model.pojo.PageResult;
import com.item_backend.model.pojo.Result;
import com.item_backend.model.pojo.StatusCode;
import com.item_backend.service.impl.FacultyAdminServiceImpl;
import com.item_backend.service.impl.SchoolAdminServiceImpl;
import com.item_backend.utils.FormatUtil;
import com.item_backend.utils.JwtTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description:
 * @Author: Mt.Li
 */
@Api(tags = "faculty_admin_api", description = "faculty_admin_api", basePath = "/faculty_admin")
@RestController
@RequestMapping("/faculty_admin")
public class FacultyAdminController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    FacultyAdminServiceImpl facultyAdminService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    FormatUtil formatUtil;

    /**
     * 获取教师列表
     * @param facultyId
     * @param schoolId
     * @param page
     * @param showCount
     * @return
     */
    @ApiOperation(value = "获取教师列表", notes = "页数加显示数量")
    @GetMapping("/teacher_List/{facultyId}/{schoolId}/{page}/{showCount}")
    public Result searchFacultyAdminList(@PathVariable(value = "facultyId") Integer facultyId, @PathVariable(value = "schoolId") Integer schoolId,
                                         @PathVariable(value = "page") Integer page, @PathVariable("showCount") Integer showCount){
        if (!formatUtil.checkPositive(page, showCount)) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        Integer count = facultyAdminService.getTeacherCount(facultyId);
        List<TeacherDto> teacherDtoList = facultyAdminService.searchTeacherList(facultyId, schoolId, page, showCount);
        if(count == 0 || teacherDtoList == null){
            return Result.create(StatusCode.OK, "查询结果为空");
        }
        // 将结果封装到分页结果类
        PageResult<TeacherDto> pageResult = new PageResult<>(count, teacherDtoList);
        return Result.create(StatusCode.OK, "查询成功", pageResult);
    }

    /**
     * 院级管理员添加教师
     * @param teacher
     * @return
     */
    @PostMapping("/add_teacher")
    public Result addSubject(@RequestBody User teacher) {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request,"院级管理员")){
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        if(!facultyAdminService.addTeacher(teacher)){
            return Result.create(StatusCode.ERROR,"添加失败");
        }
        return Result.create(StatusCode.OK,"添加成功");
    }

    /**
     * 院级管理员删除教师
     * @param userId
     * @return
     */
    @DeleteMapping("/delete_teacher/{userId}")
    public Result deleteSubject(@PathVariable(value = "userId") Integer userId) {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request,"院级管理员")){
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        // 判断数据是否为空
        if(!formatUtil.checkObjectNull(userId)){
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        if(!facultyAdminService.deleteTeacher(userId)){
            return Result.create(StatusCode.ERROR,"删除失败");
        }
        return Result.create(StatusCode.OK,"删除成功");
    }
}
