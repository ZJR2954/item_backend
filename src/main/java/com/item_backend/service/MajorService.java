package com.item_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.item_backend.model.entity.Major;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author xiao
 * @Time 2020/5/17
 * @Description TODO
 **/
@Service
public interface MajorService {

    //根据院系id查询专业列表
    Map searchMajorByFacultyId(Integer faculty_id);

    //添加专业
    Map saveMajor(String token, Major major) throws JsonProcessingException;

    //根据专业id删除专业
    Map deleteMajorByMajorId(String token, Integer major_id);
}
