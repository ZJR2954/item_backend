package com.item_backend.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.item_backend.model.entity.User;
import com.item_backend.model.pojo.Result;
import com.item_backend.model.pojo.StatusCode;
import com.item_backend.service.impl.UserServiceImpl;
import com.item_backend.utils.FormatUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Description:
 * @Author: Mt.Li & xiao
 * @Create: 2020-05-12 11:15
 */
@Api(tags = "User_api", description = "User_api", basePath = "/user")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    FormatUtil formatUtil;

    /**
     * 用户登录
     *
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
                map = userServiceImpl.login(user);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if (map.containsKey("msg")) {
                return Result.create(StatusCode.LOGINERROR, map.get("msg").toString());
            }
            return Result.create(StatusCode.OK, "登录成功", map);
        } catch (RuntimeException re) {
            return Result.create(StatusCode.LOGINERROR, re.getMessage());
        }
    }

    @ApiOperation(value = "个人信息", notes = "Result：状态码+msg+(data)", httpMethod = "GET")
    @GetMapping("/profile")
    public Result profile(HttpServletRequest request) {
        String token = request.getHeader("token");
        try {
            Map map = userServiceImpl.getProfile(token);
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
        String token = request.getHeader("token");
        try {
            Map map = userServiceImpl.updateUserDetail(token, user);
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
        String token = request.getHeader("token");
        try {
            Map map = userServiceImpl.changePassword(token, reqMap.get("oldPassword").toString(), reqMap.get("newPassword").toString());
            if (map.get("msg") != null) {
                return Result.create(StatusCode.ACCESSERROR, map.get("msg").toString());
            }
            return Result.create(StatusCode.OK, "修改登录密码成功", map);
        } catch (Exception re) {
            return Result.create(StatusCode.ERROR, re.getMessage());
        }
    }

}
