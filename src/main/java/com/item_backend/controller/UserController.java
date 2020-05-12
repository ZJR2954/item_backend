package com.item_backend.controller;

import com.item_backend.model.entity.User;
import com.item_backend.model.pojo.Result;
import com.item_backend.model.pojo.StatusCode;
import com.item_backend.service.impl.UserServiceImpl;
import com.item_backend.utils.FormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description:
 * @Author: Mt.Li
 * @Create: 2020-05-12 11:15
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    FormatUtil formatUtil;

    @PostMapping("/login")
    public Result login(User user) {
        if (!formatUtil.checkStringNull(user.getJob_number(), user.getPassword())) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        try {
            Map map = userServiceImpl.login(user);
            if (map.get("msg")!= null){
                return Result.create(StatusCode.LOGINERROR, map.get("msg").toString());
            }
            return Result.create(StatusCode.OK, "登录成功", map);
        } catch (RuntimeException re) {
            return Result.create(StatusCode.LOGINERROR, re.getMessage());
        }
    }
}
