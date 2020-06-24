package com.item_backend.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.item_backend.model.dto.UserDto;
import com.item_backend.model.entity.User;
import com.item_backend.model.pojo.PageResult;
import com.item_backend.model.pojo.Result;
import com.item_backend.model.pojo.StatusCode;
import com.item_backend.service.impl.*;
import com.item_backend.utils.FormatUtil;
import com.item_backend.utils.JwtTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Description: 用户控制层
 * @Author: Mt.Li & xiao
 * @Create: 2020-05-12 11:15
 */
@Api(tags = "User_api", description = "User_api", basePath = "/user")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @Autowired
    SubjectServiceImpl subjectService;

    @Autowired
    ChapterServiceImpl chapterService;

    @Autowired
    SchoolAdminServiceImpl schoolAdminService;

    @Autowired
    FacultyAdminServiceImpl facultyAdminService;

    @Autowired
    SuperAdminServiceImpl superAdminService;

    @Autowired
    QuestionTypeServiceImpl questionTypeService;

    @Autowired
    FormatUtil formatUtil;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    HttpServletRequest request;

    /**
     * 用户登录
     * @param user
     * @return Result：状态码+msg+(data)
     */
    @ApiOperation(value = "用户登录", notes = "Result：状态码+msg+(data)", httpMethod = "POST")
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        if (!formatUtil.checkStringNull(user.getJob_number(), user.getPassword())) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        try {
            Map map = null;
            try {
                map = userService.login(user);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if (map.containsKey("msg")){
                return Result.create(StatusCode.LOGINERROR, map.get("msg").toString());
            }
            return Result.create(StatusCode.OK, "登录成功", map);
        } catch (RuntimeException re) {
            return Result.create(StatusCode.LOGINERROR, re.getMessage());
        }
    }

    /**
     * 用户登出
     * @return Result：状态码+msg
     */
    @ApiOperation(value = "用户登出",notes = "Result：状态码+msg;删除redis中的key", httpMethod = "GET")
    @GetMapping("/logout")
    public Result logout() {
        if(userService.logout()) {
            return Result.create(StatusCode.OK, "退出成功");
        }
        return Result.create(StatusCode.ERROR, "退出失败");
    }

    @ApiOperation(value = "个人信息", notes = "Result：状态码+msg+(data)", httpMethod = "GET")
    @GetMapping("/profile")
    public Result profile(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        try {
            Map map = userService.getProfile(token);
            if (map.get("msg") != null) {
                return Result.create(StatusCode.ACCESSERROR, map.get("msg").toString());
            }
            return Result.create(StatusCode.OK, "获取个人信息成功", map);
        } catch (RuntimeException re) {
            return Result.create(StatusCode.ERROR, re.getMessage());
        }
    }

    @ApiOperation(value = "修改个人信息", notes = "Result：状态码+msg+(data)", httpMethod = "PUT")
    @PutMapping("/update")
    public Result updateUserDetail(HttpServletRequest request, @RequestBody User user) {
        String token = request.getHeader("Authorization");
        try {
            Map map = userService.updateUserDetail(token, user);
            if (map.get("msg") != null) {
                return Result.create(StatusCode.ACCESSERROR, map.get("msg").toString());
            }
            return Result.create(StatusCode.OK, "修改个人信息成功", map);
        } catch (Exception re) {
            return Result.create(StatusCode.ERROR, re.getMessage());
        }
    }

    @ApiOperation(value = "修改登录密码", notes = "Result：状态码+msg+(data)", httpMethod = "PUT")
    @PutMapping("/change_password")
    public Result changePassword(HttpServletRequest request, @RequestBody Map reqMap) {
        String token = request.getHeader("Authorization");
        try {
            Map map = userService.changePassword(token, reqMap.get("oldPassword").toString(), reqMap.get("newPassword").toString());
            if (map.get("msg") != null) {
                return Result.create(StatusCode.ACCESSERROR, map.get("msg").toString());
            }
            return Result.create(StatusCode.OK, "修改登录密码成功", map);
        } catch (Exception re) {
            return Result.create(StatusCode.ERROR, re.getMessage());
        }
    }

    /**
     * 管理员通过条件查询用户
     * @param user
     * @param page
     * @param showCount
     * @return
     */
    @PostMapping("/search_user/{page}/{showCount}")
    public Result searchUserByConditions(@RequestBody User user, @PathVariable("page") Integer page,
                                         @PathVariable("showCount") Integer showCount){
        List<UserDto> userDtoList = userService.searchUserByConditions(user, page, showCount);
        Integer count = userService.getUserCount(user);
        if(userDtoList.size() == 0){
            return Result.create(StatusCode.ERROR,"查询结果为空");
        }
        PageResult<UserDto> pageResult = new PageResult<>(count, userDtoList);
        return Result.create(StatusCode.OK, "查询成功", pageResult);
    }


    /**
     * 管理员修改下级用户类型
     * @param user
     * @return
     */
    @PutMapping("/edit_user_type")
    public Result editUserType(@RequestBody User user) throws JsonProcessingException {
        if (jwtTokenUtil.checkUserType(request, "超级管理员")) {
            Map<String, String> map = superAdminService.editUser(user);
            if (map.containsKey("repeat"))
                return Result.create(StatusCode.OK, map.get("repeat"));
            if (map.containsKey("OK"))
                return Result.create(StatusCode.OK, map.get("OK"));
            else {
                return Result.create(StatusCode.ERROR, "修改失败!");
            }
        }
        if (jwtTokenUtil.checkUserType(request, "校级管理员")) {
            Map<String, String> map = schoolAdminService.editUserType(user);
            if (map.containsKey("repeat"))
                return Result.create(StatusCode.OK, map.get("repeat"));
            if (map.containsKey("OK"))
                return Result.create(StatusCode.OK, map.get("OK"));
            else {
                return Result.create(StatusCode.ERROR, "修改失败!");
            }
        }
        if (jwtTokenUtil.checkUserType(request, "院级管理员")) {
            Map<String, String> map = facultyAdminService.editUserType(user);
            if (map.containsKey("repeat"))
                return Result.create(StatusCode.OK, map.get("repeat"));
            if (map.containsKey("OK"))
                return Result.create(StatusCode.OK, map.get("OK"));
            else {
                return Result.create(StatusCode.ERROR, "修改失败!");
            }
        }
        return Result.create(StatusCode.ACCESSERROR, "无权限");
    }

}
