package com.item_backend.service;

import com.item_backend.model.entity.TestUser;
import org.springframework.stereotype.Service;

/**
 * @Description: 测试服务接口
 * @Author: Mt.Li
 * @Create: 2020-05-10 15:37
 */
@Service
public interface TestUserService {

    // 登录接口测试
    boolean searchUser(TestUser user);
}
