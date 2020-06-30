package com.item_backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.item_backend.config.JwtConfig;
import com.item_backend.config.RedisConfig;
import com.item_backend.mapper.FacultyMapper;
import com.item_backend.mapper.SchoolMapper;
import com.item_backend.mapper.UserMapper;
import com.item_backend.mapper.UserTypeMapper;
import com.item_backend.model.dto.UserDto;
import com.item_backend.model.entity.User;
import com.item_backend.model.entity.UserType;
import com.item_backend.service.UserService;
import com.item_backend.utils.EmailUtil;
import com.item_backend.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    SchoolMapper schoolMapper;

    @Autowired
    FacultyMapper facultyMapper;

    @Autowired
    HttpServletRequest request;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    EmailUtil emailUtil;

    @Resource
    JavaMailSender javaMailSender;

    /**
     * 用户登录
     *
     * @param user
     * @return map
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map login(User user) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        // 判断是否存在用户
        User user1 = searchUserByLoginMsg(user);
        if (user1 == null || user1.getJob_number() == null || !user1.getPassword().equals(user.getPassword())) {
            map.put("msg", "用户名或密码错误");
            return map;
        }
        // 判断用户是否被封禁
        if (user1.getU_state() == 0) {
            map.put("msg", "用户被已被注销");
            return map;
        } else if (user1.getU_state() == 2) {
            map.put("msg", "用户被限制登录");
            return map;
        }
        // 判断用户是否已登录
//        if (redisTemplate.hasKey(JwtConfig.REDIS_TOKEN_KEY_PREFIX + user1.getU_id())) {
//            map.put("msg", "该用户已登录");
//            return map;
//        }
        // 判断用户类型是否正确
        if (!user.getU_type().equals(user1.getU_type())) {
            map.put("msg", "请选择正确的用户类型");
            return map;
        }
        // 封装用户信息
        UserDto userDto = new UserDto();
        UserType userType = userTypeMapper.searchUserTypeByUType(user1.getU_type());
        Integer schoolId = schoolMapper.searchSchoolIdBySchoolName(user1.getU_school());
        Integer facultyId = facultyMapper.searchFacultyIdByFacultyName(user1.getU_faculty(), user1.getU_school());
        userDto.setUser(user1);
        userDto.setUserType(userType);
        userDto.setSchool_id(schoolId);
        userDto.setFaculty_id(facultyId);

        // 根据用户详细信息生成token
        final String token = jwtTokenUtil.generateToken(userDto);

        map.put("token", jwtConfig.getPrefix() + token);
        map.put("userMsg", userDto);

        // 向redis中存储个人信息
        redisTemplate.opsForValue().
                set(RedisConfig.REDIS_USER_MESSAGE + user1.getU_id(), objectMapper.writeValueAsString(userDto), jwtConfig.getTime(), TimeUnit.SECONDS);

        // 向redis中存储token（设置过期时间30min）
        redisTemplate.opsForValue().
                set(JwtConfig.REDIS_TOKEN_KEY_PREFIX + user1.getU_id(), jwtConfig.getPrefix() + token, jwtConfig.getTime(), TimeUnit.SECONDS);
        return map;
    }

    /**
     * 根据登录信息查询用户
     *
     * @param user
     * @return User
     */
    @Override
    public User searchUserByLoginMsg(User user) {
        return userMapper.searchUserBySchoolAndJobNumber(user);
    }

    /**
     * 退出登录
     * 删除redis中的key
     */
    public boolean logout() {
        try {
            Integer uId = jwtTokenUtil.getUIDFromRequest(request);
            redisTemplate.delete(JwtConfig.REDIS_TOKEN_KEY_PREFIX + uId);
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * 获取个人信息
     *
     * @param
     * @return Map
     * @Author xiao
     */
    @Override
    public Map getProfile(String token) {
        Integer u_id = jwtTokenUtil.getUIDFromToken(token.substring(jwtConfig.getPrefix().length()));
        Map<String, Object> map = new HashMap<>();
        // 判断u_id的值
        if (u_id == null) {
            map.put("msg", "无效的token");
            return map;
        }
        String userDtoJSON = redisTemplate.opsForValue().get(RedisConfig.REDIS_USER_MESSAGE + u_id);
        UserDto userDto = JSON.parseObject(userDtoJSON, UserDto.class);

        map.put("userMsg", userDto);
        return map;
    }

    /**
     * 修改个人信息
     *
     * @param
     * @return Map
     * @Author xiao
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map updateUserDetail(String token, User user) throws JsonProcessingException {
        Integer u_id = jwtTokenUtil.getUIDFromToken(token.substring(jwtConfig.getPrefix().length()));
        Map<String, Object> map = new HashMap<>();
        // 判断u_id的值
        if (u_id != user.getU_id()) {
            map.put("msg", "当前修改的不是本人信息，请重新登录后再试一次");
            return map;
        }
        int flag = userMapper.updateUser(user);
        if (flag <= 0) {
            map.put("msg", "修改个人信息失败");
            return map;
        }

        // 更新redis中存储的个人信息
        String userDtoJSON = redisTemplate.opsForValue().get(RedisConfig.REDIS_USER_MESSAGE + u_id);
        UserDto userDto = JSON.parseObject(userDtoJSON, UserDto.class);
        userDto.setUser(user);
        redisTemplate.opsForValue().
                set(RedisConfig.REDIS_USER_MESSAGE + u_id, objectMapper.writeValueAsString(userDto), jwtConfig.getTime(), TimeUnit.SECONDS);

        return map;
    }

    /**
     * 修改登录密码
     *
     * @param
     * @return Map
     * @Author xiao
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map changePassword(String token, String oldPassword, String newPassword) {
        Integer u_id = jwtTokenUtil.getUIDFromToken(token.substring(jwtConfig.getPrefix().length()));
        Map<String, Object> map = new HashMap<>();
        // 判断u_id的值
        if (u_id == null) {
            map.put("msg", "无效的token");
            return map;
        }
        //判断旧密码是否正确
        User user = userMapper.searchUserByUIdAndPassword(u_id, oldPassword);
        if (user == null) {
            map.put("msg", "旧密码错误");
            return map;
        }
        int flag = userMapper.changePassword(u_id, newPassword);
        if (flag <= 0) {
            map.put("msg", "修改密码失败");
            return map;
        }
        return map;
    }

    public List<UserDto> searchUserByConditions(User user, Integer page, Integer showCount) {
        List<UserDto> userDtoList = new ArrayList<>();
        List<User> users = userMapper.searchUserByConditions(user, (page - 1) * showCount, showCount);
        for (int i = 0; i < users.size(); i++) {
            UserDto userDto = new UserDto();
            userDto.setUser(users.get(i));
            userDtoList.add(userDto);
            UserType userType = userTypeMapper.searchUserTypeByUType(users.get(i).getU_type());
            userDto.setUserType(userType);
        }
        return userDtoList;
    }

    public int getUserCount(User user) {
        int i = userMapper.getUserCountByConditions(user);
        return i;
    }

    @Override
    public boolean getVerificationCode(Integer u_id) {
        User user = userMapper.searchUserByUId(u_id);
        if (user != null) {
            String email = user.getEmail();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("1714015226@qq.com");
            message.setTo(email);
            message.setSubject("试题库账户改密验证码");
            String checkCode = emailUtil.getCheckCode();
            message.setText("试题库账户改密验证码为：" + checkCode + "，此验证码10分钟内有效，请不要将验证码泄露给陌生人！");
            javaMailSender.send(message);
            // 向redis中存储验证码（设置过期时间10min）
            redisTemplate.opsForValue().
                    set(RedisConfig.REDIS_VERIFICATION_CODE + user.getU_id(), checkCode, 600, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }

    /**
     * 通过邮箱验证码修改登录密码
     *
     * @param u_id
     * @param verificationCode
     * @param newPassword
     * @return
     */
    @Override
    public Map changePasswordByVerificationCode(Integer u_id, String verificationCode, String newPassword) {
        Map<String, Object> map = new HashMap<>();
        String verificationCodeJSON = redisTemplate.opsForValue().get(RedisConfig.REDIS_VERIFICATION_CODE + u_id);
        if (verificationCodeJSON == null) {
            map.put("msg", "验证码过期，请重新获取");
            return map;
        }
        if (!verificationCodeJSON.equals(verificationCode)) {
            map.put("msg", "验证码错误，修改登录密码失败");
            return map;
        }
        if (userMapper.changePassword(u_id, newPassword) <= 0) {
            map.put("msg", "修改密码失败");
        }
        redisTemplate.delete(RedisConfig.REDIS_VERIFICATION_CODE + u_id);
        return map;
    }

}
