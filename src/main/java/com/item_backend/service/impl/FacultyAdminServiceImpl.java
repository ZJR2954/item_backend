package com.item_backend.service.impl;

import com.item_backend.mapper.UserMapper;
import com.item_backend.model.dto.TeacherDto;
import com.item_backend.model.entity.User;
import com.item_backend.service.FacultyAdminService;
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
public class FacultyAdminServiceImpl implements FacultyAdminService {

    @Autowired
    UserMapper userMapper;

    // 查询该学校的院系管理员数量
    public int getTeacherCount(Integer facultyId){
        return userMapper.getTeacherCount(facultyId);
    }

    // 查询教师列表
    public List<TeacherDto> searchTeacherList(Integer facultyId, Integer schoolId, Integer page, Integer showCount){
        return userMapper.searchUserListByFacultyId(facultyId, schoolId, (page - 1) * showCount,showCount);
    }

    // 添加院级管理员
    public boolean addTeacher(User teacher){
        if(userMapper.addUser(teacher)){
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String,String> editUserType(User user){
        Map<String,String> map = new HashMap<>();
        // 判断是否重复修改（即u_type不变，院系不变）
        User goalUser = userMapper.searchUserBySchoolAndJobNumber(user);
        if(goalUser.getU_type() == user.getU_type() && goalUser.getU_faculty().equals(user.getU_faculty())){
            map.put("repeat","目标已经是该角色!");
            return map;
        }
        if(!(userMapper.updateUser(user) <= 0)){
            map.put("OK","保存成功!");
            return map;
        }
        return null;
    }

}
