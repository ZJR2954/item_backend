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

    //添加院系
    int addFaculty(Faculty faculty);

    //根据院系id查询院系信息
    Faculty searchFacultyByFacultyId(Integer faculty_id);

    //根据学校名查询院系列表
    List<Faculty> searchFacultyBySchoolName(String school);

    //根据院级管理员id查询院系
    List<Faculty> searchFacultyByUId(Integer u_id);

    // 根据用户信息的学院名查询学院id
    Integer searchFacultyIdByFacultyName(@Param("facultyName") String facultyName, @Param("schoolName") String schoolName);

    //根据院系id删除院系
    int deleteFacultyByFacultyId(Integer faculty_id);

    // 更新院级管理员
    Boolean updateFacultyAdmin(@Param("adminId")Integer adminId,@Param("facultyName") String facultyName,@Param("schoolName") String schoolName);

    //更新院系信息
    int updateFaculty(Faculty faculty);

    // 批量替换院级管理员
    void batchUpdateFacultyAdmin(@Param("newAdminId") Integer newAdminId, @Param("oldAdminId") Integer oldAdminId);

}
