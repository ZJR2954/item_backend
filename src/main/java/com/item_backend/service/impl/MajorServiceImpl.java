package com.item_backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.item_backend.config.RedisConfig;
import com.item_backend.mapper.FacultyMapper;
import com.item_backend.mapper.MajorMapper;
import com.item_backend.model.dto.MajorDto;
import com.item_backend.model.entity.Faculty;
import com.item_backend.model.entity.Major;
import com.item_backend.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author xiao
 * @Time 2020/5/17
 * @Description TODO
 **/
@Service
public class MajorServiceImpl implements MajorService {

    @Autowired
    MajorMapper majorMapper;

    @Autowired
    FacultyMapper facultyMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 根据院系id查询专业列表
     *
     * @param
     * @return List
     * @Author xiao
     */
    @Override
    public List<MajorDto> searchMajorListByFacultyId(Integer faculty_id) throws JsonProcessingException {
        // 先查询缓存中是否存在
        if (!redisTemplate.hasKey(RedisConfig.REDIS_MAJOR + faculty_id)) {
            // 缓存中不存在，先查询所有的学科信息放入redis中
            updateMajorInRedis(faculty_id);
        }
        // redis中已经存在，直接获取
        String majorJSON = redisTemplate.opsForValue().get(RedisConfig.REDIS_MAJOR + faculty_id);
        List<MajorDto> majorDtoList = JSON.parseObject(majorJSON, ArrayList.class);
        return majorDtoList;
    }

    /**
     * 添加专业
     *
     * @param
     * @return Boolean
     * @Author xiao
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean addMajor(Integer faculty_id, Major major) throws JsonProcessingException {
        major.setFaculty_id(faculty_id);
        if (majorMapper.addMajor(major) <= 0) {
            return false;
        }
        updateMajorInRedis(faculty_id);
        return true;
    }

    /**
     * 根据专业id删除专业
     *
     * @param
     * @return Boolean
     * @Author xiao
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteMajorByMajorId(Integer faculty_id, Integer major_id) throws JsonProcessingException {
        if(majorMapper.deleteMajorByMajorId(major_id) <= 0){
            return false;
        }
        updateMajorInRedis(faculty_id);
        return true;
    }

    /**
     * 更新redis中的学科信息
     *
     * @param faculty_id
     * @throws JsonProcessingException
     */
    @Override
    public void updateMajorInRedis(Integer faculty_id) throws JsonProcessingException {
        List<Major> majorList = majorMapper.searchMajorByFacultyId(faculty_id);
        if (majorList.size() <= 0) return;
        Faculty faculty = facultyMapper.searchFacultyByFacultyId(faculty_id);
        Iterator<Major> iter = majorList.iterator();
        List<MajorDto> majorDtoList = new ArrayList<>();
        while (iter.hasNext()) {
            Major m = iter.next();
            MajorDto majorDto = new MajorDto();
            majorDto.setMajor(m);
            majorDto.setFaculty(faculty);
            majorDtoList.add(majorDto);
        }
        redisTemplate.opsForValue().set(RedisConfig.REDIS_MAJOR + faculty_id, objectMapper.writeValueAsString(majorDtoList));
    }
}
