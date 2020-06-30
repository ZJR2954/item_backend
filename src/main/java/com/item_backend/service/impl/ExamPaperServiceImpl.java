package com.item_backend.service.impl;

import com.item_backend.config.JwtConfig;
import com.item_backend.mapper.ExamPaperMapper;
import com.item_backend.mapper.QuestionMapper;
import com.item_backend.mapper.UserMapper;
import com.item_backend.model.dto.ExamPaperDto;
import com.item_backend.model.entity.ExamPaper;
import com.item_backend.model.entity.Question;
import com.item_backend.model.pojo.PageResult;
import com.item_backend.service.ExamPaperService;
import com.item_backend.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author xiao
 * @Time 2020/6/14
 * @Description TODO
 **/
@Service
public class ExamPaperServiceImpl implements ExamPaperService {
    @Autowired
    private ExamPaperMapper examPaperMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 获取我的试卷
     *
     * @param token
     * @param page
     * @param showCount
     * @return
     */
    @Override
    public PageResult<ExamPaper> getMyExamPapers(String token, Integer page, Integer showCount) {
        Integer u_id = jwtTokenUtil.getUIDFromToken(token.substring(jwtConfig.getPrefix().length()));
        List<ExamPaper> examPaperList;
        if (u_id == null) {
            return null;
        }
        examPaperList = examPaperMapper.searchExamPapersByUId(u_id, (page - 1) * showCount, showCount);
        return new PageResult<>(examPaperMapper.getExamPaperCountByUId(u_id), examPaperList);
    }

    /**
     * 获取试卷详情
     *
     * @param e_id
     * @return
     */
    @Override
    public Map getExamPaperDetail(Integer e_id) {
        Map<String, Object> map = new HashMap<>();
        ExamPaperDto examPaperDto = new ExamPaperDto();
        ExamPaper examPaper = examPaperMapper.searchExamPaperByEId(e_id);
        if (examPaper == null) {
            map.put("msg", "查询结果为空");
            return map;
        }
        examPaperDto.setExamPaper(examPaper);
        List<Question> questionList = questionMapper.searchQuestionsByEId(e_id);
        for (int i = 0; i < questionList.size(); i++) {
            questionList.get(i).setQ_content(HtmlUtils.htmlUnescape(questionList.get(i).getQ_content()));
        }
        examPaperDto.setQuestionList(questionList);
        map.put("examPaperDetail", examPaperDto);
        return map;
    }

    /**
     * 手动组卷
     *
     * @param token
     * @param examPaperDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveExamPaper(String token, ExamPaperDto examPaperDto) {
        Integer u_id = jwtTokenUtil.getUIDFromToken(token.substring(jwtConfig.getPrefix().length()));
        if (u_id == null) {
            return false;
        }
        examPaperDto.getExamPaper().setU_id(u_id);
        if (examPaperMapper.addExamPaper(examPaperDto.getExamPaper()) <= 0) {
            return false;
        }
        for (int i = 0; i < examPaperDto.getQuestionList().size(); i++) {
            examPaperMapper.addExamQuestion(examPaperDto.getExamPaper().getE_id(), examPaperDto.getQuestionList().get(i).getQ_id());
        }
        return true;
    }

    /**
     * 删除试卷
     *
     * @param e_id
     * @return
     */
    @Override
    public Boolean deleteExamPaper(String token, Integer e_id) {
        Integer u_id = jwtTokenUtil.getUIDFromToken(token.substring(jwtConfig.getPrefix().length()));
        if (u_id == null) {
            return false;
        }
        examPaperMapper.deleteExamQuestion(e_id);
        if (examPaperMapper.deleteExamPaperByEIdAndUId(e_id, u_id) <= 0) {
            return false;
        }
        return true;
    }
}
