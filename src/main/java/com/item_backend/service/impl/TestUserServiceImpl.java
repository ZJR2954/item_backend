package com.item_backend.service.impl;

import com.item_backend.model.pojo.TestUser;
import com.item_backend.service.TestUserService;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: Mt.Li
 * @Create: 2020-05-10 15:43
 */
@Service
public class TestUserServiceImpl implements TestUserService {
    @Override
    public boolean searchUser(TestUser user) {
        if (user.getName().equals("test") && user.getPassword().equals("123456")){
            return true;
        }
        return false;
    }
}
