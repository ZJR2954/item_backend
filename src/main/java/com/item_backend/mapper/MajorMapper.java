package com.item_backend.mapper;

import com.item_backend.model.entity.Major;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author xiao
 * @Time 2020/5/17
 * @Description TODO
 **/
@Repository
public interface MajorMapper {

    //添加专业
    int saveMajor(Major major);

    // 根据专业id查询专业信息
    Major searchMajorById(Integer id);

    //根据院系id查询专业列表
    List<Major> searchMajorByFacultyId(@Param("faculty_id") Integer faculty_id);

    //根据专业id删除专业
    int deleteMajorByMajorId(@Param("major_id") Integer major_id);

}
