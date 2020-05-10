package com.item_backend.controller;

import com.item_backend.model.entity.Result;
import com.item_backend.model.entity.StatusCode;
import com.item_backend.model.pojo.TestUser;
import com.item_backend.service.impl.TestUserServiceImpl;
import com.item_backend.utils.FormatUtil;
import com.item_backend.utils.LoggerUtil;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: Mt.Li
 * @Create: 2020-05-10 15:32
 */
@Api(tags = "测试api", description = "测试api", basePath = "/test")
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    TestUserServiceImpl testUserService;

    @Autowired
    FormatUtil formatUtil;

    private Logger logger = LoggerUtil.loggerFactory(this.getClass());

    @PostMapping("/login")
    public Result loginTest(TestUser user){
        logger.info("登录测试开始");
        try{
            if (!formatUtil.checkStringNull(user.getName(),user.getPassword())){
                logger.info("对象为空");
                return Result.create(StatusCode.ERROR,"参数错误");
            }else if (testUserService.searchUser(user)){
                Map<String, Object> map = new HashMap<>(2);
                map.put("username",user.getName());
                map.put("password",user.getPassword());
                logger.info(user.getName() + "登录成功！");
                return Result.create(StatusCode.OK,"登陆成功",map);
            }
        }catch (NullPointerException e){
            logger.info("参数异常");
            return Result.create(StatusCode.ERROR,"参数异常");
        }
        logger.info("用户名或密码错误");
        return Result.create(StatusCode.LOGINERROR,"用户名或密码错误");
    }

}
