package com.item_backend.mapper;

import com.item_backend.model.entity.Faculty;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author xiao
 * @Time 2020/5/19
 * @Description TODO
 **/
@Repository
public interface FacultyMapper {

    //根据院级管理员id查询院系
    List<Faculty> searchFacultyByUId(Integer u_id);

}
