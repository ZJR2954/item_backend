package com.item_backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.item_backend.mapper.FacultyMapper;
import com.item_backend.mapper.SchoolMapper;
import com.item_backend.mapper.UserMapper;
import com.item_backend.model.entity.User;
import com.item_backend.service.SuperAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: Mt.Li
 * @Create: 2020-06-05 17:08
 */
@Service
public class SuperAdminServiceImpl implements SuperAdminService {

    @Autowired
    SchoolMapper schoolMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    FacultyMapper facultyMapper;

    @Autowired
    SchoolServiceImpl schoolService;

    @Autowired
    FacultyServiceImpl facultyService;

    @Autowired
    SchoolAdminServiceImpl schoolAdminService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addSchoolAdmin(User schoolAdmin) {
        if (userMapper.addUser(schoolAdmin)) {
            User oldUser = userMapper.searchUserByUId(userMapper.getUserIdBySchoolAdmin(schoolAdmin.getU_school()));
            // 查询插入的的用户id
            int newId = userMapper.searchUserBySchoolAndJobNumber(schoolAdmin).getU_id();
            //更新对应的school列表（后期操作redis）
            if (!(schoolMapper.updateSchoolAdmin(newId, schoolAdmin.getU_school()) <= 0)) {
                if (oldUser.getU_type() > 1) {
                    // 将旧管理员用户类型变更为6——普通用户
                    oldUser.setU_type(6);
                    userMapper.updateUser(oldUser);
                }
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> editUser(User user) throws JsonProcessingException {
        // 目前仅仅写了成功的返回结果，返回情况可能多变，暂时以麻烦的方式返回
        Map<String, String> map = new HashMap();
        // 更改之前根据u_id先查询用户信息
        User user1 = userMapper.searchUserByUId(user.getU_id());

        // 如果类型、学校、院系没有改变，则调用普通的用户信息更改业务
        if (user1.getU_type() == user.getU_type() && user1.getU_school().equals(user.getU_school()) && user1.getU_faculty() == user.getU_faculty()) {
            int flag = userMapper.updateUser(user);
            if (flag > 0) {
                map.put("OK", "保存成功");
                return map;
            }
        }
        // 同一个学校内进行职务变动
        if (user1.getU_school().equals(user.getU_school()) && (user.getU_type() != user1.getU_type() || user.getU_faculty() != user1.getU_faculty())) {
            // 如果是校级以下人员的职务变动
            if (user1.getU_type() > 2) {
                // 校级以下的用户升任替代校级，下级用户类型变为2，校级用户变为6
                // 如果是院级管理员，则院级管理员由新任校级暂代，即不变
                if (user.getU_type() == 2) {
                    user.setOperate_subject(null);
                    int schoolAdminId = userMapper.getUserIdBySchoolAdmin(user.getU_school());
                    if (schoolAdminId == 1) {
                        userMapper.updateUser(user);
                        schoolMapper.updateSchoolAdmin(user.getU_id(), user.getU_school());
                        facultyMapper.batchUpdateFacultyAdmin(user.getU_id(), 1);
                        facultyService.updateFacultyInRedis(user.getU_school());
                        map.put("OK", "保存成功");
                        return map;
                    }
                    User u = userMapper.searchUserByUId(schoolAdminId);
                    u.setU_type(6);
                    userMapper.updateUser(user);
                    schoolMapper.updateSchoolAdmin(user.getU_id(), user.getU_school());
                    facultyMapper.batchUpdateFacultyAdmin(user.getU_id(), schoolAdminId);
                    facultyService.updateFacultyInRedis(user.getU_school());
                    userMapper.updateUser(u);

                    // 添加返回结果
                    map.put("OK", "保存成功");
                    return map;
                } else {// 以上不是，则调用校级管理员修改用户的方法
                    map = schoolAdminService.editUserType(user);
                    return map;
                }
            } else if (user1.getU_type() == 2) {// 校级管理员的变动
                // 1、只是撤职，校级类型变为6，由超管暂代
                if (user.getU_type() == 6) {
                    userMapper.updateUser(user);
                    schoolMapper.updateSchoolAdmin(1, user.getU_school());
                    facultyMapper.batchUpdateFacultyAdmin(1, user.getU_id());
                    schoolService.updateSchoolInRedis();
                    map.put("OK", "保存成功");
                    return map;
                } else if (user.getU_type() == 3) {// 2、校级降职为院级
                    User facultyAdmin = userMapper.getUserByFacultyAndSchool(user.getU_faculty(), user.getU_school());
                    facultyAdmin.setU_type(6);
                    userMapper.updateUser(user);
                    facultyMapper.updateFacultyAdmin(user.getU_id(), user.getU_faculty(), user.getU_school());
                    schoolMapper.updateSchoolAdmin(1, user.getU_school());
                    facultyMapper.batchUpdateFacultyAdmin(1, user.getU_id());
                    userMapper.updateUser(facultyAdmin);
                    schoolService.updateSchoolInRedis();
                    map.put("OK", "保存成功");
                    return map;
                } else {
                    // 降为其他用户（教师）
                    userMapper.updateUser(user);
                    schoolMapper.updateSchoolAdmin(1, user.getU_school());
                    facultyMapper.batchUpdateFacultyAdmin(1, user.getU_id());
                    schoolService.updateSchoolInRedis();
                    map.put("OK", "保存成功");
                    return map;
                }
            }
        }
        // 不同学校内进行变动
        // 此处业务太复杂，太烧脑，为了性命着想，留与以后迭代
        // 目前解决方案为，前端超管查询用户详情时，学校一栏不得修改
        return null;
    }

}
