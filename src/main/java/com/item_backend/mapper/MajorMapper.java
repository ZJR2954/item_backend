package com.item_backend.mapper;

import com.item_backend.model.entity.Major;
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
    int addMajor(Major major);

    // 根据专业id查询专业信息
    Major searchMajorById(Integer id);

    //根据院系id查询专业列表
    List<Major> searchMajorByFacultyId(Integer faculty_id);

    //根据专业id删除专业
    int deleteMajorByMajorId(Integer major_id);

}
