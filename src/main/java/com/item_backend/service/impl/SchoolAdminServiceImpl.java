package com.item_backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.item_backend.mapper.FacultyMapper;
import com.item_backend.mapper.SchoolMapper;
import com.item_backend.mapper.UserMapper;
import com.item_backend.model.entity.User;
import com.item_backend.service.SchoolAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: Mt.Li
 * @Create: 2020-05-28 11:50
 */
@Service
public class SchoolAdminServiceImpl implements SchoolAdminService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    FacultyMapper facultyMapper;

    @Autowired
    SchoolMapper schoolMapper;

    @Autowired
    FacultyServiceImpl facultyService;

    // 查询该学校的院系管理员数量
    public int getFacultyAdminCount(Integer schoolId){
        return userMapper.getFacultyAdminCount(schoolId);
    }

    // 查询院级管理员列表
    public List<User> searchFacultyAdminList(Integer schoolId, Integer page, Integer showCount){
        return userMapper.searchUserListBySchoolId(schoolId,(page - 1) * showCount,showCount);
    }

    // 添加院级管理员
    @Transactional(rollbackFor = Exception.class)
    public boolean addFacultyAdmin(User facultyAdmin) throws JsonProcessingException {

        if(userMapper.addUser(facultyAdmin)){
            User oldUser  = userMapper.getUserByFacultyAndSchool(facultyAdmin.getU_faculty(), facultyAdmin.getU_school());
            // 查询插入的的用户id
            int newId = userMapper.searchUserBySchoolAndJobNumber(facultyAdmin).getU_id();
            //更新对应的faculty列表（后期操作redis）
            if(facultyMapper.updateFacultyAdmin(newId,facultyAdmin.getU_faculty(),facultyAdmin.getU_school())){
                if (oldUser.getU_type() > 2){
                    // 将旧管理员用户类型变更为6——普通用户
                    oldUser.setU_type(6);
                    userMapper.updateUser(oldUser);
                }
                facultyService.updateFacultyInRedis(facultyAdmin.getU_school());
                return true;
            }
            return false;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String,String> editUserType(User user) throws JsonProcessingException {
        Map<String,String> map = new HashMap<>();
        // 更改前先查询原有用户信息
        User goalUser = userMapper.searchUserBySchoolAndJobNumber(user);
        // 查询要挤掉的院级管理员信息（不管是否为院级，此操作不影响）
        User oldUser = userMapper.getUserByFacultyAndSchool(user.getU_faculty(), user.getU_school());
        //oldUser.setU_faculty(user.getU_faculty()); // 如果是校级以上的用户是没有院级信息的，这里手动添加，方便后边比较

        // 判断是否重复修改（即u_type不变，院系不变）
        if(goalUser.getU_type() == user.getU_type() && goalUser.getU_faculty().equals(user.getU_faculty())){
             map.put("repeat","目标已经是该角色!");
            return map;
        }
        // u_type不变，院系变，为平调
        else if(goalUser.getU_type() == user.getU_type() && !goalUser.getU_faculty().equals(user.getU_faculty())){
            // 院级管理员平级调任
            Integer presidentId = schoolMapper.searchSchoolIdBySchoolName(user.getU_school());
            // 将当前用户的职位暂定为校长
            if(facultyMapper.updateFacultyAdmin(presidentId, goalUser.getU_faculty(),goalUser.getU_school())){
                // 将当前用户平调
                if(facultyMapper.updateFacultyAdmin(user.getU_id(),user.getU_faculty(),user.getU_school())){
                    // 将旧管理员用户类型(除了校长和超级)变更为6——普通用户
                    if (oldUser.getU_type()>2){
                        oldUser.setU_type(6);
                        userMapper.updateUser(oldUser);
                    }
                    if (userMapper.updateUser(user) < 1){
                        return null;
                    }
                    facultyService.updateFacultyInRedis(user.getU_school());
                    map.put("OK","更改成功!");
                    return map;
                }
            }
        }
        // 修改的用户为降级(只是撤职)
        if(goalUser.getU_type() == 3 && user.getU_type() != 3){
            // 让校长暂代职位
            Integer presidentId = schoolMapper.searchSchoolIdBySchoolName(user.getU_school());
            if(facultyMapper.updateFacultyAdmin(presidentId, user.getU_faculty(),user.getU_school())){
                // 改变原先院级管理员的类型
                userMapper.updateUser(user);
                facultyService.updateFacultyInRedis(user.getU_school());
                map.put("OK","更改成功!");
                return map;
            }else {
                return null;
            }
        }else if(user.getU_type() == 3){ // 如果要升级为院级管理员

            if(facultyMapper.updateFacultyAdmin(user.getU_id(),user.getU_faculty(),user.getU_school())){
                // 将旧管理员用户类型(除了校长)变更为6——普通用户
                if (oldUser.getU_type()>2){
                    oldUser.setU_type(6);
                    userMapper.updateUser(oldUser);
                }
                user.setOperate_subject(null);
                if (userMapper.updateUser(user) < 1){
                    return null;
                }
                facultyService.updateFacultyInRedis(user.getU_school());
                map.put("OK","更改成功!");
                return map;
            }

        }
        // 不升级为管理员，也不是院级管理员降级则直接更改类型
        if(userMapper.updateUser(user) == 1){
            facultyService.updateFacultyInRedis(user.getU_school());
            map.put("OK","更改成功!");
            return map;
        }
        return null;
    }
}
