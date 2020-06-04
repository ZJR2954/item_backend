package com.item_backend.mapper;

import com.item_backend.model.entity.School;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author: Mt.Li
 */
@Repository
public interface SchoolMapper {

    // 根据用户信息中的学校名查询学校id
    Integer searchSchoolIdBySchoolName(String schoolName);

    // 根据校级管理员的id查询学校id
    Integer searchSchoolIdByAdminId(Integer id);

    //根据学校名查询学校
    School searchSchoolBySchoolName(String schoolName);

    //查询学校列表
    List<School> searchSchoolList();

    //添加学校
    int addSchool(School school);

    //根据学校id删除学校
    int deleteSchoolBySchoolId(Integer school_id);

    //更新学校学信息
    int updateSchool(School school);
}
