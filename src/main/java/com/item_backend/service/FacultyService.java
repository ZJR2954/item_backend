package com.item_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.item_backend.model.dto.FacultyDto;
import com.item_backend.model.entity.Faculty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author xiao
 * @Time 2020/5/28
 * @Description TODO
 **/
@Service
public interface FacultyService {

    //根据学校名查询院系列表
    List<FacultyDto> searchFacultyListBySchoolName(String schoolName, Integer page, Integer showCount) throws JsonProcessingException;

    //添加院系
    Boolean addFaculty(String token, Faculty faculty) throws JsonProcessingException;

    //根据院系id删除院系
    Boolean deleteFacultyByFacultyId(String schoolName, Integer faculty_id) throws JsonProcessingException;

    //更新redis中Faculty信息
    void updateFacultyInRedis(String schoolName) throws JsonProcessingException;

}
