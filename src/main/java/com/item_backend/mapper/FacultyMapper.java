package com.item_backend.mapper;

import com.item_backend.model.entity.Faculty;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author xiao
 * @Time 2020/5/19
 * @Description TODO
 **/
@Repository
public interface FacultyMapper {

    //根据院系id查询院系
    Faculty searchFacultyByFacultyId(Integer faculty_id);

    //根据院级管理员id查询院系
    List<Faculty> searchFacultyByUId(Integer u_id);

    // 根据用户信息的学院名查询学院id
    Integer searchFacultyIdByFacultyName(@Param("facultyName") String facultyName, @Param("schoolName") String schoolName);

    // 根据院级管理员id查询学院id
    Integer searchFacultyIdByAdminId(Integer id);

}
