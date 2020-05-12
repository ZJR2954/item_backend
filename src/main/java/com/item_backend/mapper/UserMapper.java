package com.item_backend.mapper;

import com.item_backend.model.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @Description:
 * @Author: Mt.Li
 * @Create: 2020-05-12 10:36
 */
@Repository
public interface UserMapper {

    // 根据登录信息查询用户
    User searchUserBySchoolAndJobNumber(User user);

}
