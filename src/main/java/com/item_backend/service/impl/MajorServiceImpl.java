package com.item_backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.item_backend.config.JwtConfig;
import com.item_backend.config.RedisConfig;
import com.item_backend.mapper.FacultyMapper;
import com.item_backend.mapper.MajorMapper;
import com.item_backend.model.dto.UserDto;
import com.item_backend.model.entity.Faculty;
import com.item_backend.model.entity.Major;
import com.item_backend.service.MajorService;
import com.item_backend.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 添加专业
     *
     * @param
     * @return Map
     * @Author xiao
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map saveMajor(String token, Major major) throws JsonProcessingException {
        Integer u_id = jwtTokenUtil.getUIDFromToken(token.substring(jwtConfig.getPrefix().length()));
        Map<String, Object> map = new HashMap<>();
        // 判断u_id的值
        if (u_id == null) {
            map.put("msg", "无效的token");
            return map;
        }
        String userDtoJSON = redisTemplate.opsForValue().get(RedisConfig.REDIS_USER_MESSAGE + u_id);
        UserDto userDto = JSON.parseObject(userDtoJSON, UserDto.class);

        //添加专业
        List<Faculty> facultyList = facultyMapper.searchFacultyByUId(u_id);
        //将操作者所在的院系id设置给专业的所属院系id
        for (Faculty faculty : facultyList) {
            if (userDto.getUser().getU_faculty().equals(faculty.getFaculty_name())) {
                major.setFaculty_id(faculty.getFaculty_id());
            }
        }
        int flag = majorMapper.saveMajor(major);
        if (flag <= 0) {
            map.put("msg", "添加专业失败");
            return map;
        }

        //更新redis中存储的专业信息
//        redisTemplate.opsForValue().
//                set(RedisConfig.REDIS_MAJOR + major.getFaculty_id() + ":" + major.getMajor_id(), objectMapper.writeValueAsString(major));


        return map;
    }

    /**
     * 根据院系id查询专业列表
     *
     * @param
     * @return Map
     * @Author xiao
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map searchMajorByFacultyId(Integer faculty_id) {
        Map<String, Object> map = new HashMap<>();
        List<Major> majorList = majorMapper.searchMajorByFacultyId(faculty_id);
        map.put("majorList", majorList);

        return map;
    }

    /**
     * 根据专业id删除专业
     *
     * @param
     * @return Map
     * @Author xiao
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map deleteMajorByMajorId(String token, Integer major_id) {
        Map<String, Object> map = new HashMap<>();
        int flag = majorMapper.deleteMajorByMajorId(major_id);
        if (flag <= 0) {
            map.put("msg", "删除专业失败");
            return map;
        }

        //更新redis中存储的专业信息


        return map;
    }
}
