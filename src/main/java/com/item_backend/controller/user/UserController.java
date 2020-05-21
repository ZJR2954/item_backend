package com.item_backend.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.item_backend.config.RedisConfig;
import com.item_backend.model.dto.SubjectDto;
import com.item_backend.model.entity.Chapter;
import com.item_backend.model.entity.QuestionType;
import com.item_backend.model.entity.Subject;
import com.item_backend.model.entity.User;
import com.item_backend.model.pojo.PageResult;
import com.item_backend.model.pojo.Result;
import com.item_backend.model.pojo.StatusCode;
import com.item_backend.service.SubjectService;
import com.item_backend.service.impl.ChapterServiceImpl;
import com.item_backend.service.impl.QuestionTypeServiceImpl;
import com.item_backend.service.impl.SubjectServiceImpl;
import com.item_backend.service.impl.UserServiceImpl;
import com.item_backend.utils.FormatUtil;
import com.item_backend.utils.JwtTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description: 用户控制层
 * @Author: Mt.Li & xiao
 * @Create: 2020-05-12 11:15
 */
@Api(tags = "User_api", description = "User_api", basePath = "/user")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    SubjectServiceImpl subjectService;

    @Autowired
    ChapterServiceImpl chapterService;

    @Autowired
    QuestionTypeServiceImpl questionTypeService;

    @Autowired
    FormatUtil formatUtil;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;



    @Autowired
    HttpServletRequest request;



    /**
     * 用户登录
     * @param user
     * @return Result：状态码+msg+(data)
     */
    @ApiOperation(value = "用户登录", notes = "Result：状态码+msg+(data)", httpMethod = "POST")
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        if (!formatUtil.checkStringNull(user.getJob_number(), user.getPassword())) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        try {
            Map map = null;
            try {
                map = userService.login(user);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if (map.containsKey("msg")){
                return Result.create(StatusCode.LOGINERROR, map.get("msg").toString());
            }
            return Result.create(StatusCode.OK, "登录成功", map);
        } catch (RuntimeException re) {
            return Result.create(StatusCode.LOGINERROR, re.getMessage());
        }
    }


    /**
     * 用户登出
     * @return Result：状态码+msg
     */
    @ApiOperation(value = "用户登出",notes = "Result：状态码+msg;删除redis中的key", httpMethod = "GET")
    @GetMapping("/logout")
    public Result logout() {
        if(userService.logout()) {
            return Result.create(StatusCode.OK, "退出成功");
        }
        return Result.create(StatusCode.ERROR, "退出失败");
    }

    @PostMapping("/profile")
    public Result profile(HttpServletRequest request) {
        String token = request.getHeader("token");
        try {
            Map map = userService.getProfile(token);
            if (map.get("msg") != null) {
                return Result.create(StatusCode.ACCESSERROR, map.get("msg").toString());
            }
            return Result.create(StatusCode.OK, "获取个人信息成功", map);
        } catch (RuntimeException re) {
            return Result.create(StatusCode.ERROR, re.getMessage());
        }
    }

    @PostMapping("/update_user_detail")
    public Result updateUserDetail(HttpServletRequest request, User user) {
        String token = request.getHeader("token");
        try {
            Map map = userService.updateUserDetail(token, user);
            if (map.get("msg") != null) {
                return Result.create(StatusCode.ACCESSERROR, map.get("msg").toString());
            }
            return Result.create(StatusCode.OK, "修改个人信息成功", map);
        } catch (Exception re) {
            return Result.create(StatusCode.ERROR, re.getMessage());
        }
    }

    @PostMapping("/change_password")
    public Result changePassword(HttpServletRequest request, String oldPassword, String newPassword) {
        String token = request.getHeader("token");
        try {
            Map map = userService.changePassword(token, oldPassword, newPassword);
            if (map.get("msg") != null) {
                return Result.create(StatusCode.ACCESSERROR, map.get("msg").toString());
            }
            return Result.create(StatusCode.OK, "修改登录密码成功", map);
        } catch (Exception re) {
            return Result.create(StatusCode.ERROR, re.getMessage());
        }
    }

    /**
     * 院级管理员查询学科列表
     * @param page
     * @param showCount
     * @return
     */
    @ApiOperation(value = "院级管理员查询学科列表", notes = "页数+显示数量")
    @GetMapping("/subject_list/{page}/{showCount}")
    public Result subjectList(@PathVariable(value = "page") Integer page, @PathVariable(value = "showCount") Integer showCount){
        if (!formatUtil.checkPositive(page, showCount)) {
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        // 判断是否拥有权限
        if (!jwtTokenUtil.checkUserType(request,"院级管理员")){
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        Integer count = subjectService.getSubjectCount(0);
        List<SubjectDto> subjectDto;
        try {
            subjectDto = subjectService.SearchSubjectList(page, showCount);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.create(StatusCode.SERVICEERROR, "服务异常");
        }
        if(count == 0 || subjectDto == null){
            return Result.create(StatusCode.OK, "查询结果为空");
        }
        PageResult<SubjectDto> pageResult = new PageResult<>(count, subjectDto);
        return Result.create(StatusCode.OK, "查询成功", pageResult);
    }

    /**
     * 院级管理员添加学科
     * @param subject
     * @return
     */
    @PostMapping("/add_subject")
    public Result addSubject(@RequestBody Subject subject) throws JsonProcessingException {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request,"院级管理员")){
                return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        if(!subjectService.addSubject(subject)){
            return Result.create(StatusCode.ERROR,"添加失败");
        }
        return Result.create(StatusCode.OK,"添加成功");
    }

    /**
     * 院级管理员删除学科
     * @param subjectId
     * @return
     */
    @DeleteMapping("/delete_subject/{subjectId}")
    public Result deleteSubject(@PathVariable(value = "subjectId") Integer subjectId) throws JsonProcessingException {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request,"院级管理员")){
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        // 判断数据是否为空
        if(!formatUtil.checkObjectNull(subjectId)){
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        if(!subjectService.deleteSubjectById(subjectId)){
            return Result.create(StatusCode.ERROR,"删除失败");
        }
        return Result.create(StatusCode.OK,"删除成功");

    }

    /**
     * 院级管理员添加题型
     * @param questionType
     * @return
     */
    @PostMapping("/add_q_type")
    public Result addQuestionType(@RequestBody QuestionType questionType) throws JsonProcessingException {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request,"院级管理员")){
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        if(!questionTypeService.addQuestionType(questionType)){
            return Result.create(StatusCode.ERROR,"添加失败");
        }
        return Result.create(StatusCode.OK,"添加成功");
    }

    /**
     * 院级管理员删除题型
     * @param qTypeId
     * @return
     */
    @DeleteMapping("/delete_q_type/{qTypeId}")
    public Result deleteQType(@PathVariable(value = "qTypeId") Integer qTypeId) throws JsonProcessingException {
        if (!jwtTokenUtil.checkUserType(request,"院级管理员")){
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        // 判断数据是否为空
        if(!formatUtil.checkObjectNull(qTypeId)){
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        if(!questionTypeService.deleteQuestionType(qTypeId)){
            return Result.create(StatusCode.ERROR,"删除失败");
        }

        return Result.create(StatusCode.OK,"删除成功");
    }

    /**
     * 院级管理员添加章节
     * @param chapter
     * @return
     */
    @PostMapping("/add_chapter")
    public Result addChapter(@RequestBody Chapter chapter) throws JsonProcessingException {
        // 判断权限
        if (!jwtTokenUtil.checkUserType(request,"院级管理员")){
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        if(!chapterService.addChapter(chapter)){
            return Result.create(StatusCode.ERROR,"添加失败");
        }
        return Result.create(StatusCode.OK,"添加成功");
    }

    /**
     * 院级管理员删除章节
     * @param chapterId
     * @return
     */
    @DeleteMapping("/delete_chapter/{chapterId}")
    public Result deleteChapter(@PathVariable(value = "chapterId") Integer chapterId) throws JsonProcessingException {
        if (!jwtTokenUtil.checkUserType(request,"院级管理员")){
            return Result.create(StatusCode.ACCESSERROR, "无权限");
        }
        // 判断数据是否为空
        if(!formatUtil.checkObjectNull(chapterId)){
            return Result.create(StatusCode.ERROR, "参数错误");
        }
        if(!chapterService.deleteChapter(chapterId)){
            return Result.create(StatusCode.ERROR,"删除失败");
        }
        return Result.create(StatusCode.OK,"删除成功");
    }
}
