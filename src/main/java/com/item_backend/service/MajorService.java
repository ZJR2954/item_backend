package com.item_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.item_backend.model.dto.MajorDto;
import com.item_backend.model.entity.Major;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author xiao
 * @Time 2020/5/17
 * @Description TODO
 **/
@Service
public interface MajorService {

    //根据院系id查询专业列表
    List<MajorDto> searchMajorListByFacultyId(Integer faculty_id) throws JsonProcessingException;

    //添加专业
    Boolean addMajor(Integer faculty_id, Major major) throws JsonProcessingException;

    //根据专业id删除专业
    Boolean deleteMajorByMajorId(Integer faculty_id, Integer major_id) throws JsonProcessingException;

    // 更新redis中Major信息
    void updateMajorInRedis(Integer faculty_id) throws JsonProcessingException;

}
