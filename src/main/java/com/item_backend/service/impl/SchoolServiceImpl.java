package com.item_backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.item_backend.config.JwtConfig;
import com.item_backend.config.RedisConfig;
import com.item_backend.mapper.SchoolMapper;
import com.item_backend.mapper.UserMapper;
import com.item_backend.model.dto.SchoolDto;
import com.item_backend.model.entity.School;
import com.item_backend.model.entity.User;
import com.item_backend.model.pojo.PageResult;
import com.item_backend.service.SchoolService;
import com.item_backend.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author xiao
 * @Time 2020/5/30
 * @Description TODO
 **/
@Service
public class SchoolServiceImpl implements SchoolService {

    @Autowired
    SchoolMapper schoolMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 查询信息列表
     *
     * @param
     * @return PageResult
     * @Author xiao
     */
    @Override
    public PageResult<SchoolDto> searchSchoolList(Integer page, Integer showCount) throws JsonProcessingException {
        // 先查询缓存中是否存在
        if (!redisTemplate.hasKey(RedisConfig.REDIS_SCHOOL)) {
            // 缓存中不存在，先查询所有的学科信息放入redis中
            updateSchoolInRedis();
        }
        // redis中已经存在，直接获取
        String schoolJSON = redisTemplate.opsForValue().get(RedisConfig.REDIS_SCHOOL);
        List<SchoolDto> schoolDtoList = JSON.parseObject(schoolJSON, ArrayList.class);
        int total = schoolDtoList.size();
        if (page != null && showCount != null) {
            schoolDtoList = schoolDtoList.size() < page * showCount ?
                    schoolDtoList.subList((schoolDtoList.size() / showCount) * showCount, schoolDtoList.size()) :
                    schoolDtoList.subList((page - 1) * showCount, page * showCount);
        }
        PageResult<SchoolDto> pageResult = new PageResult<>(total, schoolDtoList);
        return pageResult;
    }

    /**
     * 添加学校
     *
     * @param
     * @return Boolean
     * @Author xiao
     */
    @Override
    public Boolean addSchool(String token, School school) throws JsonProcessingException {
        Integer u_id = jwtTokenUtil.getUIDFromToken(token.substring(jwtConfig.getPrefix().length()));
        school.setU_id(u_id);
        if (schoolMapper.addSchool(school) <= 0) {
            return false;
        }
        updateSchoolInRedis();
        return true;
    }

    /**
     * 根据学校id删除学校
     *
     * @param
     * @return Boolean
     * @Author xiao
     */
    @Override
    public Boolean deleteSchoolBySchoolId(Integer school_id) throws JsonProcessingException {
        if (schoolMapper.deleteSchoolBySchoolId(school_id) <= 0) {
            return false;
        }
        updateSchoolInRedis();
        return true;
    }

    /**
     * 更新院系信息
     *
     * @param school
     * @return Boolean
     * @Author xiao
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateSchool(School school) throws JsonProcessingException {
        if (schoolMapper.updateSchool(school) <= 0) {
            return false;
        }
        updateSchoolInRedis();
        return true;
    }

    /**
     * 更新redis中的学校信息
     *
     * @throws JsonProcessingException
     */
    @Override
    public void updateSchoolInRedis() throws JsonProcessingException {
        List<School> schoolList = schoolMapper.searchSchoolList();
        if (schoolList.size() <= 0) {
            redisTemplate.delete(RedisConfig.REDIS_SCHOOL);
            return;
        }
        Iterator<School> iter = schoolList.iterator();
        List<SchoolDto> schoolDtoList = new ArrayList<>();
        while (iter.hasNext()) {
            School s = iter.next();
            User user = userMapper.searchUserByUId(s.getU_id());
            SchoolDto schoolDto = new SchoolDto();
            schoolDto.setSchool(s);
            schoolDto.setUser(user);
            schoolDtoList.add(schoolDto);
        }
        redisTemplate.opsForValue().set(RedisConfig.REDIS_SCHOOL, objectMapper.writeValueAsString(schoolDtoList));
    }
}
