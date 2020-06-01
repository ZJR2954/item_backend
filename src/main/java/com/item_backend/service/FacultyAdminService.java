package com.item_backend.service;

import com.item_backend.model.dto.TeacherDto;
import com.item_backend.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FacultyAdminService {

    // 查询对应学院的教师数量
    int getTeacherCount(Integer facultyId);

    // 查询教师列表
    List<TeacherDto> searchTeacherList(Integer facultyId, Integer schoolId, Integer page, Integer showCount);

    // 添加教师
    boolean addTeacher(User teacher);

}
