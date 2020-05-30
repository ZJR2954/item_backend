package com.item_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.item_backend.model.dto.SchoolDto;
import com.item_backend.model.entity.School;
import com.item_backend.model.pojo.PageResult;
import org.springframework.stereotype.Service;

/**
 * @Author xiao
 * @Time 2020/5/30
 * @Description TODO
 **/
@Service
public interface SchoolService {
    //查询学校列表
    PageResult<SchoolDto> searchSchoolList(Integer page, Integer showCount) throws JsonProcessingException;

    //添加学校
    Boolean addSchool(String token, School school) throws JsonProcessingException;

    //根据学校id删除学校
    Boolean deleteSchoolBySchoolId(Integer school_id) throws JsonProcessingException;

    //更新redis中School信息
    void updateSchoolInRedis() throws JsonProcessingException;
}
