package com.item_backend.mapper;

import com.item_backend.model.entity.School;
import org.springframework.stereotype.Repository;

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
}
