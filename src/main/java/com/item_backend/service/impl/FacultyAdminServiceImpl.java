package com.item_backend.service.impl;

import com.item_backend.mapper.UserMapper;
import com.item_backend.model.dto.TeacherDto;
import com.item_backend.model.entity.User;
import com.item_backend.service.FacultyAdminService;
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
public class FacultyAdminServiceImpl implements FacultyAdminService {

    @Autowired
    UserMapper userMapper;

    // 查询该学校的院系管理员数量
    public int getTeacherCount(Integer facultyId){
        return userMapper.getTeacherCount(facultyId);
    }

    // 查询院级管理员列表
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

}
