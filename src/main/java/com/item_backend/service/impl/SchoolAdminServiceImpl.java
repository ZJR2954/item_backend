package com.item_backend.service.impl;

import com.item_backend.mapper.UserMapper;
import com.item_backend.model.entity.User;
import com.item_backend.service.SchoolAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: Mt.Li
 * @Create: 2020-05-28 11:50
 */
@Service
public class SchoolAdminServiceImpl implements SchoolAdminService {

    @Autowired
    UserMapper userMapper;

    // 查询该学校的院系管理员数量
    public int getFacultyAdminCount(Integer schoolId){
        return userMapper.getFacultyAdminCount(schoolId);
    }

    // 查询院级管理员列表
    public List<User> searchFacultyAdminList(Integer schoolId, Integer page, Integer showCount){
        return userMapper.searchUserListBySchoolId(schoolId,(page - 1) * showCount,showCount);
    }

    // 添加院级管理员
    public boolean addFacultyAdmin(User facultyAdmin){
        if(userMapper.addUser(facultyAdmin)){
            return true;
        }
        return false;
    }

    // 删除院级管理员
    public boolean deleteFacultyAdmin(Integer userId){
        if(userMapper.deleteUser(userId)){
            return true;
        }
        return false;
    }
}
