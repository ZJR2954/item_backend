package com.item_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.item_backend.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface SchoolAdminService {

    // 查询对应学校的院级管理员数量
    int getFacultyAdminCount(Integer schoolId);

    // 查询院级管理员列表
    List<User> searchFacultyAdminList(Integer schoolId, Integer page, Integer showCount);

    // 添加院级管理员
    boolean addFacultyAdmin(User facultyAdmin);

    // 修改校级管理员下级用户类型
    Map editUserType(User user) throws JsonProcessingException;

}
