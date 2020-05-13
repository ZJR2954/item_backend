package com.item_backend.service;

import com.item_backend.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface UserService {

    // 用户登录
    Map login(User user);

    // 根据登录信息查询用户
    User searchUserByLoginMsg(User user);

    // 获取个人信息
    Map searchProfile();

}
