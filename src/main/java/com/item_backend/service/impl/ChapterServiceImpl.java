package com.item_backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.item_backend.config.RedisConfig;
import com.item_backend.mapper.ChapterMapper;
import com.item_backend.model.dto.UserDto;
import com.item_backend.model.entity.Chapter;
import com.item_backend.service.ChapterService;
import com.item_backend.service.SubjectService;
import com.item_backend.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @Author: Mt.Li
 * @Create: 2020-05-18 22:14
 */
@Service
public class ChapterServiceImpl implements ChapterService {
    @Autowired
    ChapterMapper chapterMapper;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    HttpServletRequest request;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Autowired
    SubjectServiceImpl subjectService;

    /**
     * 院级管理员添加章节
     * @param chapter
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addChapter(Chapter chapter) throws JsonProcessingException {
        if(!chapterMapper.addChapter(chapter)){
            return false;
        }
        Integer uId = jwtTokenUtil.getUIDFromRequest(request);
        UserDto userDto = JSON.parseObject(redisTemplate.opsForValue().get(RedisConfig.REDIS_USER_MESSAGE + uId), UserDto.class);
        Integer facultyId = userDto.getFaculty_id();
        subjectService.updateSubjectInRedis(facultyId);
        return true;
    }

    /**
     * 院级管理员删除章节
     * @param chapterId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteChapter(Integer chapterId) throws JsonProcessingException {
        if(!chapterMapper.deleteChapter(chapterId)){
            return false;
        }
        Integer uId = jwtTokenUtil.getUIDFromRequest(request);
        UserDto userDto = JSON.parseObject(redisTemplate.opsForValue().get(RedisConfig.REDIS_USER_MESSAGE + uId), UserDto.class);
        Integer facultyId = userDto.getFaculty_id();
        subjectService.updateSubjectInRedis(facultyId);
        return true;
    }
}
