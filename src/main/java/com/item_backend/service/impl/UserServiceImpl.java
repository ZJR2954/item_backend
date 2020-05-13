package com.item_backend.service.impl;

import com.item_backend.config.JwtConfig;
import com.item_backend.mapper.UserMapper;
import com.item_backend.mapper.UserTypeMapper;
import com.item_backend.model.dto.UserDto;
import com.item_backend.model.entity.User;
import com.item_backend.model.entity.UserType;
import com.item_backend.service.UserService;
import com.item_backend.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: Mt.Li & xiao
 * @Create: 2020-05-12 11:16
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserTypeMapper userTypeMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 用户登录
     * @param user
     * @return map
     */
    @Override
    public Map login(User user) {
        Map<String, Object> map = new HashMap<>();
        // 判断是否存在用户
        User user1 = searchUserByLoginMsg(user);
        if(user1 == null || user1.getJob_number() == null || !user1.getPassword().equals(user.getPassword())) {
            map.put("msg","用户名或密码错误");
            return map;
        }
        // 判断用户是否被封禁
        if (user1.getU_state() == 0){
            map.put("msg","用户被已被注销");
            return map;
        } else if (user1.getU_state() == 2){
            map.put("msg","用户被限制登录");
            return map;
        }
        // 判断用户是否已登录
        if(redisTemplate.hasKey(JwtConfig.REDIS_TOKEN_KEY_PREFIX + user1.getU_id())){
            map.put("msg","该用户已登录");
            return map;
        }
        // 判断用户类型是否正确
        if(!user.getU_type().equals(user1.getU_type())){
            map.put("msg","请选择正确的用户类型");
            return map;
        }
        UserDto userDto = new UserDto();
        UserType userType = userTypeMapper.searchUserTypeByUType(user1.getU_type());
        userDto.setUser(user1);
        userDto.setUserType(userType);

        // 根据用户详细信息生成token
        final String token = jwtTokenUtil.generateToken(userDto);

        map.put("token", jwtConfig.getPrefix() + token);
        map.put("userMsg", userDto);

        // 向redis中存储token（设置过期时间30min）
        redisTemplate.opsForValue().
                set(JwtConfig.REDIS_TOKEN_KEY_PREFIX + user1.getU_id(), jwtConfig.getPrefix() + token, jwtConfig.getTime(), TimeUnit.SECONDS);
        return map;
    }

    /**
     * 根据登录信息查询用户
     * @param user
     * @return User
     */
    @Override
    public User searchUserByLoginMsg(User user) {
        return userMapper.searchUserBySchoolAndJobNumber(user);
    }

    /**
     * 获取个人信息
     * @param
     * @return Map
     */
    @Override
    public Map searchProfile() {
        Map<String, Object> map = new HashMap<>();
        // 判断用户是否已登录
        if(!redisTemplate.hasKey(JwtConfig.REDIS_TOKEN_KEY_PREFIX + "1")){
            map.put("msg","请登录后获取个人信息");
            return map;
        }
        UserDto userDto = new UserDto();
//        UserType userType = userTypeMapper.searchUserTypeByUType(user1.getU_type());
//        userDto.setUser(user1);
//        userDto.setUserType(userType);
        return null;
    }


}
