package com.item_backend.mapper;

import com.item_backend.model.dto.TeacherDto;
import com.item_backend.model.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author: Mt.Li
 * @Create: 2020-05-12 10:36
 */
@Repository
public interface UserMapper {

    // 根据登录信息查询用户
    User searchUserBySchoolAndJobNumber(User user);

    // 添加用户
    Boolean addUser(User user);

    //更新用户信息
    int updateUser(User user);

    // 根据学校id获取院级管理员数量
    int getFacultyAdminCount(Integer id);

    // 根据院系id获取教师数量
    int getTeacherCount(Integer id);

    // 根据条件获取用户数量
    int getUserCountByConditions( User user);

    // 通过身份证号查询用户id
    int getUserIdByIdNumber(String idNumber);

    // 通过院系名和学校查询管理员
    User getUserByFacultyAndSchool(@Param("facultyName") String facultyName,@Param("schoolName") String schoolName);

    //根据用户id和登录密码查询用户
    User searchUserByUIdAndPassword(@Param("u_id") Integer u_id, @Param("password") String password);

    //修改用户登录密码
    int changePassword(@Param("u_id") Integer u_id, @Param("newPassword") String newPassword);

    // 根据学校id查询院级管理员用户列表
    List<User> searchUserListBySchoolId(@Param("school_id") Integer school_id,
                                        @Param("start") Integer start,@Param("showCount") Integer showCount);

    // 根据院系id查询教师列表
    List<TeacherDto> searchUserListByFacultyId(@Param("faculty_id") Integer faculty_id,@Param("school_id") Integer school_id,
                                               @Param("start") Integer start, @Param("showCount") Integer showCount);

    // 根据条件查询用户列表
    List<User> searchUserByConditions(@Param("user") User user, @Param("start") Integer start, @Param("showCount")Integer showCount);
}
